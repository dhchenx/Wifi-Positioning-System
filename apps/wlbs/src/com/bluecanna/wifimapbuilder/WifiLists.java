package com.bluecanna.wifimapbuilder;

import java.io.Serializable;

public class WifiLists implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private  WifiInfo[] ws=null;
public void setWifiInfos(WifiInfo[] ws){
	this.ws=ws;
}
public WifiInfo[] getWifiInfos(){
	return this.ws;
}
}
