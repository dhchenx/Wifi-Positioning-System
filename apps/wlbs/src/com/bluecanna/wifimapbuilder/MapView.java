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
			// 定时器
			if (isdoing == false) {
				isdoing = true;
				if (recording == true) {
					record++;
					/*
					 * myactivity.setTitle();
					 */
					ShowTips("Step 5/6. 针对点(" + cur_point_index + ")"
							+ line_id.get(cur_point_index) + "第" + record
							+ "次记录数据；切换记录点请点击黄色线框外部区域，停止录制请点击黄色线框内部。");
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

					// 将用户数据上传至服务器
					if (isupload) {
						try {

							/*
							 * String value = wu.result2; String step =
							 * line_id.get(cur_point_index); upload_step++;
							 * String mark = saveid; String key = ap_name;
							 * String table = "wifimap"; // 上传到服务器 String url =
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
			// 屏幕方面切换时获得方向
			if (myactivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// myactivity.setTitle("landscape");
				this.orientation = this.LANDSCAPE;
			}
			if (myactivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				// myactivity.setTitle("portrait");
				this.orientation = this.PORTRAIT;
			}
			// 获得屏幕大小1
			WindowManager manager = myactivity.getWindowManager();
			int width = manager.getDefaultDisplay().getWidth();
			int height = manager.getDefaultDisplay().getHeight();
			// 获得屏幕大小2
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

	public Canvas canvas; // 画布
	public Paint p; // 画笔
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

			// 保存地图
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
			BlobDAL bdl = new CameraActivity().getBloDAL(myactivity);
			// bg_idpk=bdl.GetImgKey(Integer.valueOf(cur_key));
			if (!bgid.equals("-1") && bgid != null && !bgid.equals("")) {
				bdl.UpdateImg(bgid, bitmap);
				ShowTips("保存背景图片更改");
			} else {
				bgid = Tools.getTimeStamp();
				bdl.InsertImg(bgid, bitmap);
				wdb.updateBgId(saveid, bgid);
				ShowTips("保存背景图片更改(新建)");
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
		canvas.setBitmap(bitmap); // 设置画布
		/*
		 * p = new Paint(Paint.DITHER_FLAG); p.setAntiAlias(true); //
		 * 如果为true的话是抗锯齿效果 p.setColor(Color.RED); // 设置画笔的颜色
		 * p.setStrokeCap(Paint.Cap.ROUND); p.setStrokeWidth(8); // 设置画笔的宽度
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
			int w, int h, int bx, int by, int bw, int bh) { // x,y表示绘画的起点，
		Rect src = new Rect();// 图片
		Rect dst = new Rect();// 屏幕
		// src 这个是表示绘画图片的大小
		src.left = bx; // 0,0
		src.top = by;
		src.right = bx + bw;// mBitDestTop.getWidth();,这个是桌面图的宽度，
		src.bottom = by + bh;// mBitDestTop.getHeight()/2;// 这个是桌面图的高度的一半
		// 下面的 dst 是表示 绘画这个图片的位置
		dst.left = x; // miDTX,//这个是可以改变的，也就是绘图的起点X位置
		dst.top = y; // mBitQQ.getHeight();//这个是QQ图片的高度。 也就相当于 桌面图片绘画起点的Y坐标
		dst.right = x + w; // miDTX + mBitDestTop.getWidth();// 表示需绘画的图片的右上角
		dst.bottom = y + h; // mBitQQ.getHeight() +
							// mBitDestTop.getHeight();//表示需绘画的图片的右下角
		canvas.drawBitmap(blt, src, dst, null);// 这个方法 第一个参数是图片，第二个参数是
												// 绘画该图片需显示多少。也就是说你想绘画该图片的某一些地方，而不是全部图片，第三个参数表示该图片绘画的位置

		src = null;
		dst = null;
	}

	public void ShowLocation(String text, boolean t) {
		/*
		 * Paint p = new Paint(Paint.DITHER_FLAG); p.setAntiAlias(true); //
		 * 如果为true的话是抗锯齿效果 p.setColor(Color.BLUE); // 设置画笔的颜色
		 * p.setStrokeCap(Paint.Cap.ROUND); p.setStrokeWidth(28); // 设置画笔的宽度
		 * p.setTextSize(25); canvas.drawRect(900, 650, 1280, 800,
		 * CreatePaint(Color.WHITE));
		 * 
		 * canvas.drawText("(" + map.double2(cur_x) + ", " + map.double2(cur_y)
		 * + ")", 900, 725, p); canvas.drawText(text, 900, 700, p); if (mapmode
		 * == true) { canvas.drawText("工作模式:录制地图", 900, 670, p); } else {
		 * canvas.drawText("工作模式:导航模式", 900, 670, p); } invalidate();
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
		p.setAntiAlias(true); // 如果为true的话是抗锯齿效果
		p.setColor(c); // 设置画笔的颜色
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStrokeWidth(3); // 设置画笔的宽度
		return p;
	}

	public Paint CreatePaint(int width, int c) {
		Paint p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true); // 如果为true的话是抗锯齿效果
		p.setColor(c); // 设置画笔的颜色
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStrokeWidth(width); // 设置画笔的宽度
		return p;
	}

	public Paint CreateTextPaint(int width, int c) {
		Paint p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true); // 如果为true的话是抗锯齿效果
		p.setColor(c); // 设置画笔的颜色
		p.setStrokeCap(Paint.Cap.ROUND);
		// p.setStrokeWidth(width); // 设置画笔的宽度
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
		p.setAntiAlias(true); // 如果为true的话是抗锯齿效果
		p.setColor(Color.WHITE); // 设置画笔的颜色
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStrokeWidth(28); // 设置画笔的宽度
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
	 * p.setAntiAlias(true); // 如果为true的话是抗锯齿效果 p.setColor(Color.WHITE); //
	 * 设置画笔的颜色 p.setStrokeCap(Paint.Cap.ROUND); p.setStrokeWidth(28); // 设置画笔的宽度
	 * p.setTextSize(15); canvas.drawRect(900, 0, 1280,30,
	 * CreatePaint(Color.GRAY)); canvas.drawText(s, 900,20, p); //invalidate();
	 * }
	 */
	public float d_x;
	public float d_y;

	// 当按下屏幕时的回调函数
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 导航模式不进行创建地图
		if (mapmode == false && drawmode == false) {
			ShowTips("导航模式无法创建地图，请在菜单切换到录制地图模式!");
			return true;
		}
		if (mapmode == false && drawmode == true) {
			if (event.getAction() == MotionEvent.ACTION_MOVE) { // 拖动
				canvas.drawLine(d_x, d_y, event.getX(), event.getY(),
						this.CreatePaint(drawmode_color));
				invalidate();
				d_x = event.getX();
				d_y = event.getY();

			}
			if (event.getAction() == MotionEvent.ACTION_DOWN) { // 点触
				d_x = event.getX();
				d_y = event.getY();
				canvas.drawPoint(d_x, d_y, CreatePaint(drawmode_color));
				invalidate();
			}
			return true;
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) { // 拖动
			if (step_defined_area == false) {
				canvas.drawLine(x, y, event.getX(), event.getY(),
						CreatePaint(Color.YELLOW));
				// this.CreateCanvas();
				// canvas.drawPoint(x, y,CreatePaint(Color.YELLOW));
				// System.out.println("MOVE");
				isDrawing = true;
				// myactivity.setTitle("正在创建区域...");
				ShowTips("正在创建辅助白色矩形区域...");
				invalidate(); // invalidate起到更新画布的作用吧，个人理解
			}
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) { // 点触

			if (step_defined_area == true && step_defined_line == true
					&& touch_out_count > 1
					&& !isInArea(event.getX(), event.getY())) {
				touch_out_count++;
				ShowTips("已经切换到当前点" + line_id.get(cur_point_index));

				this.MarkNextPoint();
			}
			// 开始记录数据
			if (step_defined_area == true && step_defined_line == true
					&& touch_out_count > 1
					&& isInArea(event.getX(), event.getY())) {

				try {
					WifiUtils wu = new WifiUtils();
					String level = wu.GetWifiByName1(myactivity, ap_name);
					if (level == null || level.equals(""))
						throw new Exception();
				} catch (Exception e) {
					ShowTips("当前"+ap_name+"不可用，请设置[AP设置]新的WiFi接入点重试!");
					e.printStackTrace();
					return false;
				}

				record = 0;
				touch_in_count++;
				if (touch_in_count % 2 == 1) {
					// myactivity.setTitle("正在记录..." + cur_point_index);
					ShowTips("Step 5. 正在记录WiFi的信号数据..." + "，当前点序号"
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
						// myactivity.setTitle("取消..." + cur_point_index);
						if (timer != null) {
							timer.cancel();
							timer = null;
						}
						try {
							map.CreateMapFromDB(ap_name, saveid);
						} catch (Exception e) {
							e.printStackTrace();
							this.ShowTips("处理数据出错。请在黄框内选择点");
							return true;
						}
						// 显示数据库数据
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
						ShowTips("Step 6/6. 已暂停,继续录制点击黄框内部。若开始使用该WiFi感知地图定位，请在菜单切换[导航模式]，并启动[感知]。");
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
					ShowTips("Step 4/6. 完成路径点的创建，若启动录制，请随意点击黄色内部!");
					// 保存到数据库后清除缓存
					// line_x=new ArrayList<Float>();d
					// line_y=new ArrayList<Float>();
					// line_id=new ArrayList<String>();
					}catch(Exception e){
						this.ShowTips("显示出错，请准遵循操作步骤,在黄色框内选择超过3个以上采集点");
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
					ShowTips("Step 3/6. 已创建路径点 "
							+ line_id.get(line_id.size() - 1) + " ("
							+ event.getX() + ", " + event.getY() + ") - " + "第"
							+ step + "个点 (点击外部区域完成)");
				}
			}
			// myactivity.setTitle();

			x = event.getX();
			y = event.getY();

			// System.out.println("DOWN");
			invalidate(); // 同上，貌似还有个postInvalidate方法，好像有一个是线程安全 //的吧
		}
		if (event.getAction() == MotionEvent.ACTION_UP) { // 松开
			if (step_defined_area == false && isDrawing == true) {
				this.CreateCanvas();
				isDrawing = false;
				p2_x = event.getX();
				p2_y = event.getY();
				String s = "(" + p1_x + "," + p1_y + ") - (" + p2_x + ","
						+ p2_y + ")";
				// myactivity.setTitle(s);
				step_defined_area = true;
				// 绘制矩形
				Paint p1 = new Paint(Paint.DITHER_FLAG);
				p1.setAntiAlias(true); // 如果为true的话是抗锯齿效果
				p1.setColor(Color.YELLOW); // 设置画笔的颜色
				p1.setStrokeCap(Paint.Cap.ROUND);
				p1.setStyle(Paint.Style.STROKE);
				p1.setStrokeWidth(8); // 设置画笔的宽度
				canvas.drawRect(p1_x, p1_y, p2_x, p2_y, p1);
				invalidate();
				canvas.save();
				this.step_defining_line = true;
				ShowTips("Step 2/6. 辅助区域创建完毕，请在[黄色内部区域]点选若干路径点，然后点击[黄色外部区域]完成!");
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
		// ShowTips("显示区域:" + areaid + "(" + saveid + ")");
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
		ShowTips(Tools.getTime() + "感知到:" + areaid + "(" + saveid + ") - " + remark);
	}
	 
	// 同样,onDraw方法也是在画布需要更新的时候会自动调用
	@Override
	public void onDraw(Canvas c) {
		c.drawBitmap(bitmap, 0, 0, null);
	}

}
