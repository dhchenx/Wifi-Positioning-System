/**
 * 
 */
package com.lbsserver.utils;

import com.lbsserver.core.RSSILBS;
import com.lbsserver.web.WiFiLBS;
 

/**
 * @author TungWahChan
 *
 */
public class Test {
	public static void main(String[] args) {
		//RSSILBS lt = new RSSILBS();
		/*
		 * System.out.println("◊Ó÷’Œª÷√:"+lt.GetLocation(lt.Locate(new String[] {
		 * "00:23:89:17:51:f0", "00:23:89:17:24:10","00:23:89:17:bf:50" }, new
		 * double[] { -56.56, -58.5, -77.21 })));
		 */
		WiFiLBS wfb=new WiFiLBS();
		//String session=wfb.Login("chendonghua", new MD5().getMD5ofStr("123456"));
		String value = "";
		value += "00:23:89:17:51:f0" + "," + "-56.56" + ";"
				+ "00:23:89:17:24:10" + "," + "-58.5" + ";"
				+ "00:23:89:17:bf:50" + "," + "-77.21";
		
		System.out.println(wfb.GetLocation("chendonghua", "0F51B120589055F3C263633A1C519DF4", value,"web.wlan.bjtu","test"));
		//wfb.Logout("chendonghua", session);
		
	}
	
}
