package com.bluecanna.wificlock.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bluecanna.appstat.Stat;
 
import com.bluecanna.wificlock.bll.ClockUtils;
import com.bluecanna.wificlock.bll.WiFiMonitor;
import com.bluecanna.wificlock.db.WiFiClockDB;
import com.bluecanna.wificlock.model.WiFiClockModel;
import com.bluecanna.wificlock.utils.BaseItem;
import com.bluecanna.wificlock.utils.ClockListAdapter;
import com.bluecanna.wificlock.utils.Sys;
import com.bluecanna.wificlock.utils.WifiInfo;
import com.bluecanna.wificlock.utils.WifiUtils;

import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class ClockListActivity extends ListActivity {
	WiFiClockModel w = null;
	WiFiClockModel[] wcms = null;
	ClockView cv = null;
	WiFiMonitor wm = null;
	public static long deadline=20140401173930L;
	/*
    public void CheckDead(boolean tt){
 	   try{
     	   String time=Sys.getTimeStamp();
     	   long t=Long.valueOf(time);
     	   if(t>deadline){
     		  Sys.showInfo(this,"�����ر�", "���ã��㵱ǰ�İ汾���ڲ��԰汾���ѹ��ڣ������www.bluecanna.com��ȡ�µİ汾��");
     			if(tt==true){
     		  android.os.Process.killProcess(android.os.Process
 						.myPid());
 				System.exit(0); 
     			}
     	   }
        }catch(Exception e){
     	   e.printStackTrace();
        }
         
 }
 */
	public String getMsg(WiFiClockModel wcm) {
		if (wcm.Message.equals("") || wcm.Message == null) {
			return "����Ϣ";
		} else
			return wcm.Message;
		
	}
	@Override
	public  void  setTitle(CharSequence  msg){
		 	
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	this.CheckDead(true);
		MobclickAgent.onError(this);
		MobclickAgent.onEvent(this, "Login");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main3);
		try {
			Stat.ActionStat(this, "Login");
			String value = getIntent().getExtras().getString("value");
			if (value != null && !value.equals("")) {
				w = (WiFiClockModel) getIntent().getExtras().getSerializable(
						"clock");
				//Sys.cancelNotify(this, R.layout.rsetting);
			//	Sys.showInfo(this, "��λ����", value);
				Sys.cancelNotify(this, R.layout.notify);
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("��λ����");
				builder.setMessage(getMsg(w));
				builder.setIcon(R.drawable.clock);
				builder.setPositiveButton("�رո�����",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								new WiFiClockDB(ClockListActivity.this)
										.openClock(w, false);
								Sys.cancelNotify(ClockListActivity.this,
										R.layout.rsetting);
								Sys.cancelNotify(ClockListActivity.this,
										R.layout.notify);
								finish();
							}
						});
				builder.setNegativeButton("����",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Sys.cancelNotify(ClockListActivity.this,
										R.layout.rsetting);
								Sys.cancelNotify(ClockListActivity.this,
										R.layout.notify);
								finish();
							}
						});
				builder.show();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("is in oncreat");
		this.loadClockList();

		 setTitle("WiFi��λ���� - �����б�(����ͣ����)");
		//setTitle("���� - ��λ���ӷ���");
		if (wcms == null || wcms.length <= 0) {
			System.out.println("create new");
			this.newDemo();
			this.loadClockList();
			Sys.showTips(this, "�Ѵ���ʾ�����ӣ�����[��ʼ����]�˵�����!");

		}
		this.reportList(wcms);
		wm = new WiFiMonitor(this);
		wm.setClockView(null);

	}

	public void reportList(WiFiClockModel[] wcms) {
		if (wcms != null && wcms.length > 1) {
			Map<String, String> purchase = new HashMap<String, String>();
			for (int i = 0; i < wcms.length; i++) {
				purchase.put(wcms[i].id,
						wcms[i].clockId + "," + wcms[i].aplist.size());
			}
			MobclickAgent.onEvent(this, "LoadList", purchase);
		}
	}

	public void newDemo() {
		WiFiMonitor wm = new WiFiMonitor(this);
		WiFiClockModel wcm = wm.getNowWifiClock();
		if (wcm != null && wcm.aplist.size() > 0)
			for (int i = 0; i < wcm.aplist.size(); i++) {
				for (int j = 0; j < wcm.aplist.get(i).mvlist.size(); j++) {
					if (!ClockUtils.isProperSignal(wcm.aplist.get(i).mvlist
							.get(j).value)) {
						wcm.aplist.get(i).mvlist.remove(j);
					}
				}
				if (wcm.aplist.get(i).mvlist.size() <= 0) {
					wcm.aplist.remove(i);
					i--;
				}
			}
		if (wcm.aplist.size() > 1) {
			for (int i = 1; i < wcm.aplist.size(); i++) {
				wcm.aplist.remove(i);
				i--;
			}
		}
		wcm.isRunning = WiFiClockModel.RUN;
		wcm.isOut = false;
		wcm.id = "c" + Sys.getTimeStamp();
		wcm.clockId = "ʾ������";
		wcm.alarmType = WiFiClockModel.VIBRATE_ALARM;
		wcm.Message = "��ǰ������ʾ�����ӡ�";
		WiFiClockDB wcdb = new WiFiClockDB(this);
		wcdb.addWiFiClock(wcm);
		wcdb.close();
	}

	/*
	 * @Override protected void onListItemClick(ListView l, View v, int
	 * position, long id) { Sys.showTips(this, "hello"); Intent intent = new
	 * Intent(ClockListActivity.this, SettingActivity.class); Bundle bd=new
	 * Bundle(); bd.putSerializable("newclock", wcms[position]);
	 * intent.putExtras(bd); this.startActivityForResult(intent, 5); }
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 5) {
			System.out.println("is in activity_result");
			this.loadClockList();
		}
		this.loadClockList();
		// Sys.showTips(this, "�û��ѳ�������,���ڲ˵�ˢ���б�!");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem mi = menu.add(0, 1, 1, "��������");
		mi.setIcon(R.drawable.begin);
		mi = menu.add(0, 2, 2, "�������");
		mi.setIcon(R.drawable.plus);
		mi = menu.add(0, 3, 3, "����б�");
		mi.setIcon(R.drawable.tools_trash);
		mi = menu.add(0, 4, 4, "WiFi���");
		mi.setIcon(R.drawable.search);
		mi = menu.add(0, 5, 5, "ˢ���б�");
		mi.setIcon(R.drawable.refresh);
		mi = menu.add(0, 6, 6, "����");
		mi.setIcon(R.drawable.tools_info);
		// mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		// mi.setIcon(R.drawable.delete);
		return super.onCreateOptionsMenu(menu);
	}

	public String getBestAP(Context context) {
		WifiUtils wu = new WifiUtils();
		android.net.wifi.WifiInfo w = wu.getConnectedWifi(context);
		if (w == null) {
			WifiInfo[] ws = wu.getAllWiFiList(context);
			// Sys.showTips((Activity)context, "count = "+ws.length);
			return ws[0].BSSID;
		} else {
			WifiInfo wi = new WifiInfo();
			wi.SSID = w.getSSID();
			wi.BSSID = w.getBSSID();
			wi.Level = String.valueOf(w.getRssi());
			return wi.SSID;
		}
	}

	public boolean CheckWifi() {

		WifiInfo[] ws = new WifiUtils().getAllWiFiList(this);
		if (ws != null && ws.length > 0) {
			return true;
		} else {
			Toast.makeText(this, "û���ҵ����õ�WiFi����㣬������WLAN!", Toast.LENGTH_LONG)
					.show();
			return false;
			// Sys.openWiFiSetting(this);
		}
	}

	public boolean isrunning = false;
	EditText et = null;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Stat.ActionStat(this, "item_" + item.getItemId());
		if (item.getItemId() == 1) {
			if (isrunning == false) {
				MobclickAgent.onEvent(this, "Start");
				boolean iswifi = CheckWifi();
				if (iswifi == false) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("����WiFi");
					builder.setMessage("�������б�Ӧ��ǰ��Ҫ����WiFi~");
					builder.setIcon(R.drawable.clock);
					builder.setPositiveButton("����WiFi",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Sys.openWiFiSetting(ClockListActivity.this);
								}
							});

					builder.setNegativeButton("����",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
					builder.show();
					return false;
				}
				isrunning = true;
				wm.open();
				/*
				 * Bundle bd=new Bundle(); bd.putString("value1", "֪ͨ�㵽������ط�!");
				 * 
				 * Sys.Notify(R.layout.rsetting, ClockListActivity.this,
				 * Notification.DEFAULT_VIBRATE, ClockListActivity.class,
				 * R.drawable.clock, "WiFi��λ����", "��������λ���ѷ���",bd);
				 */

				setTitle("WiFi��λ����(������)");
				item.setTitle("��ͣ����");
				item.setIcon(R.drawable.busy);
				Sys.showTips(this, "WiFi��λ����������");

			} else {
				try {
					MobclickAgent.onEvent(this, "End");
					wm.close();
					setTitle("WiFi��λ����(����ͣ)");
					item.setTitle("��������");
					isrunning = false;
					item.setIcon(R.drawable.begin);
				 
					Sys.showTips(this, "WiFi��λ������ֹͣ");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
 
		}
		if (item.getItemId() == 3) {

			new AlertDialog.Builder(this)
					.setTitle("��������б�")
					.setIcon(android.R.drawable.stat_sys_warning)
					.setMessage("ȷ����������б���?�˲������ɻָ���������������ӣ���ѡ������Ӻ���ɾ��!")
					.setPositiveButton("ɾ��ȫ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									MobclickAgent.onEvent(
											ClockListActivity.this, "Clear");
									WiFiClockDB wcdb = new WiFiClockDB(
											ClockListActivity.this);
									wcdb.clearWiFiClockAll();
									loadClockList();
									Sys.showShortTips(ClockListActivity.this,
											"�����");
								}
							}).setNegativeButton("ȡ��", null).show();

		}

		if (item.getItemId() == 2) {
			//�½�����
			MobclickAgent.onEvent(this, "TryCreate");
			et = new EditText(this);
			new AlertDialog.Builder(this)
					.setTitle("���������������")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(et)
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									MobclickAgent.onEvent(
											ClockListActivity.this, "Create");
									WiFiClockModel wcm = new WiFiClockModel();
									wcm.isRunning = WiFiClockModel.RUN;
									wcm.isOut = false;
									wcm.id = "c" + Sys.getTimeStamp();
									wcm.clockId = et.getText().toString();
							 
									Intent intent = new Intent(
											ClockListActivity.this,
											SettingActivity.class);
									Bundle bd = new Bundle();
									bd.putSerializable("newclock", wcm);
									bd.putString("isnew", "true");
									intent.putExtras(bd);
									startActivityForResult(intent,0);
									
								}
							}).setNegativeButton("ȡ��", null).show();
		}
		if (item.getItemId() == 4) {
			//���WiFi�б�
			MobclickAgent.onEvent(this, "Check");
			Intent intent = new Intent(ClockListActivity.this,
					ShowWifiListActivity.class);
			startActivityForResult(intent,0);
		}
		if (item.getItemId() == 5) {
			//ˢ�������б�
			MobclickAgent.onEvent(this, "Refresh");
			this.loadClockList();
		}
		if (item.getItemId() == 6) {
			//���ڰ���
			MobclickAgent.onEvent(this, "About");
			this.showAbout();
		}
		return true;
	}

	public void onDestroy() {
		if (wm != null) {
			// wm.close();
		}
		super.onDestroy();
	}

	public String getAlarmType(String index) {
		if (index.equals("0")) {
			return "�޲���";
		}
		if (index.equals("1"))
			return "��ģʽ";
		if (index.equals("2"))
			return "����ģʽ";
		if (index.equals("3"))
			return "�񶯼�����";
		if (index.equals("4"))
			return "LED�ƹ�";
		return index;
	}

	public String getAlarmStatus(String s) {
		if (s.equals(WiFiClockModel.RUN))
			return "��������";
		else
			return "��ͣ��";
	}

	public String getIsOut(boolean b) {
		if (b == true)
			return "����������";
		else
			return "����������";
	}

	public void loadClockList() {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		WiFiClockDB wdb = new WiFiClockDB(this);
		wcms = wdb.getModelList();

		System.out.println("=========wcms===========");
		for (int i = 0; i < wcms.length; i++) {
			System.out.println("wcms[" + i + "]=" + wcms[i].isRunning);
		}
		System.out.println("=========wcms end===========");
		if (wcms != null) {
			for (int i = 0; i < wcms.length; i++) {
				Map<String, String> map1 = new HashMap<String, String>();
				map1.put("bssid", wcms[i].clockId);
				map1.put(
						"ssid",
						"ʹ��" + wcms[i].aplist.size() + "��AP" + " - "
								+ this.getAlarmType(wcms[i].alarmType) + " - "
								+ this.getIsOut(wcms[i].isOut));
				data.add(map1);

			}
			try{
			wdb.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
			
			ClockListAdapter cla = new ClockListAdapter(this, data,
					R.layout.clocklist_v3, new BaseItem(R.id.img,
							String.valueOf(R.drawable.mapclock)), new BaseItem(
							R.id.title, "bssid"), new BaseItem(R.id.info,
							"ssid"));

			cla.setClockList(wcms);
			cla.setActivity(this);

			setListAdapter(cla);
			//ListView lv=(ListView)this.findViewById(R.id.mylist);
			//lv.setAdapter(cla);
			

		} else {
			Toast.makeText(this, "û���ҵ����õ��������ã����½�����", Toast.LENGTH_LONG).show();
			// Sys.openWiFiSetting(this);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			AlertDialog builder = new AlertDialog.Builder(
					ClockListActivity.this)
					.setIcon(R.drawable.wificlock_logo_min1)
					.setTitle("�ر�WiFi��λ����")
					.setMessage("ȷ���ر�Ӧ�û��ߺ�̨����?")
					.setPositiveButton("�رճ���",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									wm.close();
									Stat.ActionStat(ClockListActivity.this,
											"Logout");
									Sys.cancelNotify(ClockListActivity.this,
											R.layout.rsetting);
									Sys.cancelNotify(ClockListActivity.this,
											R.layout.notify);
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
									System.exit(0);
									/* User clicked OK so do some stuff */
								}
							})
					.setNeutralButton("��̨����",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									Intent mHomeIntent = new Intent(
											Intent.ACTION_MAIN);

									mHomeIntent
											.addCategory(Intent.CATEGORY_HOME);
									mHomeIntent
											.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
													| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
									startActivity(mHomeIntent);
									/* User clicked Something so do some stuff */
								}
							})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked Cancel so do some stuff */
								}
							}).show();
			/*
			 * AlertDialog.Builder builder = new AlertDialog.Builder(this);
			 * builder.setTitle("�ر�WiFi��λ����"); builder.setMessage("ȷ���رձ�����?");
			 * builder.setIcon(R.drawable.clock);
			 * builder.setPositiveButton("�ر�", new
			 * DialogInterface.OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * { Sys.cancelNotify(ClockListActivity.this, R.layout.rsetting);
			 * Sys.cancelNotify(ClockListActivity.this, R.layout.notify);
			 * android.os.Process.killProcess(android.os.Process .myPid());
			 * System.exit(0);
			 * 
			 * } });
			 * 
			 * builder.setNegativeButton("ȡ��", new
			 * DialogInterface.OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * {
			 * 
			 * } }); builder.show();
			 */
		}
		return super.onKeyDown(keyCode, event);
	}
    public void showAbout(){
    	View view=Sys.getCustomView(this, R.layout.help);
    	AlertDialog builder2 = new AlertDialog.Builder(this)
    	.setView(view).create();
		   Window window = builder2.getWindow();  
		 
		    WindowManager.LayoutParams lp = window.getAttributes();  
		 
		    // ����͸����Ϊ0.3  
		 
		    lp.alpha = 1f;  
		 
		    window.setAttributes(lp);
		    builder2.show();
    }
    
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
