package com.bluecanna.wificlock.bll;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.bluecanna.test.TipHelper;

import com.bluecanna.wificlock.db.WiFiClockDB;
import com.bluecanna.wificlock.model.APModel;
import com.bluecanna.wificlock.model.WiFiClockModel;
import com.bluecanna.wificlock.model.WiFiModel;
import com.bluecanna.wificlock.utils.Sys;
import com.bluecanna.wificlock.utils.WifiInfo;
import com.bluecanna.wificlock.utils.WifiUtils;
import com.bluecanna.wificlock.view.ClockView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class WiFiMonitor implements IWiFiMonitor {
	private Context context;

	public WiFiMonitor(Context context) {
		this.context = context;
	}

	public static int step = 0;
	public TextView tv = null;
	public Timer timer = null;
	public TimerTask task = null;
	ClockView cv = null;
	public boolean isCurrent = false;
	public boolean isSingleCurrent = false;

	public void setIsCurrent(boolean v) {
		this.isCurrent = v;
	}

	public String single_apname = "";

	public void setSingleCurrent(boolean v) {
		this.isSingleCurrent = v;
	}

	public void setTextView(TextView tv) {
		this.tv = tv;
	}

	public void setClockView(ClockView cv) {
		this.cv = cv;
	}

	public WiFiClockModel getDifferenceOfModel(WiFiClockModel cur,
			WiFiClockModel target) {

		return null;
	}

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 定时器
			if (msg.what == 1) {
				Bundle bd = msg.getData();
				String s = bd.getString("bs");
				Sys.p("result=" + s);
				if (s != null && !s.equals("")) {
					step++;
					String[] s1 = s.split(",");
					boolean[] bs = new boolean[s1.length];
					for (int i = 0; i < bs.length; i++) {
						if (s1[i].equals("0"))
							bs[i] = false;
						else
							bs[i] = true;
					}

					List<WiFiClockModel> wlist = getRunningModel();
					String str = "";
					try{
					for (int i = 0; i < wlist.size(); i++) {
						if (bs[i] == true) {
							str += wlist.get(i).id + ", ";
						}
					}
					Sys.p("place=" + str);
					}catch(Exception e){
						e.printStackTrace();
						super.handleMessage(msg);
					}
					if (str.equals("") && isCurrent != true&&bs!=null) {
						ClockImp ci = new ClockImp(context, wlist);
						ci.setB(bs);
						ci.showTips();
						// close();
					}
					/*
					 * if(!str.equals("")){ Log.d("test","need vibrate");
					 * 
					 * TipHelper.Vibrate((Activity)context, 1000L); //
					 * Sys.showTips((Activity)context,"你到了地方为："+str); //close();
					 * }
					 */

					if (tv != null) {
						tv.setText(step + ": " + str);
					}
					if (!str.equals("") && isCurrent != true) {
						ClockImp ci = new ClockImp(context, wlist);
						ci.setB(bs);
						ci.showTips();
						// close();
					}
					if (cv != null && isCurrent == true) {
						WiFiClockModel cur = getNowWifiClock();
						if (cur != null && cur.aplist.size() > 0) {

							Log.d("test",
									"value = "
											+ cur.aplist.get(0).mvlist.get(0).value);
							// cv.DisplayWiFiSignNow(cur.aplist.get(0).mvlist
							// .get(0).value);
							if (isSingleCurrent) {
								WifiUtils wu = new WifiUtils();
								List<WifiInfo> wis = wu.getWifiListByName(
										context, single_apname);
								if (wis != null && wis.size() > 0)
									cv.DisplayWiFiSignNow(Float.valueOf(wis
											.get(0).Level));
							} else
								cv.DisplayWiFiSignNow(cur);

						}

					}
				}
			}
			super.handleMessage(msg);
		}
	};

	public boolean close() {
		try {
			timer.cancel();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean open() {
		try {
			this.Monitor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public int std = 5;

	public boolean checkIsMatchWifiModel(WiFiModel w, List<WiFiModel> wfms) {
		for (int i = 0; i < wfms.size(); i++) {
			if (w.mac.equals(wfms.get(i).mac)) {
				int cur_value = Integer.valueOf(w.value);
				int ref_value = Integer.valueOf(wfms.get(i).value);
				if (cur_value >= ref_value - std
						&& cur_value <= ref_value + std && cur_value >= -90) {
					Sys.p("find a matched =" + wfms.get(i).mac);
					return true;
				} else
					return false;
			}
		}
		return false;
	}

	public boolean checkIsMatch(APModel apm, WiFiClockModel refmodel) {

		int count = 0;
		int apcount = refmodel.aplist.size();
		for (int i = 0; i < refmodel.aplist.size(); i++) {
			// Sys.p("compare:" + apm.name + " VS " +
			// refmodel.aplist.get(i).name);
			if (refmodel.aplist.get(i).name.equalsIgnoreCase(apm.name)) {
				List<WiFiModel> wms = refmodel.aplist.get(i).mvlist;

				if (wms != null && wms.size() > 0) {
					for (int j = 0; j < apm.mvlist.size(); j++) {
						boolean b = this.checkIsMatchWifiModel(
								apm.mvlist.get(j), wms);
						if (b) {
							Sys.p("apname is " + apm.name);
							count++;
						}
					}
				}

				break;
			}
		}
		System.out.println("apcount="+apcount+",count="+count);
		
		if(count>=1)
			return true;
		return false;
	}

	static int out_count = 0;

	public boolean check(WiFiClockModel nowmodel, WiFiClockModel refmodel) {
		Sys.p("is in check()");
		int count = 0;
		std=refmodel.std;
		for (int i = 0; i < nowmodel.aplist.size(); i++) {
			Sys.p("check:" + nowmodel.aplist.get(0).name);
			boolean b = this.checkIsMatch(nowmodel.aplist.get(i), refmodel);
			if (b == true  ) {
				Sys.p("check.find a model,clockid=" + refmodel.clockId + ","
						+ nowmodel.aplist.get(i).name);
				count++;
			}
		}
		int now_size = refmodel.aplist.size();
		/*
		if (now_size == 1 && count == 1) {
			out_count = 0;
			return true;
		}
		if (now_size == 2 && count == 2) {
			out_count = 0;
			return true;
		}
		if (now_size == 3 && count >= 2) {
			out_count = 0;
			return true;
		}
		if (now_size > 3 && count >= (int) (now_size * 0.7)) {
			out_count = 0;
			return true;
		}
		*/
		if(count>=1){
			out_count = 0;
			return true;
		}
		
		return false;
	}

	public WiFiClockModel getNowWifiClock() {
		WifiUtils wu = new WifiUtils();
		WifiInfo[] ws = new WifiUtils().getAllWiFiList(context);
		WiFiClockModel wcm = new WiFiClockModel();
		wcm.id = "0";
		wcm.clockId = "now";
		if (ws != null)
			for (int i = 0; i < ws.length; i++) {
				APModel ap = new APModel();
				ap.name = ws[i].SSID;
				List<WifiInfo> ww = wu.getWifiListByName(context, ap.name);
				if (ww != null) {
					for (int j = 0; j < ww.size(); j++) {
						WiFiModel wfm = new WiFiModel();
						wfm.mac = ww.get(j).BSSID;
						wfm.value = Integer.valueOf(ww.get(j).Level);
						ap.mvlist.add(wfm);
						// test
						if (j == 0) {
							if (cv != null) {
								// cv.DisplayWiFiSignNow(Float.valueOf(wfm.value));
								// Activity act=(Activity)context;
								// act.setTitle(wfm.value);
							}
						}
					}
				}
				wcm.aplist.add(ap);
			}
		return wcm;
	}

	public boolean[] checkAll() {
		List<WiFiClockModel> wlist = getRunningModel();
		Sys.p("wlist.length=" + wlist.size());
		boolean[] bs = new boolean[wlist.size()];
		for (int i = 0; i < bs.length; i++)
			bs[i] = false;
		WiFiClockModel now = this.getNowWifiClock();
		for (int i = 0; i < now.aplist.size(); i++) {
			// Sys.p("nowmodel=" + now.aplist.get(i).name);
		}
		for (int i = 0; i < wlist.size(); i++) {
			// Sys.p("dbclock = " + wlist.get(i).clockId);
			bs[i] = check(now, wlist.get(i));
		}

		
		return bs;
	}

	public List<WiFiClockModel> getRunningModel() {
		WiFiClockModel[] ws = new WiFiClockDB(context).getModelList();
		if (ws != null) {
			// Sys.p("find clock count=" + ws.length);
			List<WiFiClockModel> wcms = new ArrayList<WiFiClockModel>();
			for (int i = 0; i < ws.length; i++) {
				// Sys.p("ws" + i + ":" + ws[i].clockId + "," +
				// ws[i].isRunning);
				if (ws[i].isRunning.equals(WiFiClockModel.RUN)) {
					wcms.add(ws[i]);
				}
			}
			return wcms;
		}
		return null;
	}

	public int getSenseCount(boolean[] check) {
		int count = 0;
		for (int i = 0; i < check.length; i++) {
			if (check[i] == true)
				count++;
		}
		return count;
	}

	public void displayClocks(WiFiClockModel wcm) {
		int count = wcm.aplist.size();
		if (count != 0) {

		}

	}

	public void Monitor() {
		close();
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				try {
					message.what = 1;
					boolean check_result[] = checkAll();
					if (check_result != null) {
						String s = "";
						for (int i = 0; i < check_result.length; i++) {
							if (check_result[i] == true) {
								if (i != check_result.length - 1)
									s += "1,";
								else
									s += "1";
							}
							if (check_result[i] == false) {
								if (i != check_result.length - 1)
									s += "0,";
								else
									s += "0";
							}
						}
						Bundle bd = new Bundle();
						bd.putString("bs", s);
						message.setData(bd);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				handler.sendMessage(message);
			}
		};
		timer.schedule(task, 100, 2000);
	}
}
