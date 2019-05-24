package com.jwzt.caibian.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jwzt.caibian.activity.BackTransferActivity;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.AboutUsActivity;
import com.jwzt.caibian.activity.AudioQualityActivity;
import com.jwzt.caibian.activity.GroupChatActivity;
import com.jwzt.caibian.activity.ImageQualityActivity;
import com.jwzt.caibian.activity.LiveSettingActivity;
import com.jwzt.caibian.activity.PrivateSettingActivity;
import com.jwzt.caibian.activity.VideoLevelActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.caibian.bean.ChatMessageBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.db.ChatsDao;
import com.jwzt.caibian.db.DatabaseHelper;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.SharePreferenceUtils;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.widget.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 我的
 * @author howie
 *
 */
@SuppressLint("ValidFragment")
public class MineFragment extends Fragment implements OnClickListener {
	/**群组交流*/
	private View rl_group;
	/**昵称*/
	private View rl_nick;
	/**视频质量*/
	private View rl_video_quality;
	/**音频质量*/
	private View rl_audio_quality;
	/**图片质量*/
	private View rl_image_quality;
	/**直播质量*/
	private View rl_live_quality;
	/**配置文件*/
	private View rl_config;
	/**右上角的感叹号按钮*/
	private View iv_alert;
	/**个人资料右侧的箭头按钮*/
	private View iv_person;
	/**个人资料所在的relativeLayout*/
	private View rl;
	private View view;
	private SharePreferenceUtils mSharePreferenceUtils;
	//private TextView mTv_nick;
	private final int NEWNICK = 0;
	private final int VIDEOQUALITY = 1;
	private TextView tv_live_quality;
	private final int VIDEOLEVELQUALITY = 2;
	private TextView mTv_video_quality;
	private final int LOCATIONAUDIOQUALITY = 3;
	private TextView tv_audio_quality;
	private final int IMAGEQUALITY = 5;
	private TextView tv_image_quality,tv_messagenum;
	private com.jwzt.caibian.widget.CircleImageView riv;
	private TextView tv_nickname,tv_group;
	
	private ChatsDao chatsDao;
	private List<ChatMessageBean> chatmessageList;
	private CbApplication application;
	private LoginBean mLoginBean;
	private ImageLoader imageLoader;
    private DisplayImageOptions options;
	
	private DatabaseHelper mDatabaseHelper;
	
	public MineFragment(DatabaseHelper databaseHelper) {
		this.mDatabaseHelper=databaseHelper;
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view=View.inflate(getActivity(), R.layout.fragment_mine, null);
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.headdefault) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.headdefault) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.headdefault) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(false) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中 
        .bitmapConfig(Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
        .build(); // 构建完成  
	    imageLoader = ImageLoader.getInstance();
	    application=(CbApplication) getActivity().getApplication();
		mLoginBean=application.getmLoginBean();
		chatsDao=new ChatsDao(mDatabaseHelper);
		
	    mSharePreferenceUtils = new SharePreferenceUtils();
		
	    findView();
	    

		return view;
	}
	
	private void findView(){
		rl_group=view.findViewById(R.id.rl_group);
		rl_group.setOnClickListener(this);
		//rl_nick=view.findViewById(R.id.rl_nick);
		//rl_nick.setOnClickListener(this);
		//mTv_nick = (TextView)view.findViewById(R.id.tv_nick);
		//String USERNAMENICKNAME = mSharePreferenceUtils.getString(UIUtils.getContext(), GlobalContants.USERNAMENICKNAME, "追风筝的人");
		//mTv_nick.setText(USERNAMENICKNAME);
		rl_video_quality=view.findViewById(R.id.rl_video_quality);
		rl_video_quality.setOnClickListener(this);
		View rl_huichuan_quality = view.findViewById(R.id.rl_huichuan_quality);
		rl_huichuan_quality.setOnClickListener(this);
		mTv_video_quality = (TextView)view.findViewById(R.id.tv_video_quality);
		rl_audio_quality=view.findViewById(R.id.rl_audio_quality);
		rl_audio_quality.setOnClickListener(this);
		tv_audio_quality = (TextView) view.findViewById(R.id.tv_audio_quality);
		rl_image_quality=view.findViewById(R.id.rl_image_quality);
		rl_image_quality.setOnClickListener(this);
		tv_image_quality = (TextView) view.findViewById(R.id.tv_image_quality);
		rl_live_quality=view.findViewById(R.id.rl_live_quality);
		tv_live_quality = (TextView) view.findViewById(R.id.tv_live_quality);
		tv_messagenum=(TextView) view.findViewById(R.id.tv_messagenumber);
		tv_nickname=(TextView) view.findViewById(R.id.tv_nickname);
		tv_group=(TextView) view.findViewById(R.id.tv_group);
		
		int TARGETRESOLUTION_H = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.TARGETRESOLUTION_H, 0);
		if (TARGETRESOLUTION_H != 0) {
			tv_live_quality.setText(TARGETRESOLUTION_H+"P");
		}
		
		rl_live_quality.setOnClickListener(this);
		rl_config=view.findViewById(R.id.rl_config);
		rl_config.setOnClickListener(this);
		iv_alert=view.findViewById(R.id.iv_alert);
		iv_alert.setOnClickListener(this);
		iv_person=view.findViewById(R.id.iv_person);
		iv_person.setOnClickListener(this);
		rl=view.findViewById(R.id.rl);
		rl.setOnClickListener(this);
		
		riv=(com.jwzt.caibian.widget.CircleImageView) view.findViewById(R.id.riv);
		imageLoader.displayImage(mLoginBean.getPhoto(), riv, options);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		application=(CbApplication) getActivity().getApplication();
		mLoginBean=application.getmLoginBean();
		String photo=mLoginBean.getPhoto();
		System.out.println("===========================>>"+photo);
		if(IsNonEmptyUtils.isString(photo)){
			imageLoader.displayImage(mLoginBean.getPhoto(), riv, options);
		}
		
		if(mLoginBean!=null){
			if(IsNonEmptyUtils.isString(mLoginBean.getUsername())){
				tv_nickname.setText(mLoginBean.getUsername());
			}
			
			if(IsNonEmptyUtils.isString(mLoginBean.getDepartmentName())){
				tv_group.setText(mLoginBean.getDepartmentName());
			}
		}
		
		if(mLoginBean!=null){
			chatmessageList=chatsDao.getAllNoreadMessage(new Integer(mLoginBean.getUserID()), 1);
			if(tv_messagenum!=null){
				if(IsNonEmptyUtils.isList(chatmessageList)){
					tv_messagenum.setVisibility(View.VISIBLE);
					tv_messagenum.setText(chatmessageList.size()+"");
				}else{
					tv_messagenum.setVisibility(View.GONE);
				}
			}
		}
	}
	
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.rl_group://群组交流
			Intent intent=new Intent(getActivity(),GroupChatActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		/*case R.id.rl_nick://昵称
			Intent intent_nick=new Intent(getActivity(),UserNameModifyActivity.class);
			intent_nick.putExtra("isFromIndex", true);
			startActivityForResult(intent_nick,NEWNICK );
			getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			break;*/
		case R.id.rl_video_quality://视频质量
			Intent intent_video=new Intent(getActivity(),VideoLevelActivity.class);
			startActivityForResult(intent_video,VIDEOLEVELQUALITY );
			getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			break;
		case R.id.rl_huichuan_quality://视频质量
			Intent intent_back = new Intent(getActivity(), BackTransferActivity.class);
			intent_back.putExtra("tag", "backmanager");
			startActivity(intent_back);
			getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			break;
		case R.id.rl_audio_quality://音频质量
			Intent intent_audio=new Intent(getActivity(),AudioQualityActivity.class);
			startActivityForResult(intent_audio, LOCATIONAUDIOQUALITY);
			getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			break;
		case R.id.rl_image_quality://图片质量
			Intent intent_image=new Intent(getActivity(),ImageQualityActivity.class);
			startActivityForResult(intent_image,IMAGEQUALITY );
			getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			break;
		case R.id.rl_live_quality://直播质量
			Intent intent_live=new Intent(getActivity(),LiveSettingActivity.class);
			startActivityForResult(intent_live,VIDEOQUALITY);
			getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			
			break;
		case R.id.rl_config://配置文件
			
			break;
		case R.id.iv_alert://右上角的感叹号按钮
			Intent intent_about_us=new Intent(getActivity(),AboutUsActivity.class);
			startActivity(intent_about_us);
			getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			break;
		case R.id.iv_person://个人资料右侧的箭头按钮
			
			break;
		case R.id.rl://个人资料信息
			Intent intent_private=new Intent(getActivity(),PrivateSettingActivity.class);
			startActivity(intent_private);
			getActivity().overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			break;
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		/*case NEWNICK:
			String USERNAMENICKNAME = mSharePreferenceUtils.getString(UIUtils.getContext(), GlobalContants.USERNAMENICKNAME, "追风筝的人");
			mTv_nick.setText(USERNAMENICKNAME);
			break;*/
		case VIDEOQUALITY:
			int TARGETRESOLUTION_H = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.TARGETRESOLUTION_H, 0);
			if (TARGETRESOLUTION_H != 0) {
				tv_live_quality.setText(TARGETRESOLUTION_H+"P");
			}
			break;
			
		case VIDEOLEVELQUALITY:
			int VIDEORESOLUTION_H = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.VIDEORESOLUTION_H, 0);
			if (VIDEORESOLUTION_H != 0) {
				mTv_video_quality.setText(VIDEORESOLUTION_H+"P");
			}
			break;
			
		case LOCATIONAUDIOQUALITY:
			int VOICEQUALITY = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.VOICEQUALITY, 0);
			if (VOICEQUALITY != 0) {
				tv_audio_quality.setText(VOICEQUALITY+"kbps");
			}
			break;
			
		case IMAGEQUALITY:
			String PICTUREQUALITY = mSharePreferenceUtils.getString(UIUtils.getContext(), GlobalContants.PICTUREQUALITY, "");
			if (!TextUtils.isEmpty(PICTUREQUALITY)) {
				tv_image_quality.setText(PICTUREQUALITY);
			}
			break;
			
			

		default:
			break;
		}
	}
}
