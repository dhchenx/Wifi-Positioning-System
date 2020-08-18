package com.bluecanna.wificlock.utils;
import java.lang.reflect.Type;

import com.google.gson.*;
public class JsonUtils {
	public static String getJsonStr(Object obj){
		Gson gson=new Gson();
		String str=gson.toJson(obj);
		return str;
	}
	public static Object getObj(String str,Type c){
		Gson gson=new Gson();
		return gson.fromJson(str, c);
	}
}
