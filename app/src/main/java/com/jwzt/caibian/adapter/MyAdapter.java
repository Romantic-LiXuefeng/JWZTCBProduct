package com.jwzt.caibian.adapter;

import com.jwzt.cb.product.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

public class MyAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	
	
	public MyAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view=null;
		if(groupPosition==0){
			view=View.inflate(mContext, R.layout.chat_left_audio_layout, null);
		}else{
			view=View.inflate(mContext, R.layout.chat_left_text_layout, null);
		}
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
		// TODO Auto-generated method stub
		View view=View.inflate(mContext, R.layout.chat_right_text_layout, null);
		return view;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int getChildTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	@Override
	public int getChildType(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return super.getChildType(groupPosition, childPosition);
	}

}
