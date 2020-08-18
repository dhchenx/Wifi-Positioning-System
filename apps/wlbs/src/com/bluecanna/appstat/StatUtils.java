package com.bluecanna.appstat;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class StatUtils {
	public String getSystem() {
		return android.os.Build.VERSION.RELEASE;
	}

	public String getDisplay(Activity myactivity) {
		try {
			// 获得屏幕大小1
			WindowManager manager = myactivity.getWindowManager();
			int width = manager.getDefaultDisplay().getWidth();
			int height = manager.getDefaultDisplay().getHeight();
			// 获得屏幕大小2
			DisplayMetrics dMetrics = new DisplayMetrics();
			myactivity.getWindowManager().getDefaultDisplay()
					.getMetrics(dMetrics);
			int screenWidth = dMetrics.widthPixels;
			int screenHeight = dMetrics.heightPixels;
			return width + "*" + height + ";" + screenWidth + "*"
					+ screenHeight;
		} catch (Exception e) {
			e.printStackTrace();
			return "error";

		}

	}

	public String getModel() {
		return android.os.Build.MODEL;
	}

	public boolean isNet(Context ct) {
		ConnectivityManager con = (ConnectivityManager) ct
				.getSystemService(Activity.CONNECTIVITY_SERVICE);
		boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();
		if (wifi | internet) {
			// 执行相关操作
			return true;
		} else {
			return false;
		}
	}
	/*
	 * 另外需要权限 <uses-permission
	 * android:name="android.permission.ACCESS_NETWORK_STATE" />
	 */

}
