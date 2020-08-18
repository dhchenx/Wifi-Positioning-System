package com.bluecanna.wifimapbuilder;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PointSettingActivity extends Activity {
	String nodeid="";
	String saveid="";
String remark="";	
	EditText pointid=null;
	EditText pointremark=null;
	String ap_ssid="";
	String ap_remark="";
	String ap_interval="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pointsetting);
		/*
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.bggray);
		this.getWindow().setBackgroundDrawable(drawable);
		*/
		setTitle("WLAN定位 - 设置描点的信息");
		//读取当前的参数
		Intent intent=getIntent();
		if(intent!=null){
			try{
			Bundle bd=intent.getExtras();
		      ap_remark=bd.getString("ap_remark");
		        ap_ssid=bd.getString("ap_ssid");
		        ap_interval=bd.getString("ap_interval");
	nodeid=bd.getString("nodeid");
	saveid=bd.getString("saveid");
	remark=bd.getString("remark");
			pointid=(EditText)findViewById(R.id.pointid);
			pointremark=(EditText)findViewById(R.id.pointremark);
			pointid.setText(nodeid);
			pointremark.setText(remark);
		   
			}catch(Exception e){
				pointremark.setText("error");
				e.printStackTrace();
				//Toast.makeText(this, "Load Error", Toast.LENGTH_SHORT);
			}
		}
 /*
        //设置wifi
		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				WifiDB wdb=new WifiDB(PointSettingActivity.this);
				String nodeid=pointid.getText().toString();
				String noderemark=pointremark.getText().toString();
				wdb.updatePointRemark(saveid, nodeid,noderemark);
				wdb.Close();
				Intent intent=new Intent(PointSettingActivity.this,PointsList.class);
				Bundle bd=new Bundle();
				 
				 bd.putString("ap_ssid", ap_ssid);
				 bd.putString("ap_remark", ap_remark);
				 bd.putString("ap_interval", ap_interval);
				 
				 intent.putExtras(bd);
				 startActivity(intent);
				 finish();
				
			}
		});
		Button delbtn = (Button) findViewById(R.id.deletebutton);
		delbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				WifiDB wdb=new WifiDB(PointSettingActivity.this);
				String nodeid=pointid.getText().toString();
				wdb.execute("delete from wifilbs_site where saveid='"+saveid+"' and nodeid='"+nodeid+"'");
				
				wdb.Close();
				Intent intent=new Intent(PointSettingActivity.this,PointsList.class);
				Bundle bd=new Bundle();
				
				 bd.putString("ap_ssid", ap_ssid);
				 bd.putString("ap_remark", ap_remark);
				 bd.putString("ap_interval", ap_interval);
				 
				 intent.putExtras(bd);
				 startActivity(intent);
				 finish();
			}
		});
*/
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(PointSettingActivity.this,PointsList.class);
			Bundle bd=new Bundle();
			 
			 bd.putString("ap_ssid", ap_ssid);
			 bd.putString("ap_remark", ap_remark);
			 bd.putString("ap_interval", ap_interval);
			 
			 intent.putExtras(bd);
			 startActivity(intent);
			 finish();
		}
		return super.onKeyDown(keyCode, event);
	}
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem mi = menu.add(0, 1, 1, "返回");
		mi.setIcon(R.drawable.arrow_left_32x32);
		 mi = menu.add(0, 2, 2, "保存返回");
		 mi.setIcon(R.drawable.document_fill_32x32);
		// mi.setIcon(R.drawable.menu_settin`g);
		mi = menu.add(0, 3, 3, "删除");
		mi.setIcon(R.drawable.delete);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==1){
			Intent intent=new Intent(PointSettingActivity.this,PointsList.class);
			Bundle bd=new Bundle();
			 
			 bd.putString("ap_ssid", ap_ssid);
			 bd.putString("ap_remark", ap_remark);
			 bd.putString("ap_interval", ap_interval);
			 
			 intent.putExtras(bd);
			 startActivity(intent);
			 finish();
		}
		if(item.getItemId()==2){
			WifiDB wdb=new WifiDB(PointSettingActivity.this);
			String nodeid=pointid.getText().toString();
			String noderemark=pointremark.getText().toString();
			wdb.updatePointRemark(saveid, nodeid,noderemark);
			wdb.Close();
			Intent intent=new Intent(PointSettingActivity.this,PointsList.class);
			Bundle bd=new Bundle();
			 
			 bd.putString("ap_ssid", ap_ssid);
			 bd.putString("ap_remark", ap_remark);
			 bd.putString("ap_interval", ap_interval);
			 
			 intent.putExtras(bd);
			 startActivity(intent);
			 finish();
		}
		if(item.getItemId()==3){
			WifiDB wdb=new WifiDB(PointSettingActivity.this);
			String nodeid=pointid.getText().toString();
			wdb.execute("delete from wifilbs_site where saveid='"+saveid+"' and nodeid='"+nodeid+"'");
			
			wdb.Close();
			Intent intent=new Intent(PointSettingActivity.this,PointsList.class);
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
