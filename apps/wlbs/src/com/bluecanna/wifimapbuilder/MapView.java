package com.bluecanna.wifimapbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.bluecanna.wifimapbuilder.CameraActivity.BlobDAL;
import com.bluecanna.wifimapbuilder.WifiDB.SiteResult;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MapView extends View {
	public WifiDB wdb = null;
	private Timer timer = new Timer();
	private TimerTask task;
	public WifiDataCache wdc = null;
	public int record = 0;
	public boolean recording = false;
	public boolean isdoing = false;
	public String ap_name = "web.wlan.bjtu";
	public String saveid = "";
	public Bitmap bground = null;
	public boolean isupload = false;
	public static int upload_step = 0;

	public void setSaveId(String s) {
		this.saveid = s;
	}

	public void setApName(String v) {
		this.ap_name = v;
	}

	public void setBg(Bitmap bmp) {
		this.bground = bmp;
	}

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// ��ʱ��
			if (isdoing == false) {
				isdoing = true;
				if (recording == true) {
					record++;
					/*
					 * myactivity.setTitle();
					 */
					ShowTips("Step 5/6. ��Ե�(" + cur_point_index + ")"
							+ line_id.get(cur_point_index) + "��" + record
							+ "�μ�¼���ݣ��л���¼��������ɫ�߿��ⲿ����ֹͣ¼��������ɫ�߿��ڲ���");
					WifiUtils wu = new WifiUtils();
					String[] macvals = wu.GetWifiByName(myactivity, ap_name);
					// data.get(cur_point_index).add(macvals);
					String[] macs = new String[macvals.length / 2];
					String[] vals = new String[macvals.length / 2];
					for (int i = 0; i < macs.length; i++) {
						macs[i] = macvals[i];
					}
					for (int j = 0; j < vals.length; j++) {
						vals[j] = macvals[j + macs.length];
					}
					for (int i = 0; i < macs.length; i++) {
						Log.d("macs", macs[i]);
						Log.d("vals", vals[i]);
					}
					wdb.insertCollectRSSI(line_x.get(cur_point_index),
							line_y.get(cur_point_index), 0, ap_name,
							line_id.get(cur_point_index), macs, vals, null);

					// ���û������ϴ���������
					if (isupload) {
						try {

							/*
							 * String value = wu.result2; String step =
							 * line_id.get(cur_point_index); upload_step++;
							 * String mark = saveid; String key = ap_name;
							 * String table = "wifimap"; // �ϴ��������� String url =
							 * "http://ekyy.v050.10000net.cn/LBSServer1/map.jsp?"
							 * + "table=" + table + "&key=" + key + "&value=" +
							 * value + "&step=" + step + "&mark=" + mark;
							 * MyASync asc = new MyASync(); asc.execute(url);
							 * 
							 * Thread.sleep(500);
							 */

							Log.d("test", "is uploading");
							WiFiInfoUploader wfi = new WiFiInfoUploader(
									ap_name, saveid, "",
									line_id.get(cur_point_index),
									String.valueOf(upload_step), macs, vals);
							upload_step++;
							wfi.upload();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					isdoing = false;
				}
				super.handleMessage(msg);
			}
		}
	};

	public class ScreenInfo {
		public ScreenInfo() {
			// ��Ļ�����л�ʱ��÷���
			if (myactivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// myactivity.setTitle("landscape");
				this.orientation = this.LANDSCAPE;
			}
			if (myactivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				// myactivity.setTitle("portrait");
				this.orientation = this.PORTRAIT;
			}
			// �����Ļ��С1
			WindowManager manager = myactivity.getWindowManager();
			int width = manager.getDefaultDisplay().getWidth();
			int height = manager.getDefaultDisplay().getHeight();
			// �����Ļ��С2
			DisplayMetrics dMetrics = new DisplayMetrics();
			myactivity.getWindowManager().getDefaultDisplay()
					.getMetrics(dMetrics);
			int screenWidth = dMetrics.widthPixels;
			int screenHeight = dMetrics.heightPixels;
			this.width = width;
			this.height = height;
			this.widthpx = screenWidth;
			this.heightpx = screenHeight;
		}

		public String LANDSCAPE = "landscape";
		public String PORTRAIT = "portrait";
		public String orientation = "";
		public int width = 0;
		public int height = 0;
		public int widthpx = 0;
		public int heightpx = 0;
	}

	public ScreenInfo myscreen = null;

	public Canvas canvas; // ����
	public Paint p; // ����
	private Bitmap bitmap;
	float x, y;
	int bgColor;
	Activity myactivity = null;
	// public int bg_idpk=-1;
	public String bgid = "";
	public boolean drawmode = false;
	public int drawmode_color = Color.BLACK;

	public boolean setDrawMode() {
		if (drawmode == true) {
			drawmode = false;

			// �����ͼ
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
			BlobDAL bdl = new CameraActivity().getBloDAL(myactivity);
			// bg_idpk=bdl.GetImgKey(Integer.valueOf(cur_key));
			if (!bgid.equals("-1") && bgid != null && !bgid.equals("")) {
				bdl.UpdateImg(bgid, bitmap);
				ShowTips("���汳��ͼƬ����");
			} else {
				bgid = Tools.getTimeStamp();
				bdl.InsertImg(bgid, bitmap);
				wdb.updateBgId(saveid, bgid);
				ShowTips("���汳��ͼƬ����(�½�)");
			}
		} else {
			CreateCanvas();
			drawmode = true;
		}
		return drawmode;
	}

	public void CreateCanvas() {
		bitmap = Bitmap.createBitmap(myscreen.width, myscreen.height,
				Bitmap.Config.ARGB_8888);
		canvas = new Canvas();
		canvas.setBitmap(bitmap); // ���û���
		/*
		 * p = new Paint(Paint.DITHER_FLAG); p.setAntiAlias(true); //
		 * ���Ϊtrue�Ļ��ǿ����Ч�� p.setColor(Color.RED); // ���û��ʵ���ɫ
		 * p.setStrokeCap(Paint.Cap.ROUND); p.setStrokeWidth(8); // ���û��ʵĿ��
		 */
		p = CreatePaint(Color.WHITE);
		// canvas.drawRect(50, 50, 200, 200, CreatePaint(Color.WHITE));
		BlobDAL bdl = new CameraActivity().getBloDAL(myactivity);
		try {
			bground = bdl.ReadOneImg(bgid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bground != null) {
			// canvas.drawBitmap(bitmap, src, dst, paint)drawBitmap(bground, 0,
			// 0,p);
			drawImage(canvas, bground, 0, 0, myscreen.width, myscreen.height,
					0, 0, bground.getWidth(), bground.getHeight());
		}
		invalidate();
	}

	// GameView.drawImage(canvas, mBitDestTop, miDTX, mBitQQ.getHeight(),
	// mBitDestTop.getWidth(), mBitDestTop.getHeight()/2, 0, 0);
	public static void drawImage(Canvas canvas, Bitmap blt, int x, int y,
			int w, int h, int bx, int by, int bw, int bh) { // x,y��ʾ�滭����㣬
		Rect src = new Rect();// ͼƬ
		Rect dst = new Rect();// ��Ļ
		// src ����Ǳ�ʾ�滭ͼƬ�Ĵ�С
		src.left = bx; // 0,0
		src.top = by;
		src.right = bx + bw;// mBitDestTop.getWidth();,���������ͼ�Ŀ�ȣ�
		src.bottom = by + bh;// mBitDestTop.getHeight()/2;// ���������ͼ�ĸ߶ȵ�һ��
		// ����� dst �Ǳ�ʾ �滭���ͼƬ��λ��
		dst.left = x; // miDTX,//����ǿ��Ըı�ģ�Ҳ���ǻ�ͼ�����Xλ��
		dst.top = y; // mBitQQ.getHeight();//�����QQͼƬ�ĸ߶ȡ� Ҳ���൱�� ����ͼƬ�滭����Y����
		dst.right = x + w; // miDTX + mBitDestTop.getWidth();// ��ʾ��滭��ͼƬ�����Ͻ�
		dst.bottom = y + h; // mBitQQ.getHeight() +
							// mBitDestTop.getHeight();//��ʾ��滭��ͼƬ�����½�
		canvas.drawBitmap(blt, src, dst, null);// ������� ��һ��������ͼƬ���ڶ���������
												// �滭��ͼƬ����ʾ���١�Ҳ����˵����滭��ͼƬ��ĳһЩ�ط���������ȫ��ͼƬ��������������ʾ��ͼƬ�滭��λ��

		src = null;
		dst = null;
	}

	public void ShowLocation(String text, boolean t) {
		/*
		 * Paint p = new Paint(Paint.DITHER_FLAG); p.setAntiAlias(true); //
		 * ���Ϊtrue�Ļ��ǿ����Ч�� p.setColor(Color.BLUE); // ���û��ʵ���ɫ
		 * p.setStrokeCap(Paint.Cap.ROUND); p.setStrokeWidth(28); // ���û��ʵĿ��
		 * p.setTextSize(25); canvas.drawRect(900, 650, 1280, 800,
		 * CreatePaint(Color.WHITE));
		 * 
		 * canvas.drawText("(" + map.double2(cur_x) + ", " + map.double2(cur_y)
		 * + ")", 900, 725, p); canvas.drawText(text, 900, 700, p); if (mapmode
		 * == true) { canvas.drawText("����ģʽ:¼�Ƶ�ͼ", 900, 670, p); } else {
		 * canvas.drawText("����ģʽ:����ģʽ", 900, 670, p); } invalidate();
		 */
	}

	public boolean mapmode = false;

	public void initDraw() {
		recording = false;
		isdoing = false;
		p1_x = 0.0f;
		p1_y = 0.0f;
		p2_x = 0.0f;
		p2_y = 0.0f;
		isDrawing = false;
		step_defined_area = false;
		step_defined_line = false;
		step_defining_line = false;
		line_x = new ArrayList<Float>();
		line_y = new ArrayList<Float>();
		line_id = new ArrayList<String>();
		p_affix = "p";
		step = 0;
		touch_out_count = 0;
		touch_in_count = 0;
		cur_point_index = -1;
		data = new ArrayList<List<String[]>>();
	}

	public void setMapMode(boolean b) {
		this.mapmode = b;
		if (b == true) {

		} else {

		}
	}

	public boolean isInButton(float x, float y) {
		if (x >= 50 && x <= 200 && y >= 50 && y <= 200)
			return true;
		return false;
	}

	public Paint CreatePaint(int c) {
		Paint p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true); // ���Ϊtrue�Ļ��ǿ����Ч��
		p.setColor(c); // ���û��ʵ���ɫ
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStrokeWidth(3); // ���û��ʵĿ��
		return p;
	}

	public Paint CreatePaint(int width, int c) {
		Paint p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true); // ���Ϊtrue�Ļ��ǿ����Ч��
		p.setColor(c); // ���û��ʵ���ɫ
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStrokeWidth(width); // ���û��ʵĿ��
		return p;
	}

	public Paint CreateTextPaint(int width, int c) {
		Paint p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true); // ���Ϊtrue�Ļ��ǿ����Ч��
		p.setColor(c); // ���û��ʵ���ɫ
		p.setStrokeCap(Paint.Cap.ROUND);
		// p.setStrokeWidth(width); // ���û��ʵĿ��
		p.setTextSize(width);
		return p;
	}

	public MapView(final Context context) {
		super(context);

		myactivity = (Activity) context;
		myscreen = new ScreenInfo();
		CreateCanvas();
		this.setBackgroundResource(R.color.white);
	}

	public void setNewMap() {
		map = new Map(this.wdb);
	}

	public void setWifiDB(WifiDB wdb) {
		this.wdb = wdb;

	}

	public float p1_x = 0.0f;
	public float p1_y = 0.0f;
	public float p2_x = 0.0f;
	public float p2_y = 0.0f;
	public boolean isDrawing = false;
	public boolean step_defined_area = false;
	public boolean step_defined_line = false;
	public boolean step_defining_line = false;
	public Map map = null;
	public List<Float> line_x = new ArrayList<Float>();
	public List<Float> line_y = new ArrayList<Float>();
	public List<String> line_id = new ArrayList<String>();
	public String p_affix = "p";
	public static int step = 0;
	public int touch_out_count = 0;
	public int touch_in_count = 0;
	public int cur_point_index = -1;
	public List<List<String[]>> data = new ArrayList<List<String[]>>();

	public boolean isInArea(float x, float y) {
		// if(isInButton(x,y))return false;
		if (x < p1_x || x > p2_x || y < p1_y || y > p2_y)
			return false;
		else
			return true;

	}

	public void MarkNextPoint() {
		cur_point_index++;
		if (cur_point_index == line_id.size()) {
			cur_point_index = 0;
		}
		canvas.drawCircle(line_x.get(0), line_y.get(0), 10,
				this.CreatePaint(Color.RED));
		if (line_id.size() > 1)
			for (int i = 1; i < line_id.size(); i++) {
				canvas.drawLine(line_x.get(i - 1), line_y.get(i - 1),
						line_x.get(i), line_y.get(i),
						this.CreatePaint(Color.GRAY));
				canvas.drawCircle(line_x.get(i), line_y.get(i), 10,
						this.CreatePaint(Color.RED));
			}
		canvas.drawCircle(line_x.get(cur_point_index),
				line_y.get(cur_point_index), 10, this.CreatePaint(Color.GREEN));

	}

	public void printArray(String[][] t) {
		for (int i = 0; i < t.length; i++) {
			String s = "";
			for (int j = 0; j < t[i].length; j++) {
				s += t[i][j] + ",";
			}
			Log.d("t_data", s);
		}
	}

	void ShowTips(String s) {
		Paint p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true); // ���Ϊtrue�Ļ��ǿ����Ч��
		p.setColor(Color.WHITE); // ���û��ʵ���ɫ
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStrokeWidth(28); // ���û��ʵĿ��
		p.setTextSize(23);
		// canvas.drawRect(900, 0, 1280,30, CreatePaint(Color.GRAY));
		canvas.drawRect(adjustP(0, true), 0, adjustP(1280, true),
				adjustP(30, false), CreatePaint(Color.LTGRAY));
		canvas.drawText(s, adjustP(0, true), adjustP(20, false),
				CreateTextPaint(16, Color.BLACK));
		invalidate();
	}

	public float adjustP(float f, boolean iswidth) {
		float r = 0;
		if (iswidth) {
			r = f * myscreen.widthpx / 1280;
		} else
			r = f * myscreen.heightpx / 700;
		return r;
	}

	/*
	 * void ShowTipsNoRefresh(String s){ Paint p = new Paint(Paint.DITHER_FLAG);
	 * p.setAntiAlias(true); // ���Ϊtrue�Ļ��ǿ����Ч�� p.setColor(Color.WHITE); //
	 * ���û��ʵ���ɫ p.setStrokeCap(Paint.Cap.ROUND); p.setStrokeWidth(28); // ���û��ʵĿ��
	 * p.setTextSize(15); canvas.drawRect(900, 0, 1280,30,
	 * CreatePaint(Color.GRAY)); canvas.drawText(s, 900,20, p); //invalidate();
	 * }
	 */
	public float d_x;
	public float d_y;

	// ��������Ļʱ�Ļص�����
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// ����ģʽ�����д�����ͼ
		if (mapmode == false && drawmode == false) {
			ShowTips("����ģʽ�޷�������ͼ�����ڲ˵��л���¼�Ƶ�ͼģʽ!");
			return true;
		}
		if (mapmode == false && drawmode == true) {
			if (event.getAction() == MotionEvent.ACTION_MOVE) { // �϶�
				canvas.drawLine(d_x, d_y, event.getX(), event.getY(),
						this.CreatePaint(drawmode_color));
				invalidate();
				d_x = event.getX();
				d_y = event.getY();

			}
			if (event.getAction() == MotionEvent.ACTION_DOWN) { // �㴥
				d_x = event.getX();
				d_y = event.getY();
				canvas.drawPoint(d_x, d_y, CreatePaint(drawmode_color));
				invalidate();
			}
			return true;
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) { // �϶�
			if (step_defined_area == false) {
				canvas.drawLine(x, y, event.getX(), event.getY(),
						CreatePaint(Color.YELLOW));
				// this.CreateCanvas();
				// canvas.drawPoint(x, y,CreatePaint(Color.YELLOW));
				// System.out.println("MOVE");
				isDrawing = true;
				// myactivity.setTitle("���ڴ�������...");
				ShowTips("���ڴ���������ɫ��������...");
				invalidate(); // invalidate�𵽸��»��������ðɣ��������
			}
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) { // �㴥

			if (step_defined_area == true && step_defined_line == true
					&& touch_out_count > 1
					&& !isInArea(event.getX(), event.getY())) {
				touch_out_count++;
				ShowTips("�Ѿ��л�����ǰ��" + line_id.get(cur_point_index));

				this.MarkNextPoint();
			}
			// ��ʼ��¼����
			if (step_defined_area == true && step_defined_line == true
					&& touch_out_count > 1
					&& isInArea(event.getX(), event.getY())) {

				try {
					WifiUtils wu = new WifiUtils();
					String level = wu.GetWifiByName1(myactivity, ap_name);
					if (level == null || level.equals(""))
						throw new Exception();
				} catch (Exception e) {
					ShowTips("��ǰ"+ap_name+"�����ã�������[AP����]�µ�WiFi���������!");
					e.printStackTrace();
					return false;
				}

				record = 0;
				touch_in_count++;
				if (touch_in_count % 2 == 1) {
					// myactivity.setTitle("���ڼ�¼..." + cur_point_index);
					ShowTips("Step 5. ���ڼ�¼WiFi���ź�����..." + "����ǰ�����"
							+ cur_point_index + "");
					timer = new Timer();
					task = new TimerTask() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Message message = new Message();
							message.what = touch_in_count % 2;
							Bundle bd = new Bundle();
							message.setData(bd);
							handler.sendMessage(message);
						}
					};
					recording = true;
					timer.schedule(task, 3000, 1000);

				} else {
					try {
						// myactivity.setTitle("ȡ��..." + cur_point_index);
						if (timer != null) {
							timer.cancel();
							timer = null;
						}
						try {
							map.CreateMapFromDB(ap_name, saveid);
						} catch (Exception e) {
							e.printStackTrace();
							this.ShowTips("�������ݳ������ڻƿ���ѡ���");
							return true;
						}
						// ��ʾ���ݿ�����
						/*
						 * String[][] rssi = wdb.getTable("wifilbs_rssi", new
						 * String[] { "roomid", "mac1", "val1", "span1" },
						 * null); Log.d("test",
						 * "====================rssi================");
						 * this.printArray(rssi); Log.d("test",
						 * "====================site================");
						 * String[][] sites = wdb.getTable("wifilbs_site", new
						 * String[] { "nodeid", "x", "y", "z" }, null);
						 * this.printArray(sites); Log.d("test",
						 * "====================rela================");
						 * String[][] rel = wdb .getTable("wifilbs_rel", new
						 * String[] { "nodeid", "leafid", "distance" }, null);
						 * this.printArray(rel);
						 */
						ShowTips("Step 6/6. ����ͣ,����¼�Ƶ���ƿ��ڲ�������ʼʹ�ø�WiFi��֪��ͼ��λ�����ڲ˵��л�[����ģʽ]��������[��֪]��");
						recording = false;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

			if (step_defined_area == false && isDrawing == false) {
				p1_x = event.getX();
				p1_y = event.getY();
				isDrawing = true;
				canvas.drawPoint(p1_x, p1_y, CreatePaint(8, Color.BLUE));
			}
			if (recording != true && step_defined_line == false
					&& step_defining_line == true) {
				if (!isInArea(event.getX(), event.getY())) {
					try{
					step_defined_line = true;
					step_defining_line = false;
					this.MarkNextPoint();
					touch_out_count += 1;
					touch_out_count += 1;
					ShowTips("Step 4/6. ���·����Ĵ�����������¼�ƣ�����������ɫ�ڲ�!");
					// ���浽���ݿ���������
					// line_x=new ArrayList<Float>();d
					// line_y=new ArrayList<Float>();
					// line_id=new ArrayList<String>();
					}catch(Exception e){
						this.ShowTips("��ʾ������׼��ѭ��������,�ڻ�ɫ����ѡ�񳬹�3�����ϲɼ���");
						step_defined_line=false;
						step_defining_line=true;
						touch_out_count -= 1;
						touch_out_count -= 1;
				//		this.initDraw();
						e.printStackTrace();
					}
				} else {
					line_x.add(event.getX());
					line_y.add(event.getY());
					line_id.add(p_affix + wdb.getTimeStamp() + step);
					data.add(new ArrayList<String[]>());
					canvas.drawPoint(event.getX(), event.getY(),
							CreatePaint(8, Color.BLUE));
					step++;
					ShowTips("Step 3/6. �Ѵ���·���� "
							+ line_id.get(line_id.size() - 1) + " ("
							+ event.getX() + ", " + event.getY() + ") - " + "��"
							+ step + "���� (����ⲿ�������)");
				}
			}
			// myactivity.setTitle();

			x = event.getX();
			y = event.getY();

			// System.out.println("DOWN");
			invalidate(); // ͬ�ϣ�ò�ƻ��и�postInvalidate������������һ�����̰߳�ȫ //�İ�
		}
		if (event.getAction() == MotionEvent.ACTION_UP) { // �ɿ�
			if (step_defined_area == false && isDrawing == true) {
				this.CreateCanvas();
				isDrawing = false;
				p2_x = event.getX();
				p2_y = event.getY();
				String s = "(" + p1_x + "," + p1_y + ") - (" + p2_x + ","
						+ p2_y + ")";
				// myactivity.setTitle(s);
				step_defined_area = true;
				// ���ƾ���
				Paint p1 = new Paint(Paint.DITHER_FLAG);
				p1.setAntiAlias(true); // ���Ϊtrue�Ļ��ǿ����Ч��
				p1.setColor(Color.YELLOW); // ���û��ʵ���ɫ
				p1.setStrokeCap(Paint.Cap.ROUND);
				p1.setStyle(Paint.Style.STROKE);
				p1.setStrokeWidth(8); // ���û��ʵĿ��
				canvas.drawRect(p1_x, p1_y, p2_x, p2_y, p1);
				invalidate();
				canvas.save();
				this.step_defining_line = true;
				ShowTips("Step 2/6. �������򴴽���ϣ�����[��ɫ�ڲ�����]��ѡ����·���㣬Ȼ����[��ɫ�ⲿ����]���!");
			}
			/*
			 * else if(step_defined_line==false&&step_defining_line==true){
			 * if(!isInArea(event.getX(),event.getY())) {
			 * step_defined_line=true; step_defining_line=false;
			 * if(line_id.size()>1) for(int i=1;i<line_id.size();i++){
			 * canvas.drawLine(line_x.get(i-1), line_y.get(i-1),line_x.get(i),
			 * line_y.get(i), p); } } }
			 */
		}
		x = event.getX();
		y = event.getY();
		return true;
	}

	public void showMap(String areaid) {

		List<SiteResult> srs = wdb.getSiteData(areaid, saveid);
		this.CreateCanvas();
		canvas.drawCircle(srs.get(0).x, srs.get(0).y, 10,
				CreatePaint(Color.BLUE));
		canvas.drawText(srs.get(0).id, srs.get(0).x + 20, srs.get(0).y + 20,
				CreatePaint(Color.BLACK));
		if (srs.size() > 0)
			for (int i = 1; i < srs.size(); i++) {
				SiteResult lsr = srs.get(i - 1);
				SiteResult sr = srs.get(i);
				canvas.drawLine(lsr.x, lsr.y, sr.x, sr.y,
						CreatePaint(Color.GRAY));
				canvas.drawCircle(srs.get(i).x, srs.get(i).y, 10,
						CreatePaint(Color.BLUE));
				
				if(!sr.remark.equals("")){
				canvas.drawText(sr.id, sr.x + 20, sr.y + 20,
						CreatePaint(Color.BLACK));
				}
						 
			}
		invalidate();
		// ShowTips("��ʾ����:" + areaid + "(" + saveid + ")");
	}

	public int deleteMap(String areaid) {
		return map.DeleteMap(areaid, saveid);
	}

	public float cur_x = 0;
	public float cur_y = 0;

	public Bitmap  getDrawable(int id){
		Resources res=getResources();   
		Bitmap bmp=BitmapFactory.decodeResource(res, id);  
		return bmp;
	}
	public void showMapPoint(String areaid, String id) {

		List<SiteResult> srs = wdb.getSiteData(areaid, saveid);
		this.CreateCanvas();
		/*
		 * canvas.drawCircle(srs.get(0).x, srs.get(0).y, 10,
		 * CreatePaint(Color.BLUE));
		 */
		canvas.drawText(srs.get(0).remark, srs.get(0).x + 20,
				srs.get(0).y + 20, CreatePaint(Color.BLACK));
		String remark = "";
		if (srs.get(0).id.equals(id)) {
			 
        
			canvas.drawCircle(srs.get(0).x, srs.get(0).y, 10,
					CreatePaint(Color.RED));
			cur_x = srs.get(0).x;
			cur_y = srs.get(0).y;
			remark = srs.get(0).remark;
		Bitmap bmp=getDrawable(R.drawable.chat);
			//canvas.drawBitmap(getDrawable(R.drawable.chat), cur_x, cur_y, CreatePaint(Color.RED));
			drawImage(canvas, bmp, (int)cur_x,(int) cur_y, 48, 48,
					0, 0, bmp.getWidth(), bmp.getHeight()); 
		} else {
			/*
			canvas.drawCircle(srs.get(0).x, srs.get(0).y, 5,
					CreatePaint(5, Color.BLACK));
					*/
			cur_x = srs.get(0).x;
			cur_y = srs.get(0).y;
			
		}
		if (srs.size() > 0)
			for (int i = 1; i < srs.size(); i++) {
				SiteResult lsr = srs.get(i - 1);
				SiteResult sr = srs.get(i);

				// canvas.drawLine(lsr.x, lsr.y, sr.x,sr.y ,
				// CreatePaint(Color.GRAY));

				if (!sr.id.equals(id)) {
					/*
					canvas.drawCircle(srs.get(i).x, srs.get(i).y, 5,
							CreatePaint(5, Color.BLACK));
*/
				} else {
					canvas.drawCircle(srs.get(i).x, srs.get(i).y, 10,
							CreatePaint(Color.RED));
					cur_x = srs.get(i).x;
					cur_y = srs.get(i).y;
					remark = srs.get(i).remark;
					Bitmap bmp=getDrawable(R.drawable.chat);
					drawImage(canvas, bmp, (int)cur_x,(int) cur_y, 48, 48,
							0, 0, bmp.getWidth(), bmp.getHeight()); 
				}
				if (!sr.remark.equals("")) {
					canvas.drawText(sr.remark, sr.x + 20, sr.y + 20,
							CreatePaint(Color.BLACK));
				}
			}
		invalidate();
		ShowTips(Tools.getTime() + "��֪��:" + areaid + "(" + saveid + ") - " + remark);
	}
	 
	// ͬ��,onDraw����Ҳ���ڻ�����Ҫ���µ�ʱ����Զ�����
	@Override
	public void onDraw(Canvas c) {
		c.drawBitmap(bitmap, 0, 0, null);
	}

}
