package com.bluecanna.wificlock.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class APModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name="";
	public List<WiFiModel> mvlist=new ArrayList<WiFiModel>();
}
