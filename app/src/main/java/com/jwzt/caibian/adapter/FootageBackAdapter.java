package com.jwzt.caibian.adapter;

import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.bean.FootageBean;
import com.jwzt.caibian.bean.FootageItemClickedMessage;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 回传管理跳转拾取素材界面的适配器
 * @author howie
 *
 */
public class FootageBackAdapter extends BaseAdapter {
	private Context mContext;
	/**是否处于编辑状态*/
	private boolean isEditing =true;
	private ArrayList<FootageBean> mList;
	/**右侧的箭头是否可见*/
	private boolean isArrowVisible=true;
	
	

	public void setArrowVisible(boolean isArrowVisible) {
		this.isArrowVisible = isArrowVisible;
	}

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

	public FootageBackAdapter(Context mContext, ArrayList<FootageBean> mList2) {
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
		// TODO Auto-generated method stub
		if(convertView==null){
			convertView = View.inflate(mContext, R.layout.footage_item_layout,null);
		}
		
		ViewHolder holder = ViewHolder.getHolder(convertView);
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
				EventBus.getDefault().post(new FootageItemClickedMessage());//通知选择素材右上角的“选用”文字根据选中的个数进行更新
			}
		});
		FootageBean bean = mList.get(position);
		boolean selected = bean.isSelected();
		holder.iv_select.setImageResource(selected?R.drawable.right:R.drawable.circle_right);
		if(isArrowVisible){
			holder.iv_arrow.setVisibility(View.VISIBLE);
		}else{
			holder.iv_arrow.setVisibility(View.INVISIBLE);
		}
		
		
		return convertView;
	}
	static class ViewHolder{
		
		/**圆形选择图片和封面图片,右侧的箭头图片*/
		ImageView iv_select,iv,iv_arrow;
		/**标题，时间和标记数目*/
		TextView tv,tv_time,tv_mark_num;
		
		public ViewHolder(View convertView){
			iv_select=(ImageView) convertView.findViewById(R.id.iv_select);
			iv=(ImageView) convertView.findViewById(R.id.iv);
			tv=(TextView) convertView.findViewById(R.id.tv);
			tv_time=(TextView) convertView.findViewById(R.id.tv_time);
			tv_mark_num=(TextView) convertView.findViewById(R.id.tv_mark_num);
			iv_arrow=(ImageView) convertView.findViewById(R.id.iv_arrow);
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
