package com.jwzt.caibian.adapter;

import java.util.ArrayList;

import com.jwzt.cb.product.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * 标签网格的适配器
 * @author howie
 *
 */
public class TagGridAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<String> mList;
	
	public TagGridAdapter(Context mContext, ArrayList<String> mList) {
		super();
		this.mContext = mContext;
		this.mList = mList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
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
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ViewHolder holder = null;
		if(convertView==null){
			convertView = View.inflate(mContext, R.layout.tag_item_layout,null);
			holder.tv = (TextView) convertView.findViewById(R.id.tv);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv.setText(mList.get(position));
		if(position!=3&&position!=5){
			holder.tv.setBackgroundResource(R.drawable.tag_grey_bg_selector);
			holder.tv.setTextColor(mContext.getResources().getColor(R.color.grey3));
		}else{
			holder.tv.setBackgroundResource(R.drawable.tag_blue_bg_selector);
			holder.tv.setTextColor(mContext.getResources().getColor(R.color.lightblue));
		}
		return convertView;
	}
	static class ViewHolder{
		TextView tv;
		
	}

}
