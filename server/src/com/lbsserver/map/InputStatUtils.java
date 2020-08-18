package com.lbsserver.map;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;

public class InputStatUtils {
	public InputStatUtils(){
		
	}

	public static double getMean(double[] data){
		return StatUtils.mean(data);
	}
	public static double getStd(double[] data){
		return Math.sqrt(StatUtils.variance(data));
	}
	
	public static double[] getSmoothArray(double[] data){
		double mean=StatUtils.mean(data);
		double std=Math.sqrt(StatUtils.variance(data));
		double min=mean-2.5*std;
		double max=mean+2.5*std;
		//System.out.println("mean = "+mean);
		//System.out.println("std = "+std);
		List<Double> list=new ArrayList<Double>();
		for(int i=0;i<data.length;i++)
			list.add(data[i]);
		int out_count=0;
		for(int i=0;i<list.size();i++)
		{
			//ÒÆ³ý³¬¹ý±ß½çµÄ
			if(list.get(i)>max||list.get(i)<min){
				//System.out.println("remove "+list.get(i));	
				list.remove(i);
				out_count++;
			}
		}
		double[] new_data=new double[list.size()];
		for(int i=0;i<list.size();i++)
			new_data[i]=list.get(i);
		if(out_count>0){
			//System.out.println("next loop");
		return getSmoothArray(new_data);
		}
		else{
			//System.out.println(mean+" ["+min+","+max+"]");
			return new_data;
		}
	}
}
