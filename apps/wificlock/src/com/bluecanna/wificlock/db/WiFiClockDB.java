package com.bluecanna.wificlock.db;

import com.bluecanna.wificlock.model.WiFiClockModel;
import com.bluecanna.wificlock.utils.JsonUtils;

import android.content.Context;

public class WiFiClockDB implements IWiFiClockDB {
	private WifiDB wdb=null;
	private Context context=null;
	public WiFiClockDB(Context  ctx){
		wdb=new WifiDB(ctx);
		context =ctx;
		wdb.open();
	}
	public void close(){
		try{
			wdb.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static int SUCCESS=0;
	public static int FAIL=-1;
	public int addWiFiClock(WiFiClockModel wcm){
		try{
			WiFiClockModel w=this.getModel(wcm.id);
			if(w!=null)
				this.deleteWiFiClock(wcm.id);
			
		String id=wcm.id;
		String str=JsonUtils.getJsonStr(wcm);
		wdb.addModel(id, str);
		return WiFiClockDB.SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
		}
	    return WiFiClockDB.FAIL;
	}
	public void clearWiFiClockAll(){
		 wdb.clearTable("wc_wificlock");
	}
	
	public int updateWiFiClock(WiFiClockModel wcm){
		try{
		this.deleteWiFiClock(wcm.id);
		this.addWiFiClock(wcm);
	    return WiFiClockDB.SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			return FAIL;
		}
	}
	public WiFiClockModel openClock(WiFiClockModel wcm,boolean b){
		if(b==true)
			wcm.isRunning=WiFiClockModel.RUN;
		else
			wcm.isRunning=WiFiClockModel.STOP;
	    	this.addWiFiClock(wcm);
	    	return wcm;
	}
	public int deleteWiFiClock(String id){
		wdb.execute("delete from wc_wificlock where id='"+id+"'");
	    return WiFiClockDB.SUCCESS;
	}
	public  WiFiClockModel[] getModelList(){
		String[][] t=wdb.getTable("wc_wificlock", new String[]{"clock"},null,
				"addtime desc");
		try{
		if(t!=null){
			WiFiClockModel[] wcms=new WiFiClockModel[t.length];
			for(int i=0;i<t.length;i++){
				wcms[i]=(WiFiClockModel) JsonUtils.getObj(t[i][0], WiFiClockModel.class);
			}
			return wcms;
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public   WiFiClockModel getModel(String id){
		String[][] t=wdb.getTable("wc_wificlock", new String[]{"clock"}, "id='"+id+"'", 
				"addtime desc");
		try{
		if(t!=null&&t[0]!=null&&t[0][0]!=null)
		{
			String str=t[0][0];
			WiFiClockModel wcm=(WiFiClockModel)JsonUtils.getObj(str, WiFiClockModel.class);
			return wcm;
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
}
