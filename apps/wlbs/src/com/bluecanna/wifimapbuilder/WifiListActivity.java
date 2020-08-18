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
		setTitle("WLAN��λ - ��֪��ͼ�б�");
		
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
			// ��ʾ�û��Ƿ񴴽�һ����ͼ?
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("�����µ�ͼ");
			builder.setMessage("��û��һ����֪��ͼŶ��Ҫ��Ҫ����һ��?");
			builder.setIcon(R.drawable.chat_alt_fill_32x32);
			builder.setPositiveButton("�ţ��õ�",
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
			builder.setNegativeButton("�Ժ�~",
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
		MenuItem mi = menu.add(0, 1, 1, "����б�");
		//mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		mi.setIcon(R.drawable.delete);
		mi = menu.add(0, 2, 2, "�½���ͼ");
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		mi.setIcon(R.drawable.new_window_32x32);
		// mi.setIcon(R.drawable.menu_settin`g);
		mi = menu.add(0, 3, 3, "������ͼ");
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		mi.setIcon(R.drawable.camera_32x32);
		mi = menu.add(0, 4, 4, "����WLAN��λ");
		mi.setIcon(R.drawable.home);
		return super.onCreateOptionsMenu(menu);
	}
	EditText et=null;
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 1) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("��յ�ͼ�б�");
			builder.setMessage("ȷ��Ҫɾ��ȫ����ͼ��?��պ��޷��ָ������������!");
			builder.setIcon(R.drawable.delete);
			builder.setPositiveButton("ȫ��ɾ��",
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
			builder.setNegativeButton("ȡ��",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			builder.show();

		}
		// �½���ͼ
		if (item.getItemId() == 2) {
			 et=new EditText(this);
			new AlertDialog.Builder(this)
			.setTitle("�������ͼ����")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setView(et)
			.setPositiveButton("ȷ��",
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
			}).setNegativeButton("ȡ��", null)
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
			builder.setTitle("����WLAN��λ1.0");
			builder.setMessage(Config.getAppDesc());
			builder.setIcon(R.drawable.home);
			builder.setPositiveButton("ȷ��",null);

			builder.setNegativeButton("����չʾ",
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
			builder.setTitle("�ر�WLAN��λ");
			builder.setMessage("ȷ���رձ�����?");
			builder.setIcon(R.drawable.home);
			builder.setPositiveButton("�ر�",
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

			builder.setNegativeButton("ȡ��",
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
