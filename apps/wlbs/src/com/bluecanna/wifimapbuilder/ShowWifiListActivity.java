package com.bluecanna.wifimapbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ShowWifiListActivity extends ListActivity {
	 private List<Map<String, String>> data = new ArrayList<Map<String,String>>();  
	 WifiInfo[] ws=null;
	 public String ap_remark="";
	 public String ap_ssid="";
	 public String ap_interval="";
		public WifiInfo[] getAllNetWorkList() {
			 
			StringBuffer sb=new StringBuffer();
			// 每次点击扫描之前清空上一次的扫描结果
			if (sb != null) {
				sb = new StringBuffer();
			}
			// 开始扫描网络
			 WifiAdmin mWifiAdmin=new WifiAdmin(ShowWifiListActivity.this);
			// 扫描结果列表
			 List<ScanResult> list;
			  ScanResult mScanResult;
			
			mWifiAdmin.startScan();
			list = mWifiAdmin.getWifiList();
			if (list != null) {
				WifiInfo[] ws = new WifiInfo[list.size()];
				for (int i = 0; i < list.size(); i++) {
					// 得到扫描结果
					mScanResult = list.get(i);
					WifiInfo w = new WifiInfo();
					w.BSSID = mScanResult.BSSID;
					w.SSID = mScanResult.SSID;
					w.Capability = mScanResult.capabilities;
					w.Frequency = String.valueOf(mScanResult.frequency);
					w.Level = String.valueOf(mScanResult.level);
				 
					ws[i] = w;
				}
				this.ws=ws;
			}
			return ws;
				
		}
	    @Override  
	    public void onCreate(Bundle savedInstanceState) {  
	    	
	        super.onCreate(savedInstanceState);  
	        /*
	        Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.bggray);
			this.getWindow().setBackgroundDrawable(drawable);
			*/
	        setContentView(R.layout.wifilist);  
	        setTitle("WLAN定位 - 设置扫描AP");
	        Intent intent=this.getIntent();
	        Bundle bd = intent.getExtras();
	        getAllNetWorkList();
	        ap_remark=bd.getString("ap_remark");
	        ap_ssid=bd.getString("ap_ssid");
	        ap_interval=bd.getString("ap_interval");
	        if(ws!=null){
	        	for(int i=0;i<ws.length;i++){
	        		 Map<String, String> map1 = new HashMap<String, String>(); 
	        		map1.put("bssid",ws[i].SSID);
	        		map1.put("ssid", "强度:" +ws[i].Level+" 频率:"+ws[i].Frequency+" "+ws[i].BSSID
	        				+"\n"+ws[i].Capability
	        				);
	        		data.add(map1);
	        	}
	        }
	        setListAdapter(new MyAdapter(this,data,R.drawable.rss_32x32,"bssid","ssid"));
	        /*
	        setListAdapter(new SimpleAdapter(this,data,android.R.layout.simple_list_item_2,  
	                new String[]{"bssid","ssid"},           
	                new int[]{android.R.id.text1,android.R.id.text2}   
	               
	        ));  
	         */
	    }  
	    public int cur_position=-1;
	   // public String sel_ssid="";
	    @Override   
	    protected void onListItemClick(ListView l, View v, int position, long id) {  
	      
	       // super.onListItemClick(l, v, position, id);  
	    
	        cur_position=position;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("设置AP");
			builder.setMessage("确定要设置当前地图扫描的AP为"+ws[cur_position].SSID+"吗?可能会导致丢失原有的地图数据!");
			builder.setIcon(R.drawable.rss_32x32);
			builder.setPositiveButton("设置返回",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String name=ws[cur_position].SSID;
					        Intent intent=new Intent(ShowWifiListActivity.this,WifiLBSActivity.class);
					        intent.putExtra("ap_ssid", name);
					        intent.putExtra("ap_remark", ap_remark);
					        intent.putExtra("ap_interval", ap_interval);
					        startActivity(intent);
					        finish();

						}
					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			builder.show();
	        
	        
	    }  

		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				 Intent intent=new Intent(ShowWifiListActivity.this,WifiLBSActivity.class);
				 Bundle bd=new Bundle();
				 bd.putString("ap_ssid", ap_ssid);
				 bd.putString("ap_remark", ap_remark);
				 bd.putString("ap_interval", ap_interval);
				 intent.putExtras(bd);
				 startActivity(intent);
				 finish();
			}
			return super.onKeyDown(keyCode, event);
		}

}
