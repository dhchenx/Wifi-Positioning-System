<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
 <%@ page import="com.lbsserver.appstat.*" %>
    <%
    try{
    	StatModel sm=new StatModel(request.getQueryString());
    	out.print(new StatDB().addWebStat(sm));
    }catch(Exception e){
    	e.printStackTrace();
    	out.print("error");
    }
    %>