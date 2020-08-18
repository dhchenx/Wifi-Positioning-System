package com.bluecanna.wifimapbuilder;

import android.util.Log;

import com.bluecanna.appstat.StatUtils;

public class WiFiInfoUploader {
	private String _apname="";
	private String _areaid="";
	private String[] macs=null;
	private double[] vals=null;
	private String _step="";
	private String _pointid="";
	private String _remark="";
	public WiFiInfoUploader(String apname,String areaid,String remark,String pointid,String step,String[] macs,String[] vals){
		
		System.out.println("in wifiinfouploader");
		this._apname=apname;
		this._areaid=areaid;
		
		double[] vs=new double[vals.length];
		for(int i=0;i<vs.length;i++){
			vs[i]=Double.valueOf(vals[i]);
		}
		this.macs=macs;
		this.vals=vs;
		this._step=step;
		this._pointid=pointid;
		this._remark=remark;
	}
	public void upload(){
	 try{
		 
		/*
		String step = _step;
		 
		String mark = _areaid;
		String key = _apname;
		String table = "wifimap";
		// 上传到服务器
		String url = "http://ekyy.v050.10000net.cn/LBSServer1/map.jsp?"
				+ "table="
				+ table
				+ "&key="
				+ key
				+ "&value="
				+ _value + "&step=" + step + "&mark=" + mark;
				*/
		 String sendurl="http://ekyy.v050.10000net.cn/LBSServer2/wificollect.jsp";
		 WiFiModel wm=new WiFiModel();
		 StatUtils stat=new StatUtils();
		 wm.addtime=Tools.getTimeStamp();
		 wm.apname=_apname;
		 wm.appid=1;
		 wm.remark=_remark;
		 wm.areaid=_areaid;
		 wm.device=stat.getModel();
		 wm.pointid=_pointid;
		 wm.step=Integer.valueOf(_step);
		 wm.m=macs;
		 wm.v=vals;
		 wm.extend1=stat.getSystem();
		  sendurl+="?"+wm.getModel();
		  Log.d("param", sendurl);
		  MyASync asc = new MyASync();
		  asc.execute(sendurl);
		  Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
