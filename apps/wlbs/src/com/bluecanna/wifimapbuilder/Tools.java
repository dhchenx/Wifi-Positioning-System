package com.bluecanna.wifimapbuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {
	public static String getTimeStamp(){
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//�������ڸ�ʽ
	return df.format(new Date());
	}
	public static String getTime(){
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
	return df.format(new Date());
	}
}
