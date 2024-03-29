package com.bluecanna.wificlock.view;

import com.bluecanna.wificlock.bll.WiFiMonitor;
import com.bluecanna.wificlock.utils.Sys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;

public class DisplayWifiActivity extends Activity {
	ClockView cv = null;
	WiFiMonitor wm = null;
	String apname="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent=getIntent();
		cv = new ClockView(this);
		setContentView(cv);
		wm = new WiFiMonitor(this);
		wm.setClockView(cv);
		wm.setIsCurrent(true);
		wm.setSingleCurrent(true);
		
		apname=intent.getExtras().getString("apname");
		wm.single_apname=apname;
		setTitle("WiFi定位闹钟 - AP: "+apname);
		wm.open();
		Sys.showTips(this, "指针处于蓝色区域表示该AP适合作为定位AP");
	}

	EditText et = null;
	boolean isopen = false;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(DisplayWifiActivity.this,APSetting.class);
			setResult(11,intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	public void onDestroy(){
		wm.close();
		super.onDestroy();
	}
}