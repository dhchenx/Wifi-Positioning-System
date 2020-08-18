package com.bluecanna.wificlock.db;

 

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
			super(context,"wificlock", null, 1);	
		}
		private static final String DATABASE_CREATE_wificlock= 
			"create table wc_wificlock(id text,clock text,addtime DATETime);";
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_wificlock);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists wc_wificlock");

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
	public long addModel(String id,String model){
		this.open();
		ContentValues initialValues = new ContentValues();
		initialValues.put("id",id);
		initialValues.put("clock",model);
		initialValues.put("addtime", Sys.getTime());
		 long r= db.insert("wc_wificlock", null, initialValues);
		 this.close();
		 return r;
	}
	
	public int execute(String sql){
		 this.open();
		 db.execSQL(sql);
		 this.close();
		 return 0;
	}

	public Cursor getData(String table,String[] cols,String condition,String orderby){
		Cursor mCursor = db.query(true,table, cols,
				condition, null, null, null, orderby, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
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

	public void close(){
		try{
			DBHelper.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public String[][] getTable(String table,String[] cols,String condition,String orderby){
		this.open();
		Cursor c=this.getData(table, cols, condition,orderby);
		int count=this.getCount(c);
		if(c!=null)
			c.moveToFirst();
	//	System.out.println("find count = "+count);
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
				//½áÊøÊÂÎñ
				db.endTransaction();
				this.close();
			}
		return null;
	}
	
}
