package com.jwzt.caibian.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwzt.caibian.activity.ChuanLianShenheActivity;
import com.jwzt.caibian.activity.PreviewWanCHengActivity;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.CLDetailShenheUploadedActivity;
import com.jwzt.caibian.activity.PreviewGaojianActivity;
import com.jwzt.caibian.activity.PreviewGaojianxiugaiActivity;
import com.jwzt.caibian.activity.PreviewUploadedActivity;
import com.jwzt.caibian.bean.NewUploadinglistBean;
import com.jwzt.caibian.bean.TestGroupBean;
import com.jwzt.caibian.interfaces.RemoveIndexListener;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.TimeUtil;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.widget.SwipeMenuLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
/**
 * 已上传的适配器
 * @author howie
 *
 */
public class UploadedsAdapter extends BaseAdapter {
	public SwipeMenuLayout mSml;
	/**移除条目的监听*/
	private RemoveIndexListener mListener;
	private Context mContext;
	private List<TestGroupBean> mNewUploadinglistBean;
	private String operateType;
	private ImageLoader imageLoader;
	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			 .showStubImage(R.drawable.replace)          // 设置图片下载期间显示的图片
			    .showImageForEmptyUri(R.drawable.replace)  // 设置图片Uri为空或是错误的时候显示的图片
		        .showImageOnFail(R.drawable.replace)       // 设置图片加载或解码过程中发生错误显示的图片
		        .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
		        .cacheOnDisk(true)                          // 设置下载的图片是否缓存在SD卡中
		        .build();                                   // 创建配置过得DisplayImageOption对象
	public UploadedsAdapter(Context mContext, List<TestGroupBean> newUploadinglistBean) {
		super();
		this.mContext = mContext;
		this.mNewUploadinglistBean=newUploadinglistBean;
		imageLoader = ImageLoader.getInstance();
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mNewUploadinglistBean.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mNewUploadinglistBean.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@SuppressLint("ResourceAsColor") @Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(convertView==null){
			holder=new ViewHolder();
			convertView = View.inflate(mContext, R.layout.uploaded_item_layout,null);
			holder.iv = (ImageView)convertView.findViewById(R.id.iv);
			holder.rl=convertView.findViewById(R.id.rl);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_share=(TextView) convertView.findViewById(R.id.tv_share);
			holder.tv_share.setVisibility(View.GONE);
			holder.tv_delete=(TextView) convertView.findViewById(R.id.tv_delete);
			holder.sml=(SwipeMenuLayout) convertView.findViewById(R.id.sml);
			holder.iv_flag=(ImageView) convertView.findViewById(R.id.iv_flag);
			holder.tv_date = (TextView)convertView.findViewById(R.id.tv_date);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String status=mNewUploadinglistBean.get(position).getStatus();
				if(!status.equals("")){
				int statu=new Integer(status);
				if(statu==4){
					 Intent intent=new Intent(mContext,PreviewWanCHengActivity.class);
					 intent.putExtra("id", mNewUploadinglistBean.get(position).getId());
					 if(statu==0){
						  operateType="1";
					   }else if(statu==1){
						  operateType="6";
					   }else if(statu==2){
						  operateType="7";
					   }else{
						operateType="-1";
					   }
					intent.putExtra("name", "稿件详情");
					intent.putExtra("operateType", operateType);
					mContext.startActivity(intent);
				}else{
				   Intent intent=new Intent(mContext,PreviewWanCHengActivity.class);
				   intent.putExtra("id", mNewUploadinglistBean.get(position).getId());
				   if(statu==0){
					  operateType="1";
				   }else if(statu==1){
					  operateType="6";
				   }else if(statu==2){
					  operateType="7";
				   }else{
					operateType="-1";
				   }
					intent.putExtra("name", "稿件详情");
				intent.putExtra("operateType", operateType);
				mContext.startActivity(intent);
				}
				((Activity)mContext).overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				}else{
					 Intent intent=new Intent(mContext,PreviewGaojianxiugaiActivity.class);
					 intent.putExtra("id", mNewUploadinglistBean.get(position).getId());
					
					intent.putExtra("operateType", operateType);
					mContext.startActivity(intent);	
				}
				}
		});
		
		final SwipeMenuLayout swipeMenuLayout=holder.sml;
		holder.tv_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
//				mSml=swipeMenuLayout;
//				if(mListener!=null){
//					mListener.remove(position);
//				}
				UserToast.toSetToast(mContext, "不允许删除");
			}
		});
		/*if(mNewUploadinglistBean.get(position).status != null){
			holder.iv_flag.setImageResource(R.drawable.yicaiyong);
		}else{
			holder.iv_flag.setImageResource(R.drawable.tuigao);
		} */
		holder.tv_title.setText(mNewUploadinglistBean.get(position).getTitle());
		holder.tv_date.setText(TimeUtil.getMDHS(mNewUploadinglistBean.get(position).getCreateTime()));
		String status=mNewUploadinglistBean.get(position).getStatus();
		if(IsNonEmptyUtils.isString(status)){
			int statu=new Integer(status);
//			if(mList.get(position).getOperateType().equals("1")){//上传
//				holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.chuan));
//			}else if(mList.get(position).getOperateType().equals("4")){//选用
//				holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.caiyong));
//			}else if(mList.get(position).getOperateType().equals("6")){//审核通过
//				holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.shen));
//			}else if(mList.get(position).getOperateType().equals("7")){//审核不通过
//				holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.noshen));
//			}else{
//				holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.chuan));
//			}
			
			holder.tv_time.setVisibility(View.VISIBLE);
			if(statu==0){//0：待审、1：已审、2：退回
				holder.tv_time.setText("待提交");
				holder.tv_time.setBackgroundResource(R.drawable.tv_shenhe);
				holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.white));
			}else if(statu==1){
				holder.tv_time.setText("待审核");
				holder.tv_time.setBackgroundResource(R.drawable.tv_shenhe);
				holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.white));
			}else if(statu==2){
				holder.tv_time.setText("一审");
				holder.tv_time.setBackgroundResource(R.drawable.tv_tuigao);
				holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.white));
			}else if(statu==3){
				holder.tv_time.setText("二审");
				holder.tv_time.setBackgroundResource(R.drawable.tv_tuigao);
				holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.white));
			}else if(statu==4){
				holder.tv_time.setText("退回");
				holder.tv_time.setBackgroundResource(R.drawable.tv_caiyong);
				holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.white));
			}else{
				
			}
		}else{
			 holder.tv_time.setText("待提交");
			 holder.tv_time.setBackgroundResource(R.drawable.tv_shenhe);
			 holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.white));
		  }
		imageLoader.displayImage(mNewUploadinglistBean.get(position).getNewsImage(), holder.iv, options);
		return convertView;
	}
	public class ViewHolder{
		View rl;
		TextView tv_share,tv_delete;
		SwipeMenuLayout sml;
		ImageView iv_flag;//稿件状态的图标（退稿和已采用）
		TextView tv_title;
		TextView tv_time;
		ImageView iv;
		TextView tv_date;
	}
	/**
	 * 移除操作
	 */
	public void remove(int position) {
		mNewUploadinglistBean.remove(position);
		notifyDataSetChanged();
	}

	public void setmListener(RemoveIndexListener mListener) {
		this.mListener = mListener;
	}

	public void setList(List<TestGroupBean> newUploadinglist) {
		// TODO Auto-generated method stub
		mNewUploadinglistBean=newUploadinglist;
		notifyDataSetChanged();
	}
	

}
