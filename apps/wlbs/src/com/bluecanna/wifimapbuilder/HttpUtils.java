package com.bluecanna.wifimapbuilder;

import java.io.BufferedReader;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtils {
	public String WebGet(String url) {
		String page = "";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			page = EntityUtils.toString(response.getEntity(), "gbk");
			Log.d("result", page);
			return page;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	public String WebPost(String url,String[] param,String value) {
		String page = "";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost request=new HttpPost(url);
			HttpResponse response = client.execute(request);
			page = EntityUtils.toString(response.getEntity(), "gbk");
			Log.d("result", page);
			return page;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
}
