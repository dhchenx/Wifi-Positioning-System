package com.bluecanna.wifimapbuilder;

import java.util.List;
 
import java.util.Map;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class MyAdapter extends BaseAdapter {
	private List<Map<String, String>> mData;
	private LayoutInflater mInflater;

	public MyAdapter(Context context,List<Map<String,String>> map,int img,String title,String info) {
		this.mInflater = LayoutInflater.from(context);
		mData=map;
		setKeys(img,title,info);
	}
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

			convertView = mInflater.inflate(R.layout.vlist2, null);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.info = (TextView) convertView.findViewById(R.id.info);
			
			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.img.setBackgroundResource(preimg);
		 
		holder.title.setText((String) mData.get(position).get(title));
		holder.info.setText((String) mData.get(position).get(info));

		return convertView;
	}

}