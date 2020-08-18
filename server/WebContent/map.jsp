<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<%@ page
	import="java.sql.*,java.util.Date,java.text.SimpleDateFormat,com.lbsserver.map.*,com.lbsserver.db.*"%>
<%
	request.setCharacterEncoding("gb2312");
	String table = request.getParameter("table");
	String key = request.getParameter("key");
	String value = request.getParameter("value");
	 String step =request.getParameter("step");
	String mark=request.getParameter("mark");
	if (table != null && key != null && value != null
			&& !table.equals("")&&!value.equals("NULL")) {
		InputMap im=new InputMap();
		im.execute(table,key,mark,value,step);
	} else {
		out.print("-2");
	}
%>