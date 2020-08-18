package com.bluecanna.wificlock.view;
 
import com.bluecanna.wificlock.bll.ClockUtils;
import com.bluecanna.wificlock.bll.WiFiMonitor;
import com.bluecanna.wificlock.db.WiFiClockDB;
import com.bluecanna.wificlock.model.APModel;
import com.bluecanna.wificlock.model.WiFiClockModel;
import com.bluecanna.wificlock.utils.Sys;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingActivity extends Activity   {
	EditText et = null;
	WiFiClockModel wcm = null;
	boolean isnew=false;
	public void loadNewModel() {
		Intent intent = getIntent();
		Bundle bd = intent.getExtras();
		wcm = (WiFiClockModel) bd.getSerializable("newclock");
		String s=bd.getString("isnew");
		if(s!=null&&!s.equals("")&&s.equals("true")){
			isnew=true;
			this.autoCreate();
		}
		if (wcm != null) {
			Sys.showTips(this, wcm.clockId);
		} else
			Sys.showTips(this, "新建闹钟发生错误");

	}

	RadioGroup aplist;
	SeekBar sb=null;
	public void loadAPList() {
		aplist.removeAllViews();
		if (wcm != null)

			for (int i = 0; i < wcm.aplist.size(); i++) {
				RadioButton item = new RadioButton(this);
				item.setText(wcm.aplist.get(i).name);
				item.setId(i);
				aplist.addView(item);
			}

	}

	private ArrayAdapter<String> adapter;
   CheckBox cb=null;
   CheckBox cb2=null;
   EditText t_msg=null;
   public void renderSetting(){
	  t_msg=(EditText)findViewById(R.id.message);
	  t_msg.setText(wcm.Message);
		et = (EditText) findViewById(R.id.clock_id);
		et.setText(wcm.clockId);
		aplist = (RadioGroup) findViewById(R.id.radioGroup2);
		this.loadAPList();
		this.renderSpinner(R.id.spinner1);
	   mSpinner.setSelection(Integer.valueOf(wcm.alarmType));
	   cb=(CheckBox)findViewById(R.id.checkBox1);
	   cb2=(CheckBox)findViewById(R.id.checkBox2);
	   if(wcm.isRunning.equals(WiFiClockModel.RUN)){
		   cb.setChecked(true);
	   }
	   else
		  cb.setChecked(false);
	   if(wcm.isOut){
		   cb2.setChecked(true);
	   }
	   else
		  cb2.setChecked(false);
	   
	   cb.setOnClickListener(new OnClickListener(){
		   public void onClick(View v){
			   if(cb.isChecked()){
				 wcm.isRunning=WiFiClockModel.RUN;
				 Sys.showTips(SettingActivity.this, "启动了闹钟");
			   }else{
				   wcm.isRunning=WiFiClockModel.STOP;
				   Sys.showTips(SettingActivity.this, "停止了闹钟");
			   }
		   }
	   });
	   cb2.setOnClickListener(new OnClickListener(){
		   public void onClick(View v){
			   if(cb2.isChecked()){
				 wcm.isOut=true;
				 Sys.showTips(SettingActivity.this, "亲，离开该区域将会提醒哦!");
			   }else{
				   wcm.isOut=false;
				   Sys.showTips(SettingActivity.this, "处于本区域才提醒呢!");
			   }
		   }
	   });
	   
	   sb=(SeekBar)findViewById(R.id.seekBar1);
	   sb.setMax(40);
	   sb.setProgress(wcm.std);
	   TextView tv=(TextView)findViewById(R.id.textView1);
       tv.setText("感应误差("+sb.getProgress()+")");
	   sb.setOnSeekBarChangeListener( new OnSeekBarChangeListener()
       {
           
           @Override
           public void onStopTrackingTouch(SeekBar seekBar)
           {
               Integer i = new Integer(seekBar.getProgress());
               wcm.std=i;
               TextView tv=(TextView)findViewById(R.id.textView1);
               tv.setText("感应精度("+i+")");
           }
           
           @Override
           public void onStartTrackingTouch(SeekBar seekBar)
           {
               
               //Toast.makeText(getApplicationContext(), "onStartTrackingTouch", Toast.LENGTH_SHORT).show();
           }
           
       
           @Override
           public void onProgressChanged(SeekBar seekBar, int progress,
                   boolean fromUser)
           {
               //Toast.makeText(getApplicationContext(), "onProgressChanged", Toast.LENGTH_SHORT).show();
           }
       });
	   setTitle("WiFi定位闹钟 -  设置");
	   
   }
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		 setContentView(R.layout.setting);
		 this.loadNewModel();
		 this.renderSetting();
 
	      
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			String result_ssid = data.getStringExtra("ssid");
			APModel ap = new APModel();
			ap.name = result_ssid;
			wcm.aplist.add(ap);
			
			break;
		case 3:
			wcm = (WiFiClockModel) data.getSerializableExtra("newclock");
			
		//	 Sys.showTips(this,String.valueOf( wcm.aplist.get(0).mvlist.size()));
			break;
		}
		this.loadAPList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem mi = menu.add(0, 1, 1, "添加定位AP");
		mi.setIcon(R.drawable.plus);
		mi = menu.add(0, 2, 2, "修改所选AP");
		mi.setIcon(R.drawable.pencil);
		mi = menu.add(0, 3, 3, "保存该闹钟");
		mi.setIcon(R.drawable.tools_check);
		mi = menu.add(0, 4, 4, "删除该AP");
		mi.setIcon(R.drawable.delete);
		mi = menu.add(0, 5,5, "导入当前环境");
		mi.setIcon(R.drawable.tools_download);
		mi = menu.add(0, 6,6, "删除该闹钟");
		mi.setIcon(R.drawable.menu_exit);
		// mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		// mi.setIcon(R.drawable.delete);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			Intent intent = new Intent(SettingActivity.this,
					WiFiListActivity.class);
			Bundle bd = new Bundle();
			bd.putSerializable("newclock", wcm);
			intent.putExtras(bd);
			startActivityForResult(intent, 0);
		}
		if (item.getItemId() == 2) {
			int c = aplist.getCheckedRadioButtonId();
			if (c == -1) {
				Sys.showTips(this, "请选择一个AP来执行修改");
			}
			RadioButton r = (RadioButton) aplist.getChildAt(c);
			if (r != null) {
				String apname = r.getText().toString();
				Intent intent = new Intent(SettingActivity.this,
						APSetting.class);
				Bundle bd = new Bundle();
				bd.putString("apname", apname);
				bd.putSerializable("newclock", wcm);
				intent.putExtras(bd);
				startActivityForResult(intent, 0);
			}

		}
		if (item.getItemId() == 3) {
			saveToModel();
			/*
			 * Intent intent=new
			 * Intent(SettingActivity.this,MainActivity.class); Bundle bd=new
			 * Bundle(); bd.putSerializable("newclock", wcm);
			 * intent.putExtras(bd); setResult(4,intent); finish();
			 */
			WiFiClockDB wcdb = new WiFiClockDB(this);
			int result = wcdb.addWiFiClock(wcm);
			if (result == WiFiClockDB.SUCCESS) {
				Sys.showTips(this, "保存成功");
			} else
				Sys.showTips(this, "保存数据库失败!");
		}
		if(item.getItemId()==4){
			int c = aplist.getCheckedRadioButtonId();
			if (c == -1) {
				Sys.showTips(this, "请选择一个AP来执行修改");
			}else{
			aplist.removeViewAt(c);
			wcm.aplist.remove(c);
			}
		}
		if(item.getItemId()==5){
			this.autoCreate();
			Sys.showTips(this, "已将当前WiFi环境自动导入该闹钟");
		}
		if(item.getItemId()==6){
			WiFiClockDB wcdb = new WiFiClockDB(this);
			wcdb.deleteWiFiClock(wcm.id);
			Sys.showShortTips(this, "已删除"+wcm.clockId);
			 Intent intent=new Intent(SettingActivity.this,ClockListActivity.class);
			 setResult(5,intent);
			 finish();
		}

		return true;
	}

	private static final String[] arr = { "无操作", "振动", "响铃", "振动及响铃" };
	Spinner mSpinner ;
	public void renderSpinner(int mSpinnerId) {

		 mSpinner = (Spinner) findViewById(mSpinnerId);
		
		// 将可选内容和adapter连接起来
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arr);
		// 设置adapter的下拉风格
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将adapter添加到Spinner中去
		mSpinner.setAdapter(adapter);
		// 添加Spinner事件监听
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
			//	Sys.showTips(SettingActivity.this, "选择了" + arr[arg2]);
				String result = WiFiClockModel.NOACTION;
				switch (arg2) {
				case 1:
					result = WiFiClockModel.VIBRATE;
					break;
				case 2:
					result = WiFiClockModel.ALARM;
					break;
				case 3:
					result = WiFiClockModel.VIBRATE_ALARM;
					break;
				case 4:
					result="4";
				}
				wcm.alarmType = result;
				arg0.setVisibility(View.VISIBLE);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	public void autoCreate(){
		WiFiMonitor wm=new WiFiMonitor(this);
		WiFiClockModel wcm=wm.getNowWifiClock();
		if(wcm!=null&&wcm.aplist.size()>0)
		for(int i=0;i<wcm.aplist.size();i++){
			for(int j=0;j<wcm.aplist.get(i).mvlist.size();j++){
				if(!ClockUtils.isProperSignal(wcm.aplist.get(i).mvlist.get(j).value)){
					wcm.aplist.get(i).mvlist.remove(j);
				}
			}
			if(wcm.aplist.get(i).mvlist.size()<=0){
				wcm.aplist.remove(i);
				i--;
			}
		}
		
		if(wcm.aplist.size()>1){
			for(int i=1;i<wcm.aplist.size();i++){
				wcm.aplist.remove(i);
				i--;
			}
		}
		
		wcm.clockId=this.wcm.clockId;
		wcm.id=this.wcm.id;
		wcm.alarmType=this.wcm.alarmType;
		wcm.isRunning=this.wcm.isRunning;
		wcm.isOut=this.wcm.isOut;
		this.wcm=wcm;
		this.renderSetting();
	}
	public void saveToModel(){
		wcm.clockId=et.getText().toString();
		wcm.Message=t_msg.getText().toString();
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		saveToModel();
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			WiFiClockDB wcdb = new WiFiClockDB(this);
			System.out.println("saveclock_isrunning="+wcm.isRunning);
			int result = wcdb.addWiFiClock(wcm);
			if (result == WiFiClockDB.SUCCESS) {
				Sys.showTips(this, "保存成功");
				WiFiClockModel w1=wcdb.getModel(wcm.id);
				System.out.println("saveclock_isrunning_saved="+w1.isRunning);	
			} else
				Sys.showTips(this, "保存数据库失败!");
			wcdb.close();
			if(!isnew){
			 Intent intent=new Intent(SettingActivity.this,ClockListActivity.class);
			 setResult(5,intent);
			 finish();
			}else{
				 Intent intent=new Intent(SettingActivity.this,ClockListActivity.class);
				 setResult(0,intent);
				 finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	 
}
