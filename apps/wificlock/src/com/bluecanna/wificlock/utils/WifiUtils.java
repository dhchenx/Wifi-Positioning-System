package com.bluecanna.wificlock.utils;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;

import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiUtils {
	public String getBestAP(Context context){
		android.net.wifi.WifiInfo w=this.getConnectedWifi(context);
		if(w==null){
			WifiInfo[] ws=this.getAllWiFiList(context);		
			Sys.showTips((Activity)context, "count = "+ws.length);
		
			if(ws!=null){
				int maxid=0;
				int maxval=-200;
				Log.d("wifi_count",String.valueOf(ws.length));
				for(int i=0;i<ws.length;i++){
					if(maxval<Integer.valueOf(ws[i].Level)){
						maxid=i;
						maxval=Integer.valueOf(ws[i].Level);
					}
				}
				Log.d("wifi_count",String.valueOf(maxid));
				return ws[maxid].BSSID;
			}else{
				Sys.showTips((Activity)context, "WiFi is null");
				Log.d("wifi","wifi is null");
			}
		}else{
			WifiInfo wi=new WifiInfo();
			wi.SSID=w.getSSID();
			wi.BSSID=w.getBSSID();
			wi.Level=String.valueOf(w.getRssi());
			return wi.SSID;
		}
		return null;
	}
	
	public android.net.wifi.WifiInfo getConnectedWifi(Context context) {
		try{
		WifiManager mWifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (!mWifi.isWifiEnabled()) {
			mWifi.setWifiEnabled(true);
		}
		android.net.wifi.WifiInfo wifiInfo = mWifi.getConnectionInfo();
		return wifiInfo;
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		// 查看已经连接上的WIFI信息，在Android的SDK中为我们提供了一个叫做WifiInfo的对象，这个对象可以通过WifiManager.getConnectionInfo()来获取。WifiInfo中包含了当前连接中的相关信息。
		// getBSSID() 获取BSSID属性
		// getDetailedStateOf() 获取客户端的连通性
		// getHiddenSSID() 获取SSID 是否被隐藏
		// getIpAddress() 获取IP 地址
		// getLinkSpeed() 获取连接的速度
		// getMacAddress() 获取Mac 地址
		// getRssi() 获取802.11n 网络的信号
		// getSSID() 获取SSID
		// getSupplicanState() 获取具体客户端状态的信息
		/*
		 * StringBuffer sb = new StringBuffer();
		 * sb.append("\n获取BSSID属性（所连接的WIFI设备的MAC地址）：" + wifiInfo.getBSSID()); //
		 * sb.append("getDetailedStateOf()  获取客户端的连通性：");
		 * sb.append("\n\n获取SSID 是否被隐藏：" + wifiInfo.getHiddenSSID());
		 * sb.append("\n\n获取IP 地址：" + wifiInfo.getIpAddress());
		 * sb.append("\n\n获取连接的速度：" + wifiInfo.getLinkSpeed());
		 * sb.append("\n\n获取Mac 地址（手机本身网卡的MAC地址）：" + WifiMac);
		 * sb.append("\n\n获取802.11n 网络的信号：" + wifiInfo.getRssi());
		 * sb.append("\n\n获取SSID（所连接的WIFI的网络名称）：" + wifiInfo.getSSID());
		 * sb.append("\n\n获取具体客户端状态的信息：" + wifiInfo.getSupplicantState());
		 */

	}

	public List<WifiInfo> getWifiListByName(Context context,String name){
		WifiAdmin wf = new WifiAdmin(context);
		wf.startScan();
		List<ScanResult> list;
		list = wf.getWifiList();
		List<WifiInfo> wis = new ArrayList<WifiInfo>();
		// 获取指定名称的信号源列表
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).SSID.equals(name)) {
				WifiInfo wi = new WifiInfo();
				wi.SSID = list.get(i).SSID;
				wi.Capability = list.get(i).capabilities;
				wi.Frequency = String.valueOf(list.get(i).frequency);
				wi.BSSID = list.get(i).BSSID;
				wi.Level = String.valueOf(list.get(i).level);
				wis.add(wi);
			}
		}
		return wis;
	}
	public WifiInfo[] getAllWiFiList(Context context) {
		 
		StringBuffer sb=new StringBuffer();
		// 每次点击扫描之前清空上一次的扫描结果
		if (sb != null) {
			sb = new StringBuffer();
		}
		// 开始扫描网络
		 WifiAdmin mWifiAdmin=new WifiAdmin(context);
		// 扫描结果列表
		 List<ScanResult> list;
		  ScanResult mScanResult;
		
		mWifiAdmin.startScan();
		list = mWifiAdmin.getWifiList();
		if (list != null) {
			WifiInfo[] ws = new WifiInfo[list.size()];
			for (int i = 0; i < list.size(); i++) {
				// 得到扫描结果
				mScanResult = list.get(i);
				WifiInfo w = new WifiInfo();
				w.BSSID = mScanResult.BSSID;
				w.SSID = mScanResult.SSID;
				w.Capability = mScanResult.capabilities;
				w.Frequency = String.valueOf(mScanResult.frequency);
				w.Level = String.valueOf(mScanResult.level);
			 
				ws[i] = w;
			}
			return ws;
		}
return null;
			
	}
	private String GetWifiByName1(Context context, String name) {
		WifiAdmin wf = new WifiAdmin(context);
		wf.startScan();
		List<ScanResult> list;
		list = wf.getWifiList();
		List<WifiInfo> wis = new ArrayList<WifiInfo>();
		// 获取指定名称的信号源列表
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).SSID.equals(name)) {
				WifiInfo wi = new WifiInfo();
				wi.SSID = list.get(i).SSID;
				wi.Capability = list.get(i).capabilities;
				wi.Frequency = String.valueOf(list.get(i).frequency);
				wi.BSSID = list.get(i).BSSID;
				wi.Level = String.valueOf(list.get(i).level);
				wis.add(wi);
 
			}
		}

		if (wis != null && wis.size() > 0) {
			// 对采集的信号列表由大到小排序
			for (int i = 0; i < wis.size() - 1; i++) {
				for (int j = i + 1; j < wis.size(); j++) {
					if (Integer.valueOf(wis.get(i).Level) < Integer.valueOf(wis
							.get(j).Level)) {
						WifiInfo temp = wis.get(i);
						wis.set(i, wis.get(j));
						wis.set(j, temp);
					}

				}
			}

			// 取前5个信号源
			String str = "";
			for (int i = 0; i < wis.size() && i < 5; i++) {
				if (i != wis.size())
					str += wis.get(i).BSSID + "," + wis.get(i).Level + ";";
				else
					str += wis.get(i).BSSID + "," + wis.get(i).Level;

			}
			return str;
		}
		return null;
	}

	private String result2 = "";

	private String[] GetWifiByName(Context context, String name) {
		WifiAdmin wf = new WifiAdmin(context);
		wf.startScan();
		List<ScanResult> list;
		list = wf.getWifiList();
		List<WifiInfo> wis = new ArrayList<WifiInfo>();
		// 获取指定名称的信号源列表
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).SSID.equals(name)) {
				WifiInfo wi = new WifiInfo();
				wi.SSID = list.get(i).SSID;
				wi.Capability = list.get(i).capabilities;
				wi.Frequency = String.valueOf(list.get(i).frequency);
				wi.BSSID = list.get(i).BSSID;
				wi.Level = String.valueOf(list.get(i).level);
				wis.add(wi);

			}
		}

		if (wis != null && wis.size() > 0) {
			// 对采集的信号列表由大到小排序
			for (int i = 0; i < wis.size() - 1; i++) {
				for (int j = i + 1; j < wis.size(); j++) {
					if (Integer.valueOf(wis.get(i).Level) < Integer.valueOf(wis
							.get(j).Level)) {
						WifiInfo temp = wis.get(i);
						wis.set(i, wis.get(j));
						wis.set(j, temp);
					}

				}
			}

			List<String> alist1 = new ArrayList<String>();
			List<String> alist2 = new ArrayList<String>();
			// 取前5个信号源
			String str = "";
			for (int i = 0; i < wis.size() && i < 5; i++) {
				if (i != wis.size())
					str += wis.get(i).BSSID + "," + wis.get(i).Level + ";";
				else
					str += wis.get(i).BSSID + "," + wis.get(i).Level;
				alist1.add(wis.get(i).BSSID);
				alist2.add(wis.get(i).Level);
			}
			result2 = str;
			// 形成这样的格式10:103:2d:3d,-30;
			String[] rlist = new String[alist1.size() * 2];

			for (int i = 0; i < alist1.size(); i++) {
				rlist[2 * i] = alist1.get(i);
			}
			for (int j = 0; j < alist2.size(); j++) {
				rlist[2 * j + 1] = alist2.get(j);
			}
			Log.d("wifiutils", str);
			for (int i = 0; i < alist1.size(); i++) {
				rlist[i] = alist1.get(i);
			}
			for (int j = alist1.size(); j < rlist.length; j++) {
				rlist[j] = alist2.get(j - alist1.size());
			}
			return rlist;
		}
		return null;
	}

}
