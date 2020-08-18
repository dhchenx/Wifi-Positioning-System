package com.lbsserver.utils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lbsserver.db.Base64;


public class tools {
	public static void AppendToFile(String path,String str) {
		try{
		Writer fw = new FileWriter(path, true);
		fw.write(str);
		fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static boolean IsSessionOutdate(String startTime, String endTime,int max_min) {
		String format="yyyy-MM-dd HH:mm:ss";
		//按照传入的格式生成一个simpledateformate对象
		SimpleDateFormat sd = new SimpleDateFormat(format);
		long nd = 1000*24*60*60;//一天的毫秒数
		long nh = 1000*60*60;//一小时的毫秒数
		long nm = 1000*60;//一分钟的毫秒数
		long ns = 1000;//一秒钟的毫秒数
		long diff;
		try {
		//获得两个时间的毫秒时间差异
		diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
		long day = diff/nd;//计算差多少天
		long hour = diff%nd/nh;//计算差多少小时
		long min = diff%nd%nh/nm;//计算差多少分钟
		long sec = diff%nd%nh%nm/ns;//计算差多少秒
		//输出结果
		//System.out.println("时间相差："+day+"天"+hour+"小时"+min+"分钟"+sec+"秒。");
	//	System.out.println("min = "+min);
		if(day==0&&hour==0&&min<=max_min){
			return false;
		}else{
			return true;
		}
		
		} catch (ParseException e) {
		e.printStackTrace();
		}
		return false;
	}
	public static void copyfile(String file1,String file2){
		try{
		copyFile(new File(file1),new File(file2));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static int deleteFile(String path){
		try{
			
		File f = new File(path);  // 输入要删除的文件位置
		if(f.exists())
		    f.delete();
		return 0;
		}catch(Exception e){
			e.printStackTrace();
		}
		return 1;
	}
	   public static void copyFile(File sourceFile, File targetFile) throws IOException {
	        BufferedInputStream inBuff = null;
	        BufferedOutputStream outBuff = null;
	        try {
	            // 新建文件输入流并对它进行缓冲
	            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

	            // 新建文件输出流并对它进行缓冲
	            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

	            // 缓冲数组
	            byte[] b = new byte[1024 * 5];
	            int len;
	            while ((len = inBuff.read(b)) != -1) {
	                outBuff.write(b, 0, len);
	            }
	            // 刷新此缓冲的输出流
	            outBuff.flush();
	        } finally {
	            // 关闭流
	            if (inBuff != null)
	                inBuff.close();
	            if (outBuff != null)
	                outBuff.close();
	        }
	    }

	 public  static File getFileFromBytes(byte[] b, String outputFile) {
	        BufferedOutputStream stream = null;
	        File file = null;
	        try {
	            file = new File(outputFile);
	            FileOutputStream fstream = new FileOutputStream(file);
	            stream = new BufferedOutputStream(fstream);
	            stream.write(b);
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (stream != null) {
	                try {
	                    stream.close();
	                } catch (IOException e1) {
	                    e1.printStackTrace();
	                }
	            }
	        }
	        return file;
	    }
	public static byte[] getBytesFromFile(String path) {
		File f=new File(path);
		 try{
	    InputStream is ;	  
	    is= new FileInputStream(f);
	    long length = f.length();
	    if (length > Integer.MAX_VALUE) {
        // 文件太大，无法读取
	    	System.out.println("too large");
	    }
	    // 创建一个数据来保存文件数据
	    byte[] bytes = new byte[(int)length];
	    // 读取数据到byte数组中
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
	           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    if (offset < bytes.length) {

	        System.out.println("can't read");
	    }
    return bytes;
	}
		 }catch(Exception e){
			 e.printStackTrace();	
		 }
		 return null;
	}
	 public static String[] IPageReader(String path){
	    	try{	    
			   FileReader read = new FileReader(path);
			   BufferedReader br = new BufferedReader(read);
			   String row;
			   String result="";
			   while((row = br.readLine())!=null){
			     result+=row+"\n";
			   }
			   String[] r=result.split("\n");
			   return r;
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	 return null;
	     
	    }
	 public static String getTextFile(String filename,String encoding){
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

	 public static String TextReader(String path){
	    	try{	    
			   FileReader read = new FileReader(path);
			   BufferedReader br = new BufferedReader(read);
			   String row;
			   String result="";
			   while((row = br.readLine())!=null){
			     result+=row+"\n";
			   }
			  // [] r=result.split("\n");
			   return result;
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	 return null;
	     
	    }
		public static String getPName(String row,String sign){
			String temp="";
			for(int i=0;i<row.length();i++){
				if(row.substring(i,i+1).equals(sign)){
					return temp;
				}else{
					temp=temp+row.substring(i,i+1);
				}
			}
			return null;
		}
		public static String getPValue(String row,String sign){
			for(int i=0;i<row.length();i++){
				if(row.substring(i,i+1).equals(sign)){
					return row.substring(i+1);
				}
			}
			return null;
		}
		 public static int writefile(String path,String text,String encoding){
	          try {
	         		    FileOutputStream o = new FileOutputStream(path);
	         		    o.write(text.getBytes(encoding));
	         		    o.close();
	         		    return 0;
	         		  } catch(Exception e) {
	         			  e.printStackTrace();
	         		  }
	         		  return -1;
	         }
		   public static String GetCurrentTime(String format){
		      	Date date = new Date();     	
		      	SimpleDateFormat from = new SimpleDateFormat( format );
		      	String times = from.format(date); 
		      	
		        return times;
		      } 
		   /*
		   public static String tempfile(String appid){
			   String s=global.AppsRoot+appid+"\\rt\\rub\\"+GetCurrentTime("yyyymmddhhMMss")+".xml";
			 //  writefile(s,s1,"utf-8");
			   return s;
			   }
		   public static String tempfile(String appid,String id){
			   String s=global.AppsRoot+appid+"\\rt\\rub\\"+GetCurrentTime("yyyymmddhhMMss")+id+".xml";
			 //  writefile(s,s1,"utf-8");
			   return s;
			   }
			   */
	  public static String encodeURL(String s){
		  try{
		  byte[] b=s.getBytes("utf-8");
		  String r=new Base64().encodestr(b	,0, b.length);
		  r=r.replace("/", "%2F");
		r=r.replace("=", "%3D");
		  return r;
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return "";
	  }
	  public static String decodeURL(String s){
		  try{
			  s=s.replace("%2F", "/");
			  s=s.replace("%3D", "=");
			  Base64 bs=new Base64();
		  byte[] b=bs.decodestr(s);
		  return new String(b,"utf-8");
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return "";
	  }
	  public static void deal(){
	/*	 String s= tools.getTextFile("H:\\Users\\TungWahChan\\Desktop\\net1.txt", "gb2312");
		 String[] str=s.split("\n");
		 String temp="";
		 int c=0;
		 for(int i=0;i<str.length;i++){
			 str[i]="\""+str[i]+"\",";
			 
		 }
		 String r="";
		 for(int k=0;k<str.length;k++){
			 r+=str[k]+"\n";
		 }
		 r="String[] str=new String[]{\n"+r+"\n}";
		 tools.writefile("H:\\Users\\TungWahChan\\Desktop\\net2.txt", r, "gb2312");
		 */
	  }
		   public static void main(String[] args){
			   //System.out.print(getTextFile("D:\\test\\webs\\wias\\apps\\library\\rt\\tempxml\\temp_124108084108.xml","utf-8"));
		//  System.out.println(encodeURL("http://www.baidu.com/as?a=1&amp;b=3"));
		 // System.out.println(decodeURL(encodeURL("http://www.baidu.com/as?a=1&amp;b=3")));
		   deal();
		   }   
		 
}
