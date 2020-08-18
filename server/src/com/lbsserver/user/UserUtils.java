/**
 * 
 */
package com.lbsserver.user;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.lbsserver.utils.CreateFileUtil;
import com.lbsserver.utils.MD5;
import com.lbsserver.utils.AppLog;
import com.lbsserver.utils.TxtReader;
import com.lbsserver.utils.tools;

/**
 * @author TungWahChan
 *
 */
public class UserUtils {
	//�û�
	public class User{
		public String userid="";
		public String name="";
		public String pwd="";
		public String role="";
		public String key="1234567890";
	}
	//�Ự
	public class Session{
		public String userid="";
		public String updateTime="";
	}
	//��ȡ�Ự
	public Session getSession(String userid,String name){
		try{
			String path=global_user.server_userdata_root+userid+"\\session\\"+name+".txt";
			File file=new File(path);
			if(file.exists()){
			String s=tools.getTextFile(path,"gb2312");
			if(s==null||s.equals("")){
				return null;
			}else{
				String[] pars=s.split(",");
				Session session=new Session();
				session.userid=pars[0];
				session.updateTime=pars[1];
				System.out.println("updatetime = "+session.updateTime);
				String cur_time=tools.GetCurrentTime("yyyy-MM-dd HH:mm:ss");
				System.out.println("cur_time = "+cur_time);
				if(tools.IsSessionOutdate(session.updateTime,cur_time, 20)){
					AppLog.Info("userid = "+userid+"; session = "+name+"����");
					return null;
				}else{
					AppLog.Info("userid = "+userid+"����֤�ɹ�!");
					return session;
				}

			}
			}else{
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	//��ȡ�û�
	public User getUser(String userid){
		List<User> userlist=getUserList();
		if(userlist!=null){
			for(int i=0;i<userlist.size();i++){
				if(userid.toLowerCase().equals(userlist.get(i).userid.toLowerCase())){
					return userlist.get(i);
				}
			}
		}
		return null;
	}
	public List<User> getUserList(){
		try{
		String s=TxtReader.loadStringFromFile(new File(global_user.server_userlist_root+"userlist.txt"));
		String[] users=s.split("\n");
		List<User> myusers=new ArrayList<User>();
		for(int i=0;i<users.length;i++){
			String[] par=users[i].split(",");
			User user=new User();
			user.userid=par[0];
			user.pwd=par[1];
			//user.key=par[2];
			myusers.add(user);
		}
		return myusers;
		}catch(Exception e){
			AppLog.Error("��ȡ�û��б����");
			e.printStackTrace();
		}
		return null;
	}
	//��½����
	public static class LoginState{
		public static int Success=0;
		public static int Fail=-1;
		public static String TempSession="1234567890";
	}
	public String CreateSession(User user){
		String str=user.userid+user.pwd+user.key+tools.GetCurrentTime("yyyy-MM-dd HH:mm:ss");
		return new MD5().getMD5ofStr(str);
	}
	public void CreateSessionFile(User user,String session){
		try{
		String path=global_user.server_userdata_root+user.userid+"\\session\\"+session+".txt";
		File file=new File(path);
		if(file.exists())
			file.delete();
		tools.writefile(path, user.userid+","+tools.GetCurrentTime("yyyy-MM-dd HH:mm:ss"), "gb2312");
		}catch(Exception e){
			AppLog.Error("����Session�ļ�ʧ�ܡ�userid = "+user.userid);
		}
	}
	public int DisableSessionFile(User user,String session){
		try{
		String path=global_user.server_userdata_root+user.userid+"\\session\\"+session+".txt";
		File file=new File(path);
		if(file.exists())
			file.delete();
		tools.writefile(path, user.userid+","+"1990-09-22 00:00", "gb2312");
		AppLog.Info("ע��userid = "+user.userid+" �ɹ�!");
		return 0;
		}catch(Exception e){
			AppLog.Error("ע��Session�ļ�ʧ�ܡ�userid = "+user.userid);
		}
		return -1;
	}
	//�״ε�¼
	public String Login(String userid,String md5_pwd){
		User myuser=this.getUser(userid);
		 if(myuser!=null){
			 
			 MD5 md5=new MD5();
			 String md5_pwd1=md5.getMD5ofStr(myuser.pwd);
			 if(md5_pwd.toLowerCase().equals(md5_pwd1.toLowerCase())){
				 AppLog.Info("userid = "+userid+" ��¼�ɹ�!");
				 initUserData(userid);
				 
				 String sessionname= CreateSession(myuser);
				 CreateSessionFile(myuser,sessionname);
				 return sessionname;
				 
			 }else{
				 AppLog.Info("userid = "+userid+" ��¼ʧ��! ��½MD5����: "+md5_pwd);
			 }
		 }
		 return LoginState.TempSession;
		 
	}
	//��ʼ���û��ļ��нṹ
	public void initUserData(String userid){
		try{
			String path=global_user.server_userdata_root+userid+"\\";
			boolean b=CreateFileUtil.createDir(path);
			b=CreateFileUtil.createDir(path+"session\\");
			b=CreateFileUtil.createDir(path+"data\\");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//��¼����в���������session�ػ�
	public boolean VerifyLoginState(String userid,String session){
		Session session2=getSession(userid,session);
		if(session2==null){
			AppLog.Info("���ӷ�����ʧ�ܣ��Ự����");
			return false;
		}else{
			AppLog.Info("���ӷ������ɹ�");
			this.CreateSessionFile(this.getUser(userid), session);
			return true;
		}
		 
	}
	public String Logout(String userid,String session){
		 return String.valueOf(this.DisableSessionFile(this.getUser(userid), session));
	}
	public static void main(String[] args){
		UserUtils uu=new UserUtils();
	//	System.out.println("session = "+uu.Login("chendonghua", new MD5().getMD5ofStr("123456")));
		uu.VerifyLoginState("chendonghua", "7D7BAD55DADA1BFBB74486D60B126A7D");
		//System.out.println(tools.IsSessionOutdate("2012-12-23 17:29:01","2012-12-23 17:50:01", 50));
	}
	 
}
