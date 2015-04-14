/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.test.BTClient;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
//import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

//import com.test.BTClient.BTClient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
//import android.widget.Toast;

/**
 * 体重折线图
 */
public class WeightLineChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "体重折线图";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return " ";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    String[] titles = new String[] { "Weight", "Corfu", "Thassos", "Skiathos" };
    List<double[]> x = new ArrayList<double[]>();
    List<double[]> values = new ArrayList<double[]>();
    /*
    for (int i = 1; i < titles.length; i++) {
    	   x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
    	  //  	x.add(new double[] { 1, 2, 3, 4, 5, 6, 7});
    	    	//  x.add(new double[] { -12,-11,-10,-9,-8,-7,- 6, -5, -4, -3, -2 ,-1});
    	    }*/
    
   int []weight=read();
	 
    int m=weight[199];//体重数据的总数
   if(m<=30)
   {
    double plot_x[]=new double[m];
    for(int i=0;i<m;i++)
    	plot_x[i]=i+1;
    double plot_y[]=new double[m];
    for(int i=0;i<m;i++)
    	 plot_y[i]=weight[i];
    x.add(plot_x);
    values.add(plot_y);
 }
   
    else
    {
    	 double plot_x[]=new double[29];
    	 x.add(plot_x);
    	values.add(new double[] {weight[m-29],weight[m-28], weight[m-27], weight[m-26], weight[25],weight[m-24],weight[m-23], weight[m-22], weight[m-21], weight[20], weight[19], weight[m-18], weight[m-17], weight[m-16], weight[m-15],weight[m-14], 
    		weight[m-13],weight[m-12],weight[m-11], weight[m-10], weight[m-9], weight[8], weight[7], weight[m-6], weight[m-5], weight[m-4], weight[m-3],weight[m-2],weight[m-1] });
    	
    	
    }
    	
    x.add(new double[] {0});
    x.add(new double[] {0});
    x.add(new double[] {0});
    values.add(new double[] { 0});
    values.add(new double[] { 0});
    values.add(new double[] { 0});
    /*
   
    {
        for (int i = 1; i < titles.length; i++) {
     	   x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
     	 
     	    }
    values.add(new double[] { weight[m-12],weight[m-11], weight[m-10], weight[m-9], weight[8], weight[7], weight[m-6], weight[m-5], weight[m-4], weight[m-3],weight[m-2], 
    		weight[m-1] });
    }
    else{
    
    	for(int i=11;i>=m;i--)
    		weight[i]=20;
    	
    	  values.add(new double[] { weight[0],weight[1], weight[2], weight[3], weight[4], weight[5], weight[6], weight[7], weight[8], weight[9],weight[10], 
    	   		weight[11] });
    		
    	}
    	
    
    	//values.add(new double[] {weight[m-12],weight[m-11], weight[m-10], weight[m-9], weight[8], weight[7], weight[m-6], weight[m-5], weight[m-4], weight[m-3],weight[m-2], 
        //		weight[m-1] });
    values.add(new double[] { 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 });
    values.add(new double[] { 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 });
    values.add(new double[] { 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 });
    */
    int[] colors = new int[] { Color.BLUE, Color.BLACK,Color.BLACK, Color.BLACK };
    PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND,
        PointStyle.TRIANGLE, PointStyle.SQUARE };
    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
    int length = renderer.getSeriesRendererCount();
    for (int i = 0; i < length; i++) {
      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
    }
    setChartSettings(renderer, "History Weight", "Day", "体重(Kg)", 0.5, 12.5, 0, 80,
        Color.LTGRAY, Color.LTGRAY);
    renderer.setXLabels(12);
    renderer.setYLabels(10);
    renderer.setShowGrid(true);
    renderer.setXLabelsAlign(Align.RIGHT);
    renderer.setYLabelsAlign(Align.RIGHT);
    renderer.setZoomButtonsVisible(true);
    renderer.setPanLimits(new double[] { 0, 30, 0, 80 });
    renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
   
    renderer.setBackgroundColor(Color.GRAY);
    
    XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);
//    XYSeries series = dataset.getSeriesAt(0);
   // series.addAnnotation("Vacation", 6, 30);
    Intent intent = ChartFactory.getLineChartIntent(context, dataset, renderer,
        "体重趋势变化图");
    return intent;
  }


public int[] read() {
	// 声明流对象
	int []weight=new int[200];
	//for(int i=0;i<200;i++)
	//	weight[i]=0;
	int read_data_i = 0;// 读取数据时，数据的第几组
	// int h_i = 0;
	FileInputStream fis = null;
	try {
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){  //如果SD卡已准备好
			// 创建流对象
					File sdCardDir = android.os.Environment.getExternalStorageDirectory();  //得到SD卡根目录
			//		File BuildDir = new File(sdCardDir, "/data");
					File readFile =new File(sdCardDir.getAbsolutePath()+File.separator+"w.txt");
			fis = new FileInputStream(readFile);

			// 读取数据，并将读取到的数据存储到数组中
			byte[] data = new byte[1024]; // 数据存储的数组
			int i = 0; // 当前下标
			// 读取流中的第一个字节数据
			int n = fis.read();
			// 依次读取后续的数据
			while (n != -1) { // 未到达流的末尾
				// 将有效数据存储到数组中
				data[i] = (byte) n;

				// 下标增加
				i++;
				// 读取下一个字节的数据
				n = fis.read();
			}
			// 解析数据
			String s = new String(data, 0, i);

			String[] as = s.split("a");// 将数据流，以空格为分隔符存入数组中
			int p = as.length;// p/2即为数据的组数
			// System.out.println(p / 2 / 2);
			weight[199]=p;
			for (int t = 0; t < p; t++) {
				// System.out.println(as[t]);
				weight[read_data_i] = Integer.parseInt(as[t]);// 体重数组,字符型数字->int型数字
			//	height[read_data_i] = Integer.parseInt(as[t]);// 身高数组
			//	BMI[read_data_i] = weight[read_data_i] * 10000
			//			/ (height[read_data_i]) / height[read_data_i];
				read_data_i++;
				// System.out.println(BMI[read_data_num - 1]);
			}			
			//Toast.makeText("读取数据成功！", Toast.LENGTH_SHORT).show();
		}
		else ;
			//Toast.makeText( "没有存储卡！", Toast.LENGTH_LONG).show();
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try {
			// 关闭流，释放资源
			fis.close();
		} catch (Exception e) {
		}
	}
	return weight;
}
}
