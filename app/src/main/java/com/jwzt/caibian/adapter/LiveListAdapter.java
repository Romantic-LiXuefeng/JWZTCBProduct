package com.jwzt.caibian.adapter;

import java.util.List;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.LiveListActivity;
import com.jwzt.caibian.activity.NewLiveActivity;
import com.jwzt.caibian.bean.LiveBackListBean;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.TimeUtil;
import com.jwzt.caibian.util.UserToast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 直播列表的适配器
 * @author howie
 *
 */
public class LiveListAdapter extends BaseAdapter {
	private Context mContext;
	private List<LiveBackListBean> mList;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	public LiveListAdapter(Context mContext,List<LiveBackListBean> list) {
		super();
		this.mContext = mContext;
		this.mList=list;
		options = new DisplayImageOptions.Builder()
		 .showStubImage(R.drawable.replace)          // 设置图片下载期间显示的图片
		    .showImageForEmptyUri(R.drawable.replace)  // 设置图片Uri为空或是错误的时候显示的图片
	        .showImageOnFail(R.drawable.replace)       // 设置图片加载或解码过程中发生错误显示的图片
	        .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
	        .cacheOnDisk(true)                          // 设置下载的图片是否缓存在SD卡中
	        .build();    
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.live_list_item_layout,null);
			holder.relative=(RelativeLayout)convertView.findViewById(R.id.lives);
			
			holder.tv=(TextView) convertView.findViewById(R.id.tv);
			holder.tv_live=(TextView) convertView.findViewById(R.id.tv_live);
			holder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
			holder.iv=(ImageView) convertView.findViewById(R.id.iv);
			holder.iv_pop=(ImageView) convertView.findViewById(R.id.iv_pop);
			holder.iv_time=(ImageView) convertView.findViewById(R.id.iv_time);
			holder.iv_dui=(ImageView) convertView.findViewById(R.id.iv_dui);
			holder.iv_play=(ImageView) convertView.findViewById(R.id.iv_play);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.iv.setVisibility(View.GONE);
		//holder.iv_time.setVisibility(View.GONE);
		holder.iv_dui.setVisibility(View.GONE);
		holder.tv.setText(mList.get(position).getName());
		if(IsNonEmptyUtils.isString(mList.get(position).getCreateTime())){
			holder.tv_time.setText(TimeUtil.getMDHS(mList.get(position).getCreateTime()));
		}
		if(IsNonEmptyUtils.isString(mList.get(position).getIndexImageUrl())){
			imageLoader.displayImage(mList.get(position).getIndexImageUrl(), holder.iv, options);
		}
		String startTime=mList.get(position).getStartTime();
		String endTime=mList.get(position).getEndTime();
		if(IsNonEmptyUtils.isString(startTime)&&IsNonEmptyUtils.isString(endTime)){
			if(TimeUtil.timeCompare(startTime)){
				holder.tv_live.setText("未开始");
				holder.tv_live.setBackgroundResource(R.drawable.live_text_bg_grey2);
				holder.tv_live.setTextColor(mContext.getResources().getColor(R.color.white));
			}else{//表示未开始
				if(TimeUtil.timeCompare(endTime)){//表示已结束
					holder.tv_live.setText("LIVE");
					holder.tv_live.setBackgroundResource(R.drawable.live_text_bg2);
					holder.tv_live.setTextColor(mContext.getResources().getColor(R.color.white));
				}else{//表示正在进行
					holder.tv_live.setText("已结束");
					holder.tv_live.setBackgroundResource(R.drawable.live_text_bg_grey2);
					holder.tv_live.setTextColor(mContext.getResources().getColor(R.color.white));
				}
			}
		}else{
			holder.tv_live.setText("已结束");
			holder.tv_live.setBackgroundResource(R.drawable.live_text_bg_grey2);
			holder.tv_live.setTextColor(mContext.getResources().getColor(R.color.white));
		}
		holder.relative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String startTime=mList.get(position).getStartTime();
				String endTime=mList.get(position).getEndTime();
				if(TimeUtil.timeCompare(startTime)){
					UserToast.toSetToast(mContext, "直播尚未开始");
				}else{//表示未开始
					if(TimeUtil.timeCompare(endTime)){//表示已结束
						Intent intent=new Intent(mContext,NewLiveActivity.class);
						intent.putExtra("liveId", mList.get(position).getId());
						mContext.startActivity(intent);
					}else{//表示正在进行
						UserToast.toSetToast(mContext, "直播已结束");
					}
				}
			}
		});
		return convertView;
	}
	static class ViewHolder{
		TextView tv,tv_live,tv_time;
		ImageView iv,iv_play,iv_pop,iv_time,iv_dui;
		RelativeLayout  relative;
	}
	public void setList(List<LiveBackListBean> list) {
		this.mList=list;
		notifyDataSetChanged();
	}

}
