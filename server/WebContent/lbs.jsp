<%@ page language="java" contentType="text/html; charset=gb2312"
	pageEncoding="gb2312"%>
<%@ page import="java.sql.*,java.util.Date,com.lbsserver.web.*,com.lbsserver.user.*,com.lbsserver.utils.*"%>

<%
	String key="web.wlan.bjtu";
	String mark="test";
	request.setCharacterEncoding("gb2312");
	String action = request.getParameter("action");
	String userid = request.getParameter("userid");
	String md5_pwd = request.getParameter("md5_pwd");
	String sessionvalue = request.getParameter("session");
	String value = request.getParameter("value");
	mark=request.getParameter("mark");
	key=request.getParameter("key");
	//table=request.getParameter("table");
	 
	if (action != null & userid != null) {
		try{
		WiFiLBS wfb = new WiFiLBS();
		if (action.equals("login")) {
			out.print(wfb.Login(userid, md5_pwd));
		}else if (action.equals("connect")&&(md5_pwd!=null&&!md5_pwd.equals(""))){
			UserUtils lu=new UserUtils();
			UserUtils.User u=lu.getUser(userid);
			if(new MD5().getMD5ofStr(u.pwd).equals(md5_pwd)){
				lu.CreateSessionFile(u,sessionvalue);
				String r=wfb.GetLocation(userid,sessionvalue,value,key,mark);
				out.print(r);
			}else{
				out.print("DENIED");
			}
		}else
		if (action.equals("connect")) {
			out.print(wfb.GetLocation(userid,sessionvalue,value,key,mark));
		}else
		if (action.equals("logout")) {
			out.print(wfb.Logout(userid, sessionvalue));
		}
		}catch(Exception e){
			out.println("error");
			e.printStackTrace();
		}
	}
%>
