package com.bluecanna.wificlock.view;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.bluecanna.wificlock.bll.WiFiMonitor;
import com.bluecanna.wificlock.db.WiFiClockDB;
import com.bluecanna.wificlock.model.APModel;
import com.bluecanna.wificlock.model.WiFiClockModel;
import com.bluecanna.wificlock.utils.Sys;
import com.bluecanna.wificlock.utils.WifiInfo;
import com.bluecanna.wificlock.utils.WifiUtils;

import com.umeng.analytics.MobclickAgent;

public class ClockActivity extends Activity {
public Context context=this;
public String  cur_clock_id="";
public TextView tv=null;
WiFiMonitor wm=null;
ClockView cv=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cv=new ClockView(this);
		setContentView(cv);
		wm=new WiFiMonitor(this);
		wm.setClockView(cv);
		setTitle("WiFi��λ���� - ��ǰ����");
		//cv.DisplayWiFiSignNow(70);
	}
EditText et=null;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuItem mi=menu.add(0,1,1,"�½�����");
		mi.setIcon(R.drawable.add);
		mi=menu.add(0,2,2,"�����б�");
		mi.setIcon(R.drawable.info);
		mi=menu.add(0,3,3,"��ʼ");
		mi.setIcon(R.drawable.start);
		 
	//	mi.setIcon(R.drawable.menu_loc);
		return true;
	}
	boolean isopen=false;
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==1){
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
						 WifiInfo[] ws=new WifiUtils().getAllWiFiList(context);
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
					
						 String w=getBestAP(context);
						 
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
						 
						 Intent intent=new Intent(ClockActivity.this,SettingActivity.class);
						 Bundle bd=new Bundle();
						 bd.putSerializable("newclock", wcm);
						 intent.putExtras(bd);
						 startActivity(intent); 
						 finish();
					}
				}).setNegativeButton("ȡ��", null)
				.show();
		}	
		if(item.getItemId()==2){
			 Intent intent=new Intent(ClockActivity.this,ClockListActivity.class);
			 startActivity(intent);
			 finish();
		}
		if(item.getItemId()==3){
			if(!isopen){
			wm.open();
			Sys.showTips(this, "������");
			item.setTitle("ֹͣ");
			item.setIcon(R.drawable.stop);
			
			}else{
				wm.close();
				Sys.showTips(this, "��ֹͣ");
				item.setTitle("��ʼ");
				item.setIcon(R.drawable.start);
				
			}
			isopen=!isopen;
		}
		
		if(item.getItemId()==4){
			 Sys.showInfo(this, "����WiFi��λ����", 
					 "���ƶ�Ӧ����www.bluecannna.com��������Ȩ����2013");
		}
		
		return true;
	}
	
	public void onResume(){
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	public void onPause(){
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 4:
		WiFiClockModel	wcm=(WiFiClockModel)data.getSerializableExtra("newclock");
		
		WiFiClockDB wcdb=new WiFiClockDB(this);
		if(wcm!=null){
			cur_clock_id=wcm.id;
		int result=wcdb.addWiFiClock(wcm);
		if(result==WiFiClockDB.SUCCESS)
		Sys.showTips(this, "����ɹ�");
		else
			Sys.showTips(this, "�������ݿ�ʧ��!");
		}else{
			Sys.showTips(this, "����ʧ��");
		}
			//Sys.showTips(this, wcm.aplist.get(0).name);
			break;
		}
	}

	public String getBestAP(Context context){
		WifiUtils wu=new WifiUtils();
		android.net.wifi.WifiInfo w=wu.getConnectedWifi(context);
		if(w==null){
			WifiInfo[] ws=wu.getAllWiFiList(context);		
			Sys.showTips((Activity)context, "count = "+ws.length);
			return ws[0].BSSID;
			/*
			if(ws!=null){
				int maxid=0;
				int maxval=-200;
				Log.d("wifi_count",String.valueOf(ws.length));
				for(int i=0;i<ws.length;i++){
					if(maxval<Integer.valueOf(ws[i].Level)){
						maxid=i;
						maxval=Integer.valueOf(ws[i].Level);
					}
				}
				Log.d("wifi_count",String.valueOf(maxid));
				return ws[maxid].BSSID;
			}
			*/
		}else{
			WifiInfo wi=new WifiInfo();
			wi.SSID=w.getSSID();
			wi.BSSID=w.getBSSID();
			wi.Level=String.valueOf(w.getRssi());
			return wi.SSID;
		}
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("�ر�WiFi��λ����");
			builder.setMessage("ȷ���رձ�����?");
			builder.setIcon(R.drawable.home);
			builder.setPositiveButton("�ر�",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							android.os.Process.killProcess(android.os.Process
									.myPid());
							System.exit(0);

						}
					});

			builder.setNegativeButton("ȡ��",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			builder.show();

		}
		return super.onKeyDown(keyCode, event);
	}
	
}
