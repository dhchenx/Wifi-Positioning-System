package com.bluecanna.wificlock.bll;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import com.bluecanna.wificlock.model.APModel;
import com.bluecanna.wificlock.model.ClockModel;
import com.bluecanna.wificlock.model.WiFiClockModel;
import com.bluecanna.wificlock.model.WiFiModel;
import com.bluecanna.wificlock.utils.Sys;
import com.bluecanna.wificlock.utils.Sys.ScreenInfo;
import com.bluecanna.wificlock.view.ClockViewUtils;

public class ClockDisplay {

	Bitmap bitmap;
	Canvas c = null;
	ClockModel cm = null;
	ClockViewUtils cvu = null;

	public ClockDisplay(Activity act, ClockModel cm) {
		this.cm = cm;
		cvu = new ClockViewUtils(act);
		c = new Canvas();
		ScreenInfo si = new Sys().getScreenInfo(act);
		bitmap = Bitmap.createBitmap(si.widthpx, si.heightpx,
				Bitmap.Config.ARGB_8888);
		c.setBitmap(bitmap);
		this.CreateBg();
	}

	public float getRssi(List<WiFiModel> wms) {
		if (wms != null && wms.size() == 1)
			return wms.get(0).value;
		if (wms != null)
			for (int i = 0; i < wms.size(); i++) {
				for (int j = i + 1; j < wms.size(); j++) {
					if (wms.get(i).value < wms.get(j).value) {
						WiFiModel w = wms.get(i);
						wms.set(i, wms.get(j));
						wms.set(j, w);
					}
				}
			}
		float result = 0;
		for (int i = 0; i < wms.size(); i++) {
			result += wms.get(i).value * (1 - (i + 1) / (wms.size() + 1));
		}
		return result;
	}
	public static int step=0;
	public ClockDisplay(Activity act, WiFiClockModel wcms) {
		cvu = new ClockViewUtils(act);
		c = new Canvas();
		ScreenInfo si = new Sys().getScreenInfo(act);
		bitmap = Bitmap.createBitmap(si.widthpx, si.heightpx,
				Bitmap.Config.ARGB_8888);
		c.setBitmap(bitmap);
		if (wcms != null && wcms.aplist.size() > 1) {
			boolean first = true;
			int first_count=0;
			int second_count=0;
			for (int i = 0; i < wcms.aplist.size(); i++) {
				ClockModel cm = new ClockModel();
				APModel apm = wcms.aplist.get(i);
				float result = getRssi(apm.mvlist);
				cm.clockname=apm.name;
				cm.r = si.widthpx / 3 / 2;
				if (first) {
					cm.c_x = si.width / 4;
					cm.c_y = cm.r*1.3f+(float)first_count*(2.6f*cm.r);
					first_count++;
					first = false;
				} else {
					cm.c_x = si.width / 4*3;
					cm.c_y = si.height;
					first = true;
					cm.c_y = cm.r*1.3f+(float)second_count*(2.6f*cm.r);
					second_count++;
				}
				cm.rssi = result;
				CreateClock(cm);
			}
			step=0;
		}else{
			step++;
			c.drawText("当前没有检测到WiFi，请稍等片刻...("+step+")",si.widthpx/5, si.heightpx/2, 
					cvu.CreatePaint(Color.BLUE));
		}
	}
	private void CreateClock(ClockModel cm) {
		c.drawText(cm.clockname, cm.c_x-0.5f*cm.r, cm.c_y-1.1f*cm.r, cvu.CreatePaint(Color.BLACK));
		c.drawCircle(cm.c_x, cm.c_y, cm.r, cvu.CreatePaint(3,Color.LTGRAY));
		c.drawPoint(cm.c_x, cm.c_y, cvu.CreatePaint(10, Color.BLACK));
		c.drawCircle(cm.c_x, cm.c_y, cm.r, cvu.CreatePaint1(Color.BLACK));

		for (int i = 0; i < 12; i++) {
			float x = cm.c_x + cm.r
					* (float) Math.cos(Math.PI / 6 * i - Math.PI / 2);
			float y = cm.c_y + cm.r
					* (float) Math.sin(Math.PI / 6 * i - Math.PI / 2);

			if (i >= 0 && i < 3)
				c.drawPoint(x, y, cvu.CreatePaint(i + 3, Color.GREEN));
			if (i >= 3 && i < 9)
				c.drawPoint(x, y, cvu.CreatePaint(i +3, Color.BLUE));
			if (i >= 9 && i < 12)
				c.drawPoint(x, y, cvu.CreatePaint(i + 3, Color.RED));
			
			/*
			if(i<4)
		    	c.drawText((String.valueOf((int)(i*1.0/12*100*(-1)))),
		    			x+3, y, cvu.CreatePaint(Color.BLACK));
			else if(i>=4&&i<8)
				c.drawText((String.valueOf((int)(i*1.0/12*100*(-1)))),
		    			x-2, y+18, cvu.CreatePaint(Color.BLACK));
			else if(i>=8&&i<12)
				c.drawText((String.valueOf((int)(i*1.0/12*100*(-1)))),
		    			x-24, y, cvu.CreatePaint(Color.BLACK));
		 */
			
		}

		float pos = cm.rssi / 100 * (-1) * (float) Math.PI;
		float cur_x = cm.c_x + cm.r
				* (float) Math.cos(pos - (float) Math.PI / 2);
		float cur_y = cm.c_y + cm.r
				* (float) Math.sin(pos - (float) Math.PI / 2);
		float cur_x_end = cm.c_x + cm.r * (float) Math.cos(pos) * 80 / 100;
		float cur_y_end = cm.c_y + cm.r * (float) Math.sin(pos) * 80 / 100;
		c.drawLine(cm.c_x, cm.c_y, cur_x_end, cur_y_end,
				cvu.CreatePaint(5, Color.RED));
	}
	private void CreateBg() {
		c.drawCircle(cm.c_x, cm.c_y, cm.r, cvu.CreatePaint(Color.LTGRAY));
		c.drawPoint(cm.c_x, cm.c_y, cvu.CreatePaint(10, Color.BLACK));
		c.drawCircle(cm.c_x, cm.c_y, cm.r, cvu.CreatePaint1(Color.BLACK));

		for (int i = 0; i < 12; i++) {
			float x = cm.c_x + cm.r
					* (float) Math.cos(Math.PI / 6 * i - Math.PI / 2);
			float y = cm.c_y + cm.r
					* (float) Math.sin(Math.PI / 6 * i - Math.PI / 2);

			if (i >= 0 && i < 3)
				c.drawPoint(x, y, cvu.CreatePaint(i + 3, Color.GREEN));
			if (i >= 3 && i < 9)
				c.drawPoint(x, y, cvu.CreatePaint(i +3, Color.BLUE));
			if (i >= 9 && i < 12)
				c.drawPoint(x, y, cvu.CreatePaint(i + 3, Color.RED));
		}

		float pos = cm.rssi / 100 * (-1) * (float) Math.PI;
		float cur_x = cm.c_x + cm.r
				* (float) Math.cos(pos - (float) Math.PI / 2);
		float cur_y = cm.c_y + cm.r
				* (float) Math.sin(pos - (float) Math.PI / 2);
		float cur_x_end = cm.c_x + cm.r * (float) Math.cos(pos) * 80 / 100;
		float cur_y_end = cm.c_y + cm.r * (float) Math.sin(pos) * 80 / 100;
		c.drawLine(cm.c_x, cm.c_y, cur_x_end, cur_y_end,
				cvu.CreatePaint(5, Color.RED));
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
}
