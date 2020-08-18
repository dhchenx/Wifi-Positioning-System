/**
 * 
 */
package com.lbsserver.web;

import com.lbsserver.user.BLL;
import com.lbsserver.user.UserUtils;

/**
 * @author TungWahChan
 *
 */
public class WiFiLBS {
	//登录系统
	public String Login(String userid,String md5_pwd){
		return new UserUtils().Login(userid, md5_pwd);
	}
	//获取位置信息，前提为已登录
	public String GetLocation(String userid,String session,String mac_val,String key,String mark){
		return new BLL().GetLocation(userid, session, mac_val,key,mark);
	}
	//获取最终列表
	public String GetTraceList(String userid,String session,String begintime,String endtime){
		return "";
	}
	//注销登录
	public String Logout(String userid,String session){
		return new UserUtils().Logout(userid, session);
	}
	
	
}
