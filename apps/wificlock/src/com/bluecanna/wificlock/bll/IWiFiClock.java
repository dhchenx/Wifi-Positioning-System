package com.bluecanna.wificlock.bll;

import com.bluecanna.wificlock.model.WiFiClockModel;

public interface IWiFiClock {
	public abstract  boolean open();
	public abstract boolean close();
	public abstract boolean pause();
	public abstract boolean pause(int interval);
	//public abstract boolean setModel(WiFiClockModel wcm);
	//public abstract WiFiClockModel getModel();
}
