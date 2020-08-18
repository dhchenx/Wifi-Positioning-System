package com.bluecanna.appstat;

import android.app.Activity;

import com.bluecanna.wifimapbuilder.Tools;
import com.bluecanna.wifimapbuilder.WifiDB;

public class Stat {
	public static WifiDB wdb=null;
	public static void TryUploadData(){
		try{
			String[] urls=wdb.getStatParams();
			if(urls!=null){
				for(int i=0;i<urls.length&&i<20;i++){
					HttpAsync ha=new HttpAsync();
				    ha.wdb=wdb;
				    ha.execute(urls[i]);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void ActionStat(Activity myactivity, String action) {
		try {
			StatModel sm = new StatModel();
			sm.action = action;
			sm.actionTime = Tools.getTimeStamp();
			sm.appId = "1000";
			sm.appPriv = "as.baidu.com";
			sm.appVersion = "1.0";
			StatUtils su = new StatUtils();
			sm.device = su.getModel();
			sm.system = su.getSystem();
			sm.screen = su.getDisplay(myactivity);
			sm.userId = "normal";
			sm.sourceId="baidu";
			StatDB.wdb=wdb;
			StatDB.saveToWeb(sm);
		   
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
