package com.jwzt.caibian.adapter;

import java.util.List;



import com.jwzt.cb.product.R;
import com.jwzt.caibian.view.FontTextView;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 闹钟设置点击“闹钟声”、“播放电台”、“重复”的时候弹出的PopupWindow里面的listView的适配器
 * @author howie
 *
 */
public class AlarmClockPopListViewAdapter extends BaseAdapter {
	private List<String> mList;
	private Context mContext;
	/**textView中的文字是否居中对齐**/
	private boolean mIsCentered;
	/**
	 * 
	 * @param context
	 * @param list
	 * @param isCentered textView的文字的对齐方式，false默认代表左对齐，true代表居中对齐
	 */
	public AlarmClockPopListViewAdapter(Context context,List<String> list, boolean isCentered) {
		super();
		// TODO Auto-generated constructor stub
		mContext=context;
		mList=list;
		mIsCentered=isCentered;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder=null;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(mContext).inflate(R.layout.my_radio_setting_list_item_layout, null);
			viewHolder.tv=(FontTextView) convertView.findViewById(R.id.tv);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.tv.setText(mList.get(position));
		if(mIsCentered){
			viewHolder.tv.setGravity(Gravity.CENTER);
		}
		
		
		
		return convertView;
	}
	public static class ViewHolder{
		private FontTextView tv;
		
	}

}
