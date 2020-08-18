package com.bluecanna.wificlock.utils;

import java.io.Serializable;

public class WifiInfo implements Serializable {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String BSSID="";
	public String SSID="";
	public String Capability="";
	public String Frequency="";
	public String Level="";
	public String RSSI="";
	public String IP="";
}
