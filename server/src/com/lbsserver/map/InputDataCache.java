package com.lbsserver.map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InputDataCache {
	public class CacheResult{
		public List<String> maclist=null;
		public List<Double> vallist=null;
	}
	// ����ϴ����ݵĸ��ٻ�����
	public String cache_root = "e:\\test\\cache\\";
	public void setCacheRoot(String s){
		this.cache_root=s;
	}
	public void addData(String userid, List<String> maclist,
			List<Double> vallist) {
		String path = cache_root + userid+".txt";
		this.saveCache(path, maclist, vallist);
	}
	public void saveCache(String mark,String value)
	{
		boolean append = false;

		File file = new File(this.cache_root+mark+".txt");
		try {
			if (file.exists())
				append = true;
			FileWriter fw = new FileWriter(this.cache_root+mark+".txt", append);// ͬʱ�������ļ�
			// �����ַ����������
			BufferedWriter bf = new BufferedWriter(fw);
			// ���������ַ����������
		 
				bf.append(value+ "\n");
		 
			bf.flush();
			bf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void saveCache(String path, List<String> maclist,
			List<Double> vallist) {// ���齱������浽�ļ���
		boolean append = false;

		File file = new File(path);
		try {
			if (file.exists())
				append = true;
			FileWriter fw = new FileWriter(path, append);// ͬʱ�������ļ�
			// �����ַ����������
			BufferedWriter bf = new BufferedWriter(fw);
			// ���������ַ����������
			for (int i = 0; i < maclist.size(); i++) {
				bf.append(maclist.get(i) + "," + vallist.get(i) + "\n");
			}
			bf.flush();
			bf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 public String getCache(String filename,String encoding){
			String[] data = null;
			try {
				File file=new File(filename);
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),encoding));
				String result="";
				String row="";
				while((row = br.readLine())!=null){
					     result+=row+"\n";
					   }
				br.close();
				return result;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
		}
	
	public String[] getCache(String userid){
		
		String path=this.cache_root+userid+".txt";
		File file=new File(path);
		if(!file.exists()){
			System.out.println("�����ڸ��ļ�");
			return null;
		}
		String s=this.getCache(path, "gb2312");
		
		if(file.exists())file.delete();
		 
		if(s!=null&&!s.trim().equals("")){
			 return s.split("\n");
		}
		return null;
	}
	
	public boolean IsCacheFull(String userid){
		String path=this.cache_root+userid+".txt";
		//System.out.println("path = "+path);
		FileSize cachefilesize=new FileSize(new File(path));
		 
		long b=cachefilesize.getLongSize()/1024;
		if(b>=3){
			return true;
		}else
			return false;
	}
	
	public void clearCache(String userid){
		String path=this.cache_root+userid+".txt";
		File file=new File(path);
		if(file.exists())file.delete();
	}

	public static void main(String[] args) {
		InputDataCache idc=new InputDataCache();
		
		List<String> maclist=new ArrayList<String>();
		List<Double> vallist=new ArrayList<Double>();
		
		maclist.add("mac");
		vallist.add(-33.0);
		maclist.add("mac2");
		vallist.add(-35.0);
		maclist.add("mac");
		vallist.add(-33.0);
		idc.addData("chendonghua", maclist, vallist);
/*
		InputDataCache.CacheResult cr=null;
		if(idc.IsCacheFull("chendonghua")){
			cr=idc.getCache("chendonghua");
			if(cr!=null)
			for(int i=0;i<cr.maclist.size();i++){
				System.out.println(maclist.get(i));
			}
		}
		*/
		
	}
}
