package com.bluecanna.wifimapbuilder;

import android.app.AlertDialog;
import android.content.Context;

public class Alerts {
	public static  void showAlert(String title,String message,Context ctx){
		AlertDialog.Builder builder=new AlertDialog.Builder(ctx);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.home);
		EmptyOnClickListener el=new EmptyOnClickListener();
		builder.setPositiveButton("È·¶¨", el);
		AlertDialog ad=builder.create();
		ad.show();
	}
}
 