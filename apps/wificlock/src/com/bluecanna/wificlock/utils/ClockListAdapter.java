package com.bluecanna.wificlock.utils;

import java.util.List;

import java.util.Map;

import com.bluecanna.wificlock.db.WiFiClockDB;
import com.bluecanna.wificlock.model.WiFiClockModel;
import com.bluecanna.wificlock.view.R;
import com.bluecanna.wificlock.view.SettingActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ClockListAdapter extends BaseAdapter {
	private List<Map<String, String>> mData;
	private LayoutInflater mInflater;
	private int listid = 0;
	public Context context;
	public ListActivity myact = null;

	public void setActivity(ListActivity list) {
		this.myact = list;
	}

	public ClockListAdapter(Context context, List<Map<String, String>> map,
	// int img,String title,String info
			int listid, BaseItem img, BaseItem title, BaseItem info) {

		this.context = context;
		this.listid = listid;
		this.mInflater = LayoutInflater.from(context);
		mData = map;
		this.r_info = info.id;
		this.r_preimg = img.id;
		this.r_title = title.id;
		setKeys(Integer.valueOf(img.value), title.value, info.value);
		wcdb = 
				new WiFiClockDB(this.context);
		
	}

	WiFiClockDB wcdb = null;
	public int r_title = 0;
	public int r_info = 0;
	public int r_preimg = 0;
	public String title = "";
	public String info = "";
	public int preimg = 0;

	public void setKeys(int img, String title, String info) {
		this.preimg = img;
		this.title = title;
		this.info = info;
	}

	public WiFiClockModel[] wcms = null;

	public void setClockList(WiFiClockModel[] w) {
		this.wcms = w;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

//	public int cur_pos = 0;
	ClockViewHolder holder = null;
	class MyCheckClickListener implements OnClickListener{
		private int pos=0;
		private Context context=null;
		public MyCheckClickListener(Context context,int p){
			this.context=context;
			this.pos=p;
		}
		public void onClick(View v) {
			CheckBox cb=(CheckBox)v;
			if(cb.isChecked()){
				wcms[this.pos] = wcdb.openClock(wcms[this.pos], true);
				Sys.showShortTips((Activity)this.context, "∆Ù∂Ø¡Àƒ÷÷”"
						+ wcms[this.pos].clockId);
				cb.setChecked(true);
			}else{
				wcms[this.pos] = wcdb.openClock(wcms[this.pos], false);
				Sys.showShortTips((Activity)this.context, "Õ£÷π¡Àƒ÷÷”"
						+ wcms[this.pos].clockId);
				cb.setChecked(false);
			}
		}
	}
	
	class MyClickListener implements OnClickListener{
		private int pos=0;
		private Context ctx=null;
		public MyClickListener(Context context,int p){
			this.ctx=context;
			this.pos=p;
		}
		public void onClick(View v) {
			Intent intent = new Intent(ctx, SettingActivity.class);
			Bundle bd = new Bundle();
		 //   WiFiClockDB wcdb=new WiFiClockDB(ctx);
			WiFiClockModel wcm=wcdb.getModel(wcms[this.pos].id);
			//wcdb.close();
			bd.putSerializable("newclock", wcm);
			intent.putExtras(bd);
			((Activity) ctx).startActivityForResult(intent, 5);
		}
	}
	class MyCheckedChangeListener implements OnCheckedChangeListener {
		private Context context;
		private int pos=0;
		public MyCheckedChangeListener(Context context,int pos){
			this.context=context;
			this.pos=pos;
		}
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			System.out.println("in checkedevent");
			
			if (arg1) {
				wcms[this.pos] = wcdb.openClock(wcms[this.pos], true);
				Sys.showShortTips((Activity)this.context, "∆Ù∂Ø¡Àƒ÷÷”"
						+ wcms[this.pos].clockId);

			} else {
				wcms[this.pos] = wcdb.openClock(wcms[this.pos], false);
				Sys.showShortTips((Activity) this.context, "Õ£÷π¡Àƒ÷÷”"
						+ wcms[this.pos].clockId);
			}
			
			System.out.println("in checkedevent end");
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		

		if (convertView == null) {

			holder = new ClockViewHolder();

			convertView = mInflater.inflate(listid, null);
			holder.img = (ImageView) convertView.findViewById(this.r_preimg);

			holder.title = (TextView) convertView.findViewById(this.r_title);
			holder.info = (TextView) convertView.findViewById(this.r_info);
			holder.cb = (CheckBox) convertView.findViewById(R.id.isopen);
			// holder.setting=(ImageView)convertView.findViewById(R.id.clock_setting);
			convertView.setTag(holder);

		} else {

			holder = (ClockViewHolder) convertView.getTag();
		}
		holder.img.setOnClickListener(new MyClickListener(context,position));
		holder.title.setOnClickListener(new MyClickListener(context,position));
		holder.info.setOnClickListener(new MyClickListener(context,position));
		holder.img.setBackgroundResource(preimg);

		holder.title.setText((String) mData.get(position).get(title));
		holder.info.setText((String) mData.get(position).get(info));
		System.out.println("=====list======");
		System.out.println(wcms[position].clockId+".isrunning = "+wcms[position].isRunning);
		System.out.println("=====list end======");
		if (wcms[position].isRunning.equals("1"))
			holder.cb.setChecked(false);
		else
			holder.cb.setChecked(true);
		
		holder.cb.setOnClickListener(
				new MyCheckClickListener  (context,position));

		//convertView.setBackgroundResource(R.drawable.listitem3);
		return convertView;
	}

}