package com.bluecanna.wificlock.view;

import com.bluecanna.wificlock.bll.WiFiMonitor;
import com.bluecanna.wificlock.model.APModel;
import com.bluecanna.wificlock.model.WiFiClockModel;
import com.bluecanna.wificlock.utils.Sys;
import com.bluecanna.wificlock.utils.WifiInfo;
import com.bluecanna.wificlock.utils.WifiUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

public class CurrentWifiActivity extends Activity
{
	ClockView cv=null;
	WiFiMonitor wm=null;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		cv=new ClockView(this);
		setContentView(cv);
		wm=new WiFiMonitor(this);
		wm.setClockView(cv);
		wm.setIsCurrent(true);
		setTitle("WiFi��λ���� - ��ǰ����(��ֹͣ)");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuItem mi=menu.add(0,1,1,"��⵱ǰ����");
		mi.setIcon(R.drawable.start);
		mi=menu.add(0,2,2,"������λ����");
		mi.setIcon(R.drawable.plus);
		mi=menu.add(0,3,3,"�����б�");
		mi.setIcon(R.drawable.order_159);
	mi=menu.add(0,4,4,"����");
mi.setIcon(R.drawable.tools_info);
	//	mi.setIcon(R.drawable.menu_loc);
		return true;
	}
	EditText et=null;
	boolean isopen=false;
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==1){
			wm.setIsCurrent(true);
			if(!isopen){
			
				wm.open();
				Sys.showTips(this, "��⵱ǰ������...");
				item.setTitle("ֹͣ���");
				item.setIcon(R.drawable.stop1);
				setTitle("WiFi��λ���� - ��ǰ����(�����...)");
				}else{
					wm.close();
					Sys.showTips(this, "ֹͣ���...");
					item.setTitle("��⵱ǰ����");
					item.setIcon(R.drawable.start);		
					setTitle("WiFi��λ���� - ��ǰ����(��ֹͣ)");
				}
				isopen=!isopen;
		}	 
		if(item.getItemId()==2){
		    et=new EditText(this);
			new AlertDialog.Builder(this)
			.setTitle("����������ӱ��")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setView(et)
			.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					 WiFiClockModel wcm=new WiFiClockModel();
					 wcm.isRunning=WiFiClockModel.RUN;
					 wcm.id="c"+Sys.getTimeStamp();
					 wcm.clockId=et.getText().toString();
					// String w=new WifiUtils().getBestAP(context);
					 WifiInfo[] ws=new WifiUtils().getAllWiFiList(CurrentWifiActivity.this);
					 String maxap="";
					 if(ws!=null){
							int maxid=0;
							int maxval=-200;
						//	Log.d("wifi_count",String.valueOf(ws.length));
							for(int i=0;i<ws.length;i++){
								if(maxval<Integer.valueOf(ws[i].Level)){
									maxid=i;
									maxval=Integer.valueOf(ws[i].Level);
								}
							}
							maxap=ws[maxid].SSID;
							//Log.d("wifi_count",String.valueOf(maxid));
							//Sys.showTips((Activity)context, String.valueOf(ws[maxid].SSID)+","+ws[maxid].Level);
						}
				
					 String w=getBestAP(CurrentWifiActivity.this);
					 
					 if(w!=null){
						 APModel apm=new APModel();
						 apm.name=w;		 
						 wcm.aplist.add(apm);
					 }else{
						// Sys.showTips((Activity)context, w);
						 APModel apm=new APModel();
						 apm.name=maxap;		 
						 wcm.aplist.add(apm);
					 }
					 Intent intent=new Intent(CurrentWifiActivity.this,SettingActivity.class);
					 Bundle bd=new Bundle();
					 bd.putSerializable("newclock", wcm);
					 intent.putExtras(bd);
					 startActivity(intent); 
					 finish();
				}
			}).setNegativeButton("ȡ��", null)
			.show();
		}
		if(item.getItemId()==3){
			 Intent intent=new Intent(CurrentWifiActivity.this,ClockListActivity.class);
			 startActivity(intent);
			 finish();
		}
		if(item.getItemId()==4){
			 Sys.showInfo(this, "����WiFi��λ����", 
					 "���ƶ�Ӧ����www.bluecannna.com��������Ȩ����2013");
		}
		
		return super.onOptionsItemSelected(item);
	}
	public String getBestAP(Context context){
		WifiUtils wu=new WifiUtils();
		android.net.wifi.WifiInfo w=wu.getConnectedWifi(context);
		if(w==null){
			WifiInfo[] ws=wu.getAllWiFiList(context);		
			Sys.showTips((Activity)context, "count = "+ws.length);
			return ws[0].BSSID;
		}else{
			WifiInfo wi=new WifiInfo();
			wi.SSID=w.getSSID();
			wi.BSSID=w.getBSSID();
			wi.Level=String.valueOf(w.getRssi());
			return wi.SSID;
		}
	}
	public void onDestroy(){
		if(wm!=null){
			wm.close();
		}
		super.onDestroy();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			 Intent intent=new Intent(CurrentWifiActivity.this,ClockListActivity.class);
			 startActivity(intent);
			 finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}