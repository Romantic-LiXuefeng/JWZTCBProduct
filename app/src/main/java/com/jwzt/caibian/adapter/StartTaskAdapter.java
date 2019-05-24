package com.jwzt.caibian.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.ShowVideoActivity;
import com.jwzt.caibian.activity.TaskStartActivity;
import com.jwzt.caibian.bean.DoTaskBean;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.MediaManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 开始任务的列表的适配器
 * @author howie
 *
 */
@SuppressLint("InflateParams") public class StartTaskAdapter extends BaseAdapter {
	private Context mContext;
	/***1：文字、2：图片、3：音频、4：视频、5：文件、6：位置*/
	/**文本类型*/
	final int TYPE_TEXT=1;
	/**多张图片的类型*/
	final int TYPE_MULTIIMAGE=2;
	/**音频类型*/
	final int TYPE_AUDIO=3;
	/**视频类型*/
	final int TYPE_VIDEO=4;
	/**文件类型*/
	final int TYPE_WenJian=5;
	/**位置信息的类型*/
	final int TYPE_LOCATION=6;
	
	int pos = -1;//标记当前录音索引，默认没有播放任何一个
	
	private List<DoTaskBean> mList;
	List<AnimationDrawable> mAnimationDrawables;
	
	private DisplayImageOptions options;
	
	public StartTaskAdapter(Context mContext,List<DoTaskBean> list) {
		super();
		this.mContext = mContext;
		this.mList=list;
		mAnimationDrawables = new ArrayList<AnimationDrawable>();
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(false) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中 
        .bitmapConfig(Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
        .build(); // 构建完成  
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
		ViewHolderTEXT viewHolderTEXT=null;
		ViewHolderMULTIIMAGE viewHolderMULTIIMAGE=null;
		ViewHolderAUDIO viewHolderAUDIO=null;
		ViewHolderVIDEO viewHolderVIDEO=null;
		ViewHolderLOCATION viewHolderLOCATION=null;
		int type=getItemViewType(position);
		if(convertView==null){
			switch (type) {
			case TYPE_TEXT://文字
				viewHolderTEXT=new ViewHolderTEXT();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.timeline_text_layout, null);
				viewHolderTEXT.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				viewHolderTEXT.tv_date=(TextView) convertView.findViewById(R.id.tv_date);
				viewHolderTEXT.tv_message=(TextView) convertView.findViewById(R.id.tv_message);
				viewHolderTEXT.tv_select=(TextView) convertView.findViewById(R.id.tv_select);
				viewHolderTEXT.tv_copy=(TextView) convertView.findViewById(R.id.tv_copy);
				viewHolderTEXT.ll_pop=(LinearLayout) convertView.findViewById(R.id.ll_pop);
				convertView.setTag(viewHolderTEXT);
				break;
			case TYPE_MULTIIMAGE://图片
				viewHolderMULTIIMAGE=new ViewHolderMULTIIMAGE();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.timeline_imagegrid_layout, null);
				viewHolderMULTIIMAGE.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				viewHolderMULTIIMAGE.tv_date=(TextView) convertView.findViewById(R.id.tv_date);
				viewHolderMULTIIMAGE.gv=(GridView) convertView.findViewById(R.id.gv);
				convertView.setTag(viewHolderMULTIIMAGE);
				break;
			case TYPE_AUDIO://音频
				viewHolderAUDIO=new ViewHolderAUDIO();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.timeline_audio_layout, null);
				viewHolderAUDIO.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				viewHolderAUDIO.tv_date=(TextView) convertView.findViewById(R.id.tv_date);
				viewHolderAUDIO.tv_listen=(TextView) convertView.findViewById(R.id.tv_listen);
				viewHolderAUDIO.tv_select=(TextView) convertView.findViewById(R.id.tv_select);
				viewHolderAUDIO.tv_transer=(TextView) convertView.findViewById(R.id.tv_transer);
				viewHolderAUDIO.timelength=(TextView) convertView.findViewById(R.id.timelength);
				viewHolderAUDIO.img_luyinplay=(ImageView) convertView.findViewById(R.id.img_luyinplay);
				viewHolderAUDIO.rl_audio=(RelativeLayout) convertView.findViewById(R.id.rl_audio);
				viewHolderAUDIO.ll_pop=(LinearLayout) convertView.findViewById(R.id.ll_pop);
				convertView.setTag(viewHolderAUDIO);
				break;
			case TYPE_VIDEO://视频
				viewHolderVIDEO=new ViewHolderVIDEO();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.timeline_video_layout, null);
				viewHolderVIDEO.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				viewHolderVIDEO.tv_date=(TextView) convertView.findViewById(R.id.tv_date);
				viewHolderVIDEO.img_videobg=(ImageView) convertView.findViewById(R.id.img_videobg);
				viewHolderVIDEO.img_videoplay=(ImageView) convertView.findViewById(R.id.img_videoplay);
				convertView.setTag(viewHolderVIDEO);
				break;
			case TYPE_LOCATION://地址
				viewHolderLOCATION=new ViewHolderLOCATION();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.location_pop_layout, null);
				viewHolderLOCATION.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				viewHolderLOCATION.tv_date=(TextView) convertView.findViewById(R.id.tv_date);
				viewHolderLOCATION.tv_location=(TextView) convertView.findViewById(R.id.tv_location);
				convertView.setTag(viewHolderLOCATION);
				break;
			}
		}else{
			switch (type) {
			case TYPE_TEXT:
				viewHolderTEXT=(ViewHolderTEXT) convertView.getTag();
				break;
			case TYPE_MULTIIMAGE:
				viewHolderMULTIIMAGE=(ViewHolderMULTIIMAGE) convertView.getTag();
				break;
			case TYPE_AUDIO:
				viewHolderAUDIO=(ViewHolderAUDIO) convertView.getTag();
				break;
			case TYPE_VIDEO:
				viewHolderVIDEO=(ViewHolderVIDEO) convertView.getTag();
				break;
			case TYPE_LOCATION:
				viewHolderLOCATION=(ViewHolderLOCATION) convertView.getTag();
				break;
			}
		}
		
		switch (type) {
		case TYPE_TEXT:
			viewHolderTEXT.tv_time.setText(mList.get(position).getSendTime());
			viewHolderTEXT.tv_date.setVisibility(View.GONE);
			if(mList.get(position).getSucaiType()==6){
				Drawable rightDrawable = mContext.getResources().getDrawable(R.drawable.location_new);  
	            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());  
	            viewHolderTEXT.tv_message.setCompoundDrawables(rightDrawable, null, null, null); 
				viewHolderTEXT.tv_message.setText("\b\b\b"+mList.get(position).getSucaiContent());
			}else{
				Drawable rightDrawable = mContext.getResources().getDrawable(R.drawable.location_new);  
	            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());  
	            viewHolderTEXT.tv_message.setCompoundDrawables(null, null, null, null); 
				viewHolderTEXT.tv_message.setText(mList.get(position).getSucaiContent());
			}
			
			//复制按钮
			viewHolderTEXT.tv_copy.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
//					scaleDown(ll_pop);
				}
			});
			//选用按钮
			viewHolderTEXT.tv_select.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
//					scaleDown(ll_pop);
				}
			});
			viewHolderTEXT.tv_message.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
//					if(ll_pop.getVisibility()!=View.VISIBLE){//如果之前是不可见的
//						scaleUp(ll_pop);
//					}
				}
			});
			break;
		case TYPE_MULTIIMAGE:
			viewHolderMULTIIMAGE.tv_time.setText(mList.get(position).getSendTime());
			viewHolderMULTIIMAGE.tv_date.setVisibility(View.GONE);
			String imgPath=mList.get(position).getThumbImages();
			if(IsNonEmptyUtils.isString(imgPath)){
				String[] imgPatharray=imgPath.split(",");
				viewHolderMULTIIMAGE.gv.setAdapter(new MultiImageAdapter(mContext,imgPatharray));
			}
			break;
		case TYPE_AUDIO:
			//开始设置监听
	        final ImageView ieaLlSinger = viewHolderAUDIO.img_luyinplay;
			viewHolderAUDIO.tv_time.setText(mList.get(position).getSendTime());
			viewHolderAUDIO.tv_date.setVisibility(View.GONE);
			viewHolderAUDIO.timelength.setText(mList.get(position).getTimeLength()+"〃");
			viewHolderAUDIO.rl_audio.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
	                notifyDataSetChanged();
	                final AnimationDrawable animationDrawable = (AnimationDrawable) ieaLlSinger.getBackground();
	                //重置动画
	                resetAnim(animationDrawable);
	                animationDrawable.start();

	                //处理点击正在播放的语音时，可以停止；再次点击时重新播放。
	                if (pos == position) {
//	                    if (record.isPlaying()) {
//	                        record.setPlaying(false);
	                        MediaManager.release();
	                        animationDrawable.stop();
	                        animationDrawable.selectDrawable(0);//reset
	                        return;
//	                    } else {
//	                        record.setPlaying(true);
//	                    }
	                }
	                //记录当前位置正在播放。
	                pos = position;
//	                record.setPlaying(true);

	                //播放前重置。
	                MediaManager.release();
	                //开始实质播放
	                MediaManager.playSound(mList.get(position).getThumbImages(),
	                        new MediaPlayer.OnCompletionListener() {
	                            @Override
	                            public void onCompletion(MediaPlayer mp) {
	                                animationDrawable.selectDrawable(0);//显示动画第一帧
	                                animationDrawable.stop();
	                                //播放完毕，当前播放索引置为-1。
	                                pos = -1;
	                            }
	                        });
				}
			});
			
			viewHolderAUDIO.tv_listen.setOnClickListener(new OnClickListener() {//听筒播放
				
				@Override
				public void onClick(View arg0) {
//					scaleDown(ll_pop);
				}
			});
			viewHolderAUDIO.tv_transer.setOnClickListener(new OnClickListener() {//听筒播放
				
				@Override
				public void onClick(View arg0) {
//					scaleDown(ll_pop);
				}
			});
			viewHolderAUDIO.tv_select.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
//					scaleDown(ll_pop);
				}
			});
			break;
		case TYPE_VIDEO:
			viewHolderVIDEO.tv_time.setText(mList.get(position).getSendTime());
			viewHolderVIDEO.tv_date.setVisibility(View.GONE);
			
			viewHolderVIDEO.img_videoplay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(mContext, ShowVideoActivity.class);
					intent.putExtra("playpath", mList.get(position).getVideopath());
					mContext.startActivity(intent);
				}
			});
//			viewHolderVIDEO.img_videobg
			//视频缩略图的添加
//			Bitmap srcBitmap = ThumbnailUtils.createVideoThumbnail(mList.get(position).getVideopath(), 0);
//			srcBitmap = ThumbnailUtils.extractThumbnail(srcBitmap, 420, 300);
			Uri uri = Uri.fromFile(new File(mList.get(position).getVideopath()));
			ImageLoader.getInstance().displayImage(uri+"", viewHolderVIDEO.img_videobg, options);
//			viewHolderVIDEO.img_videobg.setImageBitmap(srcBitmap); 
//			viewHolderVIDEO.img_videobg.setImageBitmap(getVideoThumbnail(mList.get(position).getVideopath())); 
			break;
		case TYPE_LOCATION:
			viewHolderLOCATION.tv_time.setText(mList.get(position).getSendTime());
			viewHolderLOCATION.tv_date.setVisibility(View.GONE);
			viewHolderLOCATION.tv_location.setText(mList.get(position).getSucaiContent());
			break;
		}
		
		return convertView;
	}
	
	public Bitmap getVideoThumbnail(String filePath) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(filePath);
			bitmap = retriever.getFrameAtTime();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}
	
    private void resetAnim(AnimationDrawable animationDrawable) {
        if (!mAnimationDrawables.contains(animationDrawable)) {
            mAnimationDrawables.add(animationDrawable);
        }
        for (AnimationDrawable ad : mAnimationDrawables) {
            ad.selectDrawable(0);
            ad.stop();
        }
    }
	
	/**
	 * 文本类型
	 * @author pc
	 *
	 */
	public class ViewHolderTEXT{
		private TextView tv_time,tv_date,tv_message,tv_select,tv_copy;
		private LinearLayout ll_pop;
	}
	
	/**
	 * 多张图片类型
	 * @author pc
	 */
	public class ViewHolderMULTIIMAGE{
		private TextView tv_time,tv_date;
		private GridView gv;
	}
	
	/**
	 * 音频类型
	 * @author pc
	 *
	 */
	public class ViewHolderAUDIO{
		private TextView tv_time,tv_date,tv_listen,tv_transer,tv_select,timelength;
		private ImageView img_luyinplay;
		private RelativeLayout rl_audio;
		private LinearLayout ll_pop;
	}
	
	/**
	 * 视频类型
	 * @author pc
	 *
	 */
	public class ViewHolderVIDEO{
		private TextView tv_time,tv_date;
		private ImageView img_videobg,img_videoplay;
	}
	
	public class ViewHolderLOCATION{
		private TextView tv_time,tv_date,tv_location;
	}
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 6;
	}
	@Override
	public int getItemViewType(int position) {
		/***1：文字、2：图片、3：音频、4：视频、5：文件、6：位置*/
		int type=mList.get(position).getSucaiType();
		if(type==1){
			return TYPE_TEXT;
		}else if(type==2){
			return TYPE_MULTIIMAGE;
		}else if(type==3){
			return TYPE_AUDIO;
		}else if(type==4){
			return TYPE_VIDEO;
		}else if(type==6){
			return TYPE_TEXT;
		}else{
			return TYPE_TEXT;
		}
	}
	
	
	/**
	 * 弹出动画
	 */
	private void scaleUp(View view){
		view.setVisibility(View.VISIBLE);
		ScaleAnimation sa=new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 1, ScaleAnimation.RELATIVE_TO_SELF, 1);
		sa.setDuration(300);
		sa.setInterpolator(new OvershootInterpolator());
		view.startAnimation(sa);
	}
	/**
	 * 收起动画
	 */
	private void scaleDown(final View view){
		ScaleAnimation sa=new ScaleAnimation(1, 0, 1, 0, ScaleAnimation.RELATIVE_TO_SELF, 1, ScaleAnimation.RELATIVE_TO_SELF, 1);
		sa.setDuration(300);
		view.startAnimation(sa);
		sa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				view.setVisibility(View.GONE);
			}
		});
	}
}
