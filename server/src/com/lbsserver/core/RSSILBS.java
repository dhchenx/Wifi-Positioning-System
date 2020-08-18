/**
 * 
 */
package com.lbsserver.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lbsserver.db.DBImp;

 

/**
 * @author TungWahChan
 *
 */
public class RSSILBS {
	// ��������Ԥ���Ŀ��λ��
	public String GetFinalTarget(String val) {
		String[] vals = val.split(";");
		String[] macs = new String[vals.length];
		double[] values = new double[vals.length];
		for (int i = 0; i < vals.length; i++) {
			String[] v = vals[i].split(",");
			macs[i] = v[0];
			values[i] = Double.valueOf(v[1]);
		}
		return this.GetLocation(this.Locate(macs, values));
	}

	public String GetLocation(List<String> locs) {
		// ����ѡλ��Ȩ�ؼ���
		// ��ø���Ψһ�ĵ�ַ
		List<String> macList = new ArrayList<String>();
		// ÿ����ַ��Ȩ��
		List<Double> level = new ArrayList<Double>();
		for (int i = 0; i < locs.size(); i++) {
			String[] temp = locs.get(i).split(";");
			for (int j = 0; j < temp.length; j++) {
				if (!macList.contains(temp[j])) {
					macList.add(temp[j]);
					level.add(20.0 / (1.5 + i));
				}
				if (macList.contains(temp[j])) {
					level.set(macList.indexOf(temp[j]),
							level.get(macList.indexOf(temp[j])) * 1.2);
				}
			}
		}
		for (int i = 0; i < macList.size(); i++) {
			System.out.println(macList.get(i) + " : " + level.get(i));
		}
		String max_room = "";
		double max_val = 0.0;
		for (int i = 0; i < macList.size(); i++) {
			if (max_val < level.get(i)) {
				max_val = level.get(i);
				max_room = macList.get(i);
			}
		}
		if (!max_room.equals(""))
			return max_room;
		else
			return null;

	}
	public List<String> CheckLoc(List<String> locs,String[] t_mac,String[] t_mac_val,double[] t_val){
		String r="";
		r = Check(t_mac, t_mac_val, t_val);
		if (r != null)
			locs.add(r);
		//System.out.println("λ��1:" + r);
		return locs;
	}
	

	// ��ȡ�������Ƶĵ�ַ
	public List<String> Locate(String[] macs, double[] vals) {
		// if (macs.length != vals.length || macs.length < 3)
		// return null;
		if(macs==null||vals==null||macs.length!=vals.length)
			return null;
		//��ȡmac��ַ�ĸ���
		int mac_count = vals.length;
		// �Ի�ȡ��mac��ַRSSIֵ���ݽ�������
		for (int i = 0; i < vals.length; i++) {
			for (int j = i + 1; j < vals.length; j++) {
				if (vals[i] < vals[j]) {
					double temp = vals[j];
					vals[j] = vals[i];
					vals[i] = temp;
					String temp1 = macs[j];
					macs[j] = macs[i];
					macs[i] = temp1;
				}
			}
		}
		//���������ĵ�ַ�洢
		List<String> locs = new ArrayList<String>();

		//�������
		String[] t_mac = null;
		String[] t_mac_val = null;
		double[] t_val = null;
		String r = "";
		//����5��MAC��ַ
		if(mac_count>=5){
			locs=CheckLoc(locs,
				new String[]{"mac1", "mac2", "mac3" ,"mac4","mac5"},
			new String[]{macs[0], macs[1], macs[2],macs[3],macs[4]},
			new double[]{ vals[0], vals[1], vals[2],vals[3],vals[4]}
			);
			locs=CheckLoc(locs,
				new String[]{"mac1", "mac2", "mac3" ,"mac4","mac5"},
				new String[]{macs[1], macs[0], macs[2],macs[3],macs[4]},
				new double[]{ vals[1], vals[0], vals[2],vals[3],vals[4]}
				);
			locs=CheckLoc(locs,
				new String[]{"mac1", "mac2", "mac3" ,"mac4","mac5"},
				new String[]{macs[0], macs[2], macs[1],macs[3],macs[4]},
				new double[]{ vals[0], vals[2], vals[1],vals[3],vals[4]}
				);
			locs=CheckLoc(locs,
				new String[]{"mac1", "mac2", "mac3" ,"mac4","mac5"},
				new String[]{macs[0], macs[1], macs[3],macs[2],macs[4]},
				new double[]{ vals[0], vals[1], vals[3],vals[2],vals[4]}
				);
			locs=CheckLoc(locs,
					new String[]{"mac1", "mac2", "mac3" ,"mac4","mac5"},
				new String[]{macs[1], macs[0], macs[3],macs[2],macs[4]},
				new double[]{ vals[1], vals[0], vals[3],vals[2],vals[4]}
				);
			locs=CheckLoc(locs,
					new String[]{"mac1", "mac2", "mac3" ,"mac4","mac5"},
				new String[]{macs[1], macs[0], macs[3],macs[4],macs[2]},
				new double[]{ vals[1], vals[0], vals[3],vals[4],vals[2]}
				);
			locs=CheckLoc(locs,
					new String[]{"mac1", "mac2", "mac3" ,"mac4","mac5"},
				new String[]{macs[0], macs[1], macs[2],macs[4],macs[3]},
				new double[]{ vals[0], vals[1], vals[2],vals[4],vals[3]}
				);
		}
		//����4��MAC��ַ
		if(mac_count>=4){
			locs=CheckLoc(locs,
				new String[]{"mac1", "mac2", "mac3" ,"mac4"},
			new String[]{macs[0], macs[1], macs[2],macs[3]},
			new double[]{ vals[0], vals[1], vals[2],vals[3]}
			);
			locs=CheckLoc(locs,
				new String[]{"mac1", "mac2", "mac3" ,"mac4"},
				new String[]{macs[1], macs[0], macs[2],macs[3]},
				new double[]{ vals[1], vals[0], vals[2],vals[3]}
				);
			
			locs=CheckLoc(locs,
				new String[]{"mac1", "mac2", "mac3" ,"mac4"},
				new String[]{macs[0], macs[2], macs[1],macs[3]},
				new double[]{ vals[0], vals[2], vals[1],vals[3]}
				);
			locs=CheckLoc(locs,
				new String[]{"mac1", "mac2", "mac3" ,"mac4"},
				new String[]{macs[0], macs[1], macs[3],macs[2]},
				new double[]{ vals[0], vals[1], vals[3],vals[2]}
				);
			locs=CheckLoc(locs,
					new String[]{"mac1", "mac2", "mac3" ,"mac4"},
				new String[]{macs[1], macs[0], macs[3],macs[2]},
				new double[]{ vals[1], vals[0], vals[3],vals[2]}
				);
		}
		//����3��MAC��ַ
		// ����1
		if (mac_count >= 3) {
			t_mac = new String[] { "mac1", "mac2", "mac3" };
			t_mac_val = new String[] { macs[0], macs[1], macs[2] };
			t_val = new double[] { vals[0], vals[1], vals[2] };
			r = Check(t_mac, t_mac_val, t_val);
			if (r != null)
				locs.add(r);
			System.out.println("λ��1:" + r);
			// ����2
			t_mac = new String[] { "mac1", "mac2", "mac3" };
			t_mac_val = new String[] { macs[1], macs[0], macs[2] };
			t_val = new double[] { vals[1], vals[0], vals[2] };
			r = Check(t_mac, t_mac_val, vals);
			if (r != null)
				locs.add(r);
			System.out.println("λ��2:" + r);
			// ����3
			t_mac = new String[] { "mac1", "mac2", "mac3" };
			t_mac_val = new String[] { macs[0], macs[2], macs[1] };
			t_val = new double[] { vals[0], vals[2], vals[1] };
			r = Check(t_mac, t_mac_val, vals);
			if (r != null)
				locs.add(r);
			System.out.println("λ��3:" + r);
		}
		if (mac_count >= 2) {
			// ����4
			t_mac = new String[] { "mac1", "mac2" };
			t_mac_val = new String[] { macs[0], macs[1] };
			t_val = new double[] { vals[0], vals[1] };
			r = Check(t_mac, t_mac_val, vals);
			if (r != null)
				locs.add(r);
			System.out.println("λ��4:" + r);
			// ����5
			t_mac = new String[] { "mac1", "mac2" };
			t_mac_val = new String[] { macs[1], macs[0] };
			t_val = new double[] { vals[1], vals[0] };
			r = Check(t_mac, t_mac_val, vals);
			if (r != null)
				locs.add(r);
			System.out.println("λ��5:" + r);
		}
		if (mac_count >= 1) {
			// ����6
			t_mac = new String[] { "mac1" };
			t_mac_val = new String[] { macs[0] };
			t_val = new double[] { vals[0] };
			r = Check(t_mac, t_mac_val, vals);
			if (r != null)
				locs.add(r);
			System.out.println("λ��6:" + r);
			
		}
		return locs;
	}

	/*
	 * t_mac,t_mac_val,vals
	 */
	public String Check(String[] t_mac, String[] t_mac_val, double[] vals) {
		// 3 MAC

		RSSIArea[] ra = this.GetRoomList(this.GetSelectStr(t_mac, t_mac_val));
		if (ra != null && ra.length > 0) {
			List<String> results = new ArrayList<String>();
			// ���ÿһ������ɸѡ���ϵĽ��
			for (int i = 0; i < ra.length; i++) {
				RSSIArea target = ra[i];
				int is_all = 0;
				// ÿһ��Mac��ƽ��ֵ������Ŀ�귿���ڲ�
				for (int j = 0; j < t_mac.length; j++) {
					if (vals[j] >= target.getVal(t_mac[j]) - 1
							* target.getStd(t_mac[j])
							&& vals[j] <= target.getVal(t_mac[j]) + 1
									* target.getStd(t_mac[j])) {
						is_all++;
					}
				}
				if (is_all == t_mac.length) {
					results.add(target.getRoomId());
				}
			}
			String r = "";
			for (int i = 0; i < results.size(); i++) {
				if (i == results.size() - 1)
					r += results.get(i);
				else
					r += results.get(i) + ";";
			}
			// System.out.println("Position:" + r);
			if (r.equals(""))
				return null;
			else
				return r;
		}
		return null;
	}

	// ƴ�Ӳ�ѯ�ַ���
	public String GetSelectStr(String[] macnames, String[] macvalues) {
		String str = "select * from wifi_room where ";
		for (int i = 0; i < macnames.length; i++) {
			if (i != macnames.length - 1) {
				str += macnames[i] + "='" + macvalues[i] + "' and ";
			} else {
				str += macnames[i] + "='" + macvalues[i] + "'";
			}
		}
		str += " ";
		System.out.println(str);
		return str;

	}

	// ����������ȡ��ص�λ���б�
	public RSSIArea[] GetRoomList(String query) {
		String dbname = "mosystem";
		String dbuser = "mosystem";
		String dbpass = "chendonghua";
		String dburl = "27.54.227.50:3306";
		DBImp dbi = new DBImp(dburl, dbname, dbuser, dbpass);
		String[][] result = dbi.getTable(query, new String[] { "roomid",
				"mac1", "val1", "mac2", "val2", "mac3", "val3", "mac4", "val4",
				"mac5", "val5", "span1", "span2", "span3", "span4", "span5" });
		if (result != null && result.length > 0) {
			RSSIArea[] r = new RSSIArea[result.length];
			for (int i = 0; i < result.length; i++) {
				r[i] = new RSSIArea(result[i]);
			}
			return r;
		}
		return null;
	}

	public class RSSIArea {
		public String[] macs;
		public String[] vals;
		public String[] stds;
		public String roomid;

		public RSSIArea(String[] r) {
			roomid = r[0];
			macs = new String[] { r[1], r[3], r[5], r[7], r[9] };
			vals = new String[] { r[2], r[4], r[6], r[8], r[10] };
			stds = new String[] { r[11], r[12], r[13], r[14], r[15] };
		}

		public String getRoomId() {
			return roomid;
		}

		public String getMac(int i) {
			return this.macs[i];
		}

		public String getMac(String key) {
			int index = 0;
			for (int i = 1; i <= macs.length; i++) {
				if (!("mac" + i).equals(key)) {
					index++;
				} else
					break;
			}
			return this.macs[index];
		}

		public double getVal(int i) {
			return Double.valueOf(this.vals[i]);
		}

		public double getVal(String key) {
			int index = 0;
			for (int i = 1; i <= macs.length; i++) {
				if (!("mac" + i).equals(key)) {
					index++;
				} else
					break;
			}
			return Double.valueOf(this.vals[index]);
		}

		public double getStd(int i) {
			return Double.valueOf(this.stds[i]);
		}

		public double getStd(String key) {
			int index = 0;
			for (int i = 1; i <= macs.length; i++) {
				if (!("mac" + i).equals(key)) {
					index++;
				} else
					break;
			}
			return Double.valueOf(this.stds[index]);
		}

	}
	//λ�ø�֪���ݱ������ݿ�
	public void saveToDB(String deviceid,String pos){
		try{
			String dbname = "mosystem";
			String dbuser = "mosystem";
			String dbpass = "chendonghua";
			String dburl = "27.54.227.50:3306";
			DBImp dbi = new DBImp(dburl, dbname, dbuser, dbpass);
			Date date = new Date();
			SimpleDateFormat from = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String addtime = from.format(date);
			dbi.execute("insert into wifitrace(deviceid,addtime,position)values('"+deviceid+"','"+addtime+"','"+pos+"');");
			dbi.Close();
			
		}catch(Exception e){
			
		}
		
	}

    /*
	public static void main(String[] args) {
		LBSTest lt = new LBSTest();
		System.out.println("����λ��:"+lt.GetLocation(lt.Locate(new String[] {
				"00:23:89:17:51:f0", "00:23:89:17:24:10","00:23:89:17:bf:50" 
				}, new double[] { 
				-56.56, -58.5, -77.21 
				})));
	}
    */

}
