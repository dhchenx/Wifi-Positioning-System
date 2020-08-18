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
		setTitle("WLAN��λ - ���յ���");
		/*
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.bggray);
		this.getWindow().setBackgroundDrawable(drawable);
		*/
		Intent intent = getIntent();
		if (intent != null) {
			try {
				// ��ȡ��ǰ����
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

			// �� MediaStore.Images.Media.EXTERNAL_CONTENT_URI ����һ�����ݣ���ô���ر�ʶID��

			// ��������պ��µ���Ƭ���Դ˴���photoUri����. ��ʵ����ָ���˸��ļ���

			ContentValues values = new ContentValues();

			photoUri = getContentResolver().insert(

			MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

			// ׼��intent���� ָ�� �� ��Ƭ ���ļ�����photoUri��

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);

			// �������յĴ��塣��ע�� �ص�����

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
				setTitle("WiFi��λ - ��û��ͼƬ");
				e.printStackTrace();
			}
		}
		public void HandleonActivityResult(int requestCode, int resultCode,

		Intent data) {

			if (requestCode == CameraHelper.REQUEST_CODE_camera) {

				ContentResolver cr = mContext.getContentResolver();

				if (photoUri == null)

					return;

				// �� �ո�ָ�� ���Ǹ��ļ�������ѯ���ݿ⣬��ø���� ��Ƭ��Ϣ������ ͼƬ���������·��

				Cursor cursor = cr.query(photoUri, null, null, null, null);

				if (cursor != null) {

					if (cursor.moveToNext()) {

						String path = cursor.getString(1);

						// ���ͼƬ

						Bitmap bp = getBitMapFromPath(path);

						imageView1.setImageBitmap(bp);

						// д�뵽���ݿ�

						mBlobDAL.InsertImg(bp);

					}

					cursor.close();

				}

				photoUri = null;
				this.ShowPList();
			}

		}

		/* ���ͼƬ���������ʵ��� ���š� ͼƬ̫��Ļ������޷�չʾ�ġ� */
		private Bitmap compressImage(Bitmap image) {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// ����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
			int options = 100;
			while (baos.toByteArray().length / 1024 > 100) { // ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��
				baos.reset();// ����baos�����baos
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);// ����ѹ��options%����ѹ��������ݴ�ŵ�baos��
				options -= 10;// ÿ�ζ�����10
			}
			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());// ��ѹ���������baos��ŵ�ByteArrayInputStream��
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ��ByteArrayInputStream��������ͼƬ
			return bitmap;
		}

		private Bitmap getimage(String srcPath) {
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// ��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��
			newOpts.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// ��ʱ����bmΪ��

			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			// ���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ
			float hh = 800f;// �������ø߶�Ϊ800f
			float ww = 480f;// �������ÿ��Ϊ480f
			// ���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
			int be = 1;// be=1��ʾ������
			if (w > h && w > ww) {// �����ȴ�Ļ����ݿ�ȹ̶���С����
				be = (int) (newOpts.outWidth / ww);
			} else if (w < h && h > hh) {// ����߶ȸߵĻ����ݿ�ȹ̶���С����
				be = (int) (newOpts.outHeight / hh);
			}
			if (be <= 0)
				be = 1;
			newOpts.inSampleSize = be;// �������ű���
			// ���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
			bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
			return compressImage(bitmap);// ѹ���ñ�����С���ٽ�������ѹ��
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
	 * �������ݿ�
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
		 * ����ͼ
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

		// ��ȡ
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
		MenuItem mi = menu.add(0, 1, 1, "���յ���");
		mi.setIcon(R.drawable.camera_32x32);
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		mi = menu.add(0, 2, 2, "�����б�");
		mi.setIcon(R.drawable.new_window_32x32);
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		mi = menu.add(0, 3, 3, "���ò�����");
		mi.setIcon(R.drawable.pen_alt_stroke_32x32);
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		mi = menu.add(0, 4, 4, "�����淵��");
		// mi.setIcon(R.drawable.menu_settin`g);
		// mi = menu.add(0, 3, 3, "����");
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
			setTitle("WLAN��λ - ��û��ͼƬ");
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getGroupId() == 0) {
			if (item.getItemId() == 2) {
				// �鿴�����ͼƬ
			 this.ShowPList();
			}
			// ����
			if (item.getItemId() == 1) {
				mCameraHelper.OnOpenCamera();
			}
			// ������ͼ
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