/**
 * 
 */
package com.bluecanna.wifimapbuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TungWahChan
 * 
 */

// Ӧ��Ҫ��һ���ӿ�
//
public class LBS {
	public String path="e:\\tomcat\\ekyy\\history.txt";
	public void setPath(String s){
		this.path=s;
	}
	public void SaveHistory(String[] str){
		String s="";
		for(int i=0;i<str.length;i++){
			if(i!=str.length-1)
				s+=str[i]+",";
			else
				
				s+=str[i];
		}
		 try{
		File file = new File(path);
		if(file.exists())
			file.delete();
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(s);
		fileWriter.close();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	}
	public String[] GetHistory(){
		 try{
			 File file=new File(path);
			 if(file.exists()){
			 FileReader fr = new FileReader(file);
			 int ch = 0;
			  String s="";
			  while((ch = fr.read())!=-1 )
			  {
			   s+=String.valueOf((char)ch);
			  }
			  System.out.println("s = "+s);
			  fr.close();
			  if(s==null||s.equals(""))
				  return new String[]{};
			  else
			  return s.split(",");
			 }else
			 return new String[]{};
			 }catch(Exception e){
					 e.printStackTrace();
			 }
			 return new String[]{};
		 
	}
	//�����µ���ʷ��¼
	public String[] DoHistory(String[] his, String target) {
		if (his == null||his.length<=0) {
			return new String[] { target };
		}
		if (his.length >= 10) {
			for (int i = 0; i < his.length - 1; i++) {
				his[i] = his[i + 1];
			}
			his[his.length - 1] = target;
			return his;
		} else {
			String[] new_his = new String[his.length + 1];
			for (int i = 0; i < his.length; i++) {
				new_his[i] = his[i];
			}
			new_his[his.length] = target;
			return new_his ;
		}
	}
//��ʷ��¼����
	public String DoCorrect(String[] history, String predict) {
		if (history == null || history.length <= 1) {
			return predict;
		}
		List<String> his = new ArrayList<String>();
		// ��ȡ��ʷ�켣�����г��ֹ��ĵص�
		List<String> sites = new ArrayList<String>();
		// ���ֻ����ֱ�
		List<Double> marks = new ArrayList<Double>();

		for (int i = 0; i < history.length; i++) {
			if (!sites.contains(history[i])) {
				sites.add(history[i]);
				marks.add(0.0);
			}
			his.add(history[i]);
		}

		// ����ÿ����ַ�ķ�ֵ���ۼ�
		for (int i = 0; i < his.size(); i++) {
			for (int j = 0; j < sites.size(); j++) {
				if (sites.get(j).equals(his.get(i))) {
					marks.set(j, 1.0 * i * i + marks.get(j));
					break;
				}
			}
		}

		String target = "";
		Double mark = 0.0;
		// ���ҷ�ֵ���ĵ�ַ
		for (int i = 0; i < marks.size(); i++) {
			if (mark < marks.get(i)) {
				mark = marks.get(i);
				target = sites.get(i);
			}
			System.out.println(sites.get(i) + ":" + marks.get(i));
		}

		System.out.println("Ԥ��λ�ã�" + target+"\n");

		return target;

	}
//��ǰ��¼����ʷ��¼����
	public String[] DoCorrect2(String[] history,String predict){
		
		String correct1=this.DoCorrect(history, predict);
		String[] new_history=DoHistory(history,predict);
		String correct2=this.DoCorrect(new_history, correct1);
		System.out.println("��һ��Ԥ������"+correct1);
		System.out.println("�ڶ���Ԥ������"+correct2);
		//����Ԥ����ͬ
		if(correct1.equals(correct2)&&!correct1.equals("")){
			return this.DoHistory(history, correct1);
		}else{
			//����Ԥ�ⲻһ��
			return this.DoHistory(history, correct2);
		}
		
	}
	// ��ͨ����λ������ȡ��Ԥ��λ����Ҫ���о�����ʹ֮����ʵ�����������������Ϊ��ʷ�켣��¼�͸����ϵ
	public String Correct(String target, String[] neighbor, String[] distance) {
		// ��Ŀ������ڵ�λ�õ�
		List<String> loclist = new ArrayList<String>();
		for (int i = 0; i < neighbor.length; i++) {
			loclist.add(neighbor[i]);
		}
		// ��ȡĿ������������RSSI��Ϣ

		// ��ȡ�������ڵ��RSSI��Ϣ

		// ��Ŀ���͸�����RSSI��Ϣ�������Է���

		// ������ʷ�켣���ݣ�ȷ����ǰĿ��㣬��ֹ����ʷ�켣����ƫ�����

		// ����ʷ�����з���������Ϊ׼ȷ��Ŀ�����һ��λ����Ϣ��Ŀ�����λ����Ϣ���м��㣬�͸����������������·���Ƚϣ����ջ��
		// ���Ԥ��λ������һ�ڵ���ĸ������·��

		return "";
	}

	public static void main(String[] args) {
		LBS lbs = new LBS();
	 
		String[] history=new String[]
		 {"s1","s2","s2","s2","s2","s2","s4","s3","s4"};                           
		String predict = "s3";
		String[] re=lbs.DoCorrect2(history, predict);
		System.out.println("����λ��:"+re[re.length-1]);
		 
	}
}
