package com.bluecanna.wificlock.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bluecanna.wificlock.model.APModel;
import com.bluecanna.wificlock.model.WiFiClockModel;
import com.bluecanna.wificlock.utils.BaseItem;
import com.bluecanna.wificlock.utils.BaseListAdapter;
import com.bluecanna.wificlock.utils.Sys;
import com.bluecanna.wificlock.utils.WifiInfo;
import com.bluecanna.wificlock.utils.WifiUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ShowWifiListActivity extends ListActivity {
	
	WifiInfo[] ws;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main3);
		loadWiFiList();
		setTitle("可用定位AP列表");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String s = ws[position].SSID;
		Intent intent = new Intent(ShowWifiListActivity.this, DisplaySingleClock.class);
		intent.putExtra("apname", s);
		startActivityForResult(intent,5);
		
	}

	public String getRSSIStrength(String s){
		int c=Integer.valueOf(s);
		if(c>-75&&c<=-25){
			return "满足定位需求("+c+"dbm)";
		}
		else
			return "不满足定位AP需求";
	}
	
	public void loadWiFiList() {
	 List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		ws = new WifiUtils().getAllWiFiList(this);
		if (ws != null) {
			for (int i = 0; i < ws.length; i++) {
				
					Map<String, String> map1 = new HashMap<String, String>();
					map1.put("bssid", ws[i].SSID);
					map1.put("ssid", ws[i].BSSID+" - " +this.getRSSIStrength(ws[i].Level));

					data.add(map1);
				
			}
			setListAdapter(new BaseListAdapter(this, data, R.layout.vlist2,
					new BaseItem(R.id.img, String
							.valueOf(R.drawable.mapmarker_32)),
					new BaseItem(R.id.title, "bssid"), new BaseItem(R.id.info,
							"ssid")));
		} else {
			Toast.makeText(this, "没有找到可用的WiFi接入点，请设置WLAN!", Toast.LENGTH_LONG)
					.show();
			Sys.openWiFiSetting(this);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	 MenuItem mi=menu.add(0,1,1,"刷新");
		mi.setIcon(R.drawable.refresh);
		  mi=menu.add(0,2,2,"创建闹钟");
		mi.setIcon(R.drawable.plus);
		mi=menu.add(0,3,3,"闹钟列表");
		mi.setIcon(R.drawable.order_159);
	mi=menu.add(0,4,4,"关于");
mi.setIcon(R.drawable.tools_info);
	//	mi.setIcon(R.drawable.menu_loc);
		return true;
	}
	EditText et=null;
	boolean isopen=false;
	public boolean onOptionsItemSelected(MenuItem item) {
	if(item.getItemId()==1){
		this.loadWiFiList();
	}
		if(item.getItemId()==2){
		    et=new EditText(this);
			new AlertDialog.Builder(this)
			.setTitle("请输入该闹钟标记")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setView(et)
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					 WiFiClockModel wcm=new WiFiClockModel();
					 wcm.isRunning=WiFiClockModel.RUN;
					 wcm.id="c"+Sys.getTimeStamp();
					 wcm.clockId=et.getText().toString();
					// String w=new WifiUtils().getBestAP(context);
					 WifiInfo[] ws=new WifiUtils().getAllWiFiList(ShowWifiListActivity.this);
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
				
					 String w=getBestAP(ShowWifiListActivity.this);
					 
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
					 Intent intent=new Intent(ShowWifiListActivity.this,SettingActivity.class);
					 Bundle bd=new Bundle();
					 bd.putSerializable("newclock", wcm);
					 intent.putExtras(bd);
					 startActivity(intent); 
					 finish();
				}
			}).setNegativeButton("取消", null)
			.show();
		}
		if(item.getItemId()==3){
			 Intent intent=new Intent(ShowWifiListActivity.this,ClockListActivity.class);
			 startActivityForResult(intent,0);
		
		}
		if(item.getItemId()==4){
			
			 this.showAbout();
 
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
    public void showAbout(){
    	View view=Sys.getCustomView(this, R.layout.help);
    	AlertDialog builder2 = new AlertDialog.Builder(this)
    	.setView(view).create();
		   Window window = builder2.getWindow();  
		 
		    WindowManager.LayoutParams lp = window.getAttributes();  
		 
		    // 设置透明度为0.3  
		 
		    lp.alpha = 1f;  
		 
		    window.setAttributes(lp);
		    builder2.show();
    }
    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(ShowWifiListActivity.this,ClockListActivity.class);
			setResult(0,intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
