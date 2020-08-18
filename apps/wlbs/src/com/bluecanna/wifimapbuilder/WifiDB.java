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

public class WifiDB {
	
	/*
	 * DB help class, it is a DB assistant class You will need to override
	 * onCreate() and onUpgrade() method.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context,"wifilbs", null, 1);	
		}
		private static final String DATABASE_CREATE_wifilbs_rel = 
			"create table wifilbs_rel(nodeid text,leafid text, distance text, areaid text,id text,saveid text);";
		private static final String DATABASE_CREATE_wifilbs_site=
			"create table wifilbs_site(areaid text,nodeid text, x text, y text, z text,saveid text,remark text);";
		private static final String DATABASE_CREATE_wifilbs_rssi=
			"create table wifilbs_rssi(roomid text,mac1 text,val1 text,mac2 text,val2 text,"
			+"mac3 text,val3 text,"
			+"mac4 text,val4 text,"
			+"mac5 text, val5 text,"
			+"span1 text,span2 text,span3 text,span4 text,span5 text,addtime TEXT,areaid text,saveid text);";
		private static final String DATABASE_CREATE_wifilbs_collect=
			"create table wifilbs_collect(areaid text,roomid text,m1 text,v1 text,m2 text,v2 text,m3 text,v3 text,m4 text,v4 text,m5 text,v5 text,x text,y text, z text);";
		private static final String DATABASE_CREATE_wifilbs_bgs=
			"create table wifilbs_bgs(saveid text,bgid integer);";
		private static final String DATABASE_CREATE_wifilbs_stat=
				"create table wifilbs_stat(addtime datetime,params text);";
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_wifilbs_rel);
			db.execSQL(DATABASE_CREATE_wifilbs_rssi);
			db.execSQL(DATABASE_CREATE_wifilbs_site);
			db.execSQL(DATABASE_CREATE_wifilbs_collect);
			db.execSQL(DATABASE_CREATE_wifilbs_bgs);
			db.execSQL(DATABASE_CREATE_wifilbs_stat);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists wifilbs_rel");
			db.execSQL("drop table if exists wifilbs_site");
			db.execSQL("drop table if exists wifilbs_rssi");
			db.execSQL("drop table if exists wifilbs_collect");
			db.execSQL("drop table if exists wifilbs_bgs");
			db.execSQL("drop table if exists wifilbs_stat");
			onCreate(db);
		}
		
	}// end of DatabaseHelper

	// DB assistant instance

	private DatabaseHelper DBHelper;

	// DB instance

	private SQLiteDatabase db;

	/*
	 * DBAdapter constructor
	 */
	public Context context=null;
	public WifiDB(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	public WifiDB open() throws SQLException {
		// get a DB through DB assistant

		db = DBHelper.getWritableDatabase();
		return this;
	}
	public String getCurrentTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		return(df.format(new Date()));// new Date()为获取当前系统时间
	}
	public String getTimeStamp(){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		return(df.format(new Date()));// new Date()为获取当前系统时间
	}
	public int execute(String sql){
		this.open();
		 db.execSQL(sql);
		 this.Close();
		 return 0;
	}
	public String[][] BinarySelect(String sql,String[] cols){
		this.open();
		
		return null;
	}
	public int updateBgId(String saveid,String bgid){
		this.open();
		 db.execSQL("delete from wifilbs_bgs where saveid='"+saveid+"'");
		  db.execSQL("insert into wifilbs_bgs(saveid,bgid)values('"+saveid+"','"+bgid+"');");
		this.Close();
		  return 0; 
	}
	public String getBgId(String saveid){
		String[][] t=this.getTable("wifilbs_bgs", new String[]{"bgid"}, "saveid='"+saveid+"'");
		if(t!=null){
			return (t[0][0]);
		}
		return "-1";
	}
	
	public Cursor getData(String table,String[] cols,String condition){
		Cursor mCursor = db.query(true,table, cols,
				condition, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	public long addStatParam(String s){
		this.open();
		ContentValues initialValues = new ContentValues();
		initialValues.put("addtime",Tools.getTime());
		initialValues.put("params", s);
		 long r= db.insert("wifilbs_stat", null, initialValues);
		 this.Close();
		 return r;
	}
	
	public String[] getStatParams(){
		String[][] t=this.getTable("wifilbs_stat", new String[]{"params"}, null);
		if(t!=null){
			if(t.length<1)return null;
			try{
			
			String[] tt=new String[t.length];
			for(int i=0;i<tt.length;i++){
				tt[i]=t[i][0];
			}
		   this.clearTable("wifilbs_stat");
			return tt;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
		
	}
	public void clearTable(String s){
		this.open();
		db.execSQL("delete from "+s+"");
		db.close();
		
	}
	
	public int getCount(Cursor c){
		if(c!=null){
			return c.getCount();
		}else
			return 0;
	}
	public long insertCollectRSSI(double x,double y,double z,String key,String remark,String[] macs,String[] vals,String[] stds) {
		this.open();
		if(macs==null||vals==null){
			Log.e("wifilbs_collect", "macs null");
			return -1;
		}
		
		ContentValues initialValues = new ContentValues();
		initialValues.put("areaid", key);
		initialValues.put("roomid", remark);
		initialValues.put("x", x);
		initialValues.put("y", y);
		initialValues.put("z", z);
		for(int i=0;i<macs.length;i++){
			initialValues.put("m"+(i+1), macs[i]);
			initialValues.put("v"+(i+1), vals[i]);
			System.out.println(macs[i]+","+vals[i]);
		}
		 long r= db.insert("wifilbs_collect", null, initialValues);
		 this.Close();
		 return r;  
	}
	public String[][] getCord(String areaid,String roomid){
		String[][] t= this.getCollectRSSIData(new String[]{"x","y","z"}, "areaid='"+areaid+"' and roomid='"+roomid+"'");
		return t;
	}
	public String[][] getCord(String areaid,String roomid,String saveid){
		String[][] t= this.getCollectRSSIData(new String[]{"x","y","z"}, "areaid='"+areaid+"' and roomid='"+roomid+"' and saveid='"+saveid+"'");
		return t;
	}
	public List<String> getRemarks(String areaid){
		String[][] t= this.getCollectRSSIData(new String[]{"roomid"}, "areaid='"+areaid+"'");
		List<String> list=new ArrayList<String>();
		if(t!=null)
		for(int i=0;i<t.length;i++){
			list.add(t[i][0]);
		}
		return list;
	}
	public List<String> getRemarks(String areaid,String saveid){
		String[][] t= this.getCollectRSSIData(new String[]{"roomid"}, "areaid='"+areaid+"' and saveid='"+saveid+"'");
		List<String> list=new ArrayList<String>();
		if(t!=null)
		for(int i=0;i<t.length;i++){
			list.add(t[i][0]);
		}
		return list;
	}
	
	public String[][] toT(String[][] t){
		if(t==null)return null;
		String[][] tt=new String[t.length][];
		for(int i=0;i<t.length;i++){
			List<String> s=new ArrayList<String>();
			for(int j=0;j<t[i].length;j++){
				if(t[i][j]!=null&&!t[i][j].equalsIgnoreCase("null")&&!t[i][j].equals("")){
					s.add(t[i][j]);
				}
			}
			String[] t1=new String[s.size()];
			for(int j=0;j<t1.length;j++){
				t1[j]=s.get(j);
			}
			tt[i]=t1;
		}
		return tt;
	}
	public String[][] getRoomData(String areaid,String remark){
		String[][] t= this.getCollectRSSIData(new String[]{"m1","v1","m2","v2","m3","v3","m4","v4","m5","v5"}, "areaid='"+areaid+"' and roomid='"+remark+"'");
		
		
		return toT(t);
	}
	public String[][] getRoomData(String areaid,String remark,String saveid){
		String[][] t= this.getCollectRSSIData(new String[]{"m1","v1","m2","v2","m3","v3","m4","v4","m5","v5"}, "areaid='"+areaid+"' and roomid='"+remark+"' and saveid='"+saveid+"' ");
		
		
		
		return toT(t);
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
	public String[][] getCollectRSSIData(String[] cols,String condition){
		this.open();
		Cursor c=this.getData("wifilbs_collect", cols, condition);
		int count=this.getCount(c);
		if(c!=null)
			c.moveToFirst();
		System.out.println("find cols count = "+c.getColumnCount());
		System.out.println(cols.length);
		String[][] t=new String[count][cols.length];
			try
			{
				db.beginTransaction();
			
				if (c.moveToFirst())
		        {
					 
		        do{
		        	 
		        	 for(int i=0;i<cols.length;i++){
			        	 t[c.getPosition()][i]=c.getString(i);
			           }
		        	 
		        } while (c.moveToNext());
		        }
				Log.d("wifidb","check t");
				printArray(toT(t));
				return toT(t);
			}catch(Exception e){
				Log.d("wifilbs_collect","getCR Error");
				e.printStackTrace();
			}
			finally
			{
				//结束事务
				db.endTransaction();
				this.Close();
			}
		return null;
	}
	public long insertRSSI(String key,String remark,String[] macs,String[] vals,String[] stds) {
		this.open();
		if(macs==null||vals==null){
			Log.e("wifilbs_rssi", "macs null");
			return -1;
		}
		try{
		db.delete("wifilbs_rssi", "areaid='"+key+"' and roomid='"+remark+"'", null);
		}catch(Exception e){
			e.printStackTrace();
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put("areaid", key);
		initialValues.put("roomid", remark);
		initialValues.put("addtime", this.getCurrentTime());
		for(int i=0;i<macs.length;i++){
			initialValues.put("mac"+(i+1), macs[i]);
			initialValues.put("val"+(i+1), vals[i]);
			initialValues.put("span"+(i+1),stds[i]);
		}
		
		 long r= db.insert("wifilbs_rssi", null, initialValues);
		 this.Close();
		 return r;  
	}
	public long insertRSSI(String key,String remark,String saveid,String[] macs,String[] vals,String[] stds) {
		this.open();
		if(macs==null||vals==null){
			Log.e("wifilbs_rssi", "macs null");
			return -1;
		}
		try{
		db.delete("wifilbs_rssi", "areaid='"+key+"' and roomid='"+remark+"' and saveid='"+saveid+"'", null);
		}catch(Exception e){
			e.printStackTrace();
		}
		ContentValues initialValues = new ContentValues();
		initialValues.put("areaid", key);
		initialValues.put("roomid", remark);
		initialValues.put("addtime", this.getCurrentTime());
		initialValues.put("saveid", saveid);
		for(int i=0;i<macs.length;i++){
			initialValues.put("mac"+(i+1), macs[i]);
			initialValues.put("val"+(i+1), vals[i]);
			initialValues.put("span"+(i+1),stds[i]);
		}
		
		 long r= db.insert("wifilbs_rssi", null, initialValues);
		 this.Close();
		 return r;  
	}
	public String[][] getRSSIData(String sql,String[] cols,String condition){
		this.open();
		Cursor c=this.getData("wifilbs_rssi", cols, condition);
		int count=this.getCount(c);
		if(c!=null)
			c.moveToFirst();
		System.out.println("find count = "+count);
		String[][] t=new String[count][cols.length];
			try
			{
				db.beginTransaction();
				if (c.moveToFirst())
		        {
		        do{
		           for(int i=0;i<cols.length;i++){
		        	 t[c.getPosition()][i]=c.getString(i);
		           }
		        } while (c.moveToNext());
		        }
				
				return t;
			}catch(Exception e){
				Log.d("wifilbs_rssi","getCR Error");
				e.printStackTrace();
			}
			finally
			{
				//结束事务
				db.endTransaction();
				this.Close();
			}
		return null;
	}
	public class SiteResult{
		public String id="";
		public float x;
		public float y;
		public float z;
		public String remark="";
		
	}
	public class SaveResult{
		public String saveid="";
		public String areaid="";
	}
	public List<SaveResult> getSaveResult(){
		String[][] t= this.getTable("wifilbs_site",new String[]{"areaid","saveid"}, null);
		List<SaveResult> list=new ArrayList<SaveResult>();
		if(t!=null)
		for(int i=0;i<t.length;i++){
			SaveResult sr=new SaveResult();
			sr.saveid=t[i][1];
			sr.areaid=t[i][0];
			list.add(sr);
		}
		return list;
	}
	public class PointResult{
		public String id="";
		public String areaid="";
		public String saveid="";
		public String remark="";
	}
	
	public List<PointResult> getPoints(String saveid){
		String[][] t= this.getTable("wifilbs_site",new String[]{"areaid","saveid","nodeid","remark"}, "saveid='"+saveid+"'");
		List<PointResult> list=new ArrayList<PointResult>();
		if(t!=null)
		for(int i=0;i<t.length;i++){
			 PointResult pr=new PointResult();
			 pr.areaid=t[i][0];
			 pr.saveid=t[i][1];
			 pr.id=t[i][2];
			 pr.remark=t[i][3];
			 list.add(pr);
		}
		return list;
		
	}
	public void updatePointRemark(String saveid,String nodeid,String remark){
		this.open();
		this.execute("update wifilbs_site set remark='"+remark+"' where nodeid='"+nodeid+"' and saveid='"+saveid+"' ");
		this.Close();
	}
	
	public List<SiteResult> getSiteData(String areaid,String saveid){
		this.open();
		String[][] t=this.getTable("wifilbs_site",new String[]{
			"nodeid","x","y","z","remark"
		},"areaid='"+areaid+"' and saveid='"+saveid+"'");
	
		List<SiteResult> srs=new ArrayList<SiteResult>();
		if(t!=null){
			for(int i=0;i<t.length;i++){
				SiteResult sr=new SiteResult();
				sr.id=t[i][0];
				sr.x=Float.valueOf(t[i][1]);
				sr.y=Float.valueOf(t[i][2]);
				sr.z=Float.valueOf(t[i][3]);
				if(t[i][4]==null||t[i][4].equalsIgnoreCase("null"))
					sr.remark="";
				else
				sr.remark=t[i][4];
				srs.add(sr);
			}
		}
		return srs;
		
	}
	 
	public String[][] getTable(String table,String[] cols,String condition){
		this.open();
		Cursor c=this.getData(table, cols, condition);
		int count=this.getCount(c);
		if(c!=null)
			c.moveToFirst();
		System.out.println("find count = "+count);
		String[][] t=new String[count][cols.length];
			try
			{
				db.beginTransaction();
				if (c.moveToFirst())
		        {
		        do{
		           for(int i=0;i<cols.length;i++){
		        	 t[c.getPosition()][i]=c.getString(i);
		           }
		        } while (c.moveToNext());
		        }
				
				return t;
			}catch(Exception e){
				Log.d(table,"getCR Error");
				e.printStackTrace();
			}
			finally
			{
				//结束事务
				db.endTransaction();
				this.Close();
			}
		return null;
	}
	public void Close(){
		try{
			DBHelper.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
}
