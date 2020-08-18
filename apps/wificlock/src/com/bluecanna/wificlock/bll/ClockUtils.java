package com.bluecanna.wificlock.bll;

public class ClockUtils {
	public static boolean isProperSignal(float rssi){
		if(rssi>=-80 && rssi<=-20){
			return true;
		}
		return false;
	}
}
