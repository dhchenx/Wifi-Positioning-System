package com.bluecanna.wifimapbuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {
	public static String getTimeStamp(){
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
	return df.format(new Date());
	}
	public static String getTime(){
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	return df.format(new Date());
	}
}
