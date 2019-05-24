package com.jwzt.caibian.adapter;

import java.util.ArrayList;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.bean.FootageBean;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FootageAdapter extends BaseAdapter {
	private Context mContext;
	/**是否处于编辑状态*/
	private boolean isEditing;
	private ArrayList<FootageBean> mList;
	
	
	

	public boolean isEditing() {
		return isEditing;
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
		if(!isEditing){
			for (FootageBean bean : mList) {
				bean.setSelected(false);
			}
			
		}
		notifyDataSetChanged();
	}

	public FootageAdapter(Context mContext, ArrayList<FootageBean> mList2) {
		super();
		this.mContext = mContext;
		mList=mList2;
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
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.footage_item_layout,null);
			holder.iv_select=(ImageView) convertView.findViewById(R.id.iv_select);
			holder.iv=(ImageView) convertView.findViewById(R.id.iv);
			holder.tv=(TextView) convertView.findViewById(R.id.tv);
			holder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_mark_num=(TextView) convertView.findViewById(R.id.tv_mark_num);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		if(isEditing){
			holder.iv_select.setVisibility(View.VISIBLE);
		}else{
			holder.iv_select.setVisibility(View.GONE);
		}
		holder.iv_select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				
				FootageBean footageBean = mList.get(position);
				footageBean.setSelected(!footageBean.isSelected());
				notifyDataSetChanged();
			}
		});
		FootageBean bean = mList.get(position);
		boolean selected = bean.isSelected();
		holder.iv_select.setImageResource(selected?R.drawable.right:R.drawable.circle_right);
		
		
		
		return convertView;
	}
	static class ViewHolder{
		/**圆形选择图片和封面图片*/
		ImageView iv_select,iv;
		/**标题，时间和标记数目*/
		TextView tv,tv_time,tv_mark_num;
	
	}

}
