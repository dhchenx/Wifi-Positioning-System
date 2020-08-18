package com.bluecanna.wifimapbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bluecanna.wifimapbuilder.WifiDB.*;
 

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class PointsList extends ListActivity {
	 private List<Map<String, String>> data = new ArrayList<Map<String,String>>();  
	 List<SaveResult> srs=null;
	 WifiDB wdb=null;
	 String ap_ssid="";
	 String ap_remark="";
	 String ap_interval="";
	 List<PointResult> ws=null;
	 @Override  
	    public void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState);  
	        
	        setContentView(R.layout.wifilist);  
	        Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.bggray);
			this.getWindow().setBackgroundDrawable(drawable);
	        setTitle("WLAN定位 - "+ap_remark+"地图所有点 ");
	        Intent intent=this.getIntent();
	        Bundle bd = intent.getExtras();
	        ap_remark=bd.getString("ap_remark");
	        ap_ssid=bd.getString("ap_ssid");
	        ap_interval=bd.getString("ap_interval");
	        wdb=new WifiDB(this);
	       // srs=wdb.getSaveResult();
	        ws=wdb.getPoints(ap_remark);
	        if(ws!=null&&ws.size()>0){
	        	for(int i=0;i<ws.size();i++){
	        		 Map<String, String> map1 = new HashMap<String, String>(); 
	        		 map1.put("id", ws.get(i).id);
	        		  
	        		 String remark1=ws.get(i).remark;
	        		 if(remark1==null||remark1.equals("null")){
	        			 remark1="(无)";
	        		 } 
	        		 map1.put("desc", "接入点:"+ws.get(i).areaid+" 备注:"+remark1);
	        		 data.add(map1);
	        	}
	        }
	        setListAdapter(new MyAdapter(this,data,R.drawable.share_32x32,"id","desc"));
	        /*
	        setListAdapter(new SimpleAdapter(this,data,android.R.layout.simple_list_item_2,  
	                new String[]{"id","desc"},           
	                new int[]{android.R.id.text1,android.R.id.text2}   
	        ));  
	        */
	    }  
	    @Override   
	    protected void onListItemClick(ListView l, View v, int position, long id) {  
	    	 super.onListItemClick(l, v, position, id);  
	     Intent intent=new Intent(PointsList.this,PointSettingActivity.class);
	     Bundle bd=new Bundle();
	     bd.putString("ap_ssid", ap_ssid);
	     bd.putString("ap_remark", ap_remark);
	     bd.putString("ap_interval", ap_interval);
	     bd.putString("nodeid",ws.get(position).id );
	     bd.putString("saveid", ap_remark);
	     bd.putString("remark", ws.get(position).remark);
	        intent.putExtras(bd);
	        startActivity(intent);
	        finish();
	    	// Toast.makeText(this, ws.get(position).id, Toast.LENGTH_SHORT);
	    }  
	   
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				 Intent intent=new Intent(PointsList.this,WifiLBSActivity.class);
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
