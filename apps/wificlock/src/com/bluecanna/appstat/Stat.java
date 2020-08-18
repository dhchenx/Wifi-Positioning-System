package com.bluecanna.appstat;

import com.bluecanna.wificlock.utils.Sys;

import android.app.Activity;

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
			sm.actionTime = Sys.getTimeStamp();
			sm.appId = "1001";
			sm.appPriv = "WiFiClock_ads";
			sm.appVersion = "1.1";
			StatUtils su = new StatUtils();
			sm.device = su.getModel();
			sm.system = su.getSystem();
			sm.screen = su.getDisplay(myactivity);
			sm.userId = "normal";
			sm.sourceId="baidu";
			sm.IMEI=su.getIMEI(myactivity);
			sm.mobileId=su.getPhone(myactivity);
			StatDB.wdb=wdb;
			StatDB.saveToWeb(sm);
		   
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
