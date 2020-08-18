package com.bluecanna.wificlock.bll;

import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.os.Bundle;

import com.bluecanna.test.TipHelper;

 
import com.bluecanna.wificlock.model.WiFiClockModel;
import com.bluecanna.wificlock.utils.Sys;
import com.bluecanna.wificlock.view.ClockListActivity;
import com.bluecanna.wificlock.view.R;

public class ClockImp {
	private List<WiFiClockModel> wcms = null;
	private Context context = null;
    private boolean[] bs=null;
	public ClockImp(Context context, List<WiFiClockModel> list) {
		this.wcms = list;
		this.context = context;
	}
	public void setB(boolean[] bs){
		this.bs=bs;
	}
  public void doAction(WiFiClockModel wcm){
		if (wcm.alarmType.equals("1")) {
		//	type = Notification.DEFAULT_VIBRATE;
			TipHelper.Vibrate(this.context, 1000L);
		}
		if (wcm.alarmType.equals("2")) {
			//type = Notification.DEFAULT_SOUND;
		}

		if (wcm.alarmType.equals("3")) {
			//type = Notification.DEFAULT_ALL;
			TipHelper.Vibrate(this.context, 1000L);
		}
		if (wcm.alarmType.equals("4")) {
		//	type = Notification.DEFAULT_LIGHTS;
		}
  }
	public void showTips() {
		for (int i = 0; i < wcms.size(); i++) {
			WiFiClockModel wcm = wcms.get(i);
			 
			Bundle bd = new Bundle();
			String time = Sys.getTime();
			bd.putString("value", "当前所处的位置\"" + wcm.clockId + "\", " + time);
			bd.putSerializable("clock", wcm);
			int type = 0;
			if (!wcm.alarmType.equals("0")) {
				if (wcm.alarmType.equals("1")) {
					type = Notification.DEFAULT_VIBRATE;
				}
				if (wcm.alarmType.equals("2")) {
					type = Notification.DEFAULT_SOUND;
				}

				if (wcm.alarmType.equals("3")) {
					type = Notification.DEFAULT_ALL;
				}
				if (wcm.alarmType.equals("4")) {
					type = Notification.DEFAULT_LIGHTS;
				}
				String str="在";
				if(bs[i]==true){
				if(wcm.isOut==false){
					Sys.Notify(R.layout.notify, (Activity) context, type,
							ClockListActivity.class, R.drawable.clock,
								"WiFi定位闹钟 - 提醒", time +""+str+"[" + wcm.clockId+"]", bd);
					this.doAction(wcm);
				}
				}
				System.out.println("bs["+i+"]="+bs[i]);
				if(bs[i]==false){
					if(wcm.isOut){
						System.out.println("is out ="+wcm.clockId);
						Sys.Notify(R.layout.notify, (Activity) context, type,
								ClockListActivity.class, R.drawable.clock,
									"WiFi定位闹钟 - 提醒", time +""+"离开"+"[" + wcm.clockId+"]", bd);
						this.doAction(wcm);
					}
				}
			}
		}
	}

}
