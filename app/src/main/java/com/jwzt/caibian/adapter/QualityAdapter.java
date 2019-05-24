package com.jwzt.caibian.adapter;

import java.util.ArrayList;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.bean.QualityBean;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 音频质量设置的适配器
 * @author howie
 *
 */
public class QualityAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<QualityBean> mList;
	public QualityAdapter(Context mContext, ArrayList<QualityBean> mList) {
		super();
		this.mContext = mContext;
		this.mList=mList;
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if(convertView==null){
			convertView = View.inflate(mContext, R.layout.quality_item_layout,null);
		}
		
		ViewHolder holder = ViewHolder.getHolder(convertView);
		QualityBean qualityBean = mList.get(position);
		if(qualityBean.isSelected()){
			holder.iv.setImageResource(R.drawable.quality_check);
		}else{
			holder.iv.setImageResource(R.drawable.quality_uncheck);
		}
		holder.tv_quality.setText(qualityBean.getQuality());
		return convertView;
	}
	static class ViewHolder{
		ImageView iv;
		TextView tv_quality;
		
		public ViewHolder(View convertView){
			iv = (ImageView) convertView.findViewById(R.id.iv);
			tv_quality=(TextView) convertView.findViewById(R.id.tv_quality);
//			first_word = (TextView) convertView.findViewById(R.id.first_word);
		}
		public static ViewHolder getHolder(View convertView){
			ViewHolder holder = (ViewHolder) convertView.getTag();
			if(holder==null){
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			return holder;
		}
	}

}
