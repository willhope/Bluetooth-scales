#include "HX711.h"
#include "SegmentLCD.h"
#include "NRF24L01.h"

//***************************************************
#define TX_ADR_WIDTH    5   // 5 unsigned chars TX(RX) address width
#define TX_PLOAD_WIDTH  32  // 32 unsigned chars TX payload

unsigned char TX_ADDRESS[TX_ADR_WIDTH]  = 
{
  0x34,0x43,0x10,0x10,0x01
}; // Define a static TX address

unsigned char rx_buf[TX_PLOAD_WIDTH];
unsigned char tx_buf[TX_PLOAD_WIDTH];
//***************************************************

signed int Weight = 0;
signed int Last_Weight;
byte string[4]={0xFF,0,0xFE,0};
void setup()
{
        int i;
        pinMode(CE_PIN,  OUTPUT);
        pinMode(SCK_PIN, OUTPUT);
        pinMode(CSN_PIN, OUTPUT);
        pinMode(MOSI_PIN,  OUTPUT);
        pinMode(MISO_PIN, INPUT);
        pinMode(IRQ, INPUT);
	Init_Hx711();				//初始化HX711模块连接的IO设置
        Init_1621();
        init_io();                        // Initialize IO port
	for( i = 0 ; i < 6 ; i++ )
	{
		Write_1621_data(5-i,Table_Hello[i]);				//HELLO
	}
        delay(2000);
        HT1621_all_off(5);
	Serial.begin(9600);
        Serial1.begin(9600);
        
        unsigned char status=SPI_Read(STATUS);
	Serial.print("Welcome to use!\n");
        RX_Mode(); 
	Get_Maopi();		//获取毛皮
}

void loop()
{       
        Last_Weight = Weight;
	Weight = Get_Weight();	//计算放在传感器上的重物重量
        Write_1621_data(3,num[Weight%100/10]);
        Write_1621_data(2,num[Weight%10]);
        if( Last_Weight < Weight)
       {
         string[1] = Weight;
         Serial1.write(string,4);
         Serial.print(Weight);
         Serial.print("KG");
       }
       
       unsigned char status = SPI_Read(STATUS);
       if(status&RX_DR)                                                 // if receive data ready (TX_DS) interrupt
        {
          SPI_Read_Buf(RD_RX_PLOAD, rx_buf, TX_PLOAD_WIDTH);             // read playload to rx_buf
          SPI_RW_Reg(FLUSH_RX,0);            // clear RX_FIF
          string[3] = rx_buf[0];
          Serial.print("height: ");
          Serial.print(string[3],DEC);                              // print rx_buf
         
        }
        SPI_RW_Reg(WRITE_REG+STATUS,status); 
	delay(1000);				//延时1s

}


void init_io(void)
{
  digitalWrite(IRQ, 0);
  digitalWrite(CE_PIN, 0);			// chip enable
  digitalWrite(CSN_PIN, 1);                 // Spi disable	
}

/**************************************************
 * Function: SPI_RW();
 * 
 * Description:
 * Writes one unsigned char to nRF24L01, and return the unsigned char read
 * from nRF24L01 during write, according to SPI protocol
 **************************************************/
unsigned char SPI_RW(unsigned char Byte)
{
  unsigned char i;
  for(i=0;i<8;i++)                      // output 8-bit
  {
    if(Byte&0x80)
    {
      digitalWrite(MOSI_PIN, 1);    // output 'unsigned char', MSB to MOSI_PIN
    }
    else
    {
      digitalWrite(MOSI_PIN, 0);
    }
    digitalWrite(SCK_PIN, 1);                      // Set SCK_PIN high..
    Byte <<= 1;                         // shift next bit into MSB..
    if(digitalRead(MISO_PIN) == 1)
    {
      Byte |= 1;       	                // capture current MISO_PIN bit
    }
    digitalWrite(SCK_PIN, 0);         	// ..then set SCK_PIN low again
  }
  return(Byte);           	        // return read unsigned char
}
/**************************************************/

/**************************************************
 * Function: SPI_RW_Reg();
 * 
 * Description:
 * Writes value 'value' to register 'reg'
/**************************************************/
unsigned char SPI_RW_Reg(unsigned char reg, unsigned char value)
{
  unsigned char status;

  digitalWrite(CSN_PIN, 0);                   // CSN_PIN low, init SPI transaction
  status = SPI_RW(reg);                   // select register
  SPI_RW(value);                          // ..and write value to it..
  digitalWrite(CSN_PIN, 1);                   // CSN_PIN high again

  return(status);                   // return nRF24L01 status unsigned char
}
/**************************************************/

/**************************************************
 * Function: SPI_Read();
 * 
 * Description:
 * Read one unsigned char from nRF24L01 register, 'reg'
/**************************************************/
unsigned char SPI_Read(unsigned char reg)
{
  unsigned char reg_val;

  digitalWrite(CSN_PIN, 0);           // CSN_PIN low, initialize SPI communication...
  SPI_RW(reg);                   // Select register to read from..
  reg_val = SPI_RW(0);           // ..then read register value
  digitalWrite(CSN_PIN, 1);          // CSN_PIN high, terminate SPI communication

  return(reg_val);               // return register value
}
/**************************************************/

/**************************************************
 * Function: SPI_Read_Buf();
 * 
 * Description:
 * Reads 'unsigned chars' #of unsigned chars from register 'reg'
 * Typically used to read RX payload, Rx/Tx address
/**************************************************/
unsigned char SPI_Read_Buf(unsigned char reg, unsigned char *pBuf, unsigned char bytes)
{
  unsigned char status,i;

  digitalWrite(CSN_PIN, 0);                  // Set CSN_PIN low, init SPI tranaction
  status = SPI_RW(reg);       	    // Select register to write to and read status unsigned char

  for(i=0;i<bytes;i++)
  {
    pBuf[i] = SPI_RW(0);    // Perform SPI_RW to read unsigned char from nRF24L01
  }

  digitalWrite(CSN_PIN, 1);                   // Set CSN_PIN high again

  return(status);                  // return nRF24L01 status unsigned char
}
/**************************************************/

/**************************************************
 * Function: SPI_Write_Buf();
 * 
 * Description:
 * Writes contents of buffer '*pBuf' to nRF24L01
 * Typically used to write TX payload, Rx/Tx address
/**************************************************/
unsigned char SPI_Write_Buf(unsigned char reg, unsigned char *pBuf, unsigned char bytes)
{
  unsigned char status,i;

  digitalWrite(CSN_PIN, 0);                   // Set CSN_PIN low, init SPI tranaction
  status = SPI_RW(reg);             // Select register to write to and read status unsigned char
  for(i=0;i<bytes; i++)             // then write all unsigned char in buffer(*pBuf)
  {
    SPI_RW(*pBuf++);
  }
  digitalWrite(CSN_PIN, 1);                  // Set CSN_PIN high again
  return(status);                  // return nRF24L01 status unsigned char
}
/**************************************************/

/**************************************************
 * Function: RX_Mode();
 * 
 * Description:
 * This function initializes one nRF24L01 device to
 * RX Mode, set RX address, writes RX payload width,
 * select RF channel, datarate & LNA HCURR.
 * After init, CE_PIN is toggled high, which means that
 * this device is now ready to receive a datapacket.
/**************************************************/
void RX_Mode(void)
{
  digitalWrite(CE_PIN, 0);
  SPI_Write_Buf(WRITE_REG + RX_ADDR_P0, TX_ADDRESS, TX_ADR_WIDTH); // Use the same address on the RX device as the TX device
  SPI_RW_Reg(WRITE_REG + EN_AA, 0x01);      // Enable Auto.Ack:Pipe0
  SPI_RW_Reg(WRITE_REG + EN_RXADDR, 0x01);  // Enable Pipe0
  SPI_RW_Reg(WRITE_REG + RF_CH, 40);        // Select RF channel 40
  SPI_RW_Reg(WRITE_REG + RX_PW_P0, TX_PLOAD_WIDTH); // Select same RX payload width as TX Payload width
  SPI_RW_Reg(WRITE_REG + RF_SETUP, 0x07);   // TX_PWR:0dBm, Datarate:2Mbps, LNA:HCURR
  SPI_RW_Reg(WRITE_REG + CONFIG, 0x0f);     // Set PWR_UP bit, enable CRC(2 unsigned chars) & Prim:RX. RX_DR enabled..
  digitalWrite(CE_PIN, 1);                             // Set CE_PIN pin high to enable RX device
  //  This device is now ready to receive one packet of 16 unsigned chars payload from a TX device sending to address
  //  '3443101001', with auto acknowledgment, retransmit count of 10, RF channel 40 and datarate = 2Mbps.
}
/**************************************************/
