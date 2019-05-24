package com.jwzt.caibian.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwzt.caibian.activity.PhotoActivity;
import com.jwzt.caibian.activity.ShowVideoActivity;
import com.jwzt.caibian.bean.ChatMessageBean;
import com.jwzt.caibian.util.MediaManager;
import com.jwzt.cb.product.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 聊天适配器
 * @author howie
 *
 */
public class ChatAdapter extends BaseAdapter {
	/**左侧文字类型*/
	private static final int TYPE_LEFT_TEXT=1;
	/**左侧图片类型*/
	private static final int TYPE_LEFT_IMAGE=2;
	/**左侧音频类型*/
	private static final int TYPE_LEFT_AUDIO=3;
	/**左侧视频类型*/
	private static final int TYPE_LEFT_VIDEO=4;
	/**右侧文字类型*/
	private static final int TYPE_RIGHT_TEXT=5;
	/**右侧图片类型*/
	private static final int TYPE_RIGHT_IMAGE=6;
	/***右侧音频*/
	private static final int TYPE_RIGHT_AUDIO=7;
	/***右侧视频*/
	private static final int TYPE_RIGHT_VIDEO=8;
	/**左侧多张图片类型*/
	private static final int TYPE_LEFT_MORE_IMAGE=9;
	/**右侧多张图片类型*/
	private static final int TYPE_RIGHT_MORE_IMAGE=10;
	
	
	//其他类型可以继续添加。。
	private Context mContext;
	List<ChatMessageBean> mListBean;
	private String mUserId;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	int pos = -1;//标记当前录音索引，默认没有播放任何一个
	List<AnimationDrawable> mAnimationDrawables;
	
	public ChatAdapter(Context mContext,List<ChatMessageBean> listBean,String userId) {
		super();
		this.mContext = mContext;
		this.mListBean=listBean;
		this.mUserId=userId;
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(false) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中 
        .bitmapConfig(Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
        .build(); // 构建完成  
		
		mAnimationDrawables = new ArrayList<AnimationDrawable>();
	}

	@Override
	public int getCount() {
		return mListBean.size();
	}

	@Override
	public Object getItem(int position) {
		return mListBean.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup viewGroup) {
		final ChatMessageBean chatMessageBean = mListBean.get(position);
		LeftTextHolder leftTextHolder=null;
		LeftImgHolder leftImgHolder=null;
		LeftVideoHolder leftVideoHolder=null;
		LeftAudioHolder leftAudioHolder=null;
		RightTextHolder rightTextHolder=null;
		RightImgHolder rightImgHolder=null;
		RightVideoHolder rightVideoHolder=null;
		RightAudioHolder rightAudioHolder=null;
		int type=getItemViewType(position);
		if(convertView==null){
			switch (type) {
			case TYPE_LEFT_TEXT:
				leftTextHolder=new LeftTextHolder();
				convertView=LayoutInflater.from(mContext).inflate(R.layout.chat_left_text_layout, null);
				leftTextHolder.civ=(ImageView) convertView.findViewById(R.id.civ);
				leftTextHolder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
				leftTextHolder.tv_group=(TextView) convertView.findViewById(R.id.tv_group);
				leftTextHolder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				leftTextHolder.tv_message=(TextView) convertView.findViewById(R.id.tv_message);
				convertView.setTag(leftTextHolder);
				break;
			case TYPE_LEFT_IMAGE:
				leftImgHolder=new LeftImgHolder();
				convertView=LayoutInflater.from(mContext).inflate(R.layout.chat_left_image_layout, null);
				leftImgHolder.civ=(ImageView) convertView.findViewById(R.id.civ);
				leftImgHolder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
				leftImgHolder.tv_group=(TextView) convertView.findViewById(R.id.tv_group);
				leftImgHolder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				leftImgHolder.img1=(ImageView) convertView.findViewById(R.id.img1);
				leftImgHolder.img2=(ImageView) convertView.findViewById(R.id.img2);
				leftImgHolder.number = (TextView) convertView.findViewById(R.id.tv_number);
				convertView.setTag(leftImgHolder);
				break;
			case TYPE_LEFT_AUDIO:
				leftAudioHolder=new LeftAudioHolder();
				convertView=LayoutInflater.from(mContext).inflate(R.layout.chat_left_audio_layout, null);
				leftAudioHolder.civ=(ImageView) convertView.findViewById(R.id.civ);
				leftAudioHolder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
				leftAudioHolder.tv_group=(TextView) convertView.findViewById(R.id.tv_group);
				leftAudioHolder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				leftAudioHolder.tv_audiotime=(TextView) convertView.findViewById(R.id.tv_audiotime);
				leftAudioHolder.img_audioplay=(ImageView) convertView.findViewById(R.id.img_audioplay);
				leftAudioHolder.rl_audio=(RelativeLayout) convertView.findViewById(R.id.rl_audio);
				convertView.setTag(leftAudioHolder);
				break;
			case TYPE_LEFT_VIDEO:
				leftVideoHolder=new LeftVideoHolder();
				convertView=LayoutInflater.from(mContext).inflate(R.layout.chat_left_video_layout, null);
				leftVideoHolder.civ=(ImageView) convertView.findViewById(R.id.civ);
				leftVideoHolder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
				leftVideoHolder.tv_group=(TextView) convertView.findViewById(R.id.tv_group);
				leftVideoHolder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				leftVideoHolder.img_video=(ImageView) convertView.findViewById(R.id.img_video);
				leftVideoHolder.img_videoplay=(ImageView) convertView.findViewById(R.id.img_videoplay);
				leftVideoHolder.rl_video=(RelativeLayout) convertView.findViewById(R.id.rl_video);
				convertView.setTag(leftVideoHolder);
				break;
			case TYPE_RIGHT_TEXT:
				rightTextHolder=new RightTextHolder();
				convertView=LayoutInflater.from(mContext).inflate(R.layout.chat_right_text_layout, null);
				rightTextHolder.civ=(ImageView) convertView.findViewById(R.id.civ);
				rightTextHolder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
				rightTextHolder.tv_group=(TextView) convertView.findViewById(R.id.tv_group);
				rightTextHolder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				rightTextHolder.tv_message=(TextView) convertView.findViewById(R.id.tv_message);
				convertView.setTag(rightTextHolder);
				break;
			case TYPE_RIGHT_IMAGE:
				rightImgHolder=new RightImgHolder();
				convertView=LayoutInflater.from(mContext).inflate(R.layout.chat_right_image_layout, null);
				rightImgHolder.civ=(ImageView) convertView.findViewById(R.id.civ);
				rightImgHolder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
				rightImgHolder.tv_group=(TextView) convertView.findViewById(R.id.tv_group);
				rightImgHolder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				rightImgHolder.img1=(ImageView) convertView.findViewById(R.id.img1);
				rightImgHolder.img2=(ImageView) convertView.findViewById(R.id.img2);
				rightImgHolder.number = (TextView) convertView.findViewById(R.id.tv_number);
				convertView.setTag(rightImgHolder);
				break;
			case TYPE_RIGHT_AUDIO :	
				rightAudioHolder=new RightAudioHolder();
				convertView=LayoutInflater.from(mContext).inflate(R.layout.chat_right_audio_layout, null);
				rightAudioHolder.civ=(ImageView) convertView.findViewById(R.id.civ);
				rightAudioHolder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
				rightAudioHolder.tv_group=(TextView) convertView.findViewById(R.id.tv_group);
				rightAudioHolder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				rightAudioHolder.tv_audiotime=(TextView) convertView.findViewById(R.id.tv_audiotime);
				rightAudioHolder.img_audioplay=(ImageView) convertView.findViewById(R.id.img_audioplay);
				rightAudioHolder.rl_audio=(RelativeLayout) convertView.findViewById(R.id.rl_audio);
				convertView.setTag(rightAudioHolder);
				break;
			case TYPE_RIGHT_VIDEO:
				rightVideoHolder=new RightVideoHolder();
				convertView=LayoutInflater.from(mContext).inflate(R.layout.chat_right_video_layout, null);
				rightVideoHolder.civ=(ImageView) convertView.findViewById(R.id.civ);
				rightVideoHolder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
				rightVideoHolder.tv_group=(TextView) convertView.findViewById(R.id.tv_group);
				rightVideoHolder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
				rightVideoHolder.img_video=(ImageView) convertView.findViewById(R.id.img_video);
				rightVideoHolder.img_videoplay=(ImageView) convertView.findViewById(R.id.img_videoplay);
				rightVideoHolder.rl_video=(RelativeLayout) convertView.findViewById(R.id.rl_video);
				convertView.setTag(rightVideoHolder);
				break;
			}
		}else{
			switch (type) {
			case TYPE_LEFT_TEXT:
				leftTextHolder=(LeftTextHolder) convertView.getTag();
				break;
			case TYPE_LEFT_IMAGE:
				leftImgHolder=(LeftImgHolder) convertView.getTag();
				break;
			case TYPE_LEFT_VIDEO:
				leftVideoHolder=(LeftVideoHolder) convertView.getTag();
				break;
			case TYPE_LEFT_AUDIO:
				leftAudioHolder=(LeftAudioHolder) convertView.getTag();
				break;
			case TYPE_RIGHT_TEXT:
				rightTextHolder=(RightTextHolder) convertView.getTag();
				break;
			case TYPE_RIGHT_IMAGE:
				rightImgHolder=(RightImgHolder) convertView.getTag();
				break;
			case TYPE_RIGHT_AUDIO:
				rightAudioHolder=(RightAudioHolder) convertView.getTag();
				break;
			case TYPE_RIGHT_VIDEO:
				rightVideoHolder=(RightVideoHolder) convertView.getTag();
				break;
			}
		}
		
		switch (type) {
			case TYPE_LEFT_TEXT:
				imageLoader.displayImage(chatMessageBean.getSenderImage(), leftTextHolder.civ, options);
				leftTextHolder.tv_name.setText(chatMessageBean.getSenderName());
				leftTextHolder.tv_group.setText(chatMessageBean.getSenderDepartmentName());
				leftTextHolder.tv_message.setText(chatMessageBean.getContent());
				leftTextHolder.tv_time.setText(chatMessageBean.getCreateTime());
				break;
			case TYPE_LEFT_IMAGE:
				String leftimages = chatMessageBean.getImages();
				ArrayList<String> leftStrings = new ArrayList<String>();
			try {
				JSONArray jsonArray = new JSONArray(leftimages);
				for (int i = 0; i < jsonArray.length(); i++) {
					leftimages = jsonArray.getString(i);
					leftStrings.add(leftimages);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
				
				//rightimages = rightimages.substring(2, rightimages.length()-2);
				//Log.e("===============", rightimages);
				if (leftStrings.size()>1) {
					leftImgHolder.img2.setVisibility(View.VISIBLE);
					leftImgHolder.img1.setVisibility(View.VISIBLE);
					imageLoader.displayImage(leftStrings.get(0), leftImgHolder.img1, options);
					imageLoader.displayImage(leftStrings.get(1), leftImgHolder.img2, options);
					leftImgHolder.number.setVisibility(View.VISIBLE);
					leftImgHolder.number.setText(leftStrings.size() + "");
				}else {
					if (leftStrings.size() == 1) {
						if(leftImgHolder.img2!=null){
							leftImgHolder.number.setVisibility(View.GONE);
							leftImgHolder.img2.setVisibility(View.GONE);
							imageLoader.displayImage(leftStrings.get(0), leftImgHolder.img1, options);
						}
					}else {
						leftImgHolder.number.setVisibility(View.GONE);
						leftImgHolder.img2.setVisibility(View.GONE);
						leftImgHolder.img1.setVisibility(View.GONE);
					}
					
					
				}
				leftImgHolder.tv_name.setText(chatMessageBean.getSenderName());
				leftImgHolder.tv_group.setText(chatMessageBean.getSenderDepartmentName());
				leftImgHolder.tv_time.setText(chatMessageBean.getCreateTime());
				imageLoader.displayImage(chatMessageBean.getSenderImage(), leftImgHolder.civ, options);
				
				break;
			case TYPE_LEFT_AUDIO:
				final ImageView ieaLlSinger = leftAudioHolder.img_audioplay;
				imageLoader.displayImage(chatMessageBean.getSenderImage(), leftAudioHolder.civ, options);
				leftAudioHolder.tv_name.setText(chatMessageBean.getSenderName());
				leftAudioHolder.tv_group.setText(chatMessageBean.getSenderDepartmentName());
				leftAudioHolder.tv_time.setText(chatMessageBean.getCreateTime());
				leftAudioHolder.tv_audiotime.setText(chatMessageBean.getDuration());
				//imageLoader.displayImage(chatMessageBean.getImages(), rightAudioHolder.img_audioplay, options);
				leftAudioHolder.rl_audio.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
//		                notifyDataSetChanged();
		                final AnimationDrawable animationDrawable = (AnimationDrawable) ieaLlSinger.getBackground();
		                //重置动画
		                resetAnim(animationDrawable);
		                animationDrawable.start();

		                //处理点击正在播放的语音时，可以停止；再次点击时重新播放。
		                if (pos == position) {
//		                    if (record.isPlaying()) {
//		                        record.setPlaying(false);
		                        MediaManager.release();
		                        animationDrawable.stop();
		                        animationDrawable.selectDrawable(0);//reset
		                        return;
//		                    } else {
//		                        record.setPlaying(true);
//		                    }
		                }
		                //记录当前位置正在播放。
		                pos = position;
//		                record.setPlaying(true);

		                //播放前重置。
		                MediaManager.release();
		                //开始实质播放
		                MediaManager.playSound(chatMessageBean.getContent(),
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
				break;
			case TYPE_LEFT_VIDEO:
				leftVideoHolder.tv_name.setText(chatMessageBean.getSenderName());
				leftVideoHolder.tv_group.setText(chatMessageBean.getSenderDepartmentName());
				leftVideoHolder.tv_time.setText(chatMessageBean.getCreateTime());
				imageLoader.displayImage(chatMessageBean.getSenderImage(), leftVideoHolder.civ, options);
				imageLoader.displayImage(chatMessageBean.getPreviewUrl(), leftVideoHolder.img_video, options);
				leftVideoHolder.rl_video.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent=new Intent(mContext, ShowVideoActivity.class);
						intent.putExtra("playpath", chatMessageBean.getContent());
						mContext.startActivity(intent);
					}
				});
				break;
			case TYPE_RIGHT_TEXT:
				imageLoader.displayImage(chatMessageBean.getSenderImage(), rightTextHolder.civ, options);
				rightTextHolder.tv_name.setText(chatMessageBean.getSenderName());
				rightTextHolder.tv_group.setText(chatMessageBean.getSenderDepartmentName());
				rightTextHolder.tv_message.setText(chatMessageBean.getContent());
				rightTextHolder.tv_time.setText(chatMessageBean.getCreateTime());
				break;
			case TYPE_RIGHT_IMAGE:
				String rightimages = chatMessageBean.getImages();
				final ArrayList<String> rightstrings = new ArrayList<String>();
			try {
				JSONArray jsonArray = new JSONArray(rightimages);
				for (int i = 0; i < jsonArray.length(); i++) {
					rightimages = jsonArray.getString(i);
					rightstrings.add(rightimages);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
				if (rightstrings.size()>1) {
					rightImgHolder.img2.setVisibility(View.VISIBLE);
					rightImgHolder.img1.setVisibility(View.VISIBLE);
					imageLoader.displayImage(rightstrings.get(0), rightImgHolder.img1, options);
					imageLoader.displayImage(rightstrings.get(1), rightImgHolder.img2, options);
					rightImgHolder.number.setVisibility(View.VISIBLE);
					rightImgHolder.number.setText(rightstrings.size() + "");
				}else {
					if (rightstrings.size() == 1) {
						if(rightImgHolder.img2!=null){
							rightImgHolder.number.setVisibility(View.GONE);
							rightImgHolder.img2.setVisibility(View.GONE);
							imageLoader.displayImage(rightstrings.get(0), rightImgHolder.img1, options);
						}
					}else {
						rightImgHolder.number.setVisibility(View.GONE);
						rightImgHolder.img2.setVisibility(View.GONE);
						rightImgHolder.img1.setVisibility(View.GONE);
					}
				}
				rightImgHolder.tv_name.setText(chatMessageBean.getSenderName());
				rightImgHolder.tv_group.setText(chatMessageBean.getSenderDepartmentName());
				rightImgHolder.tv_time.setText(chatMessageBean.getCreateTime());
				imageLoader.displayImage(chatMessageBean.getSenderImage(), rightImgHolder.civ, options);
				rightImgHolder.img1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent=new Intent(mContext, PhotoActivity.class);
						intent.putExtra("position", (0+1)+"");
						intent.putExtra("list", (Serializable)rightstrings);
						mContext.startActivity(intent);
					}
				});
				rightImgHolder.img2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent=new Intent(mContext, PhotoActivity.class);
						intent.putExtra("position", 1+"");
						intent.putExtra("list", (Serializable)rightstrings);
						mContext.startActivity(intent);
					}
				});
				break;
			case TYPE_RIGHT_AUDIO:
				final ImageView ieaLlSinger1 = rightAudioHolder.img_audioplay;
				imageLoader.displayImage(chatMessageBean.getSenderImage(), rightAudioHolder.civ, options);
				rightAudioHolder.tv_name.setText(chatMessageBean.getSenderName());
				rightAudioHolder.tv_group.setText(chatMessageBean.getSenderDepartmentName());
				rightAudioHolder.tv_time.setText(chatMessageBean.getCreateTime());
				rightAudioHolder.tv_audiotime.setText(chatMessageBean.getDuration() + "\"");
				//imageLoader.displayImage(chatMessageBean.getSenderImage(), rightAudioHolder.img_audioplay, options);
				
				rightAudioHolder.rl_audio.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
//		                notifyDataSetChanged();
		                final AnimationDrawable animationDrawable = (AnimationDrawable) ieaLlSinger1.getBackground();
		                //重置动画
		                resetAnim(animationDrawable);
		                animationDrawable.start();

		                //处理点击正在播放的语音时，可以停止；再次点击时重新播放。
		                if (pos == position) {
//		                    if (record.isPlaying()) {
//		                        record.setPlaying(false);
		                        MediaManager.release();
		                        animationDrawable.stop();
		                        animationDrawable.selectDrawable(0);//reset
		                        return;
//		                    } else {
//		                        record.setPlaying(true);
//		                    }
		                }
		                //记录当前位置正在播放。
		                pos = position;
//		                record.setPlaying(true);

		                //播放前重置。
		                MediaManager.release();
		                //开始实质播放
		                MediaManager.playSound(chatMessageBean.getContent(),
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
				break;
			case TYPE_RIGHT_VIDEO:
				rightVideoHolder.tv_name.setText(chatMessageBean.getSenderName());
				rightVideoHolder.tv_group.setText(chatMessageBean.getSenderDepartmentName());
				rightVideoHolder.tv_time.setText(chatMessageBean.getCreateTime());
				imageLoader.displayImage(chatMessageBean.getSenderImage(), rightVideoHolder.civ, options);
				imageLoader.displayImage(chatMessageBean.getPreviewUrl(), rightVideoHolder.img_video, options);
				rightVideoHolder.rl_video.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent=new Intent(mContext, ShowVideoActivity.class);
						intent.putExtra("playpath", chatMessageBean.getContent());
						mContext.startActivity(intent);
					}
				});
				break;
		}
		
		return convertView;
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
	
	@Override
	public int getItemViewType(int position) {
		int type=new Integer(mListBean.get(position).getMsgType());//1：文字、2：图片、3：音频、4：视频
		String sendId=mListBean.get(position).getSenderId();
		if(sendId.equals(mUserId)){//表示该消息为当前登录的用户所发
			if(type==1){
				type=TYPE_RIGHT_TEXT;
			}else if(type==2){
				type=TYPE_RIGHT_IMAGE;
			}else if(type==3){
				type=TYPE_RIGHT_AUDIO;
			}else if(type==4){
				type=TYPE_RIGHT_VIDEO;
			}else{
				type=TYPE_RIGHT_TEXT;
			}
		}else{//表示该消息非当前登录的用户所发
			if(type==1){
				type=TYPE_LEFT_TEXT;
			}else if(type==2){
				type=TYPE_LEFT_IMAGE;
			}else if(type==3){
				type=TYPE_LEFT_AUDIO;
			}else if(type==4){
				type=TYPE_LEFT_VIDEO;
			}else{
				type=TYPE_LEFT_TEXT;
			}
		}
		return type;
	}
	@Override
	public int getViewTypeCount() {
		return 9;
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
	
	/**
	 * 显示在左侧的文本类型
	 * @author howie
	 */
	private static class LeftTextHolder{
		ImageView civ;//头像
		TextView tv_name,tv_group;//姓名和单位
		TextView tv_time;//发表时间
		TextView tv_message;//文本
		View ll_pop;//"选用'和“复制”所在的布局
		TextView tv_select,tv_copy;//“选用”和“复制”
	}
	
	/**
	 * 左侧图片
	 * @author pc
	 *
	 */
	public class LeftImgHolder{
		ImageView civ;//头像
		TextView tv_name,tv_group;//姓名和单位
		TextView tv_time;//发表时间
		ImageView img1;
		ImageView img2;
		TextView number;
	}
	
	/**
	 * 左侧视频
	 * @author pc
	 *
	 */
	public class LeftVideoHolder{
		ImageView civ,img_video,img_videoplay;//头像
		TextView tv_name,tv_group,tv_time;//姓名和单位
		RelativeLayout rl_video;
	}
	
	/**
	 * 左侧音频
	 * @author pc
	 *
	 */
	public class LeftAudioHolder{
		ImageView civ;//头像
		TextView tv_name,tv_group;//姓名和单位
		TextView tv_time,tv_audiotime;//发表时间
		ImageView img_audioplay;
		RelativeLayout rl_audio;
	}
	
	/**
	 * 右侧文字
	 * @author pc
	 *
	 */
	public class RightTextHolder{
		ImageView civ;//头像
		TextView tv_name,tv_group;//姓名和单位
		TextView tv_time;//发表时间
		TextView tv_message;//文本
	}
	
	/**
	 * 右侧图片
	 * @author pc
	 *
	 */
	public class RightImgHolder{
		ImageView civ;//头像
		TextView tv_name,tv_group;//姓名和单位
		TextView tv_time;//发表时间
		ImageView img1;
		ImageView img2;
		TextView number;
	}
	
	/**
	 * 右侧音频
	 * @author pc
	 *
	 */
	
	public class  RightAudioHolder{
		ImageView civ;//头像
		TextView tv_name,tv_group;//姓名和单位
		TextView tv_time,tv_audiotime;//发表时间
		ImageView img_audioplay;
		RelativeLayout rl_audio;
	}
	

	/**
	 * 右侧视频
	 * @author pc
	 *
	 */
	public class RightVideoHolder{
		ImageView civ,img_video,img_videoplay;//头像
		TextView tv_name,tv_group,tv_time;//姓名和单位
		RelativeLayout rl_video;
	}
	
	public class LeftMoreImgHolder{
		ImageView civ;//头像
		TextView tv_name,tv_group;//姓名和单位
		TextView tv_time;//发表时间
		ImageView img1,img2,img3;
		TextView number;
	}
	
	public class RightMoreImgHolder{
		ImageView civ;//头像
		TextView tv_name,tv_group;//姓名和单位
		TextView tv_time;//发表时间
		ImageView img1,img2,img3;
		TextView number;
	}
}
