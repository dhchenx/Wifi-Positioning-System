<%@ page language="java" contentType="text/html; charset=gb2312"
	pageEncoding="gb2312" import="com.lbsserver.db.*"%>
<%
	try {
		String s = request.getQueryString();
		WiFiModel wm = new WiFiModel(s);
		LBSDB lbsdb = new LBSDB();
		int result = lbsdb.addData(wm);
		out.print(result);
	} catch (Exception e) {
		out.print("-1");
		e.printStackTrace();
	}
%>