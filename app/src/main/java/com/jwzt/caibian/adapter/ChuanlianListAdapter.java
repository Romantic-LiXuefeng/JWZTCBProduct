package com.jwzt.caibian.adapter;

import java.util.List;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.ChuanLianGaodanActivity;
import com.jwzt.caibian.bean.ChuanlIanListBean;
import com.jwzt.caibian.bean.LiveBackListBean;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.TimeUtil;
import com.jwzt.caibian.view.DragListView;
import com.jwzt.caibian.view.DragListView.DragAdapter;
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
public class ChuanlianListAdapter extends BaseAdapter {
	private Context mContext;
	private List<ChuanlIanListBean> mList;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	public ChuanlianListAdapter( Context mContext,
			List<ChuanlIanListBean> data) {
		this.mContext = mContext;
		this.mList=data;
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

	 public void remove(int arg0) {//删除指定位置的item
	        mList.remove(arg0);
	        this.notifyDataSetChanged();//不要忘记更改适配器对象的数据源
	    }

	    public void insert(ChuanlIanListBean item, int arg0) {//在指定位置插入item
	    	mList.add(arg0, item);
	        this.notifyDataSetChanged();
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
			holder.lives=(RelativeLayout) convertView.findViewById(R.id.rl_iv);
			holder.tv=(TextView) convertView.findViewById(R.id.tv);
			holder.tv_name = convertView.findViewById(R.id.tv_name);
			holder.tv_live=(TextView) convertView.findViewById(R.id.tv_live);
			holder.tv_renwu=(TextView) convertView.findViewById(R.id.tv_renwu);
			holder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
			holder.iv=(ImageView) convertView.findViewById(R.id.iv);
			holder.iv_play=(ImageView) convertView.findViewById(R.id.iv_play);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.iv.setVisibility(View.GONE);
		holder.iv_play.setVisibility(View.GONE);
		holder.tv.setText(mList.get(position).getTitle());
		if(IsNonEmptyUtils.isString(mList.get(position).getCreateTime())){
			holder.tv_time.setText(TimeUtil.getMDHS(mList.get(position).getCreateTime()));
		}
		/*holder.tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				   Intent  intent=new Intent(mContext,ChuanLianGaodanActivity.class);
				   intent.putExtra("id", mList.get(position).getId());
				   mContext.startActivity(intent);
			}
		});*/
		/*holder.tv_time.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent  intent=new Intent(mContext,ChuanLianGaodanActivity.class);
				   intent.putExtra("id", mList.get(position).getId());
				   mContext.startActivity(intent);
			}
		});
		*/
		/*if(IsNonEmptyUtils.isString(mList.get(position).getIndexImageUrl())){
			imageLoader.displayImage(mList.get(position).getIndexImageUrl(), holder.iv, options);
		}*
		  
		/
		 
		/*String startTime=mList.get(position).getStartTime();
		String endTime=mList.get(position).getEndTime();*/
		//holder.tv_live.setVisibility(View.GONE);
		String status=mList.get(position).getState();
		holder.tv_name.setText(mList.get(position).getAuthor());
		holder.tv_renwu.setText(mList.get(position).getNewsCount()+"");
		if(status.endsWith("0")){//（1.待审核  2一审完成  3二审完成  4退回）
			holder.tv_live.setText("待审核");
			holder.tv_live.setTextColor(mContext.getResources().getColor(R.color.greybd));
		}else if(status.endsWith("1")){
			holder.tv_live.setTextColor(mContext.getResources().getColor(R.color.greybd));
			holder.tv_live.setText("待审核");
		}else if(status.endsWith("2")){
			holder.tv_live.setText("一审");
			holder.tv_live.setTextColor(mContext.getResources().getColor(R.color.a5));
		}else if(status.endsWith("3")){
			holder.tv_live.setText("二审");
			holder.tv_live.setTextColor(mContext.getResources().getColor(R.color.a5));
		}else if(status.endsWith("4")){
			holder.tv_live.setTextColor(mContext.getResources().getColor(R.color.greyac));
			holder.tv_live.setText("退回");
		}else{
			holder.tv_live.setText("待审核");
			holder.tv_live.setTextColor(mContext.getResources().getColor(R.color.greybd));
		}
		/*holder.lives.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				  Intent  intent=new Intent(mContext,ChuanLianGaodanActivity.class);
				   intent.putExtra("id", mList.get(position).getId());
				   mContext.startActivity(intent);
				
			}
		});*/
		return convertView;
	}
	static class ViewHolder{
		TextView tv,tv_live,tv_time,tv_name,tv_renwu;
		ImageView iv,iv_play;
		RelativeLayout lives; 
		
	}
	public void setList(List<ChuanlIanListBean> list) {
		this.mList=list;
		notifyDataSetChanged();
	}

}
