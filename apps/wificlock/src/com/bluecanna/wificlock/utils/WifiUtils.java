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
		// �鿴�Ѿ������ϵ�WIFI��Ϣ����Android��SDK��Ϊ�����ṩ��һ������WifiInfo�Ķ�������������ͨ��WifiManager.getConnectionInfo()����ȡ��WifiInfo�а����˵�ǰ�����е������Ϣ��
		// getBSSID() ��ȡBSSID����
		// getDetailedStateOf() ��ȡ�ͻ��˵���ͨ��
		// getHiddenSSID() ��ȡSSID �Ƿ�����
		// getIpAddress() ��ȡIP ��ַ
		// getLinkSpeed() ��ȡ���ӵ��ٶ�
		// getMacAddress() ��ȡMac ��ַ
		// getRssi() ��ȡ802.11n ������ź�
		// getSSID() ��ȡSSID
		// getSupplicanState() ��ȡ����ͻ���״̬����Ϣ
		/*
		 * StringBuffer sb = new StringBuffer();
		 * sb.append("\n��ȡBSSID���ԣ������ӵ�WIFI�豸��MAC��ַ����" + wifiInfo.getBSSID()); //
		 * sb.append("getDetailedStateOf()  ��ȡ�ͻ��˵���ͨ�ԣ�");
		 * sb.append("\n\n��ȡSSID �Ƿ����أ�" + wifiInfo.getHiddenSSID());
		 * sb.append("\n\n��ȡIP ��ַ��" + wifiInfo.getIpAddress());
		 * sb.append("\n\n��ȡ���ӵ��ٶȣ�" + wifiInfo.getLinkSpeed());
		 * sb.append("\n\n��ȡMac ��ַ���ֻ�����������MAC��ַ����" + WifiMac);
		 * sb.append("\n\n��ȡ802.11n ������źţ�" + wifiInfo.getRssi());
		 * sb.append("\n\n��ȡSSID�������ӵ�WIFI���������ƣ���" + wifiInfo.getSSID());
		 * sb.append("\n\n��ȡ����ͻ���״̬����Ϣ��" + wifiInfo.getSupplicantState());
		 */

	}

	public List<WifiInfo> getWifiListByName(Context context,String name){
		WifiAdmin wf = new WifiAdmin(context);
		wf.startScan();
		List<ScanResult> list;
		list = wf.getWifiList();
		List<WifiInfo> wis = new ArrayList<WifiInfo>();
		// ��ȡָ�����Ƶ��ź�Դ�б�
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
		// ÿ�ε��ɨ��֮ǰ�����һ�ε�ɨ����
		if (sb != null) {
			sb = new StringBuffer();
		}
		// ��ʼɨ������
		 WifiAdmin mWifiAdmin=new WifiAdmin(context);
		// ɨ�����б�
		 List<ScanResult> list;
		  ScanResult mScanResult;
		
		mWifiAdmin.startScan();
		list = mWifiAdmin.getWifiList();
		if (list != null) {
			WifiInfo[] ws = new WifiInfo[list.size()];
			for (int i = 0; i < list.size(); i++) {
				// �õ�ɨ����
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
		// ��ȡָ�����Ƶ��ź�Դ�б�
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
			// �Բɼ����ź��б��ɴ�С����
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

			// ȡǰ5���ź�Դ
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
		// ��ȡָ�����Ƶ��ź�Դ�б�
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
			// �Բɼ����ź��б��ɴ�С����
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
			// ȡǰ5���ź�Դ
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
			// �γ������ĸ�ʽ10:103:2d:3d,-30;
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
