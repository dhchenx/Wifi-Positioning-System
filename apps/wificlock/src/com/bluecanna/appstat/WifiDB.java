package com.bluecanna.appstat;


import java.text.SimpleDateFormat;
import java.util.Date;
import com.bluecanna.wificlock.utils.Sys;

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
			super(context,"wifclock_stat", null, 1);	
		}

		private static final String DATABASE_CREATE_wificlock_stat=
				"create table wificlock_stat(addtime datetime,params text);";
		@Override
		public void onCreate(SQLiteDatabase db) {
	 
			db.execSQL(DATABASE_CREATE_wificlock_stat);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	 
			db.execSQL("drop table if exists wificlock_stat");
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
		initialValues.put("addtime",Sys.getTime());
		initialValues.put("params", s);
		 long r= db.insert("wificlock_stat", null, initialValues);
		 this.Close();
		 return r;
	}
	
	public String[] getStatParams(){
		String[][] t=this.getTable("wificlock_stat", new String[]{"params"}, null);
		if(t!=null){
			if(t.length<1)return null;
			try{
			
			String[] tt=new String[t.length];
			for(int i=0;i<tt.length;i++){
				tt[i]=t[i][0];
			}
		   this.clearTable("wificlock_stat");
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
