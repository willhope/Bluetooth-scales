#ifndef __SEGMENTLCD__H__
#define __SEGMENTLCD__H__

#include <Arduino.h>

//定义HT1621的命令 
#define  ComMode    0x52  //4COM,1/3bias  1000    010 1001  0  
#define  RCosc      0x30  //内部RC振荡器(上电默认)1000 0011 0000 
#define  LCD_on     0x06  //打开LCD 偏压发生器1000     0000 0 11 0 
#define  LCD_off    0x04  //关闭LCD显示 
#define  Sys_en     0x02  //系统振荡器开 1000   0000 0010 
#define  CTRl_cmd   0x80  //写控制命令 
#define  Data_cmd   0xa0  //写数据命令 

//设置变量寄存器函数
#define sbi(x, y)  (x |= (1 << y))   /*置位寄器x的第y位*/
#define cbi(x, y)  (x &= ~(1 <<y ))  /*清零寄器x的第y位*/  

//IO端口定义
#define LCD_DATA 4
#define LCD_WR 5
#define LCD_CS 6


//定义端口HT1621数据端口 
#define LCD_DATA1    digitalWrite(LCD_DATA,HIGH) 
#define LCD_DATA0    digitalWrite(LCD_DATA,LOW) 
#define LCD_WR1      digitalWrite(LCD_WR,HIGH)  
#define LCD_WR0      digitalWrite(LCD_WR,LOW)   
#define LCD_CS1      digitalWrite(LCD_CS,HIGH)  
#define LCD_CS0      digitalWrite(LCD_CS,LOW)

//函数声明
extern void SendBit_1621(unsigned char sdat,unsigned char cnt); //data 的高cnt 位写入HT1621，高位在前
extern void SendCmd_1621(unsigned char command);			//发送指令
extern void Write_1621(unsigned char addr,unsigned char sdat);
extern void HT1621_all_off(unsigned char num);
extern void HT1621_all_on(unsigned char num);
extern void HT1621_all_on_num(unsigned char num,unsigned char xx);
extern void LCDoff(void);
extern void LCDon(void);
extern void Displaybihua(void);
extern void Init_1621(void);
extern void Displayall8(void);
//extern void Displaydata(long int t,int p,char bat1,char bat2,char bat3);	//屏显示，bat1，bat2，bat3 右侧电池
extern void Write_1621_data(unsigned char num,unsigned char sdat) ;



extern char dispnum[5];
extern const char num[]; 
extern const char Table_Hello[];
extern const char Table_Error[];

#endif