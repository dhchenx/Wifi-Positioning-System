package com.bluecanna.wifimapbuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * DB format: _id, isbn, title, publisher
 */

/*
 * Database operator class, to create, open, use, and close DB. *
 */
public class WifiDataCache {
	public static final String KEY_ROWID = "_id";
	private static final String TAG = "WifiDemo";
	private static final String DATABASE_NAME = "wifidemo";
	private static final String DATABASE_TABLE = "wificache";
	private static final int DATABASE_VERSION = 1;

	/*
	 * create table SQL
	 */
	private static final String DATABASE_CREATE = "create table wificache ("+getCreateStr()+");";
	public static String getCreateStr(){
		String v="_id integer primary key autoincrement,remark text,addtime text,ap text,";
		for(int i=1;i<=5;i++){
			if(i!=5){
			v+="m"+i+" text,";
			v+="v"+i+" text,";
			}else{
				v+="m"+i+" text,";
				v+="v"+i+" text";
			}
		}
		return v;
	}
	private final Context context;

	// DB assistant instance

	private DatabaseHelper DBHelper;

	// DB instance

	private SQLiteDatabase db;

	/*
	 * DBAdapter constructor
	 */
	public WifiDataCache(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	/*
	 * DB help class, it is a DB assistant class You will need to override
	 * onCreate() and onUpgrade() method.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d("create_stat_1", DATABASE_CREATE);
			db.execSQL("DROP TABLE IF EXISTS wificache");
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS wificache");
			onCreate(db);
		}
	}// end of DatabaseHelper

	/*****************************************************
	 * Below are all DBAdaptor method: create, open...
	 ****************************************************/

	/*
	 * Open DB
	 */
	public WifiDataCache open() throws SQLException {
		// get a DB through DB assistant

		db = DBHelper.getWritableDatabase();
		return this;
	}

	/*
	 * close DB
	 */
	public void close() {
		// close DB through DB assistant

		DBHelper.close();
	}
	public String getCurrentTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		return(df.format(new Date()));// new Date()为获取当前系统时间
	}
	/*
	 * Insert one title
	 */
	public long insert(String ap,String remark,String[] macs,String[] vals) {
		if(macs==null||vals==null){
			Log.e("wifidatacache", "macs null");
			return -1;
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put("ap", ap);
		initialValues.put("remark", remark);
		initialValues.put("addtime", this.getCurrentTime());
		for(int i=0;i<macs.length;i++){
			initialValues.put("m"+(i+1), macs[i]);
			initialValues.put("v"+(i+1), vals[i]);
		}
		
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	/*
	 * Delete one title
	 */
	public boolean delete(String remark,String ap) {
		return db.delete(DATABASE_TABLE, "remark" + "=" + "'"+remark+"' and "
				+"ap='"+ap+"'",
				null) > 0;
	}

	/*
	 * Query all titles
	 */
	public Cursor getAll() {
		return db.query(DATABASE_TABLE, new String[] { KEY_ROWID, "addtime","remark",
				"m1","v1","m2","v2","m3","v3","m4","v4","m5","v5" }, null, null, null, null, null);
	}

	/*
	 * Query a specified title
	 */
	public Cursor getAllByRemark(String remark,String ap) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {"m1","v1","m2","v2","m3","v3","m4","v4","m5","v5" },
				"remark"
				+ "=" +"'"+ remark+"' and ap='"+ap+"'", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	public class CResult{
		public String[] maclist=null;
		public String[] vallist=null;
	}
	public int getCount(Cursor c){
		 
		if(c!=null){
			return c.getCount();
		}else
			return 0;
	}
	
	public CResult getData(String remark,String ap){
		Cursor c=this.getAllByRemark(remark,ap);
		
		int count=this.getCount(c);
		String[][] t=new String[count][10];
		Log.d("get_count",String.valueOf(count));
		if(count>10){
			
			try
			{
				int max_count=10;
				db.beginTransaction();
				if (c.moveToFirst())
		        {
		        do{
		           for(int i=0;i<max_count;i++){
		        	 t[c.getPosition()][i]=c.getString(i);
		           }
		        } while (c.moveToNext());
		        }
				
				CResult t_r=this.CreateMapFeature(t);
				
				//
				db.delete("wificache", "remark="+"'"+remark+"'", null);
				
				db.setTransactionSuccessful();
				return t_r;
			}catch(Exception e){
				Log.d("wifidatacache","getCR Error");
				e.printStackTrace();
			}
			finally
			{
				//结束事务
				db.endTransaction();
			}
		}
		
		return null;
	}
	

	/*
	 * update a title
	 */
	/*
	public boolean updateTitle(long rowId, String isbn, String title,
			String publisher) {
		ContentValues args = new ContentValues();
		args.put(KEY_ISBN, isbn);
		args.put(KEY_TITLE, title);
		args.put(KEY_PUBLISHER, publisher);
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
	*/

	//陈东华(192.168.107.184) (2012/12/27 15:26:00)
	public CResult CreateMapFeature(String[][] t){
			
			/////////////////////////////汇总数据////////////////////////////////////////////////
			List<String> maclist = new ArrayList<String>();
			for (int i = 0; i < t.length; i++) {
				for (int j = 0; j < t[i].length; j++) {
					//System.out.print(t[i][j] + "\t");
					if (j % 2 == 0) {
						if (t[i][j] != null
								&& t[i][j].length() == "06:25:d3:bc:64:b2".length()
								&& !maclist.contains(t[i][j])) {
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
				mydata=this.getSmoothArray(mydata);
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
						
						String mac=maclist.get(i);
						maclist.set(i, maclist.get(j));
						maclist.set(j, mac);
								
					}
				}

			}
			String[] new_mac=null;
			String[] new_val=null;
			if(maclist.size()>5){
				new_mac=new String[5];
				new_val=new String[5];
			}else{
				new_mac=new String[maclist.size()];
				new_val=new String[avgs.length];
			}
			for(int i=0;i<new_mac.length;i++){
				new_mac[i]=maclist.get(i);
				new_val[i]=String.valueOf(avgs[i]);
				Log.d("avg_data", new_mac[i]+"="+new_val[i]);
			}
			CResult cr=new CResult();
			cr.maclist=new_mac;
			cr.vallist=new_val;
			
			return cr;
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
	
	public  double getAvg(double[] data){
		double s=0;
		for(int i=0;i<data.length;i++)
			s+=data[i];
		return s/data.length;
	} 
	public double getStd(double[] data){
		double var=0;
		double avg=this.getAvg(data);
		for(int i=0;i<data.length;i++){
			var+=(data[i]-avg)*(data[i]-avg);
		}
		return Math.sqrt(var/data.length);
	}
	
	public double[] getSmoothArray(double[] data){
		 
		double mean=this.getAvg(data);
		double std=this.getStd(data);
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
			//移除超过边界的
			if(list.get(i)>max||list.get(i)<min){
				//System.out.println("remove "+list.get(i));	
				Log.d("outliers","remove "+list.get(i));
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
