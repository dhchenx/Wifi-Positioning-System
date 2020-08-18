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
		p.setAntiAlias(true); // ���Ϊtrue�Ļ��ǿ����Ч��
		p.setColor(c); // ���û��ʵ���ɫ
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStrokeWidth(3); // ���û��ʵĿ��
		return p;
	}
	public Paint CreatePaint1(int c) {
		Paint p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true); // ���Ϊtrue�Ļ��ǿ����Ч��
		p.setColor(c); // ���û��ʵ���ɫ
	//	p.setStrokeCap(Paint.Cap.ROUND);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(3); // ���û��ʵĿ��
		return p;
	}
	public static void drawImage(Canvas canvas, Bitmap blt, int x, int y,
			int w, int h, int bx, int by, int bw, int bh) { // x,y��ʾ�滭����㣬
		Rect src = new Rect();// ͼƬ
		Rect dst = new Rect();// ��Ļ
		// src ����Ǳ�ʾ�滭ͼƬ�Ĵ�С
		src.left = bx; // 0,0
		src.top = by;
		src.right = bx + bw;// mBitDestTop.getWidth();,���������ͼ�Ŀ�ȣ�
		src.bottom = by + bh;// mBitDestTop.getHeight()/2;// ���������ͼ�ĸ߶ȵ�һ��
		// ����� dst �Ǳ�ʾ �滭���ͼƬ��λ��
		dst.left = x; // miDTX,//����ǿ��Ըı�ģ�Ҳ���ǻ�ͼ�����Xλ��
		dst.top = y; // mBitQQ.getHeight();//�����QQͼƬ�ĸ߶ȡ� Ҳ���൱�� ����ͼƬ�滭����Y����
		dst.right = x + w; // miDTX + mBitDestTop.getWidth();// ��ʾ��滭��ͼƬ�����Ͻ�
		dst.bottom = y + h; // mBitQQ.getHeight() +
							// mBitDestTop.getHeight();//��ʾ��滭��ͼƬ�����½�
		canvas.drawBitmap(blt, src, dst, null);// ������� ��һ��������ͼƬ���ڶ���������
												// �滭��ͼƬ����ʾ���١�Ҳ����˵����滭��ͼƬ��ĳһЩ�ط���������ȫ��ͼƬ��������������ʾ��ͼƬ�滭��λ��

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
		p.setAntiAlias(true); // ���Ϊtrue�Ļ��ǿ����Ч��
		p.setColor(c); // ���û��ʵ���ɫ
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStrokeWidth(width); // ���û��ʵĿ��
		return p;
	}

	public Paint CreateTextPaint(int width, int c) {
		Paint p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true); // ���Ϊtrue�Ļ��ǿ����Ч��
		p.setColor(c); // ���û��ʵ���ɫ
		p.setStrokeCap(Paint.Cap.ROUND);
		// p.setStrokeWidth(width); // ���û��ʵĿ��
		p.setTextSize(width);
		return p;
	}
}
