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
	//��¼ϵͳ
	public String Login(String userid,String md5_pwd){
		return new UserUtils().Login(userid, md5_pwd);
	}
	//��ȡλ����Ϣ��ǰ��Ϊ�ѵ�¼
	public String GetLocation(String userid,String session,String mac_val,String key,String mark){
		return new BLL().GetLocation(userid, session, mac_val,key,mark);
	}
	//��ȡ�����б�
	public String GetTraceList(String userid,String session,String begintime,String endtime){
		return "";
	}
	//ע����¼
	public String Logout(String userid,String session){
		return new UserUtils().Logout(userid, session);
	}
	
	
}
