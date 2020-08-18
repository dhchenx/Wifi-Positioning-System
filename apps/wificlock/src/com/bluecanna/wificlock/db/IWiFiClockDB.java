package com.bluecanna.wificlock.db;

import com.bluecanna.wificlock.model.WiFiClockModel;

public interface IWiFiClockDB {
	public abstract int addWiFiClock(WiFiClockModel wcm);
	public abstract int updateWiFiClock(WiFiClockModel wcm);
	public abstract int deleteWiFiClock(String id);
	public abstract WiFiClockModel[] getModelList();
	public abstract WiFiClockModel getModel(String id);
}
