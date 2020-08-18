package com.lbsserver.map;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.*;

import com.lbsserver.db.DBImp;
import com.lbsserver.utils.tools;

public class Test1 {
	public static List<String> vallist1 = null;
	public static List<String> vallist2 = null;

	// 求标准差
	public static double GetStd(List<String> list) {
		double total = 0.0f;
		for (int i = 0; i < list.size(); i++)
			total += Double.valueOf(list.get(i));
		double avg = total / list.size();
		double sqr = 0.0;
		for (int i = 0; i < list.size(); i++) {
			sqr += (Double.valueOf(list.get(i)) - avg)
					* (Double.valueOf(list.get(i)) - avg);
		}
		sqr = sqr * 1.0 / list.size();
		return Math.sqrt(sqr);
	}

	public static double[] GetStdArray(List<List<String>> list) {
		double[] d = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			d[i] = GetStd(list.get(i));
		}
		return d;
	}

	public static String[] getData(String remark) {
		String dbname = "mosystem";
		String dbuser = "mosystem";
		String dbpass = "chendonghua";
		String dburl = "27.54.227.50:3306";
		DBImp db = new DBImp(dburl, dbname, dbuser, dbpass);

		String sql = "SELECT m1,v1,m2,v2,m3,v3,m4,v4,m5,v5 FROM wifimap where remark='"
				+ remark + "' limit 10";
		;
		String[] type = new String[] { "string", "string", "string", "string",
				"string", "string", "string", "string", "string", "string" };
		String[] col = new String[] { "m1", "v1", "m2", "v2", "m3", "v3", "m4",
				"v4", "m5", "v5" };
		String[][] t = db.BinarySelect(sql, type, col);
		List<String> vallist1 = new ArrayList<String>();
		for (int i = 0; i < t.length; i++) {

			vallist1.add(t[i][1]);
		}
		Test1.vallist1 = vallist1;
		
		/////////////////////////////汇总数据////////////////////////////////////////////////
		List<String> maclist = new ArrayList<String>();
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < t[i].length; j++) {
				System.out.print(t[i][j] + "\t");
				if (j % 2 == 0) {
					if (t[i][j] != null
							&& t[i][j].length() == "06:25:d3:bc:64:b2".length()
							&& !maclist.contains(t[i][j])) {
						maclist.add(t[i][j]);
						// System.out.println("mac = "+t[i][j]);
					}
				}
			}
			System.out.println("");
		}
		double[] values = new double[maclist.size()];
		int[] len = new int[maclist.size()];
		for (int i = 0; i < values.length; i++) {
			values[i] = 0.0f;
			len[i] = 0;
		}

		List<List<String>> vls = new ArrayList<List<String>>();
		for (int i = 0; i < maclist.size(); i++) {
			vls.add(new ArrayList<String>());
		}
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < maclist.size(); j++) {
				for (int k = 0; k < t[i].length; k++)

					if (k % 2 == 0 && maclist.get(j).equals(t[i][k])) {
						System.out.print("find");
						values[j] += Double.valueOf(t[i][k + 1]);
						len[j] += 1;
						// 存储数据准备求标准差
						vls.get(j).add(t[i][k + 1]);
					}
			}
		}

		//
		System.out.println("===============data1=================");
		for (int i = 0; i < vls.size(); i++) {
			for (int j = 0; j < vls.get(i).size(); j++) {
				System.out.print(vls.get(i).get(j) + "\t");
			}
			System.out.print("\n");
		}

		// 求出各个AP在该地方的平均信号强度
		double[] avgs = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			avgs[i] = values[i] * 1.0 / len[i];
		}
		double[] spans = GetStdArray(vls);
		// 排序
		for (int i = 0; i < avgs.length - 1; i++) {
			for (int j = i + 1; j < avgs.length; j++) {
				if (avgs[i] < avgs[j]) {
					double d = avgs[i];
					avgs[i] = avgs[j];
					avgs[j] = d;

					d = spans[i];
					spans[i] = spans[j];
					spans[j] = d;
				}
			}

		}
		String roomid = "";

		// 将处理结果存入数据库中
		String insert = "insert into wifi_room(roomid,addtime,";
		String value = "'" + roomid + "','"
				+ tools.GetCurrentTime("yyyy-MM-dd HH:mm:ss") + "',";
		for (int i = 0; i < avgs.length && i < 5; i++) {
			if (i != 4) {
				insert += "mac" + (i + 1) + "," + "val" + (i + 1) + ",span"
						+ (i + 1) + ",";
				value += "'" + maclist.get(i) + "'," + double2(avgs[i]) + ","
						+ double2(spans[i]) + ",";
			} else {
				insert += "mac" + (i + 1) + "," + "val" + (i + 1) + ",span"
						+ (i + 1);
				value += "'" + maclist.get(i) + "'," + double2(avgs[i]) + ","
						+ double2(spans[i]);
			}

		}
		
		insert += ")values(" + value + ")";
		
		//
		//for (int i = 0; i < maclist.size(); i++) {
		//	System.out.println("mac(" + maclist.get(i) + ") = " + avgs[i]);
		//}
		
		db.execute(insert);
		///////////////////////////////////////////////////////////////////////////////////////////////
		
	

		return null;

	}

	public static String double2(double d) {
		String result = String.format("%.2f", d);
		return result;
	}

	public static String double2(String d) {
		return double2(Double.valueOf(d));
	}

	public static double[] toArray(List<String> list) {
		System.out.println("=======data=============");
		double[] d = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			d[i] = Double.valueOf(list.get(i));
			// System.out.println(d[i]);
		}
		System.out.println("=======end data==========");
		return d;
	}

	public static void main(String[] args) {
		double[] mydata = new double[] { 2, 1, 1, 2.2, 10, 3, 2, 11, 2, 3, 30,
				2, 2, 3, 4, 2, 3 };
		// System.out.println(StatUtils.mean(mydata));;
		// System.out.println(StatUtils.sum(mydata));
		// System.out.println(StatUtils.variance(mydata));
		/*
		getData("myro");
		mydata = toArray(vallist1);
		// mydata=InputCorrect(mydata);
		System.out
				.println("均值 = "
						+ InputStatUtils.getMean(InputStatUtils
								.getSmoothArray(mydata)));
								*/
		
		/*
		 * for(int i=0;i<=5;i++){ System.out.println("第"+i+"次剔除");
		 * mydata=InputCorrect(mydata); }
		 */
		CreateMapFeature("s1_test","myro",2000);
		
	}

	//
	public static double[] InputCorrect(double[] data) {
		double mean = StatUtils.mean(data);
		double std = Math.sqrt(StatUtils.variance(data));
		double min = mean - 2.5 * std;
		double max = mean + 2.5 * std;
		// System.out.println("mean = "+mean);
		// System.out.println("std = "+std);
		List<Double> list = new ArrayList<Double>();
		for (int i = 0; i < data.length; i++)
			list.add(data[i]);
		int out_count = 0;
		for (int i = 0; i < list.size(); i++) {
			// 移除超过边界的
			if (list.get(i) > max || list.get(i) < min) {
				System.out.println("remove " + list.get(i));
				list.remove(i);
				out_count++;
			}
		}
		double[] new_data = new double[list.size()];
		for (int i = 0; i < list.size(); i++)
			new_data[i] = list.get(i);
		if (out_count > 0) {
			// System.out.println("next loop");
			return InputCorrect(new_data);
		} else
			return new_data;
	}
	//汇总数据
	public static void CreateMapFeature(String roomid,String remark,int max_count){
		//创建数据库
		String dbname = "mosystem";
		String dbuser = "mosystem";
		String dbpass = "chendonghua";
		String dburl = "27.54.227.50:3306";
		DBImp db = new DBImp(dburl, dbname, dbuser, dbpass);
		//获取数据源
		String sql = "SELECT m1,v1,m2,v2,m3,v3,m4,v4,m5,v5 FROM wifimap where remark='"
				+ remark + "' order by addtime desc  limit "+max_count;
		;
		String[] type = new String[] { "string", "string", "string", "string",
				"string", "string", "string", "string", "string", "string" };
		String[] col = new String[] { "m1", "v1", "m2", "v2", "m3", "v3", "m4",
				"v4", "m5", "v5" };
		//转换成表
		String[][] t = db.BinarySelect(sql, type, col);
		List<String> vallist1 = new ArrayList<String>();
		for (int i = 0; i < t.length; i++) {

			vallist1.add(t[i][1]);
		}
		Test1.vallist1 = vallist1;
		
		/////////////////////////////汇总数据////////////////////////////////////////////////
		List<String> maclist = new ArrayList<String>();
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < t[i].length; j++) {
				System.out.print(t[i][j] + "\t");
				if (j % 2 == 0) {
					if (t[i][j] != null
							&& t[i][j].length() == "06:25:d3:bc:64:b2".length()
							&& !maclist.contains(t[i][j])) {
							maclist.add(t[i][j]);
						// System.out.println("mac = "+t[i][j]);
					}
				}
			}
			System.out.println("");
		}
		//数据平滑处理
		
		double[] values = new double[maclist.size()];
		int[] len = new int[maclist.size()];
		for (int i = 0; i < values.length; i++) {
			values[i] = 0.0f;
			len[i] = 0;
		}

		
		List<List<String>> vls = new ArrayList<List<String>>();
		for (int i = 0; i < maclist.size(); i++) {
			vls.add(new ArrayList<String>());
		}
		
		
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < maclist.size(); j++) {
				for (int k = 0; k < t[i].length; k++)

					if (k % 2 == 0 && maclist.get(j).equals(t[i][k])) {
						//System.out.print("find");
						//values[j] += Double.valueOf(t[i][k + 1]);
						//len[j] += 1;
						// 存储数据准备求标准差
						vls.get(j).add(t[i][k + 1]);
					}
			}
		}
		//================平滑处理====================
	
		for(int i=0;i<vls.size();i++){
			double[] mydata=new double[vls.get(i).size()];
			//System.out.println("mac = "+maclist.get(i));
			for(int j=0;j<vls.get(i).size();j++){
				mydata[j]=Double.valueOf(vls.get(i).get(j));
			}
			//System.out.println("平滑处理前:"+mydata.length);
			mydata=InputStatUtils.getSmoothArray(mydata);
			List<String> new_vallist=new ArrayList<String>();
			for(int j=0;j<mydata.length;j++){
				new_vallist.add(String.valueOf(mydata[j]));
			}
			//System.out.println("平滑处理后:"+new_vallist.size());
			vls.set(i, new_vallist);
		}
		//=============================================
		//求和
		 for(int i=0;i<vls.size();i++){
			 for(int j=0;j<vls.get(i).size();j++){
				 values[i]+=Double.valueOf(vls.get(i).get(j));
				 len[i]++;
			 }
		 }
		
		//-=========================================
		//System.out.println("===============data1=================");
		for (int i = 0; i < vls.size(); i++) {
			for (int j = 0; j < vls.get(i).size(); j++) {
				//System.out.print(vls.get(i).get(j) + "\t");
			}
			//System.out.print("\n");
		}

		// 求出各个AP在该地方的平均信号强度
		double[] avgs = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			avgs[i] = values[i] * 1.0 / len[i];
		}
		double[] spans = GetStdArray(vls);
		// 排序
		for (int i = 0; i < avgs.length - 1; i++) {
			for (int j = i + 1; j < avgs.length; j++) {
				if (avgs[i] < avgs[j]) {
					double d = avgs[i];
					avgs[i] = avgs[j];
					avgs[j] = d;

					d = spans[i];
					spans[i] = spans[j];
					spans[j] = d;
				}
			}

		}
	 

		// 将处理结果存入数据库中
		String insert = "insert into wifi_room(roomid,addtime,";
		String value = "'" + roomid + "','"
				+ tools.GetCurrentTime("yyyy-MM-dd HH:mm:ss") + "',";
		for (int i = 0; i < avgs.length && i < 5; i++) {
			if (i != 4) {
				insert += "mac" + (i + 1) + "," + "val" + (i + 1) + ",span"
						+ (i + 1) + ",";
				value += "'" + maclist.get(i) + "'," + double2(avgs[i]) + ","
						+ double2(spans[i]) + ",";
			} else {
				insert += "mac" + (i + 1) + "," + "val" + (i + 1) + ",span"
						+ (i + 1);
				value += "'" + maclist.get(i) + "'," + double2(avgs[i]) + ","
						+ double2(spans[i]);
			}

		}
		
		insert += ")values(" + value + ")";
		
		//
		//for (int i = 0; i < maclist.size(); i++) {
		//	System.out.println("mac(" + maclist.get(i) + ") = " + avgs[i]);
		//}
		db=new DBImp(dburl, dbname, dbuser, dbpass);
		db.execute(insert);
	}
	

}
