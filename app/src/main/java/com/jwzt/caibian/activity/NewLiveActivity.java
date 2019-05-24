package com.jwzt.caibian.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.message.BasicNameValuePair;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.caibian.application.GlobalConfig;
import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.activity.NewLiveActivity.WifiChangeBroadcastReceiver;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LiveBackListBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.kit.KSYPipStreamer;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.CheckMemorySpace;
import com.jwzt.caibian.util.DynamicRequestPermissionUtils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.SdcardSize;
import com.jwzt.caibian.util.SharePreferenceUtils;
import com.jwzt.caibian.util.SingleToast;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.view.CameraHintView;
import com.jwzt.caibian.xiangce.ImageLoader;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.streamer.capture.camera.CameraTouchHelper;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautyProFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgFilterBase;
import com.ksyun.media.streamer.filter.imgtex.ImgTexFilterBase;
import com.ksyun.media.streamer.filter.imgtex.ImgTexFilterMgt;
import com.ksyun.media.streamer.kit.KSYStreamer.OnErrorListener;
import com.ksyun.media.streamer.kit.KSYStreamer.OnInfoListener;
import com.ksyun.media.streamer.kit.StreamerConstants;
import com.ksyun.media.streamer.util.gles.GLRender;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Created by Administrator on 2017/5/11.
 */
public class NewLiveActivity extends BaseActivity implements OnClickListener
		 {
	private ImageView iv_tpscl_3x;
	private static final int REQUESTCODEVIDEO = 0;

	private static final int REQUESTCODEIMAGE = 1;
	private static final int REQUESTCODEIMAGETWO = 2;
	private String picUri;
	private String videoUri;
	private boolean isVideoing = false;
	private boolean isPicing = false;
	private String TAG = NewLiveActivity.class.getName();
	private RelativeLayout mNew_new_rl_pic_tl;
	private boolean isSetting = false;
	private boolean isShow = false;
	private TranslateAnimation mStartAnimation;
	private TranslateAnimation mEndAnimation;
	private String mUriOne = null;
	private String mUriTwo = null;
	private boolean isStartpush = false;
	private boolean isStartpushTow;
	private RelativeLayout mRl_new_top_isshowmNew;
///////////////////////////////这里面都是拷贝过来的//////////////////////////////////
	public static final int BATTERLEVEL = 100;
	private boolean isStart = false;
	private TelephonyManager mTelephonyManager;
	private PhoneStatListener mListener;
	private MediaPlayer shootMP;
	private boolean isSwitchCamera = false;

	////////////////////////////////////////////////////
	CameraTouchHelper mCameraTouchHelper;
	// ///////////////////////////////////////////////////////
	private View rl_root;
	private String livetime;
	private String tempString;
	private String mPipPath ;
	
	//这里是路径/***视频文件保存路径*/
	public final String BLSavePath=Configs.BLSavePath;
	/***图片文件保存路径*/
	public final String BLSavePathImg=Configs.BLSavePathImg;
	/**
	 * 推流的地址
	 */
	private String mRtmpUrl = GlobalConfig.URL_RTMP_PUSH;
	
	private String id;
	

	
	// 是否正在推流
	private boolean mPublishing = false;
	///////////////判断是否检测是否第一次弹出框//////////////
	private boolean isFirstCheckMemory = true;
	// 横竖屏设置参数0为横屏 1为竖屏
	static int mPublishOrientation = 0;
	///////////////////////判断是否显示了画中画///////////////////////
	private  boolean mPipMode = false;
	// //////////////////这里是handle的状态码/////////////////////////////////////
	private static final int RECORD_TIME = 9;
	// /////////////////////////////////////////////////
	/**
	 * 00 : 00 : 00 hour
	 */
	private int hour = 0;
	/**
	 * 00 : 00 : 00 minute
	 */
	private int minute = 0;
	/**
	 * 00 : 00 : 00 second
	 */
	private int second = 0;
	// ////////////////////////////////////////////////
	// ///////////网络速度第一次取到数值和第二次取到数值/////////
	private long mOne = 0;
	private long startTraffic = 0;
	private long totalTraffic = 0;
	private long mTwo = 0;
	private long mCurrent = 0;
	private static final int TIME = 1000;
	///////////////////////画中画控件/////////////////////
	private CameraHintView mCamera_hint;

	private boolean isWifi;
	// Handle状态码监视wifi网速变化
	private static final int WIFI_NETWORK_SPEED = 0;

	private static final int MOBILE_NETWORK_SPEED = 1;
	private KSYPipStreamer mStreamer;
	private LinearLayout mLl_beauty_set;
	/////////滤镜///////////////
	private ImgFilterBase filter;
	///////////////TAG打印标记///////////////////////////
	// 这里是找到的控件
	// /////////信号变化的值///////////////////
	private int gsmSignalStrength = 0;
	
	private NewLiveActivity mActivity;
	private ImageView mIv_back_return;// 这里是返回的按钮
	private ImageView mIv_voice_open;// 这里声音开启关闭的按钮
	private ImageView mIv_shot_change;// 这里是摄像头转换的按钮
	private ImageView mIv_lamp_closed;// 这里是开启关闭闪光灯的按钮
	private ImageView mIv_meiyan_open;// 这里是美颜的按钮
	private ImageView mIv_tpscl_3x;// 这里是图片链接按钮
	private ImageView mIv_photo_round;// 这里是图片截屏按钮
	private ImageView mIv_cemera_round;// 这里是推流开始和关闭的按钮
	private ImageView mIv_set_round;// 这里是设置按钮
	private ImageView mIv_iv_net;// 这里是WiFi还是数据流量的显示表示
	private ImageView mIv_count;// 这里是开始计时的显示标识
	private TextView mTv_time;// 这里是计时的时间
	private ImageView mIv_ttery_100;// 这里是电量变化的标识
	private TextView mTv_ssml;// 这里是码率提示
	private TextView mTv_ssmlbh;// 这里是码率时时变化的展示
	private TextView mTv_total_flow;// 这里是总流量的展示
	private TextView mTv_flowbh;// 这里是总流量时时变化的展示
	private ImageView mIv_uploading_pic;// 这里是请上传图片
	private ImageView mIv_tv_add_pic;// 这里是加号
	private Button mBt_end_live;// 这里是结束推流
	private Button mBt_bt_start_push_live;// 这里是开始推流
	private CameraHintView camera_hint;// 这里是画中画控件
	private GLSurfaceView mCamera_preview;// 这里是glsurfaceview
	private RelativeLayout mNew_activity_live_left;
	private RelativeLayout mNew_rl_pic_tl;
	private TextView tv_live_close;
	private TextView tv_not_du;
	private TextView mTv_signal;
	private TextView mTv_New_tv_show_hint;// 这里是提示的显示
	private RelativeLayout mRl_new_top_isshow;
	private TextView mTv_new_Network_signal_is_not_stable;// 底部网络的提示
	private RelativeLayout mRl_new_is_show_beauty;
	private SeekBar mNew_beauty_seekbar;
	private SeekBar mNew_light_seekbar;
	private TextView tv_new_beauty;
	private TextView tv_new_light;
	private RelativeLayout mRelativeLayout2;
	
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				
//				InitKSYStreamer();
//				initNetReceiver();
//				initCameraTouchHelper(mCamera_preview);//初始化CameraTouchHelper
//				setOnBeautyErrorListener();
//				telephoneClickListener();
				mRtmpUrl = createLiveBean.getLiveUrl();
//				mRtmpUrl = "rtmp://47.93.185.248:1935/live/1361499399061042_tzwj_1000k";
				System.out.println("mRtmpUrl:"+mRtmpUrl);
				mStreamer.setUrl(mRtmpUrl);
				break;
			// ///////////推流录制的时间变化/////////////////////
			case RECORD_TIME:
				if (second <= 59) {
					second++;
				}

				if (second > 59) {
					second = 0;
					minute++;
					if (minute > 59) {
						minute = 0;
						hour++;
					}
				}
				// format time
				mTv_time.setText(String.format(livetime, hour, minute, second));
				mHandler.sendEmptyMessageDelayed(RECORD_TIME, 1000);
				checkMemory();
				break;
			case WIFI_NETWORK_SPEED:
				mTwo = 1024 *8* mStreamer.getUploadedKBytes();
				totalTraffic = (int)((mTwo - startTraffic)/8);
				mCurrent = mTwo - mOne;
				mOne = mTwo;
				mCurrent = Math.abs(mCurrent);
				if (mCurrent<1024) {
					mCurrent = 1024 ;
				}
				mTv_ssmlbh.setText(Formatter.formatFileSize(
						UIUtils.getContext(), mCurrent));
				mTv_flowbh.setText(Formatter.formatFileSize(
						UIUtils.getContext(), totalTraffic));
				mHandler.sendEmptyMessageDelayed(WIFI_NETWORK_SPEED, TIME);
				break;
			/*case MOBILE_NETWORK_SPEED:
				mTwo = 1024 *8* mStreamer.getUploadedKBytes();
				mCurrent = mTwo - mOne;
				mOne = mTwo;
				mCurrent = Math.abs(mCurrent);
				if (mCurrent<1024) {
					mCurrent = 1024 ;
				}
				cellData.setText(Formatter.formatFileSize(
						UIUtils.getContext(), mCurrent));
				setPointAnimation(mCurrent);
				mHandler.sendEmptyMessageDelayed(MOBILE_NETWORK_SPEED, TIME);
				break;*/
				case BATTERLEVEL:
					((NewLiveActivity)mActivity).chengBatterUpdateUi(UIUtils.getBatteryManager());
					sendEmptyMessageDelayed(BATTERLEVEL,1000*60);
					break;
			}
		}

	};


	private boolean isMute = false;
	private boolean isToggle = false;
	private boolean isBelity = false;
	private View new_activity_live;
	public boolean isNetConnected = false;
	private String mRecordUrl;
	private long mAvailableSize;
	private boolean isHaveSdcard = false;
	private boolean iSLOCALVIDEO = false;
	private SharePreferenceUtils mSharePreferenceUtils;
	private SdcardSize mSdcardSize;	
	private File mPathFileBLSavePath;
	private File mPathFileBLSavePathImg;
	private ImageLoader mImageLoader;
	
	private LiveBackListBean createLiveBean;
	private NetChangeReceive netChangeReceive;
	private CbApplication application;
	private LoginBean mLoginBean;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new_activity_live = View.inflate(getApplicationContext(),R.layout.new_activity_live, null);
		setContentView(new_activity_live);
		id=getIntent().getStringExtra("liveId");
		application=(CbApplication) getApplication();
		mLoginBean=application.getmLoginBean();
		//申请相机权限
		DynamicRequestPermissionUtils.requestPermissionCAMERA(NewLiveActivity.this, new String[]{Manifest.permission.CAMERA},1);
		initRequestLiveUrl();
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (UIUtils.isHaveSdcard()) {
			isHaveSdcard  = true;
			if (mPathFileBLSavePath == null) {
				mPathFileBLSavePath = new File(BLSavePath);
				if (!mPathFileBLSavePath.exists()) {
					mPathFileBLSavePath.mkdirs();
				}
			}
			if (mPathFileBLSavePathImg == null) {
				mPathFileBLSavePathImg = new File(BLSavePathImg);
				if (!mPathFileBLSavePathImg.exists()) {
					mPathFileBLSavePathImg.mkdirs();
				}
			}
		}else {
			isHaveSdcard  = false;
		}
		
		if (mSharePreferenceUtils == null) {
			mSharePreferenceUtils = new SharePreferenceUtils();
		}
		this.mActivity = this;
		mSdcardSize = new SdcardSize();
		initData();
		initview();
		initAnimation();
		
//		initRequestLiveUrl();
		
		InitKSYStreamer();
		initNetReceiver();
		initCameraTouchHelper(mCamera_preview);//初始化CameraTouchHelper
		setOnBeautyErrorListener();
		telephoneClickListener();
		
	
	}
	
	private void initRequestLiveUrl(){
//		String liveUrl=String.format(Configs.liveDetailsUrl, id);
//		String liveUrl="http://47.93.185.248:9080/bvCaster_live/phone/live/activity/v_view.jspx?id=136";
//		RequestData(liveUrl, Configs.liveDetailsCode, 1);
			// TODO
//				try {
//					String createUrl = String.format(Configs.createLiveUrl, mLoginBean.getUserID(),URLEncoder.encode(name, "UTF-8"),startTime,endTime);
//					RequestData(createUrl, Configs.createLiveCode, 1);
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
		
//		mHandler.sendEmptyMessage(1);
		
				RequestParams params=new RequestParams();
				HttpUtils httpUtils=new HttpUtils();
//				?userId=%s&name=%s&startTime=%s&endTime=%s
				params.addBodyParameter(new BasicNameValuePair("id", id));     
				params.addBodyParameter(new BasicNameValuePair("userId", mLoginBean.getUserID()));     
				httpUtils.send(HttpMethod.POST, Configs.liveDetailsUrl, params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException c, String arg1) {
						System.out.println("==========="+c+arg1);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						System.out.println("========responseInfo.result===="+responseInfo.result);
						if(IsNonEmptyUtils.isString(responseInfo.result)){
							JSONObject jsonObject=JSON.parseObject(responseInfo.result);
							String status=jsonObject.getString("status");
							if(status.equals("100")){//表示获取成功
								String data=jsonObject.getString("data");
								createLiveBean=JSON.parseObject(data,LiveBackListBean.class);
								if(createLiveBean!=null){
									mHandler.sendEmptyMessage(1);
								}else{
									mHandler.sendEmptyMessage(0);
								}
							}
						}
					}
				});
	}

	private void initData() {
		mImageLoader = ImageLoader.getInstance();
	}

	private void initAnimation() {
		mStartAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
				0, Animation.RELATIVE_TO_PARENT, 0,
				Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT,
				0);
		mStartAnimation.setDuration(1000);
		mEndAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0,
				Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT,
				0, Animation.RELATIVE_TO_PARENT, 1);
		mEndAnimation.setDuration(1000);
	}
	
		private void initCameraTouchHelper(GLSurfaceView mCameraPreview) {
		// touch focus and zoom support
		mCameraTouchHelper = new CameraTouchHelper();
		mCameraTouchHelper.setCameraCapture(mStreamer.getCameraCapture());
		mCameraPreview.setOnTouchListener(mCameraTouchHelper);
		// set CameraHintView to show focus rect and zoom ratio
		mCameraTouchHelper.setCameraHintView(mCamera_hint);
	}

		private void initview() {
		mNew_new_rl_pic_tl = (RelativeLayout) findViewById(R.id.rl_pic_tl);
		// ///////////////////这里是初始化glsurfaceview和画中画控件/////////////////////////
		mCamera_preview = (GLSurfaceView) findViewById(R.id.camera_preview);
		camera_hint = (CameraHintView) findViewById(R.id.camera_hint);
		// ////////////////这里是左边的按钮///////////////////////////////////////
		mIv_back_return = (ImageView) findViewById(R.id.iv_back_return);
		mIv_voice_open = (ImageView) findViewById(R.id.iv_voice_open);
		mIv_shot_change = (ImageView) findViewById(R.id.iv_shot_change);
		mIv_lamp_closed = (ImageView) findViewById(R.id.iv_lamp_closed);
		mIv_meiyan_open = (ImageView) findViewById(R.id.iv_meiyan_open);
		mIv_tpscl_3x = (ImageView) findViewById(R.id.iv_tpscl_3x);
		// ///////////////////////////////////////////////////////////////////////
		// ////////////////这里是右边的按钮///////////////////////////////////////
		mIv_photo_round = (ImageView) findViewById(R.id.iv_photo_round);
		mIv_cemera_round = (ImageView) findViewById(R.id.iv_cemera_round);
		mIv_set_round = (ImageView) findViewById(R.id.iv_set_round);
		// ///////////////////////////////////////////////////////////////////////
		// //////////////////////这里是上边的按钮/////////////////////////////////
		mIv_iv_net = (ImageView) findViewById(R.id.iv_net);
		mIv_count = (ImageView) findViewById(R.id.iv_count);
		mTv_time = (TextView) findViewById(R.id.tv_time);
		livetime = mActivity.getResources().getString(R.string.livetime); // 时间显示格式
		tempString = String.format(livetime, 0, 0, 0);// 转换好的时间格式
		mTv_time.setText(tempString);
		mIv_ttery_100 = (ImageView) findViewById(R.id.iv_ttery_100);

		// ////////////////////这里是显示界面时时更新的内容/////////////////
		mTv_ssml = (TextView) findViewById(R.id.tv_ssml);
		mTv_ssmlbh = (TextView) findViewById(R.id.tv_ssmlbh);
		mTv_total_flow = (TextView) findViewById(R.id.tv_total_flow);
		mTv_flowbh = (TextView) findViewById(R.id.tv_total_flowbh);

		// ///////////////////////////这里是下面四方块内的控件/////////////////////
		mIv_uploading_pic = (ImageView) findViewById(R.id.iv_uploading_pic);
		mIv_uploading_pic.setOnClickListener(this);
		mIv_tv_add_pic = (ImageView) findViewById(R.id.iv_add_pic);
		mIv_tv_add_pic.setOnClickListener(this);
		mBt_end_live = (Button) findViewById(R.id.bt_end_live);
		mBt_end_live.setOnClickListener(this);
		mBt_bt_start_push_live = (Button) findViewById(R.id.bt_start_push_live);
		mBt_bt_start_push_live.setOnClickListener(this);

		// /////////////////////////这里是左下的布局////////////////
		mNew_activity_live_left = (RelativeLayout) findViewById(R.id.new_activity_live_left);
		mNew_rl_pic_tl = (RelativeLayout) findViewById(R.id.rl_pic_tl);

		
		// /////////////////////这里是信号类型的变化数值/////////////////////////
		mTv_signal = (TextView) findViewById(R.id.tv_signal);

		// //////////////////////////////获取对话框的组件////////////////////////
		
		// ////////////代替toast的提示///////////////////
		mTv_New_tv_show_hint = (TextView) findViewById(R.id.new_tv_show_hint);
		// 这里是头布局来控制它的显示与消失
		mRl_new_top_isshow = (RelativeLayout) findViewById(R.id.rl_new_top_isshow);
		mTv_new_Network_signal_is_not_stable = (TextView) findViewById(R.id.tv_new_Network_signal_is_not_stable);

		mRl_new_is_show_beauty = (RelativeLayout) findViewById(R.id.rl_new_is_show_beauty);
		// ///////////////////////美颜进度条的控制/////////////////////////////////////////

		mNew_beauty_seekbar = (SeekBar) findViewById(R.id.new_beauty_seekbar);
		mNew_beauty_seekbar.setMax(100);
		mNew_light_seekbar = (SeekBar) findViewById(R.id.new_light_seekbar);
		mNew_light_seekbar.setMax(100);
		mNew_light_seekbar.setProgress(50);
		tv_new_beauty = (TextView) findViewById(R.id.tv_new_beauty);
		tv_new_light = (TextView) findViewById(R.id.tv_new_light);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv_new_light
				.getLayoutParams();
		params.leftMargin = (int) ((UIUtils.dip2px(160) / 100.0f) * 50);
		tv_new_light.setLayoutParams(params);
		tv_new_light.setText("50");
		mRelativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
		
		mIv_back_return.setOnClickListener(this);// 设置返回的点击事件
		mIv_voice_open.setOnClickListener(this);// 设置声音开启关闭的点击事件
		mIv_shot_change.setOnClickListener(this);// 设置摄像头转换的点击事件
		mIv_lamp_closed.setOnClickListener(this);// 设置闪光灯开启关闭的点击事件
		mIv_lamp_closed.setEnabled(false);
		mIv_meiyan_open.setOnClickListener(this);// 设置美颜的点击事件
		mIv_tpscl_3x.setOnClickListener(this);// 设置图片链接点击事件
		mIv_photo_round.setOnClickListener(this);// 设置图片截屏点击事件
		mIv_cemera_round.setOnClickListener(this);// 设置推流开始和关闭点击事件
		mIv_set_round.setOnClickListener(this);
		beautyAndLight();
		setVisiable(true);

	}
		////////////////////////////////////把top下的一行gon掉///////////////////////
		public void setVisiable(boolean isGon){
			if (isGon) {
			mTv_ssml.setVisibility(View.GONE);
			mTv_ssmlbh.setVisibility(View.GONE);
			mTv_total_flow.setVisibility(View.GONE);
			mTv_flowbh.setVisibility(View.GONE);
			mRelativeLayout2.setVisibility(View.GONE);
			}else {
			mTv_ssml.setVisibility(View.VISIBLE);
			mTv_ssmlbh.setVisibility(View.VISIBLE);
			mTv_total_flow.setVisibility(View.VISIBLE);
			mTv_flowbh.setVisibility(View.VISIBLE);
			mRelativeLayout2.setVisibility(View.VISIBLE);
			}
		}

		// //////////////////////////////////
	@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUESTCODEVIDEO) {
			isVideoing = true;
			mStreamer.getMediaPlayerCapture()
					.getMediaPlayer().pause();
			mStreamer.getPictureCapture().stop();
			videoUri = data.getStringExtra(GlobalContants.VIDEOURI);
			// long time = data.getLongExtra(GlobalContants.VIDEOTIME,0);
			final Bitmap bitmap = UIUtils
					.getVideoThumbnail(videoUri);
			UIUtils.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (bitmap != null) {
						/*Glide.with(mActivity)
								.load(UIUtils.Bitmap2Bytes(bitmap))
								.into(mIv_tv_add_pic);*/
						mIv_tv_add_pic.setImageBitmap(bitmap);
					}
				}
			});
			startVideoPip(videoUri);
		} else if (requestCode == REQUESTCODEIMAGE) {
			isVideoing = false;
			String picUriOne = getImageUri(resultCode, requestCode,
					REQUESTCODEIMAGE, data);
			mUriOne = picUriOne;
			if (picUriOne != null) {
				// Toast.makeText(UIUtils.getContext(),"图片路径"+picUriOne,Toast.LENGTH_LONG).show();
				//Glide.with(this).load(picUriOne).into(mIv_uploading_pic);
				mImageLoader.display(picUriOne, mIv_uploading_pic, mIv_uploading_pic.getWidth(), mIv_uploading_pic.getHeight());
			}
			// mNewLiveActivityControl.startPicPip(picUriOne);
		} else if (requestCode == REQUESTCODEIMAGETWO) {
			isVideoing = false;
			String picUriTwo = getImageUri(resultCode, requestCode,
					REQUESTCODEIMAGETWO, data);
			mUriTwo = picUriTwo;
			if (picUriTwo != null) {
				// Toast.makeText(UIUtils.getContext(), "图片路径" + picUriTwo,
				// Toast.LENGTH_LONG).show();
				//Glide.with(this).load(picUriTwo).into(mIv_tv_add_pic);
				mImageLoader.display(picUriTwo, mIv_tv_add_pic, mIv_tv_add_pic.getWidth(), mIv_tv_add_pic.getHeight());
				
			}
		}

	}

	// /////////////////////activity的生命周期/////////////////////
	// ////////////////////////////Activity生命周期的回调处理////////////////

	@Override
	protected void onResume() {
		super.onResume();
		// 一般可以在onResume中开启摄像头预览
		mStreamer.startCameraPreview();
				// 调用KSYStreamer的onResume接口
		mStreamer.onResume();
					// 如果正在推流，切回音视频模式
		if (mStreamer.isRecording()) {
				mStreamer.setAudioOnly(false);
			}
		if (mPipMode) {
				mStreamer.getMediaPlayerCapture().getMediaPlayer().start();
				mStreamer.getPictureCapture().start(mActivity, picUri);
			}
		mHandler.sendEmptyMessage(BATTERLEVEL);
		mTelephonyManager.listen(mListener,
				PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mStreamer.onPause();
		// 一般在这里停止摄像头采集
		mStreamer.stopCameraPreview();
		// 如果正在推流，切换至音频推流模式
		if (mStreamer.isRecording()) {
			mStreamer.setAudioOnly(true);
		}

		// ...
		if (mPipMode) {
			mStreamer.getMediaPlayerCapture().getMediaPlayer().pause();
			mStreamer.getPictureCapture().stop();
		}
		// ...
		mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_NONE);
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopPip();
		if(netChangeReceive!=null){
			unregisterReceiver(netChangeReceive);
		}
		finish();
		// 清理相关资源
		mStreamer.release();
		UIUtils.getHandler().removeCallbacksAndMessages(null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_tpscl_3x:
			if (isShow) {
				mNew_new_rl_pic_tl.setVisibility(View.GONE);
				isShow = !isShow;
			} else {
				mNew_new_rl_pic_tl.setVisibility(View.VISIBLE);
				isShow = !isShow;
			}
			isBelity=false;
			mRl_new_is_show_beauty.setVisibility(View.GONE);
			break;
		case R.id.iv_add_pic:
			Intent intent1 = new Intent(
					Intent.ACTION_PICK,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent1, REQUESTCODEIMAGETWO);
			break;
		case R.id.iv_uploading_pic:
			Intent intentUploading_pic = new Intent(
					Intent.ACTION_PICK,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intentUploading_pic, REQUESTCODEIMAGE);
			break;
		case R.id.bt_start_push_live:// 开始推流

			if (mUriOne != null) {
				if (!isStartpush) {

					startLivePush();
				} else {
					mBt_bt_start_push_live.setBackgroundColor(getResources().getColor(R.color.buttonleft));
					mBt_end_live.setBackgroundColor(getResources().getColor(
							R.color.buttonleft));
					mBt_bt_start_push_live.setText("开始推流");
					mBt_end_live.setText("开始推流");
					isStartpush = !isStartpush;
					endLivePush();
					stopPIpInPip();
				}
			}
			break;
		case R.id.bt_end_live:// 第二个button开始推流
			if (mUriTwo != null) {
				if (!isStartpushTow) {
					mBt_end_live.setBackgroundColor(getResources().getColor(R.color.buttonright));
					mBt_bt_start_push_live.setBackgroundColor(getResources().getColor(R.color.buttonleft));
					mBt_bt_start_push_live.setText("开始推流");
					mBt_end_live.setText("结束推流");
					stopPip();
					startPicPip(mUriTwo);
					mNewLiveActivityControlstartLivePush();
					isStartpushTow = !isStartpushTow;
				} else {
					mBt_end_live.setBackgroundColor(getResources().getColor(
							R.color.buttonleft));
					mBt_bt_start_push_live.setBackgroundColor(getResources()
							.getColor(R.color.buttonleft));
					mBt_end_live.setText("开始推流");
					mBt_bt_start_push_live.setText("开始推流");
					isStartpushTow = !isStartpushTow;
					endLivePush();
					stopPIpInPip();
				}
			}
			break;
		case R.id.iv_set_round:// 设置按钮
			// setImgSettings();
			// 这个设置左边的控制栏显示和消失的逻辑
			if (isSetting) {
				// mRl_new_top_isshowmNew.setVisibility(View.GONE);
				// mNew_new_rl_pic_tl.startAnimation(mStartAnimation);
				mNew_activity_live_left.setVisibility(View.GONE);
				mNew_new_rl_pic_tl.setVisibility(View.GONE);
				mRl_new_is_show_beauty.setVisibility(View.GONE);
				isShow = !isShow;
				isSetting = !isSetting;
			} else {
				// mRl_new_top_isshowmNew.setVisibility(View.VISIBLE);
				mNew_activity_live_left.setVisibility(View.VISIBLE);
				mNew_activity_live_left.startAnimation(mStartAnimation);
				isSetting = !isSetting;
			}
			break;
		case R.id.iv_back_return:// 返回事件
			stopPip();
			mActivity.finish();
			break;
		case R.id.iv_voice_open:// 声音开启关闭
			if (isMute) {
				UIUtils.showHint(mTv_New_tv_show_hint, mActivity,
						"声音已关闭!");
				UIUtils.isShowToast("声音已关闭!");
				// SingleToast.show(mActivity, "声音已关闭!",
				// SingleToast.LENGTH_SHORT);
				mIv_voice_open
						.setImageResource(R.drawable.new_voice_closed);
				mStreamer.setMuteAudio(false);
				isMute = !isMute;

			} else {
				UIUtils.showHint(mTv_New_tv_show_hint, mActivity,
						"声音已开启!");
				UIUtils.isShowToast("声音已开启!");
				// SingleToast.show(mActivity, "声音已开启!",
				// SingleToast.LENGTH_SHORT);
				mStreamer.setMuteAudio(true);
				mIv_voice_open
						.setImageResource(R.drawable.new_voice_open);
				isMute = !isMute;
			}
			break;
		case R.id.iv_shot_change:// 摄像头转换的点击事件
			if (isSwitchCamera) {
			//	System.out.println("转换的点击事件   if");
				isSwitchCamera = false;
				if(isToggle) {
					mIv_lamp_closed.setEnabled(false);
					mIv_lamp_closed
							.setImageResource(R.drawable.new_lamp_closed);
					mStreamer.toggleTorch(false);
					isToggle = false;
				}
			} else {
			//	System.out.println("转换的点击事件   else");
				mIv_lamp_closed.setEnabled(true);
				isSwitchCamera = true;

			}
			mStreamer.switchCamera();
			break;
		case R.id.iv_lamp_closed:// 闪光灯开启关闭的点击事件
			if (!isToggle ) {
				UIUtils.showHint(mTv_New_tv_show_hint, mActivity,
						"闪光灯已开启!");
				UIUtils.isShowToast("闪光灯已开启!");
				// SingleToast.show(mActivity, "闪光灯已开启!",
				// SingleToast.LENGTH_SHORT);
				mIv_lamp_closed
						.setImageResource(R.drawable.new_lamp_open);
				mStreamer.toggleTorch(true);
				isToggle = true;
			} else {
				UIUtils.showHint(mTv_New_tv_show_hint, mActivity,
						"闪光灯已关闭!");
				UIUtils.isShowToast("闪光灯已关闭!");
				// SingleToast.show(mActivity, "闪光灯已关闭!",
				// SingleToast.LENGTH_SHORT);
				mIv_lamp_closed
						.setImageResource(R.drawable.new_lamp_closed);
				mStreamer.toggleTorch(false);
				isToggle = false;
			}
			break;
		case R.id.iv_meiyan_open:// 美颜开启和关闭的点击事件
			if (!isBelity ) {
				mRl_new_is_show_beauty.setVisibility(View.VISIBLE);
				isBelity = !isBelity;
			} else {
				mRl_new_is_show_beauty.setVisibility(View.GONE);
				isBelity = !isBelity;
			}
			isShow=false;
			mNew_new_rl_pic_tl.setVisibility(View.GONE);
			break;
		
		case R.id.iv_photo_round:// 截屏的点击事件
			mStreamer
					.requestScreenShot(new GLRender.ScreenShotListener() {

						private String filename;

						@Override
						public void onBitmapAvailable(Bitmap bitmap) {
							shootSound();
							BufferedOutputStream bos = null;
							try {
								Date date = new Date();
								SimpleDateFormat dateFormat = new SimpleDateFormat(
										"yyyy_MM_dd_HH_mm_ss");
								if (isHaveSdcard) {
									filename = BLSavePathImg
											+ dateFormat.format(date) + ".jpg";
								} else {
									File file = new File(UIUtils
											.getMyPrivatelyDir(), dateFormat
											.format(date) + ".jpg");
									filename = file.getPath();
								}
								bos = new BufferedOutputStream(
										new FileOutputStream(filename));
								if (bitmap != null) {
									bitmap.compress(Bitmap.CompressFormat.JPEG,
											90, bos);
									UIUtils.runOnUiThread(new Runnable() {
										public void run() {
											/*
											 * Glide.with(mActivity)
											 * .load(filename)
											 * .into(mViewList.mIv_tv_add_pic);
											 */
											Toast.makeText(
													UIUtils.getContext(),
													"保存截图到 " + filename,
													Toast.LENGTH_SHORT).show();
										}
									});
								}
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} finally {
								if (bos != null)
									try {
										bos.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
							}
						}
					});
			break;
		case R.id.iv_cemera_round:// 推流开始和关闭点击事件
			setLiveButtonClick();
			break;
		
		
		}
	}
	
	
	// /////////////////////////这是设置点击推流的点击事件//////////////////
		private void setLiveButtonClick() {
			View view = View.inflate(UIUtils.getContext(),
					R.layout.new_live_stop_dialog, null);
			TextView tv_live_stop_not_du = (TextView) view
					.findViewById(R.id.tv_live_stop_not_du);
			tv_live_stop_not_du.setText("暂不处理");
			TextView tv_live_live_stop_close = (TextView) view
					.findViewById(R.id.tv_live_live_stop_close);
			tv_live_live_stop_close.setText("结束直播");
			/*
			 * AlertDialog.Builder builder = new
			 * AlertDialog.Builder((NewLiveActivity
			 * )mActivity,R.style.MyCustomDialog); final AlertDialog dialog =
			 * builder.create(); dialog.setView(view, 0, 0, 0, 0); dialog.show();
			 */

			if (isStart) {
				//这里弹出popupwindow提示用户是否结束或继续直播
				final PopupWindow popupWindow = new PopupWindow(view,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
				popupWindow.setBackgroundDrawable(new BitmapDrawable(mActivity.getResources()));

				popupWindow.showAtLocation(new_activity_live,
						Gravity.CENTER_HORIZONTAL, 0, 0);
				//弹出框的结束推流点击事件
				tv_live_live_stop_close.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						endLivePush();
						popupWindow.dismiss();
					}
				});
				//弹出框的取消点击事件
				tv_live_stop_not_du.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						popupWindow.dismiss();
					}
				});
				
				//isStart = !isStart;
			} else {
				//这里判断在用户设置的网络,当用户设置为数据优先时直接开始推流,当设置仅wifi时,处于数据时弹出提示框
				int i = 0;
				if (isWifi?true:false) {
					i = 0;
				}else {
					i = 1;
				}
				switch (mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.NETSETING, i)) {
				case 0:
					mNewLiveActivityControlstartLivePush();
					break;
				case 1:
					showWifiAndFlow();
					break;
				case 2:
					mNewLiveActivityControlstartLivePush();
					break;
					//弹出提示框
					
				}
				
			}
		}


	//开启推流这里推流图片时调用
	public void startLivePush() {
//		mIv_cemera_round.setEnabled(false);
		mBt_bt_start_push_live.setBackgroundColor(getResources().getColor(R.color.buttonright));
		mBt_bt_start_push_live.setText("结束推流");
		mBt_end_live.setText("开始推流");
		mBt_end_live.setBackgroundColor(getResources().getColor(R.color.buttonleft));
		stopPip();
		startPicPip(mUriOne);
		mNewLiveActivityControlstartLivePush();
		isStartpush = !isStartpush;
	}
	//这里设置电池状态的实时更新
	public void chengBatterUpdateUi(int level) {
		changBatterUpdateUi(level);
	}

	// //////////////////////////////获取图片的url///////////////////
	private String getImageUri(int resultCode, int requestCode, int IMAGE_CODE,
			Intent data) {
		if (resultCode != RESULT_OK) { // 此处的 RESULT_OK 是系统自定义得一个常量
			Log.e(TAG, "ActivityResult resultCode error");
			return null;
		}
		Bitmap bm = null;
		// 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
		ContentResolver resolver = getContentResolver();
		// 此处的用于判断接收的Activity是不是你想要的那个
		if (requestCode == IMAGE_CODE) {
			try {
				Uri originalUri = data.getData(); // 获得图片的uri
				bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); // 显得到bitmap图片
				// 这里开始的第二部分，获取图片的路径：
				String[] proj = { MediaStore.Images.Media.DATA };
				// 好像是android多媒体数据库的封装接口，具体的看Android文档
				Cursor cursor = managedQuery(originalUri, proj, null, null,
						null);
				// 按我个人理解 这个是获得用户选择的图片的索引值
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// 将光标移至开头 ，这个很重要，不小心很容易引起越界
				cursor.moveToFirst();
				// 最后根据索引值获取图片路径www.2cto.com
				String path = cursor.getString(column_index);
				return path;
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
		}
		return null;
	}

	

	// //////////设置视频帧率的方法//////////////////////
	public void bitrateChanged(int bitrate) {
		int min = (bitrate / 20) * 6;
		int average = (bitrate / 20) * 10;
		if (mStreamer != null) {
			mStreamer.setVideoKBitrate(average,
					bitrate, min);
		}

	}
	/////////////////////结束推流/////////////////////
	
	public void endLivePush() {
		setVisiable(true);
		isStart = false;
		mIv_tpscl_3x.setEnabled(true);
		mIv_meiyan_open.setEnabled(true);
		mIv_meiyan_open.setImageResource(R.drawable.new_meiyan_open);
		mTv_ssmlbh.setTextColor(mActivity.getResources().getColor(
				R.color.whiteml));
		mTv_ssmlbh.setText("0KB/S");
		mStreamer.stopStream();
		//结束推流时我们需要取消handle的循环
		mHandler
				.removeMessages(MOBILE_NETWORK_SPEED);
		mHandler
				.removeMessages(RECORD_TIME);
		mHandler
				.removeMessages(WIFI_NETWORK_SPEED);

		// //////时间归零////////////////
		hour = 0;
		minute = 0;
		second = 0;
		// //////时间归零结束//////////////
		mTv_flowbh.setText("0 M");
		mTv_time.setText(tempString);
		mPublishing = false;
		/*SingleToast.show(UIUtils.getContext(), "推流已经结束",
				SingleToast.LENGTH_SHORT);*/
		// UIUtils.ShowUpToast("推流已经结束");
		mIv_cemera_round.setImageResource(R.drawable.new_camera);
		mIv_set_round.setEnabled(true);
		if (iSLOCALVIDEO) {
			//UIUtils.showToast("视频保存为本地结束");
			mStreamer.stopRecord();
		}
		
	}
	
	
	////////////////////////////////////这里是从LiveActivityInitUI拷贝过来的////////////////////////////////////////////////////////
	// ///////////////////////////创建KSYStreamer实例并配置推流相关参数/////////////////////////////////////////
			private void InitKSYStreamer() {
				//在这里取出用户的设置,得到设置后对配置进行初始化设置
				int PREVIEWRESOLUTION_W = mSharePreferenceUtils.getInt(UIUtils.getContext(), 
						GlobalContants.PREVIEWRESOLUTION_W, 640);//预览分辨率宽
				int PREVIEWRESOLUTION_H = mSharePreferenceUtils.getInt(UIUtils.getContext(), 
						GlobalContants.PREVIEWRESOLUTION_H, 480);//预览分辨率高 
				int TARGETRESOLUTION_W = mSharePreferenceUtils.getInt(UIUtils.getContext(), 
						GlobalContants.TARGETRESOLUTION_W, 640);//推流分辨率宽
				TARGETRESOLUTION_W = PREVIEWRESOLUTION_W;
				int TARGETRESOLUTION_H = mSharePreferenceUtils.getInt(UIUtils.getContext(), 
						GlobalContants.TARGETRESOLUTION_H, 480);//推流分辨率高
				TARGETRESOLUTION_H = PREVIEWRESOLUTION_H;
				int VIDEOKBITRATE_AVE_MAX = mSharePreferenceUtils.getInt(UIUtils.getContext(), 
						GlobalContants.VIDEOKBITRATE_AVE_MAX, 2000);//设置最高 平均码率
				//这里是十分钟消耗多少空间
				mAvailableSize = (int)((VIDEOKBITRATE_AVE_MAX/8)*60*10);
				
				int VIDEOFBS = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.PREVIEWVIDEOFBS, 15);//设置预览视频帧率
				int lIVEVIDEOFBS = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.lIVEVIDEOFBS, 15);//设置推流视频帧率
				int VOICEBITRATE = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.VOICEBITRATE, 48);//设置音频码流
				int PROGRESS = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.PROGRESS, 0);//美颜级别
				//int PICTUREQUALITY = SharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.PICTUREQUALITY, 0);//设置图片质量
				//boolean NETSETING = SharePreferenceUtils.getBoolean(UIUtils.getContext(), GlobalContants.NETSETING, false);//首选网络设置
				int MICROPHONEVOLUME = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.MICROPHONEVOLUME, 1);//麦克风音量设置
				boolean ISSTARTUSIGLOGO = mSharePreferenceUtils.getBoolean(UIUtils.getContext(), GlobalContants.ISSTARTUSIGLOGO, false);//直播时是否启用logo
				int LIVELOGOSETING = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.LIVELOGOSETING, 0);//直播logo设置
				String liveLogoPath = mSharePreferenceUtils.getString(UIUtils.getContext(), GlobalContants.LIVELOGOPATH, "");
				Bitmap liveLogoBitmap = BitmapUtils.yasuoimg(liveLogoPath);
				iSLOCALVIDEO = mSharePreferenceUtils.getBoolean(UIUtils.getContext(), GlobalContants.ISLOCALVIDEO, true);
				int LOGOPOSITION = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.LOGOPOSITION, 0);//直播logo位置
				int LOCALVIDEOLOGOSETING = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.LOCALVIDEOLOGOSETING, 0);//本地录制logo设置
				int LOCALVIDEOKBITRATE_AVE_MAX = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.LOCALVIDEOKBITRATE_AVE_MAX, 800);//设置本地最高 平均码率
				int LOCALVIDEOFBS = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.LOCALVIDEOFBS, 15);//设置本地视频帧率
				int LOCALVOICEBITRATE = mSharePreferenceUtils.getInt(UIUtils.getContext(), GlobalContants.LOCALVOICEBITRATE, 48);//设置本地音频码流
				
		
     			// 创建KSYStreamer实例
				mStreamer = new KSYPipStreamer(mActivity);
				// 设置推流url（需要向相关人员申请，测试地址并不稳定！）
				mStreamer.setUrl(mRtmpUrl);
				//麦克风音量设置
				float currentMic = (MICROPHONEVOLUME/100)*4;
				mStreamer.setVoiceVolume(currentMic); 
				// 设置预览帧率
				mStreamer.setPreviewFps(VIDEOFBS);
				// 设置推流帧率，当预览帧率大于推流帧率时，编码模块会自动丢帧以适应设定的推流帧率
				mStreamer.setTargetFps(lIVEVIDEOFBS);
				//直播时是否启用logo
				if (ISSTARTUSIGLOGO) {
					switch (LOGOPOSITION) {
					case 0:
						mStreamer.showWaterMarkLogo(liveLogoBitmap, 0, 0, 0.2f, 0.2f, 1.0f);
						break;
					case 1:
						mStreamer.showWaterMarkLogo(liveLogoBitmap, 0.8f, 0, 0.2f, 0.2f, 1.0f);
						break;
					case 2:
						mStreamer.showWaterMarkLogo(liveLogoBitmap, 0, 0.8f, 0.2f, 0.2f, 1.0f);
						break;
					case 3:
						mStreamer.showWaterMarkLogo(liveLogoBitmap, 0.8f, 0.8f, 0.2f, 0.2f, 1.0f);
						break;


					default:
						break;
					}
					
				}else {
					mStreamer.hideWaterMarkLogo();
				}
				
				// 设置预览分辨率, 当一边为0时，SDK会根据另一边及实际预览View的尺寸进行计算
				mStreamer.setPreviewResolution(PREVIEWRESOLUTION_H, PREVIEWRESOLUTION_W);
				// 设置推流分辨率，可以不同于预览分辨率
				mStreamer.setTargetResolution(TARGETRESOLUTION_H, TARGETRESOLUTION_W);
				//mStreamer.getCameraCapture().setPreviewSize(480, 640);
				// 设置视频码率，分别为初始平均码率、最高平均码率、最低平均码率，单位为kbps，另有setVideoBitrate接口，单位为bps
				bitrateChanged(VIDEOKBITRATE_AVE_MAX);
				// 设置音频采样率
				mStreamer.setAudioSampleRate(44100);
				// 设置音频码率，单位为kbps，另有setAudioBitrate接口，单位为bps
				mStreamer.setAudioKBitrate(VOICEBITRATE);
				/**
				 * 设置编码模式(软编、硬编): StreamerConstants.ENCODE_METHOD_SOFTWARE
				 * StreamerConstants.ENCODE_METHOD_HARDWARE
				 */
				mStreamer.setEncodeMethod(StreamerConstants.ENCODE_METHOD_SOFTWARE);
				// 设置屏幕的旋转角度，支持 0, 90, 180, 270
				mStreamer.setRotateDegrees(90);
				// 开启推流统计功能
				mStreamer.setEnableStreamStatModule(true);
				// ////////////////////////////////
				// 设置预览View
				mStreamer.setDisplayPreview(mCamera_preview);
				// ////////////////////////////////
				
			
				//这里是对美颜进行更新时取出的参数
				float mProgress = (float)(PROGRESS/100);
				//这里是界面启动时就对美颜参数进行更新先注释需要时使用
				//setBeahtly(mProgress);
				setOnInfoListener();
				
			}
			//这里是对美颜参数进行更新的方法
			private void setBeahtly(float level) {
				filter = new ImgBeautyProFilter(
						mStreamer.getGLRender(), mActivity);
				mStreamer.getImgTexFilterMgt().setFilter(filter);
				// 设置美颜级别（首先判断是否支持，然后进行设置）
				// 设置磨皮级别，范围0-1
				if (filter.isGrindRatioSupported()) {
					filter.setGrindRatio(level);
				}
				// 设置美白级别，范围0-1
				if (filter.isWhitenRatioSupported()) {
					filter.setWhitenRatio(level);
				}
				// 设置红润级别，一般范围为0-1，BeautyPro为-1.0~1.0
				if (filter.isRuddyRatioSupported()) {
					filter.setRuddyRatio(0.1f);
				}
			}
			
			
			// ////////////////////////////////////创建推流事件监听/////////////////
			private void setOnInfoListener() {
				mStreamer.setOnInfoListener(new OnInfoListener() {

					@Override
					public void onInfo(int what, int msg1, int msg2) {
						switch (what) {
						/*case StreamerConstants.KSY_STREAMER_CAMERA_INIT_DONE:
							UIUtils.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									//UIUtils.showToast("初始化完毕");
								}
							});
							break;*/
						case StreamerConstants.KSY_STREAMER_OPEN_STREAM_SUCCESS:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									SingleToast.show(UIUtils.getContext(), "直播开始", SingleToast.LENGTH_SHORT);
								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_FRAME_SEND_SLOW:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(),
											"网络状态不佳，数据发送可能有延迟", 0).show();

								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_EST_BW_RAISE:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									//Toast.makeText(UIUtils.getContext(), "码率上调了", 0).show();

								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_EST_BW_DROP:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									//Toast.makeText(UIUtils.getContext(), "码率下调了", 0).show();

								}
							});
							break;

						default:
							break;
						}

					}
				});

				mStreamer.setOnErrorListener(new OnErrorListener() {

					@Override
					public void onError(int what, int msg1, int msg2) {
						switch (what) {
						case StreamerConstants.KSY_STREAMER_ERROR_DNS_PARSE_FAILED:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "url域名解析失败", 0).show();
									endLivePush();
								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_ERROR_CONNECT_FAILED:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "网络连接失败，无法建立连接", 0).show();
									endLivePush();
								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_ERROR_PUBLISH_FAILED:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "跟RTMP服务器完成握手后,向{streamname}推流失败)", 0).show();
									endLivePush();

								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_ERROR_CONNECT_BREAKED:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "网络连接断开", 0).show();
									endLivePush();

								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_ERROR_AV_ASYNC:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "音视频采集pts差值超过5s", 0).show();

								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "编码器初始化失败", 0).show();
									endLivePush();

								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "视频编码失败", 0).show();
									endLivePush();

								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_AUDIO_ENCODER_ERROR_UNSUPPORTED:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "音频初始化失败", 0).show();
									endLivePush();

								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_AUDIO_ENCODER_ERROR_UNKNOWN:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "音频编码失败", 0).show();
									endLivePush();

								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "摄像头未知错误", 0).show();
									endLivePush();

								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "打开摄像头失败", 0).show();
									endLivePush();

								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "系统Camera服务进程退出", 0).show();
									endLivePush();

								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "录音开启失败", 0).show();

								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "录音开启未知错误", 0).show();

								}
							});
							break;
						case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_EVICTED:
							UIUtils.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(UIUtils.getContext(), "Camera服务异常退出", 0).show();
									endLivePush();
								}
							});
							break;



						default:
							break;
						}
					}
				});
			}
	//////////////////创建美颜机型不支持时回调//////////////////////////
		private void setOnBeautyErrorListener(){
		mStreamer.getImgTexFilterMgt().setOnErrorListener(new ImgTexFilterBase.OnErrorListener() {
		  @Override
		  public void onError(ImgTexFilterBase filter, int errno) {
			  Toast.makeText(UIUtils.getContext(), "当前机型不支持该滤镜",
					  Toast.LENGTH_SHORT).show();
			  mStreamer.getImgTexFilterMgt().setFilter(mStreamer.getGLRender(),
					  ImgTexFilterMgt.KSY_FILTER_BEAUTY_DISABLE);
		  }
		});
		}
		
		///////////////////////////////画中画效果的处理//////////////////////
		public void startVideoPip(final String uri) {
		mPipPath = uri;
		if (mPipMode) {
		return;
		}
		mPipMode = true;
		mStreamer.getMediaPlayerCapture().getMediaPlayer()
		.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
		
		@Override
		public void onCompletion(IMediaPlayer iMediaPlayer) {
		
			Log.d(TAG, "End of the currently playing video");
			mStreamer.showBgVideo(null);
		}
		});
		mStreamer.getMediaPlayerCapture().getMediaPlayer()
		.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
		@Override
		public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
			Log.e(TAG, "MediaPlayer error, what=" + what + " extra=" + extra);
			return false;
		}
		});
		//mStreamer.showBgPicture(mActivity, mBgPicPath);
		mStreamer.showBgVideo(uri);
		mStreamer.getMediaPlayerCapture().getMediaPlayer().setVolume(0.4f, 0.4f);
		mStreamer.setCameraPreviewRect(1.0f, 1.0f, 1.0f, 1.0f);
		
		// disable touch focus
		mCameraTouchHelper.setEnableTouchFocus(false);
		}
		
		///////////////////这里三开启和关闭图片的播放/////////////////
		//开启图片的推流
		public void startPicPip(final String uri) {
			if (mPipMode) {
				return;
			}

			mPipMode = true;
			mStreamer.getMediaPlayerCapture().getMediaPlayer()
					.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {

						@Override
						public void onCompletion(IMediaPlayer iMediaPlayer) {

							Log.d(TAG, "End of the currently playing video");
							mStreamer.showBgVideo(null);
						}
					});
			mStreamer.getMediaPlayerCapture().getMediaPlayer()
					.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
						@Override
						public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
							Log.e(TAG, "MediaPlayer error, what=" + what + " extra=" + extra);
							return false;
						}
					});
			mStreamer.showBgPicture((NewLiveActivity)mActivity, uri);
			//mStreamer.showBgVideo(uri);
			mStreamer.getMediaPlayerCapture().getMediaPlayer().setVolume(0.4f, 0.4f);
			mStreamer.setCameraPreviewRect(1.0f, 1.0f, 1.0f, 1.0f);

			// disable touch focus
			mCameraTouchHelper.setEnableTouchFocus(false);
		}
		//关闭图片的推流
		public void stopPip() {
			if (!mPipMode) {
				return;
			}
			mStreamer.hideBgPicture();
			mStreamer.hideBgVideo();
			mStreamer.setCameraPreviewRect(0.f, 0.f, 1.f, 1.f);
			mPipMode = false;

			// enable touch focus
			mCameraTouchHelper.setEnableTouchFocus(true);
		}

		//这里是开始推流
		public void mNewLiveActivityControlstartLivePush() {
			setVisiable(false);
			isStart = true;
            mRl_new_is_show_beauty.setVisibility(View.GONE);
            isBelity = false;

			mNew_new_rl_pic_tl.setVisibility(View.GONE);
			isShow =false;
			mIv_tpscl_3x.setEnabled(false);
			mIv_meiyan_open.setEnabled(false);
			mIv_meiyan_open.setImageResource(R.drawable.new_fair_closed);
			mTv_ssmlbh.setTextColor(mActivity.getResources().getColor(
					R.color.redml));
			mStreamer.startStream();
			mOne = 1024 * 8 * mStreamer.getUploadedKBytes();
			startTraffic = mOne;
			mHandler.removeMessages(MOBILE_NETWORK_SPEED);
			mHandler.sendEmptyMessageDelayed(
					WIFI_NETWORK_SPEED,
					TIME);
			mTv_time.setText("00: 00: 00");
			mHandler.sendEmptyMessageDelayed(
					RECORD_TIME, 1000);
			mPublishing = true;
			/*SingleToast.show(UIUtils.getContext(), "推流已经开始",
					SingleToast.LENGTH_SHORT);*/
			//mIv_set_round.setEnabled(false);
			mIv_cemera_round.setImageResource(R.drawable.new_camera_living);
			////////////////////直播时是否保存录像////////////////////
			if (iSLOCALVIDEO) {
				//mStreamer.startRecord(mRecordUrl);
				Date date = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy_MM_dd_HH_mm_ss");
				if (isHaveSdcard) {
					mRecordUrl = BLSavePath
							+ dateFormat.format(date) + ".mp4";
					mStreamer.startRecord(mRecordUrl);
					UIUtils.showToast("视频录像保存路径为" + mRecordUrl);
				} /*else {
					File file = new File(UIUtils.getMyPrivatelyDir(), dateFormat.format(date) + ".mp4");
					mRecordUrl = file.getPath();
					isHaveSdcard = false;
					UIUtils.showToast("视频录像保存为系统内存中,路径为" + mRecordUrl);
				}*/
				
			}
		}
		
		
        /////////////////直播时开始检测内存还能剩余多少空间,能够录制多少分钟的方法//////////////
		private void checkMemory() {
			long mSDAvailableSize = CheckMemorySpace.getSDAvailableSize();
			long mSystemAvailableSize = CheckMemorySpace.getSystemAvailableSize();
			long mCurrentAvailableSize = 0;
			if (isHaveSdcard) {
				//mCurrentAvailableSize = mSDAvailableSize;
				mCurrentAvailableSize = mSystemAvailableSize;
			}
			if (mCurrentAvailableSize < mAvailableSize && isFirstCheckMemory && isStart) {
				
				
				
				final View mView = View.inflate(UIUtils.getContext(),
						R.layout.new_run_out_of_memory_dialog, null);
				TextView tv_batter_not_du = (TextView) mView
						.findViewById(R.id.tv_out_of_memory_not_du);
				tv_batter_not_du.setText("暂不处理");
				TextView tv_live_batter_stop_close = (TextView) mView
						.findViewById(R.id.tv_live_out_of_memory_close);
				tv_live_batter_stop_close.setText("结束直播");
				final PopupWindow popupWindow = new PopupWindow(mView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
				popupWindow.setBackgroundDrawable(new BitmapDrawable(mActivity.getResources()));

				popupWindow.showAtLocation(new_activity_live,
						Gravity.CENTER_HORIZONTAL, 0, 0);

				tv_live_batter_stop_close.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						endLivePush();
						isFirstCheckMemory = true;
						popupWindow.dismiss();
					}
				});

				tv_batter_not_du.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						popupWindow.dismiss();
						isFirstCheckMemory = false;
					}
				});
		
			mSharePreferenceUtils.putBoolean(mActivity,
					GlobalContants.ISSHOWDIALOG, true);
				
			}
			
		}
        /////////////////停止画中画///////////////////////
		public void stopPIpInPip() {
		// 隐藏背景图片
		mStreamer.hideBgPicture();
		//隐藏视频背景
		mStreamer.hideBgVideo();
		//恢复摄像头预览区域
		mStreamer.setCameraPreviewRect(0.f, 0.f, 1.f, 1.f);
		
		//重新使能触摸对焦
		mCameraTouchHelper.setEnableTouchFocus(true);
		}
		
		
		// ///////////////////////////////////美颜调节进度和亮度的///////////////////
		public void beautyAndLight() {
			mNew_beauty_seekbar
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							int progress = seekBar.getProgress();
							mSharePreferenceUtils.putInt(UIUtils.getContext(),
									GlobalContants.PROGRESS, progress);
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							
						}

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							float mProgress = (float) (progress / 100);
							setBeahtly(mProgress);
							int width = seekBar.getWidth();
							width = width-UIUtils.dip2px(20);
							RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv_new_beauty.getLayoutParams();
							params.leftMargin = (int)((width/100.0f)*progress);
							tv_new_beauty.setText(progress + "");
							tv_new_beauty.setLayoutParams(params);
						}
					});
			mNew_light_seekbar
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							setCameraLight(seekBar.getProgress());
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							float mProgress = (float) (progress / 100);
							int width = seekBar.getWidth();
							width = width-UIUtils.dip2px(20);
							RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv_new_light.getLayoutParams();
							params.leftMargin = (int)((width/100.0f)*progress);
							tv_new_light.setText(progress + "");
							tv_new_light.setLayoutParams(params);

						}
					});
		}
		
		
		private class PhoneStatListener extends PhoneStateListener {
			public void onSignalStrengthsChanged(SignalStrength signalStrength) {
				super.onSignalStrengthsChanged(signalStrength);
				gsmSignalStrength = signalStrength.getGsmSignalStrength();
				// Toast.makeText(UIUtils.getContext(),""+gsmSignalStrength,Toast.LENGTH_SHORT).show();
				gsmSignalStrength = (int) (gsmSignalStrength / 34.0f);
				UIUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setPhoneStatListenerUi(gsmSignalStrength);
					}
				});
			}
		}
		
		
		// 信号强度的监听
		private void telephoneClickListener() {
			// 获取telephonyManager
			mTelephonyManager = (TelephonyManager) mActivity
					.getSystemService(Context.TELEPHONY_SERVICE);
			mListener = new PhoneStatListener();
			mTelephonyManager.listen(mListener,
					PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);
		}

		// ///////////////更新信号的强度////////////////////////
		public void setPhoneStatListenerUi(int signalStrength) {
			if (!isWifi && isNetConnected) {
				switch (signalStrength) {
				case 0:
					mIv_iv_net.setImageResource(R.drawable.new_net3);
					mTv_new_Network_signal_is_not_stable
							.setVisibility(View.VISIBLE);
					break;
				case 1:
					mIv_iv_net.setImageResource(R.drawable.new_net6);
					mTv_new_Network_signal_is_not_stable
							.setVisibility(View.GONE);
					break;

				case 2:
					mIv_iv_net.setImageResource(R.drawable.new_net);
					mTv_new_Network_signal_is_not_stable
							.setVisibility(View.GONE);
					break;
				}
			}
			
		}

		// /////////////////////////////播放照相机的声音/////////////////////
		/**
		 * 播放系统拍照声音
		 */
		public void shootSound() {
			AudioManager meng = (AudioManager) mActivity
					.getSystemService(Context.AUDIO_SERVICE);
			int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

			if (volume != 0) {
				if (shootMP == null)
					shootMP = MediaPlayer
							.create(mActivity,
									Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
				if (shootMP != null)
					shootMP.start();
			}
		}

		

		// //////////////////////调整相机亮度的方法/////////////////////////////////////
		public void setCameraLight(int progress) {
			Parameters parameters = mStreamer
					.getCameraCapture().getCameraParameters();
			progress = (int) (progress / 4) - 12;
			if (progress > 12) {
				progress = 12 ;
			}
			parameters.setExposureCompensation(progress);
			mStreamer.getCameraCapture().setCameraParameters(
					parameters);
		}
		
		
		/////////////////////////////////////////////////////////////////////////
		// ///////////////////////电池电量更新变化的设置UI///////////////////
		public void changBatterUpdateUi(int levels) {
			switch (levels) {
			case 0:
				mIv_ttery_100.setImageResource(R.drawable.ttery_10);
				if (!mSharePreferenceUtils.getBoolean(mActivity,
						GlobalContants.ISSHOWDIALOG, false)) {
					if (isStart) {
						final View mView = View.inflate(UIUtils.getContext(),
								R.layout.new_batter_alitter_dialog, null);
						TextView tv_batter_not_du = (TextView) mView
								.findViewById(R.id.tv_live_stop_not_du);
						tv_batter_not_du.setText("暂不处理");
						TextView tv_live_batter_stop_close = (TextView) mView
								.findViewById(R.id.tv_live_live_stop_close);
						tv_live_batter_stop_close.setText("结束直播");
						final PopupWindow popupWindow = new PopupWindow(mView,
								LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
						popupWindow.setBackgroundDrawable(new BitmapDrawable(mActivity.getResources()));

						popupWindow.showAtLocation(new_activity_live,
								Gravity.CENTER_HORIZONTAL, 0, 0);

						tv_live_batter_stop_close.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								endLivePush();
								popupWindow.dismiss();
							}
						});

						tv_batter_not_du.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								popupWindow.dismiss();
							}
						});
				
					mSharePreferenceUtils.putBoolean(mActivity,
							GlobalContants.ISSHOWDIALOG, true);
					}
				}
					
				break;
			case 1:
				mIv_ttery_100.setImageResource(R.drawable.ttery_20);
				mSharePreferenceUtils.putBoolean(mActivity,
						GlobalContants.ISSHOWDIALOG, false);
				break;
			case 2:
				mIv_ttery_100.setImageResource(R.drawable.ttery_30);
				break;
			case 3:
				mIv_ttery_100.setImageResource(R.drawable.ttery_40);
				break;
			case 4:
				mIv_ttery_100.setImageResource(R.drawable.ttery_50);
				break;
			case 5:
				mIv_ttery_100.setImageResource(R.drawable.ttery_60);
				break;
			case 6:
				mIv_ttery_100.setImageResource(R.drawable.ttery_70);
				break;
			case 7:
				mIv_ttery_100.setImageResource(R.drawable.ttery_80);
				break;
			case 8:
				mIv_ttery_100.setImageResource(R.drawable.ttery_90);
				break;
			case 9:
				mIv_ttery_100.setImageResource(R.drawable.ttery_100);
				break;
			}
		}
		////////////////////设置推流按钮能否点击/////////////////
		private void setEnableLiveButton(boolean b) {
			mBt_bt_start_push_live.setEnabled(b);
			
		}
		

		// ///////////////////////////广播接收者////////////////////////
		private class NetChangeReceive extends BroadcastReceiver {
			@Override
			public void onReceive(Context context, Intent intent) {

				// 管理网络连接的系统服务类
				ConnectivityManager manager = (ConnectivityManager) mActivity
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = manager.getActiveNetworkInfo();

				// info.getTypeName()可得到网络类型
				if (info != null && info.isAvailable()) {
					isNetConnected  = true;
					String name = info.getTypeName();
					Log.d("FreeStar", "当前网络名称：" + name);
					if (name.toLowerCase().equals("wifi")) {
						isWifi = true;
						mTv_signal.setText("WiFi");
						UIUtils.showHint(mTv_New_tv_show_hint, mActivity,
								"当前网络已切换至WiFi");
					} else {// 移动网络
						isWifi = false;
						mTv_signal.setText("4G");
						UIUtils.showHint(mTv_New_tv_show_hint, mActivity,
								"当前网络已切换至4G");
					}
					// Toast.makeText(SecondActivity.this, "当前网络名称：" + name,
					// 0).show();
				} else {
					Log.d("FreeStar", "没有可用网络");
					mTv_signal.setText("无网络");
					UIUtils.showHint(mTv_New_tv_show_hint, mActivity,
							"当前没有可用网络");
					// Toast.makeText(SecondActivity.this, "没有可用网络", 0).show();
					isNetConnected = false;
				}
			}
		}
		
		// /////////////这里是注册广播//////////////////////
		@SuppressWarnings("unused")
		private void initNetReceiver() {
			netChangeReceive = new NetChangeReceive();
			// TODO Auto-generated method stub
			IntentFilter filter = new IntentFilter();
			// 网络状态改变的监听
			filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
			if (netChangeReceive == null) {
				netChangeReceive = new NetChangeReceive();
			}
			registerReceiver(netChangeReceive, filter);
			
			
			WifiChangeBroadcastReceiver mWifiChangeBroadcastReceiver = new WifiChangeBroadcastReceiver();
			IntentFilter wifiFilter = new IntentFilter();
			wifiFilter.addAction("android.net.wifi.RSSI_CHANGED");
			registerReceiver(mWifiChangeBroadcastReceiver, wifiFilter);
			
		}
		
		
		
		///////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////使用wifi还是数据流量/////////////////////////////////////////////
		private void showWifiAndFlow(){
			if (isWifi) {
				mNewLiveActivityControlstartLivePush();
			}else {
				/**
				 * 显示网络设置的对话框
				 */
				
				final AlertDialog alertDialog=new AlertDialog.Builder(NewLiveActivity.this).create();
				alertDialog.show();
				View view=View.inflate(UIUtils.getContext(), R.layout.layout_alert_no_wifi, null);
				TextView new_only_one = (TextView)view.findViewById(R.id.new_only_one);
				TextView new_not_do=(TextView) view.findViewById(R.id.new_not_do);
//					tv_3g=(TextView) view.findViewById(R.id.tv_3g);
//					tv_2g=(TextView) view.findViewById(R.id.tv_2g);
				
				new_only_one.setOnClickListener(new OnClickListener(){
					public void onClick(View v) {
						mNewLiveActivityControlstartLivePush();
						mSharePreferenceUtils.putInt(UIUtils.getContext(), GlobalContants.NETSETING, 1);
						alertDialog.dismiss();
					};
				});
				new_not_do.setOnClickListener(new OnClickListener(){
					public void onClick(View v) {
						alertDialog.dismiss();
					};
				});
				
				
				alertDialog.setContentView(view);
			}
		}
		
		
		/////////////////////监听wifi信号强度网络的变化///////////////////////////////////////
		 public class WifiChangeBroadcastReceiver extends BroadcastReceiver {  
		        private Context mContext;  
		        @Override  
		        public void onReceive(Context context, Intent intent) {  
		            mContext=context;  
		            System.out.println("Wifi发生变化");  
		            getWifiInfo();  
		        }  
		          
		        private void getWifiInfo() {  
		            WifiManager wifiManager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);  
		            WifiInfo wifiInfo = wifiManager.getConnectionInfo();  
		            if (wifiInfo.getBSSID() != null) {  
		                //wifi名称  
		                String ssid = wifiInfo.getSSID();  
		                //wifi信号强度  
		                int signalLevel = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
		                if (signalLevel > 3) {
		                	signalLevel = 2;
						}else if (signalLevel==2||signalLevel == 3){
							signalLevel = 1;
						}else {
							signalLevel = 0;
						}
		                if (isWifi && isNetConnected) {
		    				switch (signalLevel) {
		    				case 0:
		    					mIv_iv_net.setImageResource(R.drawable.new_net3);
		    					mTv_new_Network_signal_is_not_stable
		    							.setVisibility(View.VISIBLE);
		    					break;
		    				case 1:
		    					mIv_iv_net.setImageResource(R.drawable.new_net6);
		    					mTv_new_Network_signal_is_not_stable
		    							.setVisibility(View.GONE);
		    					break;

		    				case 2:
		    					mIv_iv_net.setImageResource(R.drawable.new_net);
		    					mTv_new_Network_signal_is_not_stable
		    							.setVisibility(View.GONE);
		    					break;
		    				}
		    			}
		               // UIUtils.showToast(signalLevel + "");
		                //wifi速度  
		                int speed = wifiInfo.getLinkSpeed();  
		                //wifi速度单位  
		                String units = WifiInfo.LINK_SPEED_UNITS;  
		                System.out.println("ssid="+ssid+",signalLevel="+signalLevel+",speed="+speed+",units="+units);  
		            }  
		       }  
		      
		    }


		@Override
		protected void initRequestOnSuccess(String result, int code, int biaoshi) {
			// TODO Auto-generated method stub
			if(code==Configs.liveDetailsCode){
				JSONObject jsonObject=JSON.parseObject(result);
				String status=jsonObject.getString("status");
				if(status.equals("100")){//表示获取成功
					String data=jsonObject.getString("data");
					createLiveBean=JSON.parseObject(data,LiveBackListBean.class);
					if(createLiveBean!=null){
						mHandler.sendEmptyMessage(1);
					}else{
						mHandler.sendEmptyMessage(0);
					}
				}
			}
		}

		@Override
		protected void initRequestOnStart(String result, int code, int biaoshi) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void initRequestOnFailure(String failure, int code,
				int biaoshi) {
			// TODO Auto-generated method stub
			System.out.println();
		}

		@Override
		protected void initRequestOnCache(String result, int code, int biaoshi) {
			// TODO Auto-generated method stub
			System.out.println();
		}  
		 
		
}
