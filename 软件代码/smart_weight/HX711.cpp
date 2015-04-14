#include "hx711.h"

unsigned long HX711_Buffer = 0;
unsigned int Weight_Maopi = 0;
signed long Weight_Shiwu = 0;

//****************************************************
//初始化HX711
//****************************************************
void Init_Hx711()
{
	pinMode(HX711_SCK, OUTPUT);	
	pinMode(HX711_DT, INPUT);
}


//****************************************************
//获取毛皮重量
//****************************************************
void Get_Maopi()
{
	HX711_Buffer = HX711_Read();
	Weight_Maopi = HX711_Buffer/100;		
} 

//****************************************************
//称重
//****************************************************
unsigned int Get_Weight()
{
	HX711_Buffer = HX711_Read();
	HX711_Buffer = HX711_Buffer/100;
      /*  Serial.print("HX711_Buffer:");
	Serial.print(HX711_Buffer);
	Serial.print("\n");*/
	
		Weight_Shiwu = HX711_Buffer;
		Weight_Shiwu = Weight_Shiwu - Weight_Maopi;				//获取实物的AD采样数值。
      /*	Serial.print("Weight_Maopi:");
                Serial.print(Weight_Maopi);
                Serial.print("\n");
                Serial.print("Weight_Shiwu:");
                Serial.print(Weight_Shiwu);
	        Serial.print("\n");*/
		Weight_Shiwu = (131060-Weight_Shiwu)*0.004; 	

			//计算实物的实际重量
			//因为不同的传感器特性曲线不一样，因此，每一个传感器需要矫正这里的4.30这个除数。
			//当发现测试出来的重量偏大时，增加该数值。
			//如果测试出来的重量偏小时，减小改数值。
			//该数值一般在4.0-5.0之间。因传感器不同而定。
			//+0.05是为了四舍五入百分位

	return Weight_Shiwu;
}

//****************************************************
//读取HX711
//****************************************************
unsigned long HX711_Read(void)	//增益128
{
	unsigned long count; 
	unsigned char i;
	bool Flag = 0;

	digitalWrite(HX711_DT, HIGH);
	delayMicroseconds(1);

	digitalWrite(HX711_SCK, LOW);
	delayMicroseconds(1);

  	count=0; 
  	while(digitalRead(HX711_DT)); 
  	for(i=0;i<24;i++)
	{ 
	  	digitalWrite(HX711_SCK, HIGH); 
		delayMicroseconds(1);
	  	count=count<<1; 
		digitalWrite(HX711_SCK, LOW); 
		delayMicroseconds(1);
	  	if(digitalRead(HX711_DT))
			count++; 
	} 
 	digitalWrite(HX711_SCK, HIGH); 
	delayMicroseconds(1);
	digitalWrite(HX711_SCK, LOW); 
	delayMicroseconds(1);
	//count ^= 0x800000;
	return(count);
}
