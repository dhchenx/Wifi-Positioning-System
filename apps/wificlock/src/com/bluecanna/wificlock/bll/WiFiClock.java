package com.bluecanna.wificlock.bll;

import com.bluecanna.wificlock.model.WiFiClockModel;

public class WiFiClock implements IWiFiClock {
	private WiFiClockModel wcm=null;
	public WiFiClock(WiFiClockModel wcm){
		this.wcm=wcm;
		
	}
	
	public boolean open() {
		return false;
	}

	public boolean close() {
		return false;
	 
	}
	public boolean pause(){
		return false;
	}
	
	public boolean pause(int interval){
		return false;
	}
 
}
