package com.test.BTClient;


//import BTClient;

//import java.text.SimpleDateFormat;
//import java.util.Date;
//import org.achartengine.chartdemo.*;
//import org.achartengine.chartdemo.demo.*;
//import org.achartengine.chartdemo.demo.chart.AverageTemperatureChart;
//import org.achartengine.chartdemo.demo.chart.IDemoChart;


import java.io.File;
//import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.io.OutputStream;
import java.util.UUID;


import com.test.BTClient.DeviceListActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;            //如使用菜单加入此三包
//import android.view.MenuInflater;
//import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class BTClient extends Activity {

	private final static int REQUEST_CONNECT_DEVICE = 1;    //宏定义查询设备句柄

	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号

	private InputStream is;    //输入流，用来接收蓝牙数据
	//private TextView text0;    //提示栏解句柄
	//private EditText edit0;    //发送数据输入句柄
	private TextView dis;       //接收数据显示句柄
	//	private ScrollView sv;      //翻页句柄
	private String w_value = "";    //显示体重
	//	private String fmsg = "";    //保存用数据缓存
	private String h_value = ""; // 显示身高
	private String BMI_value = "";
	private String w_save = "";
	private String h_save = "";

	int[] weight = new int[100];// 读取文件后存入体重数组
	int[] height = new int[100];// 读取文件后存入身高数组
	float[] BMI = new float[100];// BMI指数 = kg/(m^2)

	public String filename=""; //用来保存存储的文件名
	BluetoothDevice _device = null;     //蓝牙设备
	BluetoothSocket _socket = null;      //蓝牙通信socket
	boolean _discoveryFinished = false;    
	boolean bRun = true;
	boolean bThread = false;

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备

	//private TextView dis2;
	//private TextView dis3;



	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);   //设置画面为主画面 main.xml

		//text0 = (TextView)findViewById(R.id.Text0);  //得到提示栏句柄
		//	edit0 = (EditText)findViewById(R.id.Edit0);   //得到输入框句柄
		//  sv = (ScrollView)findViewById(R.id.ScrollView01);  //得到翻页句柄
		dis = (TextView) findViewById(R.id.weight);      //得到数据显示句柄
		//		dis2 = (TextView) findViewById(R.id.height); 
		//如果打开本地蓝牙设备不成功，提示信息，结束程序
		if (_bluetooth == null){
			Toast.makeText(this, "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		// 设置设备可以被搜索  
		new Thread(){
			public void run(){
				if(_bluetooth.isEnabled()==false){
					_bluetooth.enable();
				}
			}   	   
		}.start();      
	}

	//发送按键响应
	/*
	public void onSendButtonClicked(View v){
		int i=0;
		int n=0;
		try{
			OutputStream os = _socket.getOutputStream();   //蓝牙连接输出流
			byte[] bos = edit0.getText().toString().getBytes();
			for(i=0;i<bos.length;i++){
				if(bos[i]==0x0a)n++;
			}
			byte[] bos_new = new byte[bos.length+n];
			n=0;
			for(i=0;i<bos.length;i++){ //手机中换行为0a,将其改为0d 0a后再发送
				if(bos[i]==0x0a){
					bos_new[n]=0x0d;
					n++;
					bos_new[n]=0x0a;
				}else{
					bos_new[n]=bos[i];
				}
				n++;
			}

			os.write(bos_new);	
		}catch(IOException e){  		
		}  	
	}*/

	//接收活动结果，响应startActivityForResult()
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case REQUEST_CONNECT_DEVICE:     //连接结果，由DeviceListActivity设置返回
			// 响应返回结果
			if (resultCode == Activity.RESULT_OK) {   //连接成功，由DeviceListActivity设置返回
				// MAC地址，由DeviceListActivity设置返回
				String address = data.getExtras()
						.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// 得到蓝牙设备句柄      
				_device = _bluetooth.getRemoteDevice(address);

				// 用服务号得到socket
				try{
					_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
				}catch(IOException e){
					Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
				}
				//连接socket
				Button btn = (Button) findViewById(R.id.Button03);
				try{
					_socket.connect();
					Toast.makeText(this, "连接"+_device.getName()+"成功！", Toast.LENGTH_SHORT).show();
					btn.setText("断开");
				}catch(IOException e){
					try{
						Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
						_socket.close();
						_socket = null;
					}catch(IOException ee){
						Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
					}

					return;
				}

				//打开接收线程
				try{
					is = _socket.getInputStream();   //得到蓝牙数据输入流
				}catch(IOException e){
					Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show();
					return;
				}
				if(bThread==false){
					ReadThread.start();
					bThread=true;
				}else{
					bRun = true;
				}
			}
			break;
		default:break;
		}
	}

	//接收数据线程
	Thread ReadThread=new Thread(){

		public void run(){
			int num = 0;
			byte[] buffer = new byte[1024];
			byte[] buffer_new = new byte[1024];

			double	BMI=0;
			float BMI_save=0;
			//	byte[] buffer_save = new byte[1024];//qzxin:添加

			int i = 0;
			int n = 0;
			bRun = true;
			//接收线程
			while(true){
				try{
					while(is.available()==0){
						while(bRun == false){}
					}
					Thread.sleep(200);
					while(true){
						num = is.read(buffer);         //读入数据
						n=0;

						//		String s0 = new String(buffer,0,num);
						//		fmsg+=s0;    //保存收到数据

						for(i=0;i<num;i++){
							Log.i("BUF", String.valueOf(buffer[i]));
							if(buffer[i] == (byte)0xff){
								buffer_new[n] = buffer[++i];
							}
							if(buffer[i] == (byte)0xfe){
								buffer_new[n] = buffer[++i];
							}

							n++;

						}

						//qzxin
						w_save=String.valueOf(buffer_new[0]);
						h_save="1"+String.valueOf(buffer_new[1]);
						//	buffer_new[2]=10000*buffer_new[0]/(buffer_new[1]);
						int tmp_w=Integer.parseInt(String.valueOf(buffer_new[0]));
						int tmp_h=Integer.parseInt("1"+String.valueOf(buffer_new[1]));
						BMI=10000*tmp_w/Math.pow(tmp_h, 2);
						BMI_save=(float)BMI;
						if(BMI_save<18.5)
							BMI_value="BMI:"+String.valueOf(BMI_save)+"\n"+"偏瘦";
						else if(BMI_save>24)
							BMI_value="BMI:"+String.valueOf(BMI_save)+"\n"+"你该努力了哟";
						else
							BMI_value="BMI:"+String.valueOf(BMI_save)+"\n"+"健康，请保持";


						h_value="1"+String.valueOf(buffer_new[1])+"cm"+"\n"+BMI_value;
						w_value=String.valueOf(buffer_new[0])+"Kg"+"  "+h_value;   //写入接收缓存
						if(is.available()==0)break;  //短时间没有数据才跳出进行显示
					}
					//发送显示消息，进行显示刷新
					handler.sendMessage(handler.obtainMessage());       	    		
				} catch(IOException e){
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	//消息处理队列
	Handler handler= new Handler(){
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			dis.setText(w_value);   //显示体重数据
			//	dis2.setText(h_value);
			//	dis3.setText(BMI_value);

			//sv.scrollTo(0,dis.getMeasuredHeight()); //跳至数据最后一页
		}
	};





	//关闭程序掉用处理部分

	public void onDestroy(){
		super.onDestroy();
		if(_socket!=null)  //关闭连接socket
			try{
				_socket.close();
			}catch(IOException e){}
		//	_bluetooth.disable();  //关闭蓝牙服务
	}



	//连接按键响应函数
	public void onConnectButtonClicked(View v){ 
		if(_bluetooth.isEnabled()==false){  //如果蓝牙服务不可用则提示
			Toast.makeText(this, " 打开蓝牙中...", Toast.LENGTH_LONG).show();
			return;
		}


		//如未连接设备则打开DeviceListActivity进行设备搜索
		Button btn = (Button) findViewById(R.id.Button03);
		if(_socket==null){
			Intent serverIntent = new Intent(this, DeviceListActivity.class); //跳转程序设置
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //设置返回宏定义
		}
		else{
			//关闭连接socket
			try{

				is.close();
				_socket.close();
				_socket = null;
				bRun = false;
				btn.setText("连接");
			}catch(IOException e){}   
		}
		return;
	}

	//保存按键响应函数
	public void onSaveButtonClicked(View v){
		Save();
	}

	//历史按键响应函数


	//退出按键响应函数
	public void onQuitButtonClicked(View v){
		finish();
	}

	//保存功能实现
	private void Save() {
		//显示对话框输入文件名
		//		LayoutInflater factory = LayoutInflater.from(BTClient.this);  //图层模板生成器句柄
		//	final View DialogView =  factory.inflate(R.layout.sname, null);  //用sname.xml模板生成视图模板
		new AlertDialog.Builder(BTClient.this)
		.setTitle("")
		//							.setView(DialogView)   //设置视图模板
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() //确定按键响应函数
		{
			public void onClick(DialogInterface dialog, int whichButton){
				//EditText text1 = (EditText)DialogView.findViewById(R.id.sname);  //得到文件名输入框句柄
				filename = "w.txt";  //得到文件名

				try{
					if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  //如果SD卡已准备好

						//filename =filename+".txt";   //在文件名末尾加上.txt										
						File sdCardDir = Environment.getExternalStorageDirectory();  //得到SD卡根目录
						//File BuildDir = new File(sdCardDir, "/data");   //打开data目录，如不存在则生成
						//if(BuildDir.exists()==false)BuildDir.mkdirs();
						File saveFile =new File(sdCardDir.getAbsolutePath()+File.separator+filename);  //新建文件句柄，如已存在仍新建文档
						FileOutputStream stream = new FileOutputStream(saveFile,true);  //打开文件输入流
						//stream.write(fmsg.getBytes());
						stream.write(w_save.getBytes());
						stream.write("a".getBytes());
						/*
						stream.write(h_save.getBytes());
						stream.write("a".getBytes());*/
						stream.close();
						Toast.makeText(BTClient.this, "存储成功！", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(BTClient.this, "没有存储卡！", Toast.LENGTH_LONG).show();
					}

				}catch(IOException e){
					return;
				}



			}
		})
		.setNegativeButton("取消",   //取消按键响应函数,直接退出对话框不做任何处理 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
			}
		}).show();  //显示对话框
	} 







	private IDemoChart[] mCharts = new IDemoChart[] { new WeightLineChart()};
	//历史按键响应函数
	public void _ChartDemo(View view)
	{
		/*
	Intent intent=new Intent();
	intent.setClass(this, ChartDemo.class);
	startActivity(intent);*/


		Intent intent= mCharts[0].execute(this);
		startActivity(intent);
	}

}
