package com.bluecanna.wificlock.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bluecanna.wificlock.model.WiFiClockModel;
import com.bluecanna.wificlock.utils.BaseItem;
import com.bluecanna.wificlock.utils.BaseListAdapter;
import com.bluecanna.wificlock.utils.Sys;
import com.bluecanna.wificlock.utils.WifiInfo;
import com.bluecanna.wificlock.utils.WifiUtils;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class WiFiListActivity extends ListActivity {
	
	WifiInfo[] ws;
	WiFiClockModel wcm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main3);
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bd = intent.getExtras();
			wcm = (WiFiClockModel) bd.getSerializable("newclock");
		}

		loadWiFiList();
		setTitle("WiFi定位闹钟 - 选择定位AP");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String s = ws[position].SSID;
		if(this.isExistInModel(s)){
			Sys.showTips(this, "该AP已经存在该闹钟中；若修改，请返回选择[修改AP]");
		 return;
		}
		Intent intent = new Intent(WiFiListActivity.this, SettingActivity.class);
		intent.putExtra("ssid", s);
		setResult(RESULT_OK, intent);
		finish();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Sys.showTips(this, "用户已尝试设置,请在菜单刷新列表!");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem mi = menu.add(0, 1, 1, "刷新列表");
		mi.setIcon(R.drawable.refresh);
		// mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		// mi.setIcon(R.drawable.delete);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			this.loadWiFiList();
		}
		return true;
	}

	public boolean isExistInModel(String ap) {
		if (wcm != null) {
			if (wcm.aplist.size() > 0)
				for (int i = 0; i < wcm.aplist.size(); i++) {
					if (wcm.aplist.get(i).name.equalsIgnoreCase(ap)) {
						return true;
					}
				}
		}
		return false;
	}
	public String getRSSIStrength(String s){
		int c=Integer.valueOf(s);
		if(c>-75&&c<=-25){
			return "满足定位AP需求("+c+"dbm)";
		}
		else
			return "不满足定位AP需求("+c+"dbm)";
	}
	
	public void loadWiFiList() {
	 List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		ws = new WifiUtils().getAllWiFiList(this);
		if (ws != null) {
			for (int i = 0; i < ws.length; i++) {
				
				Map<String, String> map1 = new HashMap<String, String>();
				map1.put("bssid", ws[i].SSID);
				map1.put("ssid", ws[i].BSSID+" - " +this.getRSSIStrength(ws[i].Level));

					data.add(map1);
				
			}
			setListAdapter(new BaseListAdapter(this, data, R.layout.vlist2,
					new BaseItem(R.id.img, String
							.valueOf(R.drawable.chartuser)),
					new BaseItem(R.id.title, "bssid"), new BaseItem(R.id.info,
							"ssid")));
		} else {
			Toast.makeText(this, "没有找到可用的WiFi接入点，请设置WLAN!", Toast.LENGTH_LONG)
					.show();
			Sys.openWiFiSetting(this);
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(WiFiListActivity.this,APSetting.class);
			setResult(11,intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
