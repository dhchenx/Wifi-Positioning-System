package com.lbsserver.utils;
import org.apache.log4j.*; 

import com.lbsserver.user.global_user;

public class AppLog { 
	private static Logger printDebugLog=Logger.getLogger("printDebugInfo"); 
	private static Logger printOperateLog=Logger.getLogger("printOperateInfo"); 
	private static Logger printErrorLog=Logger.getLogger("printErrorInfo"); 
	static { 
	String configureFile =global_user.server_root+"applog\\logconfig\\"+"log4j.properties" ; 
	System.out.println("Configure File: "+configureFile);
	PropertyConfigurator.configure(configureFile); 
	} 
	public static void Debug(String message) 
	{ 
	printDebugLog.debug(message); 
	 
	} 

	public static void Info(String message) 
	{ 
	printOperateLog.info(message); 
	} 

	public static void Error(String message) 
	{ 

	printErrorLog.error(message); 
	} 

	public static void main(String[] args) { 

	AppLog.Debug("aaaaaaaaa"); 
	AppLog.Info("abc");
	AppLog.Error("error");
   
	} 
	}  