/**
 * 
 */
package com.lbsserver.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lbsserver.db.DBImp;
import com.lbsserver.user.global_user;
import com.lbsserver.utils.tools;

/**
 * @author TungWahChan
 *
 */
public class LBSService {
	public String getFinalLocation(String userid,String session,String table,String key,String value){
		String result="";
		if (table != null && key != null && value != null
				&& !table.equals("")&&!value.equals("NULL")) {
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
				insert="insert into "+table+"(id,addtime,";
				String values="'"+key+"','"+addtime+"',";
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
				try{
				int r=dbi.execute(insert);
					System.out.println("r = "+r);
				}catch(Exception e){
					System.out.println("execute error");
				}
				///read location
				
				String location="-4";
				//	
				try{

				RSSILBS lbs=new RSSILBS();
				//out.print(value);
				location=lbs.GetFinalTarget(value);
				
				//位置缓存纠正
				try{
				LBS lbs_correct=new LBS();
				//设置缓存的位置
				lbs_correct.setPath(global_user.server_userdata_root+userid+"\\data\\history_cache.txt");
				String mac_val_path=global_user.server_userdata_root+userid+"\\data\\"+userid+"_"+session+"_request.txt";
				//记录下追踪日志
				tools.AppendToFile(mac_val_path, addtime+"|"+value+"\n");
				String[] history=lbs_correct.GetHistory();
				System.out.println("old = "+history.length);
				String[] new_history=lbs_correct.DoCorrect2(history,location);
				System.out.println("new = "+new_history.length);
				lbs_correct.SaveHistory(new_history);
				location=new_history[new_history.length-1];
				}catch(Exception e){
					e.printStackTrace();
				}
				
				lbs.saveToDB("myid",location);
				// out.println("location = "+location);
				dbi.Close();
				}catch(Exception e){
				e.printStackTrace();
					result="-3";
				}
				

				result=location;
			} catch (Exception e) {
				e.printStackTrace();
				result="-1";
			}
		} else {
			result="-2";
		}
		return result;
	}
}
