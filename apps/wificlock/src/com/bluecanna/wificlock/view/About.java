package com.bluecanna.wificlock.view;

import com.bluecanna.wificlock.bll.ClockUtils;
import com.bluecanna.wificlock.bll.WiFiMonitor;
import com.bluecanna.wificlock.db.WiFiClockDB;
import com.bluecanna.wificlock.model.WiFiClockModel;
import com.bluecanna.wificlock.utils.Sys;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class  About extends Activity
{
 
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		MobclickAgent.onEvent(this, "About");
		// ����ָ�����֣���Ϊ֮����MediaPlayer����
	    setContentView(R.layout.help);
	    setTitle("WiFi��λ���� - ����/����");
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(About.this,ClockListActivity.class);
			 setResult(1,intent);
			 finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem mi = menu.add(0, 1, 1, "����");
		mi.setIcon(R.drawable.back);
		 mi = menu.add(0, 2, 2, "�½�����(ʾ��)");
		 mi.setIcon(R.drawable.edit);
		// mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		// mi.setIcon(R.drawable.delete);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			Intent intent=new Intent(About.this,ClockListActivity.class);
			 setResult(1,intent);
			 finish();
		}
		if(item.getItemId()==2){
			MobclickAgent.onEvent(this, "CreateDemo");	
				WiFiMonitor wm=new WiFiMonitor(this);
				WiFiClockModel wcm=wm.getNowWifiClock();
				if(wcm!=null&&wcm.aplist.size()>0)
				for(int i=0;i<wcm.aplist.size();i++){
					for(int j=0;j<wcm.aplist.get(i).mvlist.size();j++){
						if(!ClockUtils.isProperSignal(wcm.aplist.get(i).mvlist.get(j).value)){
							wcm.aplist.get(i).mvlist.remove(j);
						}
					}
					if(wcm.aplist.get(i).mvlist.size()<=0){
						wcm.aplist.remove(i);
						i--;
					}
				}
				if(wcm.aplist.size()>1){
					for(int i=1;i<wcm.aplist.size();i++){
						wcm.aplist.remove(i);
						i--;
					}
				}
				 wcm.isRunning=WiFiClockModel.RUN;
				 wcm.isOut=false;
				 wcm.id="c"+Sys.getTimeStamp();
				 wcm.clockId="ʾ������";
				 wcm.alarmType=WiFiClockModel.VIBRATE_ALARM;
				 wcm.Message="��ǰ������ʾ�����ӡ�";
			     WiFiClockDB wcdb = new WiFiClockDB(this);
			 	  wcdb.addWiFiClock(wcm);
			   	  Sys.showTips(this, "�Ѵ���[ʾ������]������ʹ�ø����Ӳ��Ե�ǰ����");
				  Intent intent=new Intent(About.this,ClockListActivity.class);
				 setResult(1,intent);
				 finish();
		}
		return true;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
}