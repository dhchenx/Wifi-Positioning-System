package com.bluecanna.wificlock.view;
 
import com.bluecanna.wificlock.bll.ClockDisplay;
import com.bluecanna.wificlock.model.ClockModel;
import com.bluecanna.wificlock.model.WiFiClockModel;
import com.bluecanna.wificlock.utils.Sys;
import com.bluecanna.wificlock.utils.Sys.ScreenInfo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class ClockView extends View {
	public Bitmap bitmap=null;
	public ClockViewUtils cvu=null;
	int heightpx=0;
	int widthpx=0;
	Context context=null;
	public ClockView(final Context context) {
		super(context);
		this.context=context;
		cvu=new ClockViewUtils((Activity)context);
		ScreenInfo si=new Sys().getScreenInfo((Activity)context);
		heightpx=si.heightpx;
		widthpx=si.widthpx;
		this.createClockBg();
	//	this.CreateCircle();
	}
	public void createClockBg() {
	  bitmap = Bitmap.createBitmap(widthpx,heightpx
				,Bitmap.Config.ARGB_8888);
		Canvas	canvas = new Canvas();
		canvas.setBitmap(bitmap); 
		Paint p = cvu.CreatePaint(Color.WHITE);
		cvu.drawImage(canvas, bitmap, 0, 0, widthpx, heightpx, 0, 0,bitmap.getWidth(), bitmap.getHeight());
		invalidate();
	}
	// 当按下屏幕时的回调函数
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 导航模式不进行创建地图
		
		return true;
	}
	public void DisplayWiFiSignNow(float rssi){
		ClockModel cm=new ClockModel();
		cm.c_x=widthpx/2;
		cm.c_y=heightpx/2.5f;
		cm.r=widthpx/3;
		cm.rssi=rssi;
		ClockDisplay cd=new ClockDisplay((Activity)context,cm);
		bitmap=cd.getBitmap();
		// invalidate();
		postInvalidate();
	}
	public void DisplayWiFiSignNow(WiFiClockModel wcms){
		ClockDisplay cd=new ClockDisplay((Activity)context,wcms);
		bitmap=cd.getBitmap();
		// invalidate();
		postInvalidate();
	}
	public void CreateCircle(){
		/*
		Canvas c=new Canvas();
		  bitmap = Bitmap.createBitmap(widthpx,heightpx
					,Bitmap.Config.ARGB_8888);
		  c.setBitmap(bitmap);
		  c.drawCircle(40, 40, 50, cvu.CreatePaint1(Color.BLACK));
		 */
		ClockModel cm=new ClockModel();
		cm.c_x=widthpx/2;
		cm.c_y=heightpx/2.5f;
		cm.r=widthpx/3;
		cm.rssi=50;
		
		ClockDisplay cd=new ClockDisplay((Activity)context,cm);
		 bitmap=cd.getBitmap();
		 invalidate();
	}



	 
	// 同样,onDraw方法也是在画布需要更新的时候会自动调用
	@Override
	public void onDraw(Canvas c) {
		c.drawBitmap(bitmap, 0, 0, null);
	}

}
