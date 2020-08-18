package com.bluecanna.wificlock.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bluecanna.wificlock.db.WiFiClockDB;
import com.bluecanna.wificlock.model.APModel;
import com.bluecanna.wificlock.model.WiFiClockModel;
import com.bluecanna.wificlock.model.WiFiModel;
import com.bluecanna.wificlock.utils.BaseItem;
import com.bluecanna.wificlock.utils.BaseListAdapter;
import com.bluecanna.wificlock.utils.Sys;
import com.bluecanna.wificlock.utils.WifiInfo;
import com.bluecanna.wificlock.utils.WifiUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;

public class APSetting extends ListActivity {
	EditText et = null;
	public String apname = null;

	List<WifiInfo> ws = null;
	WiFiClockModel wcm = null;

	public String getAPName() {
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bd = intent.getExtras();
			wcm = (WiFiClockModel) bd.getSerializable("newclock");

			return bd.getString("apname");
		}
		return null;
	}

	public int getAPIndex() {
		for (int i = 0; i < wcm.aplist.size(); i++) {
			if (wcm.aplist.get(i).name.equalsIgnoreCase(apname)) {
				return i;
			}
		}
		return -1;
	}

	public void loadList() {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		if (ws != null && ws.size() > 0) {

			for (int i = 0; i < ws.size(); i++) {

				Map<String, String> map1 = new HashMap<String, String>();
				map1.put("bssid", ws.get(i).BSSID);
				map1.put("ssid", ws.get(i).Level);
				data.add(map1);

			}

			setListAdapter(new BaseListAdapter(
					this,
					data,
					R.layout.vlist2,
					new BaseItem(R.id.img, String.valueOf(R.drawable.chartuser)),
					new BaseItem(R.id.title, "bssid"), new BaseItem(R.id.info,
							"ssid")));

		} else {
			// Sys.showTips(this, "当前该AP没有可用接入点，请稍后再试");
		}

	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main3);

		apname = getAPName();
		if (apname != null && !apname.equals("")) {

			Intent intent = getIntent();
			if (intent != null) {
				// Bundle bd = intent.getExtras();
				// wcm = (WiFiClockModel) bd.getSerializable("newclock");
				if (wcm != null)
					for (int i = 0; i < wcm.aplist.size(); i++) {
						if (wcm.aplist.get(i).name.equalsIgnoreCase(apname)) {
							List<WifiInfo> wi = new ArrayList<WifiInfo>();
							for (int j = 0; j < wcm.aplist.get(i).mvlist.size(); j++) {
								WifiInfo w = new WifiInfo();
								w.SSID = apname;
								w.Level = String
										.valueOf(wcm.aplist.get(i).mvlist
												.get(j).value);
								w.BSSID = wcm.aplist.get(i).mvlist.get(j).mac;
								wi.add(w);
							}
							ws = wi;
							break;
						}

					}
				this.loadList();
			}
		
		}

		if (ws == null) {
			Sys.showTips(this, "没有数据");
			return;
		} else
			Sys.showTips(this, "找到" + ws.size() + "个AP!");
		selecteds = new boolean[ws.size()];
		mullist = new String[ws.size()];
		for (int i = 0; i < ws.size(); i++) {
			selecteds[i] = false;
			mullist[i] = ws.get(i).BSSID;
		}
		setTitle("WiFi定位闹钟 - AP:" + apname);
	}

	SeekBar sb = null;
	int cur_position = 0;

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		cur_position = position;
		if (selecteds[position]) {
			Sys.showTips(this, "你已将该路由排除到闹钟激活条件，故不能选择！");
			return;
		}
		sb = new SeekBar(this);
		sb.setMax(100);
		int p = -1 * Integer.valueOf(ws.get(position).Level);
		sb.setProgress(p);
		new AlertDialog.Builder(this).setTitle("调整闹钟精度")
				.setIcon(android.R.drawable.ic_dialog_info).setView(sb)

				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Sys.showTips(APSetting.this,
						// String.valueOf(sb.getProgress()));
						WifiInfo w = ws.get(cur_position);
						w.Level = String.valueOf(-1 * sb.getProgress());
						ws.set(cur_position, w);
						loadList();
						Sys.showTips(APSetting.this, "已设置");
					}
				}).setNegativeButton("取消", null).show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			String result_ssid = data.getStringExtra("ssid");
			et.setText(result_ssid);
			break;

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem mi = menu.add(0, 1, 1, "删除多个");
		mi.setIcon(R.drawable.delete);
		mi = menu.add(0, 2, 2, "保存返回");
		mi.setIcon(R.drawable.tools_check);
		mi = menu.add(0, 3, 3, "使用当前AP");
		mi.setIcon(R.drawable.tools_download);
		mi = menu.add(0, 4, 4, "查看该信号时钟");
		mi.setIcon(R.drawable.smallclock);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			showDialog(0);
		}
		if (item.getItemId() == 2) {
			APModel apm = new APModel();
			apm.name = apname;
			for (int i = 0; i < selecteds.length; i++) {
				if (selecteds[i] == false) {
					WiFiModel wm = new WiFiModel();
					wm.mac = ws.get(i).BSSID;
					wm.value = Integer.valueOf(ws.get(i).Level);
					apm.mvlist.add(wm);
				}
			}
			int k = getAPIndex();
			if (k != -1)
				wcm.aplist.set(k, apm);
			else
				wcm.aplist.add(apm);
			this.callBack();
		}
		if (item.getItemId() == 3) {
			ws = new WifiUtils().getWifiListByName(this, apname);
			if (ws != null) {
				this.loadList();
				selecteds = new boolean[ws.size()];
				mullist = new String[ws.size()];
				for (int i = 0; i < ws.size(); i++) {
					selecteds[i] = false;
					mullist[i] = ws.get(i).BSSID;
				}
			} else
				Sys.showTips(this, "当前环境没有AP");

		}
		if (item.getItemId() == 4) {
			Intent intent=new Intent(APSetting.this,DisplayWifiActivity.class);
			Bundle bd=new Bundle();
			bd.putString("apname", apname);
			intent.putExtras(bd);
			startActivityForResult(intent,1);
		}
		return true;
	}

	public void callBack() {
		Intent intent = new Intent(APSetting.this, SettingActivity.class);
		Bundle bd = new Bundle();
		bd.putSerializable("newclock", wcm);
		intent.putExtras(bd);
		setResult(3, intent);
		finish();
	}

	public boolean[] selecteds = null;
	public String[] mullist;

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 0) {
			Dialog dialog = null;
			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("勾选需要删除的定位MAC");
			builder.setIcon(R.drawable.ic_launcher);
			DialogInterface.OnMultiChoiceClickListener mutiListener = new DialogInterface.OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which,
						boolean isChecked) {
					selecteds[which] = isChecked;
				}
			};

			builder.setMultiChoiceItems(mullist, selecteds, mutiListener);
			DialogInterface.OnClickListener btnListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					String str = "";
					for (int i = 0; i < selecteds.length; i++) {
						if (selecteds[i] == true) {
							str += i + ",";
						}
					}
					//Sys.showTips(APSetting.this, "去掉了" + str);
				}
			};
			builder.setPositiveButton("确定", btnListener);
			dialog = builder.create();
			return dialog;
		}
		return null;

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			this.callBack();
		}
		return super.onKeyDown(keyCode, event);
	}

}
