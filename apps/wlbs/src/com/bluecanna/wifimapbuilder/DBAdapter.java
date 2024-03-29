package com.bluecanna.wifimapbuilder;

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
public class DBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_ISBN = "isbn";
	public static final String KEY_TITLE = "title";
	public static final String KEY_PUBLISHER = "publisher";
	private static final String TAG = "Book";
	private static final String DATABASE_NAME = "books";
	private static final String DATABASE_TABLE = "titles";
	private static final int DATABASE_VERSION = 1;

	/*
	 * create table SQL
	 */
	private static final String DATABASE_CREATE = "create table titles (_id integer primary key autoincrement, "
			+ "isbn text not null, title text not null, "
			+ "publisher text not null);";

	private final Context context;

	// DB assistant instance

	private DatabaseHelper DBHelper;

	// DB instance

	private SQLiteDatabase db;

	/*
	 * DBAdapter constructor
	 */
	public DBAdapter(Context ctx) {
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
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
	}// end of DatabaseHelper

	/*****************************************************
	 * Below are all DBAdaptor method: create, open...
	 ****************************************************/

	/*
	 * Open DB
	 */
	public DBAdapter open() throws SQLException {
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

	/*
	 * Insert one title
	 */
	public long insertTitle(String isbn, String title, String publisher) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ISBN, isbn);
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_PUBLISHER, publisher);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	/*
	 * Delete one title
	 */
	public boolean deleteTitle(long rowId) {
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/*
	 * Query all titles
	 */
	public Cursor getAllTitles() {
		return db.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_ISBN,
				KEY_TITLE, KEY_PUBLISHER }, null, null, null, null, null);
	}

	/*
	 * Query a specified title
	 */
	public Cursor getTitle(long rowId) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_ISBN, KEY_TITLE, KEY_PUBLISHER }, KEY_ROWID
				+ "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/*
	 * update a title
	 */
	
	public boolean updateTitle(long rowId, String isbn, String title,
			String publisher) {
		ContentValues args = new ContentValues();
		args.put(KEY_ISBN, isbn);
		args.put(KEY_TITLE, title);
		args.put(KEY_PUBLISHER, publisher);
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
}
