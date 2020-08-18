package com.bluecanna.wifimapbuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.List;

import com.bluecanna.wifimapbuilder.CameraActivity.BlobDAL.Img;

import android.net.Uri;

import android.os.Bundle;

import android.provider.MediaStore;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.ContentResolver;
import android.content.DialogInterface;

import android.content.ContentValues;

import android.content.Context;

import android.content.Intent;
import android.content.res.Resources;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;

import android.view.Menu;

import android.view.View;

import android.view.View.OnClickListener;

import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;

public class CameraActivity extends Activity {

	Button btnPaizhao;
	CameraHelper mCameraHelper;

	ImageView imageView1;
	String cur_key = "-1";
	BlobDAL mBlobDAL;
	public String ap_name = "web.wlan.bjtu";
	public String ap_remark = "bjtu";
	public String ap_interval = "1000";
   public Context context;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.camera_view);
		setTitle("WLAN定位 - 拍照导入");
		/*
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.bggray);
		this.getWindow().setBackgroundDrawable(drawable);
		*/
		Intent intent = getIntent();
		if (intent != null) {
			try {
				// 获取当前连接
				Bundle bd = intent.getExtras();
				if (bd != null) {
					ap_name = bd.getString("ap_ssid");
					ap_remark = bd.getString("ap_remark");
					ap_interval = bd.getString("ap_interval");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		;

		mBlobDAL = new BlobDAL(this);

		imageView1 = (ImageView) findViewById(R.id.imageView1);
		/*
		 * findViewById(R.id.btnReadDB).setOnClickListener(new OnClickListener()
		 * {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * List<Bitmap> bpArr = mBlobDAL.ReadImg();
		 * 
		 * ViewGroup gp = (ViewGroup) findViewById(R.id.div);
		 * 
		 * gp.removeAllViews();
		 * 
		 * for (int i = 0; i < bpArr.size(); i++) {
		 * 
		 * ImageView iv = new ImageView(CameraActivity.this);
		 * 
		 * Bitmap bp = bpArr.get(i);
		 * 
		 * if (bp != null) {
		 * 
		 * iv.setImageBitmap(bp);
		 * 
		 * } else {
		 * 
		 * iv.setImageBitmap(null);
		 * 
		 * }
		 * 
		 * gp.addView(iv);
		 * 
		 * }
		 * 
		 * }
		 * 
		 * });
		 * 
		 * btnPaizhao = (Button) findViewById(R.id.btnPaizhao);
		 * 
		 * btnPaizhao.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) {
		 * 
		 * mCameraHelper.OnOpenCamera();
		 * 
		 * }
		 * 
		 * });
		 */
		mCameraHelper = new CameraHelper(this);
context=this;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			mCameraHelper.HandleonActivityResult(requestCode, resultCode, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class CameraHelper {

		Context mContext;

		public CameraHelper(Context ctx) {

			mContext = ctx;

		}

		Uri photoUri;

		public static final int REQUEST_CODE_camera = 2222;

		public void OnOpenCamera() {

			// 向 MediaStore.Images.Media.EXTERNAL_CONTENT_URI 插入一个数据，那么返回标识ID。

			// 在完成拍照后，新的照片会以此处的photoUri命名. 其实就是指定了个文件名

			ContentValues values = new ContentValues();

			photoUri = getContentResolver().insert(

			MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

			// 准备intent，并 指定 新 照片 的文件名（photoUri）

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);

			// 启动拍照的窗体。并注册 回调处理。

			startActivityForResult(intent, REQUEST_CODE_camera);

		}
		public void ShowPList(){
			
			try {
				List<Img> bpArr = mBlobDAL.ReadImg();

				ViewGroup gp = (ViewGroup) findViewById(R.id.div);

				gp.removeAllViews();
				if (bpArr != null && bpArr.size() > 0)
					for (int i = 0; i < bpArr.size(); i++) {

						ImageView iv = new ImageView(CameraActivity.this);
						iv.setDrawingCacheEnabled(true);
						Bitmap bp = bpArr.get(i).bmp;

						if (bp != null) {

							iv.setImageBitmap(bp);

						} else {

							iv.setImageBitmap(null);

						}

						iv.setOnClickListener(new ImageClickListener(bpArr
								.get(i).bgid, imageView1, iv));
						gp.addView(iv);

					}
			} catch (Exception e) {
				setTitle("WiFi定位 - 还没有图片");
				e.printStackTrace();
			}
		}
		public void HandleonActivityResult(int requestCode, int resultCode,

		Intent data) {

			if (requestCode == CameraHelper.REQUEST_CODE_camera) {

				ContentResolver cr = mContext.getContentResolver();

				if (photoUri == null)

					return;

				// 按 刚刚指定 的那个文件名，查询数据库，获得更多的 照片信息，比如 图片的物理绝对路径

				Cursor cursor = cr.query(photoUri, null, null, null, null);

				if (cursor != null) {

					if (cursor.moveToNext()) {

						String path = cursor.getString(1);

						// 获得图片

						Bitmap bp = getBitMapFromPath(path);

						imageView1.setImageBitmap(bp);

						// 写入到数据库

						mBlobDAL.InsertImg(bp);

					}

					cursor.close();

				}

				photoUri = null;
				this.ShowPList();
			}

		}

		/* 获得图片，并进行适当的 缩放。 图片太大的话，是无法展示的。 */
		private Bitmap compressImage(Bitmap image) {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 100;
			while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				options -= 10;// 每次都减少10
			}
			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
			return bitmap;
		}

		private Bitmap getimage(String srcPath) {
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
			newOpts.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
			float hh = 800f;// 这里设置高度为800f
			float ww = 480f;// 这里设置宽度为480f
			// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
			int be = 1;// be=1表示不缩放
			if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
				be = (int) (newOpts.outWidth / ww);
			} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
				be = (int) (newOpts.outHeight / hh);
			}
			if (be <= 0)
				be = 1;
			newOpts.inSampleSize = be;// 设置缩放比例
			// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
			bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
			return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
		}

		private Bitmap getBitMapFromPath(String imageFilePath) {

			Display currentDisplay = getWindowManager().getDefaultDisplay();

			int dw = currentDisplay.getWidth();

			int dh = currentDisplay.getHeight();

			// Load up the image's dimensions not the image itself

			BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();

			bmpFactoryOptions.inJustDecodeBounds = true;

			Bitmap bmp = BitmapFactory.decodeFile(imageFilePath,

			bmpFactoryOptions);

			int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight

			/ (float) dh);

			int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth

			/ (float) dw);

			// If both of the ratios are greater than 1,

			// one of the sides of the image is greater than the screen

			if (heightRatio > 1 && widthRatio > 1) {

				if (heightRatio > widthRatio) {

					// Height ratio is larger, scale according to it

					bmpFactoryOptions.inSampleSize = heightRatio;

				} else {

					// Width ratio is larger, scale according to it

					bmpFactoryOptions.inSampleSize = widthRatio;

				}

			}

			// Decode it for real

			bmpFactoryOptions.inJustDecodeBounds = false;

			bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);

			bmp = compressImage(bmp);

			return bmp;

		}

	}

	public BlobDAL getBloDAL(Context context) {
		return new BlobDAL(context);
	}

	/*
	 * 
	 * 操作数据库
	 */
	public class BlobDAL extends SQLiteOpenHelper {

		public BlobDAL(Context context) {

			super(context, "imgDemo.db", null, 1);

			// TODO Auto-generated constructor stub

		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			String str = "CREATE TABLE [IMGS] ( [IDPK] integer PRIMARY KEY autoincrement,IMG_DATA blob,bgid text )";

			db.execSQL(str);

		}

		/*
		 * 
		 * 插入图
		 */

		public void InsertImg(Bitmap bmp) {

			SQLiteDatabase db = getWritableDatabase();

			ContentValues cv = new ContentValues();

			ByteArrayOutputStream os = new ByteArrayOutputStream();

			bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
			cv.put("bgid", Tools.getTimeStamp());
			cv.put("IMG_DATA", os.toByteArray());

			db.insert("IMGS", null, cv);

		}

		public void InsertImg(String bgid, Bitmap bmp) {

			SQLiteDatabase db = getWritableDatabase();

			ContentValues cv = new ContentValues();

			ByteArrayOutputStream os = new ByteArrayOutputStream();

			bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
			cv.put("bgid", bgid);
			cv.put("IMG_DATA", os.toByteArray());

			db.insert("IMGS", null, cv);

		}

		public void UpdateImg(String id, Bitmap bmp) {

			SQLiteDatabase db = getWritableDatabase();

			ContentValues cv = new ContentValues();

			ByteArrayOutputStream os = new ByteArrayOutputStream();

			bmp.compress(Bitmap.CompressFormat.PNG, 100, os);

			cv.put("IMG_DATA", os.toByteArray());

			db.update("IMGS", cv, "bgid='" + id + "'", null);
		}

		// 读取
		public class Img {
			public Bitmap bmp = null;
			public String bgid = null;
		}

		public List<Img> ReadImg() {

			SQLiteDatabase db = getReadableDatabase();

			Cursor cr = db.rawQuery("select * from IMGS order by [IDPK] desc ",
					null);

			List<Img> lst = new ArrayList<Img>();

			while (cr.moveToNext()) {

				byte[] in = cr.getBlob(cr.getColumnIndex("IMG_DATA"));
				Img img = new Img();
				img.bmp = (BitmapFactory.decodeByteArray(in, 0, in.length));
				img.bgid = cr.getString(cr.getColumnIndex("bgid"));
				lst.add(img);
			}

			return lst;

		}

		public Bitmap ReadOneImg(String bgid) {

			SQLiteDatabase db = getReadableDatabase();

			Cursor cr = db.rawQuery("select * from IMGS where bgid='" + bgid
					+ "'", null);

			// List<Bitmap> lst = new ArrayList<Bitmap>();
			int i = 0;
			while (cr.moveToNext()) {

				byte[] in = cr.getBlob(cr.getColumnIndex("IMG_DATA"));

				return BitmapFactory.decodeByteArray(in, 0, in.length);
			}

			return null;

		}

		public int GetImgKey(int key) {

			SQLiteDatabase db = getReadableDatabase();
			Cursor cr = db.rawQuery("select * from IMGS order by [IDPK] desc ",
					null);
			// List<Bitmap> lst = new ArrayList<Bitmap>();
			int i = 0;
			try {
				if (cr != null && cr.getCount() > 0)
					while (cr.moveToNext()) {
						if (key == i) {
							// byte[] in =
							// cr.getBlob(cr.getColumnIndex("IMG_DATA"));
							int k = cr.getInt(cr.getColumnIndex("IDPK"));
							return k;
						} else
							i++;
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return -1;

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// TODO Auto-generated method stub

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem mi = menu.add(0, 1, 1, "拍照导入");
		mi.setIcon(R.drawable.camera_32x32);
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		mi = menu.add(0, 2, 2, "背景列表");
		mi.setIcon(R.drawable.new_window_32x32);
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		mi = menu.add(0, 3, 3, "设置并返回");
		mi.setIcon(R.drawable.pen_alt_stroke_32x32);
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		mi = menu.add(0, 4, 4, "不保存返回");
		// mi.setIcon(R.drawable.menu_settin`g);
		// mi = menu.add(0, 3, 3, "拍照");
		return super.onCreateOptionsMenu(menu);
	}

	public void ShowPList(){
		
		try {
			List<Img> bpArr = mBlobDAL.ReadImg();

			ViewGroup gp = (ViewGroup) findViewById(R.id.div);

			gp.removeAllViews();
			if (bpArr != null && bpArr.size() > 0)
				for (int i = 0; i < bpArr.size(); i++) {

					ImageView iv = new ImageView(CameraActivity.this);
					iv.setDrawingCacheEnabled(true);
					Bitmap bp = bpArr.get(i).bmp;

					if (bp != null) {

						iv.setImageBitmap(bp);

					} else {

						iv.setImageBitmap(null);

					}

					iv.setOnClickListener(new ImageClickListener(bpArr
							.get(i).bgid, imageView1, iv));
					gp.addView(iv);

				}
		} catch (Exception e) {
			setTitle("WLAN定位 - 还没有图片");
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getGroupId() == 0) {
			if (item.getItemId() == 2) {
				// 查看保存的图片
			 this.ShowPList();
			}
			// 拍照
			if (item.getItemId() == 1) {
				mCameraHelper.OnOpenCamera();
			}
			// 产生地图
			if (item.getItemId() == 3) {
				WifiDB wdb = new WifiDB(this);
				wdb.updateBgId(ap_remark, cur_key);
				Intent intent = new Intent(CameraActivity.this,
						WifiLBSActivity.class);
				Bundle bd = new Bundle();
				bd.putString("ap_ssid", ap_name);
				bd.putString("ap_remark", ap_remark);
				bd.putString("ap_interval", ap_interval);
				intent.putExtras(bd);
				startActivity(intent);
				finish();
			}
			if(item.getItemId()==4){
				Intent intent = new Intent(CameraActivity.this,
						WifiLBSActivity.class);
				Bundle bd = new Bundle();
				bd.putString("ap_ssid", ap_name);
				bd.putString("ap_remark", ap_remark);
				bd.putString("ap_interval", ap_interval);
				intent.putExtras(bd);
				startActivity(intent);
				finish();
			}
		}
		return true;
	}

	private class ImageClickListener implements OnClickListener {
		private ImageView target = null;
		private ImageView me = null;
		private String key = "";

		public ImageClickListener(String key, ImageView target, ImageView me) {
			this.target = target;
			this.me = me;
			this.key = key;
		}

		public void onClick(View v) {
			try {
				target.setImageBitmap(mBlobDAL.ReadOneImg(key));
				cur_key = key;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(CameraActivity.this,
					WifiLBSActivity.class);
			Bundle bd = new Bundle();
			bd.putString("ap_ssid", ap_name);
			bd.putString("ap_remark", ap_remark);
			bd.putString("ap_interval", ap_interval);
			intent.putExtras(bd);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}