package com.bluecanna.wifimapbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bluecanna.wifimapbuilder.WifiDB.SaveResult;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class WifiListActivity extends ListActivity {
	private List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	List<SaveResult> srs = null;
	WifiDB wdb = null;
	String ap_ssid = "web.wlan.bjtu";
	String ap_remark = "bjtu";
	String ap_interval = "1000";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifilist);
		/*
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.bggray);
		this.getWindow().setBackgroundDrawable(drawable);
		*/
		setTitle("WLAN定位 - 感知地图列表");
		
		Intent intent = this.getIntent();
		if (intent != null) {
			try {
				Bundle bd = intent.getExtras();
				ap_remark = bd.getString("ap_remark");
				ap_ssid = bd.getString("ap_ssid");
				ap_interval = bd.getString("ap_interval");
			} catch (Exception e) {
				Toast.makeText(this, "Error in recept data of ap_ssid",
						Toast.LENGTH_SHORT);
				e.printStackTrace();
			}
		}
		wdb = new WifiDB(this);
		srs = wdb.getSaveResult();
		if (srs != null && srs.size() > 0) {
			for (int i = 0; i < srs.size(); i++) {
				Map<String, String> map1 = new HashMap<String, String>();
				map1.put("saveid",  srs.get(i).saveid
						);
				map1.put("areaid", srs.get(i).areaid);

				data.add(map1);
			}
			/*
			setListAdapter(new SimpleAdapter(this, data,
					android.R.layout.simple_list_item_2, new String[] {
							"saveid", "areaid" }, new int[] {
							android.R.id.text1, android.R.id.text2 }));
			*/
			setListAdapter(new MyAdapter(this,data,R.drawable.vector,"saveid","areaid"));
		} else {
			// 提示用户是否创建一个地图?
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("创建新地图");
			builder.setMessage("还没有一个感知地图哦，要不要创建一个?");
			builder.setIcon(R.drawable.chat_alt_fill_32x32);
			builder.setPositiveButton("嗯，好的",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String map_name = "Map" + Tools.getTimeStamp();
							Intent intent = new Intent(WifiListActivity.this,
									WifiLBSActivity.class);
							Bundle bd = new Bundle();
							bd.putString("ap_ssid", ap_ssid);
							bd.putString("ap_remark", map_name);
							bd.putString("ap_interval", ap_interval);
							intent.putExtras(bd);
							startActivity(intent);
							finish();
						}
					});
			builder.setNegativeButton("稍后~",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			builder.show();

		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		// super.onListItemClick(l, v, position, id);
		String saveid = srs.get(position).saveid;
		String areaid = srs.get(position).areaid;

		Intent intent = new Intent(WifiListActivity.this, WifiLBSActivity.class);
		intent.putExtra("ap_remark", saveid);
		intent.putExtra("ap_ssid", areaid);
		intent.putExtra("ap_interval", ap_interval);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem mi = menu.add(0, 1, 1, "清空列表");
		//mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		mi.setIcon(R.drawable.delete);
		mi = menu.add(0, 2, 2, "新建地图");
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		mi.setIcon(R.drawable.new_window_32x32);
		// mi.setIcon(R.drawable.menu_settin`g);
		mi = menu.add(0, 3, 3, "创建底图");
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		mi.setIcon(R.drawable.camera_32x32);
		mi = menu.add(0, 4, 4, "关于WLAN定位");
		mi.setIcon(R.drawable.home);
		return super.onCreateOptionsMenu(menu);
	}
	EditText et=null;
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 1) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("清空地图列表");
			builder.setMessage("确定要删除全部地图吗?清空后无法恢复，请谨慎操作!");
			builder.setIcon(R.drawable.delete);
			builder.setPositiveButton("全部删除",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							com.bluecanna.wifimapbuilder.Map map = new com.bluecanna.wifimapbuilder.Map(wdb);
							map.DeleteAllMap();
							Intent intent = new Intent(WifiListActivity.this,
									WifiLBSActivity.class);
							startActivity(intent);
							finish();
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
		// 新建地图
		if (item.getItemId() == 2) {
			 et=new EditText(this);
			new AlertDialog.Builder(this)
			.setTitle("请输入地图名称")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setView(et)
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					String map_name=et.getText().toString();
					if(map_name==null||map_name.equals(""))
						map_name="Map" + Tools.getTimeStamp();
					//String map_name = "Map" + Tools.getTimeStamp();
					Intent intent = new Intent(WifiListActivity.this,
							WifiLBSActivity.class);
					Bundle bd = new Bundle();
					bd.putString("ap_ssid", ap_ssid);
					bd.putString("ap_remark", map_name);
					bd.putString("ap_interval", ap_interval);
					intent.putExtras(bd);
					startActivity(intent);
					finish();
				}
			}).setNegativeButton("取消", null)
			.show();
			
		}
		if (item.getItemId() == 3) {
			String map_name = "Map" + Tools.getTimeStamp();
			Intent intent = new Intent(WifiListActivity.this,
					CameraActivity.class);
			Bundle bd = new Bundle();
			bd.putString("ap_ssid", ap_ssid);
			bd.putString("ap_remark", map_name);
			bd.putString("ap_interval", ap_interval);
			intent.putExtras(bd);
			startActivity(intent);
			finish();
		}
		if(item.getItemId()==4){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("关于WLAN定位1.0");
			builder.setMessage(Config.getAppDesc());
			builder.setIcon(R.drawable.home);
			builder.setPositiveButton("确定",null);

			builder.setNegativeButton("功能展示",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
								Intent intent=new Intent(WifiListActivity.this,MainActivity.class);
								startActivity(intent);
								finish();
						}
					});
			builder.show();
		}
		
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("关闭WLAN定位");
			builder.setMessage("确定关闭本程序?");
			builder.setIcon(R.drawable.home);
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

		}
		return super.onKeyDown(keyCode, event);
	}

}
