/**
 * 
 */
package com.lbsserver.user;

import com.lbsserver.core.LBS;
import com.lbsserver.core.LBSService;
import com.lbsserver.map.LBSData;
import com.lbsserver.map.LBSMap;
import com.lbsserver.map.LBSMap.Point;

/**
 * @author TungWahChan
 *
 */
public class BLL {
	public String Login(String userid,String md5_pwd){
		return new UserUtils().Login(userid, md5_pwd);
	}
	public String GetLocation(String userid,String session,String mac_val,String key,String mark){
		if(new UserUtils().VerifyLoginState(userid, session)==true){
			//���ӳɹ�
			LBSService lbss=new LBSService();
			String table="wifimap";
			
			String value=mac_val;
			String result=lbss.getFinalLocation(userid,session,table, key, value);
			//��ȡ����λ��
			if(result!=null){
				LBSMap lbsmap=new LBSMap();
				String nodeid=result.trim();
				Point r=lbsmap.getSiteLocation(key, nodeid);
				if(r!=null){
					result=result+"("+r.x+","+r.y+")";
					LBSData lbs=new LBSData(
						key,//����id
						0,0,0,//������������
						320,480,//�����С
						nodeid,//��λid
						r.x,r.y,r.z//��λ������
					);
					result=lbs.getData();
				}else{
					LBSData lbs=new LBSData(
							key,//����id
							0,0,0,//������������
							320,480,//�����С
							"UNKNOWN",//��λid
							-1,-1,-1//��λ������
						);
					result=lbs.getData();
				}
			}
			
			return result;
		}else
		return "LostSession";
	}
}
