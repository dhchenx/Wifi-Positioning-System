/**
 * 
 */
package com.lbsserver.map;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lbsserver.db.DBImp;
import com.lbsserver.user.global_user;
import com.lbsserver.utils.AppLog;

/**
 * @author TungWahChan
 *
 */
public class InputMap {
	public void execute(String table,String key,String mark,String value,String step){
		AppLog.Debug("mark = "+mark+"; step"+step+"; value = "+value);
		//平滑处理数据	
		try{
			if(Double.valueOf(step)%200==0){
				AppLog.Debug("开始批量处理数据 step="+step+";mark="+mark);
				InputMacUtils.CreateMapFeature(mark, mark, 2000);
				return;
			}
		}catch(Exception e){
			AppLog.Debug("平滑处理出现错误!");
			e.printStackTrace();
		}
		try{
			this.addToDB(table, key, mark, value, step);
		}catch(Exception e){
			AppLog.Debug("InputMap:添加数据到数据库中出错");
			e.printStackTrace();
		}
		
		
		/*
		String[] str=this.getCacheData(mark, value);
		if(str!=null){
			for(int i=0;i<str.length;i++){
				//System.out.println("history:"+str[i]);
				
				try{
				this.addToDB(table, key, mark, str[i], step);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			//System.out.println("缓存读取了"+str.length+"个记录");
			
		}
		*/
	}
	
	public String[] getCacheData(String mark,String value){
		String path=global_user.server_map_cache_root;
		InputDataCache idc=new InputDataCache();
		idc.setCacheRoot(path);
		idc.saveCache(mark+"_upload_cache", value);
		if(idc.IsCacheFull(mark+"_upload_cache")){
			String[] re=idc.getCache(mark+"_upload_cache");
			return re;
		}else
		return null;
	}
	public void addToDB(
			String table,String key,String mark,String value,String step){
		String dbname = "mosystem";
		String dbuser = "mosystem";
		String dbpass = "chendonghua";
		String dburl = "27.54.227.50:3306";
		String insert="";
		try {
			DBImp dbi = new DBImp(dburl,dbname,dbuser,dbpass);
			Date date = new Date();
			SimpleDateFormat from = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String addtime = from.format(date);
			String[] mv=value.split(";");
			String[] m=new String[mv.length];
			String[] v=new String[mv.length];
			insert="insert into "+table+"(id,addtime,step,remark,";
		String values="'"+key+"','"+addtime+"','"+step+"','"+mark+"',";
			for(int i=0;i<mv.length;i++){
				String[] s=mv[i].split(",");
				m[i]=s[0];
				v[i]=s[1];
				if(i!=mv.length-1){
				insert+="m"+(i+1)+","+"v"+(i+1)+",";
				values+="'"+m[i]+"','"+v[i]+"',";
				}
				else{
					insert+="m"+(i+1)+","+"v"+(i+1);
					values+="'"+m[i]+"','"+v[i]+"'";
				}	
			}
			insert+=")values("+values+")";
			dbi.execute(insert);
			dbi.Close();
	
		} catch (Exception e) {
	e.printStackTrace();
		}
	}
	public static void main(String[] args){
		String table="wifimap";
		String key="test";
		String mark="s2";
		String step="1";
		String value="00:23:89:17:51:f0,-10;00:23:89:17:52:f1,30;00:23:89:17:53:21,44";
		InputMap im=new InputMap();
		for(int i=0;i<45;i++){
		im.execute(table, key, mark, value, step);
		}
		
	}
}
