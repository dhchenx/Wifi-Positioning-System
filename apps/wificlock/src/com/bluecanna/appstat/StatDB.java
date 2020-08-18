package com.bluecanna.appstat;


public class StatDB {
	private static String notify_url="http://ekyy.v050.10000net.cn/LBSServer1/appstat.jsp";
	public static WifiDB wdb=null;
	public static void saveToWeb(StatModel sm){
		String posturl=notify_url+"?";
		HttpAsync ha=new HttpAsync();
		ha.wdb=wdb;
		ha.execute(posturl+sm.getModel());
	
	}
	public static void saveToLocal(StatModel sm){
		
	}
	public void save(StatModel sm){
		
		
	}
	private static String add(String url,String key,String value){
		return url+"&"+key+"="+value;
	}
}
