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

// 应该要做一个接口
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
	//构建新的历史记录
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
//历史记录纠正
	public String DoCorrect(String[] history, String predict) {
		if (history == null || history.length <= 1) {
			return predict;
		}
		List<String> his = new ArrayList<String>();
		// 获取历史轨迹中所有出现过的地点
		List<String> sites = new ArrayList<String>();
		// 评分基础分表
		List<Double> marks = new ArrayList<Double>();

		for (int i = 0; i < history.length; i++) {
			if (!sites.contains(history[i])) {
				sites.add(history[i]);
				marks.add(0.0);
			}
			his.add(history[i]);
		}

		// 计算每个地址的分值，累加
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
		// 查找分值最大的地址
		for (int i = 0; i < marks.size(); i++) {
			if (mark < marks.get(i)) {
				mark = marks.get(i);
				target = sites.get(i);
			}
			System.out.println(sites.get(i) + ":" + marks.get(i));
		}

		System.out.println("预测位置：" + target+"\n");

		return target;

	}
//当前记录和历史记录纠正
	public String[] DoCorrect2(String[] history,String predict){
		
		String correct1=this.DoCorrect(history, predict);
		String[] new_history=DoHistory(history,predict);
		String correct2=this.DoCorrect(new_history, correct1);
		System.out.println("第一次预测结果："+correct1);
		System.out.println("第二次预测结果："+correct2);
		//两次预测相同
		if(correct1.equals(correct2)&&!correct1.equals("")){
			return this.DoHistory(history, correct1);
		}else{
			//二次预测不一样
			return this.DoHistory(history, correct2);
		}
		
	}
	// 当通过定位计算后获取的预测位置需要进行纠正，使之符合实际情况，纠正的依据为历史轨迹记录和各点关系
	public String Correct(String target, String[] neighbor, String[] distance) {
		// 和目标点相邻的位置点
		List<String> loclist = new ArrayList<String>();
		for (int i = 0; i < neighbor.length; i++) {
			loclist.add(neighbor[i]);
		}
		// 获取目标点所处区域的RSSI信息

		// 获取各个相邻点的RSSI信息

		// 将目标点和各相邻RSSI信息建立线性方程

		// 分析历史轨迹数据，确定当前目标点，防止与历史轨迹数据偏差过大。

		// 从历史数据中分析出来最为准确的目标点上一次位置信息和目标点今次位置信息进行计算，和各个该两点间若干条路径比较，最终获得
		// 求出预测位置在上一节点的哪个方向的路径

		return "";
	}

	public static void main(String[] args) {
		LBS lbs = new LBS();
	 
		String[] history=new String[]
		 {"s1","s2","s2","s2","s2","s2","s4","s3","s4"};                           
		String predict = "s3";
		String[] re=lbs.DoCorrect2(history, predict);
		System.out.println("最终位置:"+re[re.length-1]);
		 
	}
}
