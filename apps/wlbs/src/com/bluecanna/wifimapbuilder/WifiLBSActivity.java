package com.bluecanna.wifimapbuilder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.bluecanna.appstat.Stat;
import com.bluecanna.wifimapbuilder.CameraActivity.BlobDAL;
import com.bluecanna.wifimapbuilder.WifiDB.SaveResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class WifiLBSActivity extends Activity {
	/** Called when the activity is first created. */
	public WifiDB wdb = null;
	public Timer timer = null;
	public TimerTask task = null;
	public Context context = null;
	public String ap_name = "web.wlan.bjtu";
	public String ap_remark = "bjtu";
	public String ap_interval = "1000";
	public String bg_id = "";
	//
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 定时器
			if(msg.what==1){
			RSSILBS rlbs = new RSSILBS();
			rlbs.setWifiDB(wdb);
			String value = msg.getData().getString("value");
			String loc = rlbs.GetFinalTarget(ap_name, ap_remark, value);
			Log.d("value", value);
			if (loc != null) {
				Log.d("location", loc);
				mv.showMapPoint(ap_name, loc);
				mv.ShowLocation("您当前位置: " + loc, true);
			} else {
			    mv.ShowTips(Tools.getTime()+" - 未找到位置");
				mv.ShowLocation("未找到位置! ", false);
				Log.d("location", "no find location");
			}
			}
			super.handleMessage(msg);

		}
	};
	public MapView mv = null;

	void initFirstMap() {
		List<SaveResult> srs = wdb.getSaveResult();
		if (srs != null && srs.size() > 0) {
			ap_name = srs.get(srs.size() - 1).areaid;
			ap_remark = srs.get(srs.size() - 1).saveid;
		}
	}

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	Resources res = getResources();
	//	Drawable drawable = res.getDrawable(Color.BLACK);
		//this.getWindow().setBackgroundDrawable(drawable);
		
		Intent intent = getIntent();
		mv = new MapView(this);
		
		wdb = new WifiDB(this);
		Stat.wdb=wdb;
		// 首次登陆初始化为上一个地图
		this.initFirstMap();
		mv.setApName(ap_name);
		mv.setWifiDB(wdb);
		mv.setNewMap();
		mv.setSaveId(ap_remark);
		
		if (intent != null) {
			try {
				// 获取当前连接
				Bundle bd = intent.getExtras();
				if (bd != null) {
					ap_name = bd.getString("ap_ssid");
					mv.setApName(ap_name);
					mv.setSaveId(bd.getString("ap_remark"));
					ap_remark = mv.saveid;
					ap_interval = bd.getString("ap_interval");
					// 获取背景图片
					bg_id = wdb.getBgId(ap_remark);
					BlobDAL bdal = new CameraActivity().getBloDAL(this);
					if (!bg_id.equals("-1")) {
						Bitmap bmp=bdal.ReadOneImg(bg_id);
						mv.bgid=bg_id;
						mv.setBg(bmp);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		;
		mv.CreateCanvas();
		setContentView(mv);
		setTitle(this.getAppTitle("导航"));
		
		context = this;
		//检测若在绘图状态，长按则提示是否保存所做修改。
		
		 mv.setOnLongClickListener(new View.OnLongClickListener() {
             
	            @Override
	            public boolean onLongClick(View v) {
	                if(mv.drawmode==true){
	                	AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    				builder.setTitle("保存修改后的地图");
	    				builder.setMessage("你对地图背景所做的修改需要保存吗？");
	    				 builder.setIcon(R.drawable.delete);
	    				builder.setPositiveButton("保存",
	    						new DialogInterface.OnClickListener() {
	    							@Override
	    							public void onClick(DialogInterface dialog, int which) {
	    								mv.bgid=bg_id;
	    								mv.setDrawMode();
	    								bg_id=mv.bgid;
	    							}
	    						});
	    				builder.setNegativeButton("取消",
	    						new DialogInterface.OnClickListener() {
	    							@Override
	    							public void onClick(DialogInterface dialog, int which) {
	    								 
	    							}
	    						});
	    				builder.show();	
	                }
	                return false;
	            }
	        });
		
		
		/*
		 * String[][] t=null;
		 * 
		 * wdb.insertRSSI("web.wlan.bjtu", "d1",new String[]{
		 * "00:22:44:33","33:22:FF:12"}, new String[]{"-57","-70"}, new
		 * String[]{"3","5"}); wdb.insertRSSI("web.wlan.bjtu", "d2",new
		 * String[]{ "00:22:66:32","33:22:AC:12"}, new String[]{"-10","-60"},
		 * new String[]{"6","10"});
		 */
		/*
		 * t=wdb.getRSSIData("select * from wifilbs_rssi", new
		 * String[]{"mac1","mac2" ,"val1","val2","span1","span2" },null); //
		 * "roomid='d1' and areaid='web.wlan.bjtu'" if(t!=null){
		 * 
		 * for(int i=0;i<t.length;i++){ String s=""; for(int
		 * j=0;j<t[i].length;j++){ s+=t[i][j]+", "; } Log.d("test",s+"\n"); }
		 * }else{ Log.d("test","none records"); }
		 */
		/*
		 * RSSILBS rlbs=new RSSILBS(); rlbs.setWifiDB(wdb); String loc=
		 * rlbs.GetFinalTarget("web.wlan.bjtu",
		 * "00:22:66:32,-12;33:22:AC:12,-70"); if(loc!=null)
		 * Log.d("location",loc); else Log.d("location","no find");
		 */

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			 Intent intent=new Intent(WifiLBSActivity.this,WifiListActivity.class);
			 startActivity(intent);
			 finish();
			 /*
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("关闭程序");
			builder.setMessage("确定关闭本程序?");
			// builder.setIcon(R.drawable.stat_sys_warning);
			builder.setPositiveButton("关闭",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						

							Intent startMain = new Intent(Intent.ACTION_MAIN);
							startMain.addCategory(Intent.CATEGORY_HOME);
							startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(startMain);
							finish();
							android.os.Process.killProcess(android.os.Process
									.myPid());
							System.exit(0);
						

							
						}
					});
					
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			builder.show();
			*/
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem mi = menu.add(0, 1, 1, "录制地图");
		mi.setIcon(R.drawable.home);
		creatingmap = false;
		mv.setMapMode(creatingmap);
		mi = menu.add(0, 2, 2, "显示图点");
		mi.setIcon(R.drawable.sense);
		mi = menu.add(0, 3, 3, "开始感知");
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		mi.setIcon(R.drawable.begin);
		mi = menu.add(0, 4, 4, "停止感知");
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		mi.setIcon(R.drawable.end);
		mi = menu.add(0, 5, 5, "地图列表");
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		mi.setIcon(R.drawable.list);
		mi = menu.add(1, 6, 6, "录制设置");
		mi.setIcon(R.drawable.setting);
		mi = menu.add(1, 7, 7, "删除地图");
		mi.setIcon(R.drawable.delete);
		mi = menu.add(1, 8, 8, "AP设置");
		mi.setIcon(R.drawable.rss_32x32);
		mi = menu.add(1, 9, 9, "查看描点");
		mi.setIcon(R.drawable.share_32x32);
		mi = menu.add(1, 10, 10, "修饰地图");
		mi.setIcon(R.drawable.eyedropper_32x32);
	//	mi = menu.add(1, 11, 11, "上传到服务器");
//		mi.setIcon(R.drawable.sense);
		mi = menu.add(1, 12, 12, "关于本软件");
		mi.setIcon(R.drawable.home);
		// mi.setIcon(R.drawable.rss_32x32);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean creatingmap = false;

	public void closeTimer() {
		try {
			if (timer != null) {
				timer.cancel();
				timer = null;
				creatingmap = false;
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}
public boolean is_start=false;
public MenuItem cur_item=null;
int[] color=new int[]{Color.BLACK,Color.WHITE,Color.BLUE,Color.RED,Color.rgb(0, 99, 00),Color.YELLOW,Color.GRAY};
String[] txt=new String[] {"黑色","白色","蓝色","红色","绿色","黄色","灰色"};
   String getAppTitle(){
	   return "WLAN定位 - ["+ap_remark+", "+ap_name+"]";
   }
   String getAppTitle(String s){
	   return "WLAN定位 - ["+ap_remark+", "+ap_name+"]("+s+")";
   }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		cur_item=item;
		Stat.ActionStat(this,String.valueOf( item.getItemId()));
		if (item.getGroupId() == 0) {
			if (item.getItemId() == 1) {
				// 创建地图
				if (creatingmap == true) {
					// 首先关闭位置感知
					closeTimer();
					item.setTitle("录制地图");
					setTitle(this.getAppTitle("导航"));
					mv.ShowTips("导航模式，若使用该地图导航请点击[启动]");
					creatingmap=false; 
					mv.initDraw();
				} else {
					creatingmap = true;
					item.setTitle("导航地图");
				//	mv.ShowTips("录制地图");
					setTitle(this.getAppTitle("录制"));
					mv.CreateCanvas();
					mv.ShowTips("Step 1/6. 录制地图中，请在需要录制的区域画上矩形区域的一条对角线，将创建录制区域!");
				}
				mv.setMapMode(creatingmap);
			}
			if (item.getItemId() == 2) {
				// 显示指定ssid已有地图
				try {
				//	mv.ShowTips("显示地图");
					this.mv.CreateCanvas();
					mv.showMap(ap_name);
					mv.ShowTips("显示区域:" + ap_name + "(" + ap_remark + ")");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (item.getItemId() == 3) {
				try{
				 
					is_start=true;
					
				closeTimer();
				// 启动位置感知
				// 显示指定ssid已有地图
				mv.CreateCanvas();
				try {
					mv.ShowTips("开始感知...");
					mv.showMap(ap_name);
					setTitle(this.getAppTitle("感知中..."));
					mv.CreateCanvas();
				} catch (Exception e) {
					e.printStackTrace();
				}
				//在开始之前做一下网络连接测试
				try{
				WifiUtils wu = new WifiUtils();
				String level = wu.GetWifiByName1(context, ap_name);
				if(level==null||level.equals(""))
				throw new Exception();
				}catch(Exception e){
					mv.ShowTips("网络连接测试失败，请确保已启动WiFi模块(不必联网)");
					e.printStackTrace();
					return false;
				}
				timer = new Timer();
				task = new TimerTask() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Message message = new Message();
						try{
						message.what = 1;
						WifiUtils wu = new WifiUtils();
						
						String level = wu.GetWifiByName1(context, ap_name);
						Bundle bd = new Bundle();
						bd.putString("value", level);
						message.setData(bd);
						}catch(Exception e){
							message.what=-1;
							mv.ShowTips("WLAN连接异常，请打开移动设备的WiFi模块!");
							e.printStackTrace();
						}
						handler.sendMessage(message);
					}
				};
				timer.schedule(task, 100, Integer.valueOf(ap_interval));
			
				}catch(Exception e){
					mv.ShowTips("当前WLAN接入点在本区域不可用，请切换AP或重试。");
					timer=null;
					task=null;
					e.printStackTrace();
				}
			}
			if (item.getItemId() == 4) {
		 
				is_start=false;
				setTitle(this.getAppTitle("感知已停止"));
				mv.ShowTips("感知已关闭，若继续使用请点击[启动]!");
				// 关闭位置感知
				closeTimer();
				
			}
			if (item.getItemId() == 5) {
				closeTimer();
				Intent intent = new Intent(WifiLBSActivity.this,
						WifiListActivity.class);
				Bundle bd = new Bundle();
				bd.putString("ap_ssid", ap_name);
				bd.putString("ap_remark", ap_remark);
				bd.putString("ap_interval", ap_interval);
				intent.putExtras(bd);
				startActivity(intent);
				finish();
			}
		}
		if(item.getGroupId()==1){
			if (item.getItemId() == 6) {
				closeTimer();
				// 设置
				Intent intent = new Intent(WifiLBSActivity.this,
						ScanSettingActivity.class);

				Bundle bd = new Bundle();
				bd.putString("ap_ssid", ap_name);
				bd.putString("ap_interval", ap_interval);
				bd.putString("ap_step", "1");
				bd.putString("ap_remark", ap_remark);
				intent.putExtras(bd);
				startActivity(intent);
				finish();
			}
		
			if (item.getItemId() == 7) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("删除该地图");
				builder.setMessage("确定要删除该地图(" + ap_name + ", " + ap_remark
						+ ")吗?");
				 builder.setIcon(R.drawable.delete);
				builder.setPositiveButton("确定删除",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									closeTimer();
									// 清空数据库数据
									mv.deleteMap(ap_name);
									mv.initDraw();
									mv.CreateCanvas();
									mv.ShowTips("该地图数据已清空!，创建或使用新的地图请到[地图列表]");
									
								} catch (Exception e) {
									e.printStackTrace();
									mv.ShowTips("删除出错!");
								}

							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mv.ShowTips("删除取消!");
							}
						});
				builder.show();

				// Toast.makeText(this, "已删除", Toast.LENGTH_SHORT);
			}
			if (item.getItemId() == 8) {
				closeTimer();
				Bundle bd = new Bundle();
				bd.putString("ap_remark", ap_remark);
				bd.putString("ap_ssid", ap_name);
				bd.putString("ap_interval", ap_interval);
				Intent intent = new Intent(WifiLBSActivity.this,
						ShowWifiListActivity.class);
				intent.putExtras(bd);
				startActivity(intent);
				finish();
			}
	
			if (item.getItemId() == 9) {
				Bundle bd = new Bundle();
				bd.putString("ap_remark", ap_remark);
				bd.putString("ap_ssid", ap_name);
				bd.putString("ap_interval", ap_interval);
				Intent intent = new Intent(WifiLBSActivity.this,
						PointsList.class);
				intent.putExtras(bd);
				startActivity(intent);
				finish();
			}
			if(item.getItemId()==10){
				if(mv.drawmode==false){
				new AlertDialog.Builder(this)
				.setTitle("画笔颜色选择")
				.setItems(txt,  new ColorDialog())
				.setNegativeButton("确定", new ColorDialog())
				.show();
				}
				mv.bgid=bg_id;
				mv.setDrawMode();
				bg_id=mv.bgid;
				if(mv.drawmode==true){
					cur_item.setTitle("保存该背景");
					 
				}else{
					cur_item.setTitle("修饰地图背景");
				}
			}
			if(item.getItemId()==11){
				if(mv.isupload==false){
					cur_item.setTitle("不要上传到服务器");
					mv.isupload=true;
				}
				else{
					cur_item.setTitle("上传到服务器");
					mv.isupload=false;
				}
				
			}
			
			
			
			if (item.getItemId() == 12) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("关于WLAN定位1.0");
				builder.setMessage(Config.getAppDesc());
				builder.setIcon(R.drawable.home);
				builder.setPositiveButton("确定",null);

				builder.setNegativeButton("查看介绍",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
									Intent intent=new Intent(WifiLBSActivity.this,MainActivity.class);
									startActivity(intent);
									finish();
							}
						});
				builder.show();
				
				
				 
			}
		}

		return true;
	}
	
 
	public class ColorDialog implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog,
				int which) {
			try {
				 
				mv.drawmode_color=color[which];
				
			} catch (Exception e) {
				e.printStackTrace();
				mv.ShowTips("绘图出错!");
			}

		}
	}
}