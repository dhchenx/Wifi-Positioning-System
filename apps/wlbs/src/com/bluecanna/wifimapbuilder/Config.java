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
		 str+="��Ӧ�������ڵ�GPS���߻��������Ӳ�ͨ���Ļ����п��ٹ��쵼����ͼ������λ�ö�λ��" +
		 		"\n���������߾������п��ٹ���δ֪�����WiFi�źŵ�ͼ����ʵ�ֻ���WLAN�ź����ھ�׼��λ�͵����Ĺ��ߡ�";
	     str+="\n���ƶ�Ӧ����www.bluecanna.com����! ��Ȩ���� 2013����ֹ�������޸ģ���������ϵtungwahchan@gmail.com��";
		 return str;
	}
	
}