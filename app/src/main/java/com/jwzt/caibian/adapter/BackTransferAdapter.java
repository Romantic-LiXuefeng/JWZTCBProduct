package com.jwzt.caibian.adapter;

import java.util.List;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.view.CircleProgressView;
import com.jwzt.upload.main.TaskInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * 回传管理的适配器
 * @author howie
 *
 */
public class BackTransferAdapter extends BaseAdapter {
	private Context mContext;
	private List<TaskInfo> mList;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	public BackTransferAdapter(Context mContext,List<TaskInfo> list) {
		super();
		this.mContext = mContext;
		this.mList=list;
		options = new DisplayImageOptions.Builder()
		 .showStubImage(R.drawable.new_pass_back_display)          // 设置图片下载期间显示的图片
		    .showImageForEmptyUri(R.drawable.new_pass_back_display)  // 设置图片Uri为空或是错误的时候显示的图片
	        .showImageOnFail(R.drawable.new_pass_back_display)       // 设置图片加载或解码过程中发生错误显示的图片
	        .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
	        .cacheOnDisk(true)                          // 设置下载的图片是否缓存在SD卡中
	        .build();  
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		float average = -1;
		ViewHolder holder = null;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.back_transfer_item_layout,null);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
			holder.tv_size=(TextView) convertView.findViewById(R.id.tv_size);
			holder.iv=(ImageView) convertView.findViewById(R.id.iv);
			holder.pb=(CircleProgressView) convertView.findViewById(R.id.pro);
			holder.iv_scchenggong = (ImageView) convertView.findViewById(R.id.iv_scchenggong);
			holder.iv_scshibai = (ImageView) convertView.findViewById(R.id.iv_scshibai);
			holder.iv_sczt = (ImageView) convertView.findViewById(R.id.iv_sczt);
			holder.iv_scdengdai = (ImageView) convertView.findViewById(R.id.iv_scdengdai);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(IsNonEmptyUtils.isString(mList.get(position).getTask_imgPath())){
			if(mList.get(position).getTask_imgPath().startsWith("file:///")){
				imageLoader.displayImage(mList.get(position).getTask_imgPath(), holder.iv, options);
			}else{
				imageLoader.displayImage("file:///"+mList.get(position).getTask_imgPath(), holder.iv, options);
			}
		}else{
			holder.iv.setImageBitmap(BitmapUtils.readBitMap(mContext, R.drawable.new_pass_back_display));
		}
			holder.tv_title.setText(mList.get(position).getFileName());
			String totol = mList.get(position).getTask_length();
			System.out.println("==============" + totol);
			if (TextUtils.isEmpty(totol)) {
				totol = 0*1024*1024 + "";
			}
			if (!TextUtils.isEmpty(totol)) {
				Long i = Long.parseLong(totol);
				average = (i/102400)/100.0f;
				String format = Formatter.formatFileSize(mContext,i);
				holder.tv_size.setVisibility(View.GONE);
				//holder.pb.setMax((int)(Integer.parseInt(/*mList.get(position).getTask_length()*/totol)/102400));
			}
			int upsize = -1;
			int total = 0;
			String uploadsize=mList.get(position).getTask_upsize();
			if(IsNonEmptyUtils.isString(totol)){
				total=(int)(Integer.parseInt(totol)/102400);
			}
			if(IsNonEmptyUtils.isString(uploadsize)){
				upsize=(int)(Integer.parseInt(uploadsize)/102400);
			}
			if(total==upsize){
				if (!TextUtils.isEmpty(totol)) {
					String format = Formatter.formatFileSize(mContext, Long.parseLong(totol));
					holder.tv_status.setText(format+"/"+format);
				}
				holder.pb.setProgress(100);
				holder.showConcle(false, true, false, false, false);
				
			}else{
				if(mList.get(position).getTask_statu()==1){//表示正在上传
					holder.showConcle(true, false, false, false, false);
					if (!TextUtils.isEmpty(totol)) {
						String format = Formatter.formatFileSize(mContext, Long.parseLong(totol));
						String format1 = Formatter.formatFileSize(mContext, mList.get(position).getLength());
						holder.tv_status.setText(format1+"/"+format);
					}
					holder.pb.setProgress((int) ((mList.get(position).getLength()/102400)/average));
					System.out.println("==============" + mList.get(position).getLength()/102400);
				}else if(mList.get(position).getTask_statu()==2){//表示暂停
					holder.showConcle(false, false, false, true, false);
				}else{
					holder.showConcle(true, false, false, false, false);
					if (!TextUtils.isEmpty(totol)) {
						String format = Formatter.formatFileSize(mContext, Long.parseLong(totol));
						String format1 = Formatter.formatFileSize(mContext, mList.get(position).getLength());
						holder.tv_status.setText(format1+"/"+format);
					}
					holder.pb.setProgress((int) ((mList.get(position).getLength()/102400)/average));
					System.out.println("==============" + mList.get(position).getLength()/102400);
				}
				
			}
			if (mList.get(position).getLength() == Integer.parseInt(totol) ) {
				holder.showConcle(false, true, false, false, false);
			}
			//holder.pb.setProgress(22);
			//holder.pb.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.mprogress_drawable_blue));
		return convertView;
	}
	
	
	static class ViewHolder{
		/**标题、下载状态和大小*/
		TextView tv_title,tv_status,tv_size;
		ImageView iv;
		CircleProgressView pb;
		ImageView iv_scchenggong;
		private ImageView iv_scshibai;
		private ImageView iv_sczt;
		private ImageView iv_scdengdai;
		
		
		public void showConcle (boolean ispb,boolean isiv_scchenggong,boolean isiv_scshibai,boolean isiv_sczt,boolean isiv_scdengdai){
			if (ispb) {
				pb.setVisibility(View.VISIBLE);
				pb.setTextSize(20);
				iv_scchenggong.setVisibility(View.GONE);
				iv_scshibai.setVisibility(View.GONE);
				iv_sczt.setVisibility(View.GONE);
				iv_scdengdai.setVisibility(View.GONE);
			}
			if (isiv_scchenggong) {
				pb.setVisibility(View.GONE);
				iv_scchenggong.setVisibility(View.VISIBLE);
				iv_scshibai.setVisibility(View.GONE);
				iv_sczt.setVisibility(View.GONE);
				iv_scdengdai.setVisibility(View.GONE);
			}
			if (isiv_scshibai) {
				pb.setVisibility(View.GONE);
				iv_scchenggong.setVisibility(View.GONE);
				iv_scshibai.setVisibility(View.VISIBLE);
				iv_sczt.setVisibility(View.GONE);
				iv_scdengdai.setVisibility(View.GONE);
			}
			if (isiv_sczt) {
				pb.setVisibility(View.VISIBLE);
				pb.setTextSize(0);
				iv_scchenggong.setVisibility(View.GONE);
				iv_scshibai.setVisibility(View.GONE);
				iv_sczt.setVisibility(View.VISIBLE);
				iv_scdengdai.setVisibility(View.GONE);
			}
			
			if (isiv_scdengdai) {
				pb.setVisibility(View.GONE);
				iv_scchenggong.setVisibility(View.GONE);
				iv_scshibai.setVisibility(View.GONE);
				iv_sczt.setVisibility(View.GONE);
				iv_scdengdai.setVisibility(View.VISIBLE);
			}
			
		}
		
	}


	public void setList(List<TaskInfo> list) {
		// TODO Auto-generated method stub
		mList=list;
		notifyDataSetChanged();
	}

}
