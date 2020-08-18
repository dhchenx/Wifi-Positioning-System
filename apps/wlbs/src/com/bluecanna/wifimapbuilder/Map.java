/**
 * 
 */
package com.bluecanna.wifimapbuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;
//


/**
 * @author TungWahChan
 * 
 */

public class Map {
	public WifiDB wdb=null;
	public Map(WifiDB wdb){
		this.wdb=wdb;
	}
 
	public class MapPoint {
		public String id = "";
		public double x = 0f;
		public double y = 0f;
		public double z = 0f;
	}

	public class MapRelation {
		public String id = "";
		public String p1 = "";
		public String p2 = "";
		public double distance = 0.0f;
	}

	public class MacValue {
		public MacValue() {

		}

		public MacValue(String mac, double value) {
			this.mac = mac;
			this.rssi = value;
		}

		public MacValue(String mac, double value, double std) {
			this.mac = mac;
			this.rssi = value;
			this.std = std;
		}

		public double std = 0;
		public String mac = "";
		public double rssi = -200;
	}

	public class RSSIFeature {
		public String pid;
		public List<MacValue> mv;
	}

	public RSSIFeature getNewRSSIFeature(String pid, String[] macs,
			double[] vals, double[] stds) {
		RSSIFeature r = new RSSIFeature();
		List<MacValue> mv = new ArrayList<MacValue>();
		for (int i = 0; i < macs.length; i++) {
			MacValue m = new MacValue(macs[i], vals[i], stds[i]);
			mv.add(m);
		}
		r.pid = pid;
		r.mv = mv;
		return r;
	}

	public MapPoint getNewPoint(String id, double x, double y, double z) {
		MapPoint p1 = new MapPoint();
		p1.id = id;
		p1.x = x;
		p1.y = y;
		p1.z = z;
		return p1;
	}

	public MapRelation getNewMapRelation(String id, String p1, String p2,
			double d) {
		MapRelation mr = new MapRelation();
		mr.id = id;
		mr.p1 = p1;
		mr.p2 = p2;
		mr.distance = d;
		return mr;
	}
/*
	private DBImp getDB() {
		String dbname =Config.dbname;
		String dbuser = Config.dbuser;
		String dbpass =Config.dbpass;
		String dburl = Config.dburl;
		DBImp db = new DBImp(dburl, dbname, dbuser, dbpass);
		return db;
	}
*/
	private int insertPoint(String areaid,String saveid, double x, double y, double z,
			String id) { 
		int r = wdb.execute("insert into "+Config.wifilbs_site+"(nodeid,x,y,z,areaid,saveid)values('"
				+ id + "'," + double2(x) + "," + double2(y) + "," + double2(z) + ",'" + areaid + "','"+saveid+"');");
		return r;
	}

	private int insertRelation(String id, String areaid,String saveid, String p1, String p2,
			double distance) {
	
		int r = wdb
				.execute("insert into "+Config.wifilbs_rel+"(id,nodeid,leafid,areaid,distance,saveid)values('"
						+ id
						+ "','"
						+ p1
						+ "','"
						+ p2
						+ "','"
						+ areaid
						+ "',"
						+ double2(distance) + ",'"+saveid+"');");
		return r;
	}
	   private String GetCurrentTime(String format){
	      	Date date = new Date();     	
	      	SimpleDateFormat from = new SimpleDateFormat( format );
	      	String times = from.format(date); 
	      	
	        return times;
	      } 
	private String GetSelectStr(String areaid,String saveid, String roomid, String[] maclist,
			String[] avgs, String[] spans) {
		String insert = "insert into "+Config.wifilbs_rssi+"(roomid,areaid,addtime,saveid,";
		String value = "'" + roomid + "','" + areaid + "','"
				+ GetCurrentTime("yyyy-MM-dd HH:mm:ss") + "','"+saveid+"',";
		for (int i = 0; i < avgs.length && i < 5; i++) {
			if (i != avgs.length - 1 && i != 4) {
				insert += "mac" + (i + 1) + "," + "val" + (i + 1) + ",span"
						+ (i + 1) + ",";
				value += "'" + maclist[i] + "'," + avgs[i] + "," + spans[i]
						+ ",";
			} else {
				insert += "mac" + (i + 1) + "," + "val" + (i + 1) + ",span"
						+ (i + 1);
				value += "'" + maclist[i] + "'," + avgs[i] + "," + spans[i];
			}

		}
		insert += ")values(" + value + ")";
		return insert;

	}
	
	private int insertRSSI(String areaid,String saveid, String pid, String[] m, double[] v,
			double[] std) {
		try{
		String[] vv = new String[v.length];
		String[] ss = new String[std.length];
		for (int i = 0; i < m.length; i++) {
			vv[i] = String.valueOf(double2(v[i]));
			ss[i] = String.valueOf(double2(std[i]));
		}
		String insert = this.GetSelectStr(areaid,saveid, pid, m, vv, ss);
		Log.d("insert",insert);
		int r = wdb.execute(insert);
		Log.d("insert_result",String.valueOf(r));
		return r;
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}

	}

	private int deleteArea(String areaid,String saveid) {
		
		int r = wdb.execute("delete from "+Config.wifilbs_rel+" where areaid='" + areaid + "' and saveid='"+saveid+"'");
		 
		r = wdb.execute("delete from "+Config.wifilbs_site+" where areaid='" + areaid + "' and saveid='"+saveid+"'");
	 
		r = wdb.execute("delete from "+Config.wifilbs_rssi+" where areaid='" + areaid + "' and saveid='"+saveid+"'");
		 
		return r;
	}

	private String[] getMacArray(List<MacValue> mv) {
		String[] m = new String[mv.size()];
		for (int i = 0; i < mv.size(); i++) {
			m[i] = mv.get(i).mac;
		}
		return m;
	}

	private double[] getValueArray(List<MacValue> mv) {
		double[] m = new double[mv.size()];
		for (int i = 0; i < mv.size(); i++) {
			m[i] = mv.get(i).rssi;
		}
		return m;
	}

	private double[] getStdArray(List<MacValue> mv) {
		double[] m = new double[mv.size()];
		for (int i = 0; i < mv.size(); i++) {
			m[i] = mv.get(i).std;
		}
		return m;
	}

	public void CreateMap(String areaid,String saveid, List<MapPoint> points,
			List<MapRelation> relations, List<RSSIFeature> features) {
		if (points != null) {
			for (int i = 0; i < points.size(); i++) {
				insertPoint(areaid, saveid,points.get(i).x, points.get(i).y,
						points.get(i).z, points.get(i).id);
			}
			for (int i = 0; i < relations.size(); i++) {
				insertRelation(relations.get(i).id, areaid,saveid,
						relations.get(i).p1, relations.get(i).p2,
						relations.get(i).distance);
			}
			for (int i = 0; i < features.size(); i++) {
				this.insertRSSI(areaid, saveid,features.get(i).pid,
						this.getMacArray(features.get(i).mv),
						this.getValueArray(features.get(i).mv),
						this.getStdArray(features.get(i).mv));

			}
		}
	}

	public void CreateMap(String areaid,String saveid, List<MapPoint> points,
			List<MapRelation> relations) {
		if (points != null) {
			for (int i = 0; i < points.size(); i++) {
				insertPoint(areaid,saveid, points.get(i).x, points.get(i).y,
						points.get(i).z, points.get(i).id);
			}
			for (int i = 0; i < relations.size(); i++) {
				insertRelation(relations.get(i).id, areaid,saveid,
						relations.get(i).p1, relations.get(i).p2,
						relations.get(i).distance);
			}

		}
	}

	public int DeleteMap(String areaid,String saveid) {
		return deleteArea(areaid,saveid);
	}
public int DeleteAllMap(){
	int r = wdb.execute("delete from "+Config.wifilbs_rel+" ");
	 
	r = wdb.execute("delete from "+Config.wifilbs_site+" ");
 
	r = wdb.execute("delete from "+Config.wifilbs_rssi+" ");
	 
	return r;
}
	// ///
	public double getDistance(MapPoint p1, MapPoint p2) {
		return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
				* (p1.y - p2.y) + (p1.z - p2.z) * (p1.z - p2.z));
	}

	public double getK(MapPoint p1, MapPoint p2) {
		return (p2.y - p1.y) / (p2.x - p1.x + 0.00001);
	}

	public double getA(MapPoint p1, MapPoint p2) {
		double k = this.getK(p1, p2);
		return -k * p1.x + p1.y;
	}

	public class RenderResult {
		public List<MapPoint> points;
		public List<MapRelation> relations;
		public List<RSSIFeature> features;
	}

	public List<MapPoint> toMapPointList(String[] id, double[] x, double[] y,
			double[] z) {
		List<MapPoint> points = new ArrayList<MapPoint>();
		for (int i = 0; i < x.length; i++) {
			MapPoint mapPoint = new MapPoint();
			mapPoint.id = id[i];
			mapPoint.x = x[i];
			mapPoint.y = y[i];
			mapPoint.z = z[i];
			points.add(mapPoint);
		}
		return points;
	}

	public List<MacValue> toMacValueList(String[] macs, double[] val) {
		List<MacValue> mv = new ArrayList<MacValue>();
		for (int i = 0; i < macs.length; i++) {
			MacValue m = new MacValue();
			m.mac = macs[i];
			m.rssi = val[i];
			mv.add(m);
		}
		return mv;

	}

	public List<RSSIFeature> toRSSIFeatureList(String[] p,
			List<List<MacValue>> mv) {
		List<RSSIFeature> rl = new ArrayList<RSSIFeature>();
		for (int i = 0; i < p.length; i++) {
			RSSIFeature r = new RSSIFeature();
			r.pid = p[i];
			r.mv = mv.get(i);
			rl.add(r);
		}
		return rl;
	}

	public List<MapRelation> toMapRelationList(String[] id, String[] p1,
			String[] p2, double[] d) {
		List<MapRelation> mr = new ArrayList<MapRelation>();
		for (int i = 0; i < id.length; i++) {
			MapRelation m = new MapRelation();
			m.id = id[i];
			m.p1 = p1[i];
			m.p2 = p2[i];
			m.distance = d[i];
			mr.add(m);
		}
		return mr;
	}

	public List<MacValue> combineMV(RSSIFeature rf1, RSSIFeature rf2, int d) {
		return null;
	}
	public double getDifference(List<MacValue> m1, List<MacValue> m2, String mac) {
		double d1 = -100;
		double d2 = -100;
		for (int i = 0; i < m1.size(); i++)
			if (m1.get(i).mac.equals(mac))
				d1 = m1.get(i).rssi;
		for (int i = 0; i < m2.size(); i++) {
			if (m2.get(i).mac.equals(mac)) {
				d2 = m2.get(i).rssi;
			}
		}
		return d2 - d1;
	}

	public List<MacValue> combineMV(List<MacValue> m1, List<MacValue> m2) {
		List<String> maclist = new ArrayList<String>();
		List<Double> vallist = new ArrayList<Double>();
		for (int i = 0; i < m1.size(); i++) {
			if (!maclist.contains(m1.get(i).mac)) {
				maclist.add(m1.get(i).mac);
				vallist.add(m1.get(i).rssi);
			}
		}
		for (int i = 0; i < m2.size(); i++) {
			if (!maclist.contains(m2.get(i).mac)) {
				maclist.add(m2.get(i).mac);
				vallist.add(m2.get(i).rssi);
			} else {
				vallist.set(i, (vallist.get(i) + m2.get(i).rssi) / 2);
			}
		}
		List<MacValue> result = new ArrayList<MacValue>();
		for (int i = 0; i < maclist.size(); i++) {
			result.add(new MacValue(maclist.get(i), vallist.get(i)));
		}
		return result;
	}

	public void CreateMapFromRenderResult(String areaid,String saveid, RenderResult rr) {
		List<MapPoint> points = rr.points;
		List<MapRelation> relations = rr.relations;
		List<RSSIFeature> features = rr.features;
		this.CreateMap(areaid, saveid,points, relations, features);
	}
	public String[][] getData(String areaid,String remark){
		 /*
		DBImp db = new DBImp(Config.dburl, Config.dbname, Config.dbuser, Config.dbpass);
		//获取数据源
		String sql = "SELECT m1,v1,m2,v2,m3,v3,m4,v4,m5,v5 FROM "+Config.wifilbs_collect+" where remark='"
				+ remark + "' and id='"+areaid+"' order by addtime asc ";
		;
		String[] type = new String[] { "string", "string", "string", "string",
				"string", "string", "string", "string", "string", "string" };
		String[] col = new String[] { "m1", "v1", "m2", "v2", "m3", "v3", "m4",
				"v4", "m5", "v5" };
		//转换成表
		String[][] t = db.BinarySelect(sql, type, col);
		db.Close();
		 */
		String[][] t=null;
		return t;
	}
	
	public List<String> getRemarks(String areaid){
		/*
		DBImp db = new DBImp(Config.dburl, Config.dbname, Config.dbuser, Config.dbpass);
		//获取数据源
		String sql = "SELECT remark FROM "+Config.wifilbs_collect+" where id='"+areaid+"' order by addtime asc ";
		;
		String[] type = new String[] { "string"};
		String[] col = new String[] { "remark"};
		//转换成表
		String[][] t = db.BinarySelect(sql, type, col);
		db.Close();
		if(t!=null){
		  List<String> list=new ArrayList<String>();
		  for(int i=0;i<t.length;i++){
			  if(!list.contains(t[i][0])){
				  list.add(t[i][0]);
			  }
		  }
		  return list;
		}
		*/
		return null;
	}
	//汇总数据
	public  RSSIFeature CreateRSSIFeature(String[][] t,String remark){
	
		/////////////////////////////汇总数据////////////////////////////////////////////////
		List<String> maclist = new ArrayList<String>();
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < t[i].length; j++) {
				//System.out.print(t[i][j] + "\t");
				if (j % 2 == 0) {
					if (t[i][j] != null
							&& t[i][j].length() == "06:25:d3:bc:64:b2".length()
							&& !maclist.contains(t[i][j])) {
						if(t[i][j]==null){
							Log.d("test","there is a null of t[i][j]");
						}
							maclist.add(t[i][j]);
						// System.out.println("mac = "+t[i][j]);
					}
				}
			}
			//System.out.println("");
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
				for (int k = 0; k < t[i].length-1; k++)

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
			
			//mydata=InputStatUtils.getSmoothArray(mydata);
			
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
	String[] mlist=new String[maclist.size()];
	for(int i=0;i<maclist.size();i++){
		mlist[i]=maclist.get(i);
	}
		RSSIFeature r=this.getNewRSSIFeature(remark, mlist, avgs, spans);
		return r;
	 
	}
	public static String double2(double d) {
		String result = String.format("%.2f", d);
		return result;
	}
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
	public static String double2(String d) {
		return double2(Double.valueOf(d));
	}
	public void printArray(String[][] t){
		for(int i=0;i<t.length;i++){
			String s="";
			for(int j=0;j<t[i].length;j++){
			s+=t[i][j]+",";
			}
			Log.d("t_data",s);
		}
	}
	public void CreateMapFromDB(String areaid,String saveid){
			List<String> rs=wdb.getRemarks(areaid);
			for(int i=0;i<rs.size();i++){
				Log.d("remark",rs.get(i));
			}
			List<MapPoint> plist=new ArrayList<MapPoint>();
			List<RSSIFeature> rlist=new ArrayList<RSSIFeature>();
			if(rs!=null){
				for(int i=0;i<rs.size();i++){
					String[][] t=wdb.getRoomData(areaid,rs.get(i));
					printArray(t);
					 
					Log.d("wdb.getroomdata",String.valueOf(t.length));
					RSSIFeature r=this.CreateRSSIFeature(t, rs.get(i));
					String str="";
					for(int k=0;k<r.mv.size();k++){
						str+=r.mv.get(k).mac+"="+r.mv.get(k).rssi+"("+r.mv.get(k).std+")";
					}
					Log.d("mv",str);
					Log.d("RSSIFeature",r.pid+r.mv.size());
					String[][] cd=wdb.getCord(areaid, r.pid);
					double x=0,y=0,z=0;
					if(cd!=null&&cd.length>=1){
						x=Double.valueOf(cd[0][0]);
						y=Double.valueOf(cd[0][1]);
						z=Double.valueOf(cd[0][2]);
					}
					MapPoint p=this.getNewPoint(r.pid, x, y, z);
					plist.add(p);
					rlist.add(r);
				}
			}
			Log.d("test","end loop");
			Log.d("plist,rlist",String.valueOf(plist.size()+","+rlist.size()));
			this.CreateMapFromRenderResult(areaid,saveid, this.RenderLineAsMap(plist, rlist, 1));
			wdb.execute("delete from wifilbs_collect where areaid='"+areaid+"'");
	}
	
	public void CreateMapFromMetaData(String areaid,String saveid, String[] p, double[] x,
			double[] y, double[] z, String[][] macs, double[][] vals,
			double[][] std, String[] r_id, String[] p1, String[] p2, double[] d) {
		List<MapPoint> points = new ArrayList<MapPoint>();
		List<RSSIFeature> rs = new ArrayList<RSSIFeature>();
		for (int i = 0; i < p.length; i++) {
			points.add(this.getNewPoint(p[i], x[i], y[i], z[i]));
			rs.add(this.getNewRSSIFeature(p[i], macs[i], vals[i], std[i]));
		}
		List<MapRelation> mr = new ArrayList<MapRelation>();
		for (int i = 0; i < r_id.length; i++) {
			mr.add(this.getNewMapRelation(r_id[i], p1[i], p2[i], d[i]));
		}
		this.CreateMap(saveid,areaid, points, mr, rs);
	}
	
	// 将折线轨迹的若干个取样点渲染成连续的RSSI路径图，涉及到点之间插值和渲染
	public RenderResult RenderLineAsMap(List<MapPoint> plist,
			List<RSSIFeature> rf, double dd) {
		
		
		Log.d("in render","");
		
		for(int i=0;i<rf.size();i++){
			String s="";
			for(int j=0;j<rf.get(i).mv.size();j++){
				MacValue m=rf.get(i).mv.get(j);
				s+=m.mac+"="+m.rssi+"("+m.std+")";
				}
			Log.d("before",s);
		}
		
		if (plist.size() < 2)
			return null;
		Log.d("in render",String.valueOf(rf.size()));
		List<MapPoint> mp = new ArrayList<MapPoint>();
		List<MapRelation> mr = new ArrayList<MapRelation>();
		List<RSSIFeature> ms = new ArrayList<RSSIFeature>();
		// 便利若干点
		mp.add(plist.get(0));
		ms.add(rf.get(0));
		for (int i = 1; i < plist.size(); i++) {

			MapPoint p1 = plist.get(i - 1);
			MapPoint p2 = plist.get(i);
			RSSIFeature rf1 = rf.get(i - 1);
			RSSIFeature rf2 = rf.get(i);

			MapRelation mr2 = new MapRelation();
			// 分成D段
			double d = dd;
			for (int j = 1; j <= d; j++) {

				// 生成点
				MapPoint temp = new MapPoint();
				temp.id = p1.id + "_" + j;
				temp.x = (p2.x - p1.x) / d * j + p1.x;
				temp.y = (p2.y - p1.y) / d * j + p1.y;
				temp.z = (p2.z - p1.z) / d * j + p1.z;
				
				// 生成与上一点的关系
				if (i == 1 && j == 1) {
					MapRelation m1 = new MapRelation();
					m1.id = p1.id + "_" + j + "_" + 1;
					m1.p1 = p1.id;
					m1.p2 = p1.id + "_" + j;
					m1.distance = this.getDistance(p1, temp);
				}
				MapRelation temp_r = new MapRelation();
				temp_r.id = mp.get(mp.size() - 1).id + "-" + temp.id;
				temp_r.p1 = mp.get(mp.size() - 1).id;
				temp_r.p2 = temp.id;
				temp_r.distance = this.getDistance(mp.get(mp.size() - 1), temp);

				// 生成RSSI特征值
				RSSIFeature temp_s = new RSSIFeature();
				temp_s.pid = temp_r.id;
				// 获取前一点的mac值-对
				if (j == 1) {
					RSSIFeature rf_last = rf1;
					List<MacValue> temp_mv = new ArrayList<MacValue>();
					for (int k = 0; k < rf_last.mv.size(); k++) {
						temp_mv.add(new MacValue(rf_last.mv.get(k).mac, this
								.getDifference(rf1.mv, rf2.mv,
										rf_last.mv.get(k).mac)
								/ d * j + rf1.mv.get(k).rssi));
					}
					temp_s.mv = temp_mv;

				} else {
					RSSIFeature rf_last = ms.get(ms.size() - 1);
					List<MacValue> temp_mv = new ArrayList<MacValue>();
					for (int k = 0; k < rf_last.mv.size(); k++) {
						temp_mv.add(new MacValue(rf_last.mv.get(k).mac, this
								.getDifference(rf1.mv, rf2.mv,
										rf_last.mv.get(k).mac)
								/ d * j + rf1.mv.get(k).rssi));
					}
					temp_s.mv = temp_mv;
				}

				if (j == d) {
					MapRelation t = new MapRelation();
					t.id = mp.get(mp.size() - 1).id + "_" + p2.id;
					t.p1 = mp.get(mp.size() - 1).id;
					t.p2 = p2.id;
					t.distance = this.getDistance(mp.get(mp.size() - 1), p2);
					mr.add(t);
				} else {
					mp.add(temp);
					mr.add(temp_r);
					ms.add(temp_s);
				}
			}

			mp.add(p2);
			ms.add(rf2);
		}
		RenderResult rr = new RenderResult();
		rr.points = mp;
		rr.features = ms;
		rr.relations = mr;
		for(int i=0;i<rr.points.size();i++){
			Log.d("points",rr.points.get(i).id);
			String s="";
			
			for(int j=0;j<rr.features.get(i).mv.size();j++)
			{
				 MacValue m=rr.features.get(i).mv.get(j);
				 s+=m.mac+"="+m.rssi+"("+m.std+")";
			}
			Log.d("feature",s);
	//Log.d("relations",
		//	rr.relations.get(i).id+","+rr.relations.get(i).p1+","+rr.relations.get(i).p2);
		
		}
		
		return rr;
	}

}
