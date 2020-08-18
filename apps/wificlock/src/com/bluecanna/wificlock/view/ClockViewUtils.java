package com.bluecanna.wificlock.view;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class ClockViewUtils {
	public Activity myactivity;
	public ClockViewUtils(Activity act){
		this.myactivity=act;
	}
	public Paint CreatePaint(int c) {
		Paint p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true); // 如果为true的话是抗锯齿效果
		p.setColor(c); // 设置画笔的颜色
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStrokeWidth(3); // 设置画笔的宽度
		return p;
	}
	public Paint CreatePaint1(int c) {
		Paint p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true); // 如果为true的话是抗锯齿效果
		p.setColor(c); // 设置画笔的颜色
	//	p.setStrokeCap(Paint.Cap.ROUND);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3); // 设置画笔的宽度
		return p;
	}
	public static void drawImage(Canvas canvas, Bitmap blt, int x, int y,
			int w, int h, int bx, int by, int bw, int bh) { // x,y表示绘画的起点，
		Rect src = new Rect();// 图片
		Rect dst = new Rect();// 屏幕
		// src 这个是表示绘画图片的大小
		src.left = bx; // 0,0
		src.top = by;
		src.right = bx + bw;// mBitDestTop.getWidth();,这个是桌面图的宽度，
		src.bottom = by + bh;// mBitDestTop.getHeight()/2;// 这个是桌面图的高度的一半
		// 下面的 dst 是表示 绘画这个图片的位置
		dst.left = x; // miDTX,//这个是可以改变的，也就是绘图的起点X位置
		dst.top = y; // mBitQQ.getHeight();//这个是QQ图片的高度。 也就相当于 桌面图片绘画起点的Y坐标
		dst.right = x + w; // miDTX + mBitDestTop.getWidth();// 表示需绘画的图片的右上角
		dst.bottom = y + h; // mBitQQ.getHeight() +
							// mBitDestTop.getHeight();//表示需绘画的图片的右下角
		canvas.drawBitmap(blt, src, dst, null);// 这个方法 第一个参数是图片，第二个参数是
												// 绘画该图片需显示多少。也就是说你想绘画该图片的某一些地方，而不是全部图片，第三个参数表示该图片绘画的位置

		src = null;
		dst = null;
	}
	public Bitmap  getDrawable(int id){
		Resources res=myactivity.getResources();   
		Bitmap bmp=BitmapFactory.decodeResource(res, id);  
		return bmp;
	}
	public Paint CreatePaint(int width, int c) {
		Paint p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true); // 如果为true的话是抗锯齿效果
		p.setColor(c); // 设置画笔的颜色
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStrokeWidth(width); // 设置画笔的宽度
		return p;
	}

	public Paint CreateTextPaint(int width, int c) {
		Paint p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true); // 如果为true的话是抗锯齿效果
		p.setColor(c); // 设置画笔的颜色
		p.setStrokeCap(Paint.Cap.ROUND);
		// p.setStrokeWidth(width); // 设置画笔的宽度
		p.setTextSize(width);
		return p;
	}
}
