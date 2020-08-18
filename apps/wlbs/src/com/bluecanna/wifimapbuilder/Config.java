package com.bluecanna.wifimapbuilder;
public  class Config{
	public static String dbname = "";
	public static String dbuser = "";
	public static String dbpass = "";
	public static String dburl = "";
	public static String wifilbs_rel="wifilbs_rel";
	public static String wifilbs_rssi="wifilbs_rssi";
	public static String wifilbs_site="wifilbs_site";
	public static String wifilbs_collect="wifimap";
	public static long deadline=20130627173930L;
	public static String getAppDesc(){
		 String str="";
		 str+="本应用在室内等GPS或者互联网连接不通畅的环境中快速构造导航地图并用于位置定位。" +
		 		"\n它是在无线局域网中快速构建未知区域的WiFi信号地图并能实现基于WLAN信号室内精准定位和导航的工具。";
	     str+="\n本移动应用由www.bluecanna.com发布! 版权所有 2013，禁止反编译修改，合作请联系tungwahchan@gmail.com。";
		 return str;
	}
	
}