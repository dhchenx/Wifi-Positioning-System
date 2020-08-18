package com.bluecanna.appstat;


import android.os.AsyncTask;
import android.util.Log;
 
public class HttpAsync extends  AsyncTask<String, Integer, String> {
	public String p="";
	public WifiDB wdb=null;
	
	protected String doInBackground(String... params) {
		try {
			String url = params[0];
			p=url;
			HttpUtils hu = new HttpUtils();
			String result = hu.WebGet(url);
			Thread.sleep(100);
			return result;
		} catch (Exception e) {
			return "FAIL";
		}
	}
	protected void onPostExecute(String Result) {
		Log.d("result", Result);
		 try{
		   String r=Result.trim();
		  if(!r.equalsIgnoreCase("success")){
			 wdb.addStatParam(p);
		  }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		
	}
}
