package com.bluecanna.wifimapbuilder;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;


public class MyASync extends AsyncTask<String, Integer, String> {
	protected String doInBackground(String... params) {
		try {
			String url = params[0];
			HttpUtils hu = new HttpUtils();
			String result = hu.WebGet(url);
			Thread.sleep(100);
			
			return result;
		} catch (Exception e) {
			return "NULL";
		}
	}

	protected void onPostExecute(String Result) {
		Log.d("result", Result);
		
	}

}
