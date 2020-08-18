package com.bluecanna.wificlock.utils;

import java.util.List;
 
import java.util.Map;

import com.bluecanna.wificlock.view.R;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class BaseListAdapter extends BaseAdapter {
	private List<Map<String, String>> mData;
	private LayoutInflater mInflater;
	private int listid=0;
	public BaseListAdapter(Context context,List<Map<String,String>> map,
		//	int img,String title,String info
			int listid,BaseItem  img,BaseItem title,BaseItem info
			) {
		this.listid=listid;
		this.mInflater = LayoutInflater.from(context);
		mData=map;
		this.r_info=info.id;
		this.r_preimg=img.id;
		this.r_title=title.id;
		setKeys(Integer.valueOf(img.value),title.value,info.value);
	}
	public int r_title=0;
	public int r_info=0;
	public int r_preimg=0;
	public String title="";
	public String info="";
	public int preimg=0;
	public void setKeys(int img,String title,String info){
		this.preimg=img;
		this.title=title;
		this.info=info;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {

			holder = new ViewHolder();

			convertView = mInflater.inflate(listid, null);
			holder.img = (ImageView) convertView.findViewById(this.r_preimg);
			
			holder.title = (TextView) convertView.findViewById(this.r_title);
			holder.info = (TextView) convertView.findViewById(this.r_info);
			
			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.img.setBackgroundResource(preimg);
		 
		holder.title.setText((String) mData.get(position).get(title));
		holder.info.setText((String) mData.get(position).get(info));
		//convertView.setBackgroundResource(R.drawable.listitem3);
		return convertView;
	}

}