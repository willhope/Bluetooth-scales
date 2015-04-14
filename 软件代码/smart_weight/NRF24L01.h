#ifndef NRF24L01_h
#define NRF24L01_h

#include "API.h"

//---------------------------------------------
#define TX_ADR_WIDTH    5   
// 5 unsigned chars TX(RX) address width
#define TX_PLOAD_WIDTH  1  
// 20 unsigned chars TX payload
//---------------------------------------------
#define CE_PIN       8
// CE_PIN_BIT:   Digital Input     Chip Enable Activates RX or TX mode
#define CSN_PIN      9
// CSN_PIN BIT:  Digital Input     SPI Chip Select
#define SCK_PIN      10
// SCK_PIN BIT:  Digital Input     SPI Clock
#define MOSI_PIN     11
// MOSI_PIN BIT: Digital Input     SPI Slave Data Input
#define MISO_PIN     12
// MISO_PIN BIT: Digital Output    SPI Slave Data Output, with tri-state option
#define IRQ      13
// IRQ BIT:  Digital Output    Maskable interrupt pin
//*********************************************
#endif
