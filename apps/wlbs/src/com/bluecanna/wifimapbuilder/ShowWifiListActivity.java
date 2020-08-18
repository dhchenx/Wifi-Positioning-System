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
			// ÿ�ε��ɨ��֮ǰ�����һ�ε�ɨ����
			if (sb != null) {
				sb = new StringBuffer();
			}
			// ��ʼɨ������
			 WifiAdmin mWifiAdmin=new WifiAdmin(ShowWifiListActivity.this);
			// ɨ�����б�
			 List<ScanResult> list;
			  ScanResult mScanResult;
			
			mWifiAdmin.startScan();
			list = mWifiAdmin.getWifiList();
			if (list != null) {
				WifiInfo[] ws = new WifiInfo[list.size()];
				for (int i = 0; i < list.size(); i++) {
					// �õ�ɨ����
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
	        setTitle("WLAN��λ - ����ɨ��AP");
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
	        		map1.put("ssid", "ǿ��:" +ws[i].Level+" Ƶ��:"+ws[i].Frequency+" "+ws[i].BSSID
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
			builder.setTitle("����AP");
			builder.setMessage("ȷ��Ҫ���õ�ǰ��ͼɨ���APΪ"+ws[cur_position].SSID+"��?���ܻᵼ�¶�ʧԭ�еĵ�ͼ����!");
			builder.setIcon(R.drawable.rss_32x32);
			builder.setPositiveButton("���÷���",
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
			builder.setNegativeButton("ȡ��",
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
