package com.bluecanna.wifimapbuilder;


import com.bluecanna.wifimapbuilder.CameraActivity.BlobDAL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ScanSettingActivity extends Activity {
	public String ap_ssid="";
	public String ap_interval="";
	public String ap_step="";
	public String ap_remark="";
	public String bg_id="-1";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanmap);
		//读取当前的参数
		/*
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.bggray);
		this.getWindow().setBackgroundDrawable(drawable);
		*/
		Intent intent=getIntent();
		if(intent!=null){
			try{
				setTitle("WLAN定位 - 设置参数");
			Bundle bd=intent.getExtras();
			ap_ssid=bd.getString("ap_ssid");
			ap_interval=bd.getString("ap_interval");
			ap_step=bd.getString("ap_step");
			ap_remark=bd.getString("ap_remark");
			EditText _ssid=(EditText)findViewById(R.id.ap_ssid);
			EditText _interval=(EditText)findViewById(R.id.ap_interval);
			EditText _remark=(EditText)findViewById(R.id.ap_remark);
		    _ssid.setText(ap_ssid);
		    _interval.setText(ap_interval);
		  //  _step.setText(ap_step);
		    _remark.setText(ap_remark);
		    WifiDB wdb = new WifiDB(this);
			bg_id = wdb.getBgId(ap_remark);
		    BlobDAL bdal = new CameraActivity().getBloDAL(this);
			if (!bg_id.equals("-1")&&!bg_id.equals("")) {
				Bitmap bmp=bdal.ReadOneImg(bg_id);
				ImageView iv=(ImageView)findViewById(R.id.imageView1);
				iv.setImageBitmap(bmp);
			}
			}catch(Exception e){
				e.printStackTrace();
				Toast.makeText(this, "Load Error", Toast.LENGTH_SHORT);
			}
		}
        //设置wifi
		/*
		Button btn = (Button) findViewById(R.id.OK);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				EditText _ssid=(EditText)findViewById(R.id.ap_ssid);
				EditText _interval=(EditText)findViewById(R.id.ap_interval);
				//EditText _step=(EditText)findViewById(R.id.ap_step);
				EditText _remark=(EditText)findViewById(R.id.ap_remark);
				Intent intent =new Intent(ScanSettingActivity.this,WifiLBSActivity.class);
				Bundle bd=new Bundle();
				bd.putString("ap_ssid",_ssid.getText().toString());
				bd.putString("ap_interval",_interval.getText().toString());
				//bd.putString("ap_step",_step.getText().toString());
				bd.putString("ap_remark",_remark.getText().toString());
				bd.putInt("key", R.id.OK);
				intent.putExtras(bd);
				startActivity(intent);
				finish();
			}
		});
		*/
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			 Intent intent=new Intent(ScanSettingActivity.this,WifiLBSActivity.class);
			 Bundle bd=new Bundle();
			 bd.putString("ap_ssid", ap_ssid);
			 bd.putString("ap_remark", ap_remark);
			 bd.putString("ap_interval", ap_interval);
			 intent.putExtras(bd);
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
		MenuItem mi = menu.add(0, 2, 2, "拍照导入");
		 mi.setIcon(R.drawable.camera_32x32);
		 mi = menu.add(0, 1, 1, "保存返回");
		 mi.setIcon(R.drawable.arrow_left_32x32);
		
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==1){
			EditText _ssid=(EditText)findViewById(R.id.ap_ssid);
			EditText _interval=(EditText)findViewById(R.id.ap_interval);
			//EditText _step=(EditText)findViewById(R.id.ap_step);
			EditText _remark=(EditText)findViewById(R.id.ap_remark);
			Intent intent =new Intent(ScanSettingActivity.this,WifiLBSActivity.class);
			Bundle bd=new Bundle();
			bd.putString("ap_ssid",_ssid.getText().toString());
			bd.putString("ap_interval",_interval.getText().toString());
			//bd.putString("ap_step",_step.getText().toString());
			bd.putString("ap_remark",_remark.getText().toString());
			bd.putInt("key", 1);
			intent.putExtras(bd);
			startActivity(intent);
			finish();
		}
		if(item.getItemId()==2){
 			Intent intent=new Intent(ScanSettingActivity.this,CameraActivity.class);
			 Bundle bd=new Bundle();
			 bd.putString("ap_ssid", ap_ssid);
			 bd.putString("ap_remark", ap_remark);
			 bd.putString("ap_interval", ap_interval);
			 intent.putExtras(bd);
			 startActivity(intent);
			 finish();
		}
		return true;
	}
}
