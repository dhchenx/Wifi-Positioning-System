package com.bluecanna.wificlock.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WiFiClockModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public WiFiClockModel(){
		 
	}
	public String id="";
	public String clockId="";
    public List<APModel> aplist=new ArrayList<APModel>();
	public  String alarmType=this.NOACTION;
	public static String NOACTION="0";
	public static String VIBRATE="1";
	public static String ALARM="2";
	public static String VIBRATE_ALARM ="3";
	public String isRunning=RUN;
	public static String RUN="0";
	public static String STOP="1";
	public boolean isOut=false;
	public boolean is2Check=false;
	public int std=10;
	public String Message="";
	
}
