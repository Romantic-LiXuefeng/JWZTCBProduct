package com.jwzt.caibian.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.ScriptAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.bean.LocationNoUploadBean;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.ResourcesBean;
import com.jwzt.caibian.bean.TypeBean;
import com.jwzt.caibian.bean.UploadingDetailsBean;
import com.jwzt.caibian.db.LocationUploadDao;
import com.jwzt.caibian.interfaces.CallBackUtilsInterface;
import com.jwzt.caibian.interfaces.VolumeListener;
import com.jwzt.caibian.util.AnimationUtils;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.DialogHelp;
import com.jwzt.caibian.util.FileUtil;
import com.jwzt.caibian.util.GETImageUntils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.MediaManager;
import com.jwzt.caibian.util.TimeUtil;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.util.Utils;
import com.jwzt.caibian.widget.SelectAudioPopupWindow;
import com.jwzt.caibian.widget.SelectPhotoPopupWindow;
import com.jwzt.caibian.widget.SelectVideoPopupWindow;
import com.jwzt.caibian.xiangce.Bimp;
import com.jwzt.caibian.xiangce.ItemImage;
import com.jwzt.upload.configs.Configs;
import com.zc.RecordDemo.MyAudioRecorder;
/**
 * 新建稿件
 * @author howie
 *
 */
@SuppressLint({ "HandlerLeak", "ClickableViewAccessibility", "ShowToast" }) 
public class NewScriptActivity extends BaseActivity implements OnClickListener,CallBackUtilsInterface,VolumeListener{
	/**标题*/
	private TextView tv_titles;
	/**本地选择图片*/
	private static final int TAKE_PICTURE = 1;
	/***拍照*/
	public static final int PHOTOHRAPH = 2;
	/***本地选择视频*/
	public static final int VIDEOSELECT=3;
	/***拍摄视频*/
	public static final int VIDEOPAISHE=4;
	/**分类*/
	private static final int CODE_CATEGORY=5;
	/**定位*/
	private static final int CODE_LOCATION=6;
	/**发送*/
	private static final int BACKRSSENDESULT = 7;
	/***素材库选择*/
	private static final int SELECTRESOURCE=8;
	
	/***图片保存地址*/
	private String IMG_SAVE_NAME;
	private GridView gv;
	private View viewMask;
	/**地点*/
	private TextView tv_location,tv_category;
	/**取消设置地点的红叉按钮*/
	private Drawable drawable_cancel;
	/**蓝色地标图片*/
	private Drawable location_blue;
	private View iv_locaiton_reset;
	/**分类按钮*/
	private SelectPhotoPopupWindow selectPhotoPopupWindow;
	private SelectVideoPopupWindow selectVideoPopupWindow;
	private SelectAudioPopupWindow selectAudioPopupWindow;
	
	//录音
	private MyAudioRecorder recorder;
	/**开始录音的时间**/
	private long start_record;
	/**结束录音的时间**/
	private long end_record;
	/** "录音中"提示框 **/
	private RelativeLayout rl_recording;
	/***录音音量显示图片*/
	private ImageView iv_recording;
	/***定义计时器*/ 
	private boolean bool;
	/***录音时长*/
	private int luYinTime;
	/***录音完成后保存的路径*/
	private String mp3Path;
	/**发送按钮*/
	private TextView tv_send;
	/***标题,正文内容输入框*/
	private EditText et_title,et_content;
	
	private CbApplication application;
	private LoginBean mLoginBean;
	private TypeBean mTypeBean;
	private String title,content;
	private AlertDialog alertDialog;
//	private ArrayList<ItemImage> selectImage;
	
	/**
	 * 从未上传稿件详情页面点击编辑进入该页面的标识为detailsnoup
	 * 从稿件详情页面点击编辑进入该页面的标识为 details
	 * 从快速发稿进入该页面的标识为 speediness
	 * 从采集新的素材页面点击上传时进入 resources
	 * 
	 */
	private String tag;
	private UploadingDetailsBean mNewUploadinglistBean;
	private LocationNoUploadBean locationNoUpBean;
	
	/**选择文件*/
	private ImageView iv_back,iv_doc,iv_image,iv_camera,iv_mic,iv_save;
	private LocationUploadDao noUpLoadtask;
	
//	private ArrayList<ItemImage> saveSelectImg = new ArrayList<ItemImage>(); 
	
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				iv_recording.setImageResource(R.drawable.record01);
				break;
			case 2:
				iv_recording.setImageResource(R.drawable.record02);
				break;
			case 3:
				iv_recording.setImageResource(R.drawable.record03);
				break;
			case 4:
			    iv_recording.setImageResource(R.drawable.record04);
				break;
			case 5:
				iv_recording.setImageResource(R.drawable.record05);
				break;
			case 6:
				iv_recording.setImageResource(R.drawable.record06);
				break;
			case 7:
				iv_recording.setImageResource(R.drawable.record07);
				break;
			case 8:
				iv_recording.setImageResource(R.drawable.record08);
				break;
			case 9:
				initList();
				break;
			case 10:
				DialogHelp.dismisLoadingDialog();
				break;
			case 11:
				initViewData();
				break;
			}
		};
	};
	private boolean isVisibility = false;
	private ImageView tv_cancel_new_audio;
	private ScriptAdapter scriptAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		setContentView(R.layout.activity_new_script);
		application=(CbApplication) getApplication();
		mLoginBean=application.getmLoginBean();
//		selectImage=new ArrayList<ItemImage>();
		noUpLoadtask=new LocationUploadDao(getHelper());
		
		tag=getIntent().getStringExtra("tag");//当跳转到该页面时都传递该表示已以做不同处理
		
		recorder = new MyAudioRecorder("voice_luyin");
		drawable_cancel=getResources().getDrawable(R.drawable.red_cha);
		drawable_cancel.setBounds(0,0,drawable_cancel.getMinimumWidth(),drawable_cancel.getMinimumHeight());
		location_blue=getResources().getDrawable(R.drawable.location_blue);
		location_blue.setBounds(0,0,location_blue.getMinimumWidth(),location_blue.getMinimumHeight());
		if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
			Bimp.tempSelectBitmap.clear();
		}else{
			Bimp.tempSelectBitmap.add(null);
		}
		
		findViews();
	}
	
	
	private void initList() {
		if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
			scriptAdapter = new ScriptAdapter(NewScriptActivity.this,Bimp.tempSelectBitmap,NewScriptActivity.this);
			gv.setAdapter(scriptAdapter);
		}else{
			Bimp.tempSelectBitmap.add(null);
		}
		
		mHandler.post(new Runnable() {  
		    @Override  
		    public void run() {  
		    	mSl_screen.fullScroll(ScrollView.FOCUS_DOWN);  
		    }  
		}); 
	}
	
	
	private void findViews() {
		tv_send=(TextView) findViewById(R.id.tv_send);
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		iv_back=(ImageView) findViewById(R.id.iv_back);
		iv_doc=(ImageView) findViewById(R.id.iv_doc);
		iv_image=(ImageView) findViewById(R.id.iv_image);
		iv_camera=(ImageView) findViewById(R.id.iv_camera);
		iv_mic=(ImageView) findViewById(R.id.iv_mic);
		iv_save=(ImageView) findViewById(R.id.iv_save);
		viewMask=findViewById(R.id.viewMask);
		tv_location=(TextView) findViewById(R.id.tv_location);
		iv_locaiton_reset=findViewById(R.id.iv_locaiton_reset);
		tv_category=(TextView) findViewById(R.id.tv_category);
		rl_recording=(RelativeLayout) findViewById(R.id.rl_recording);
		iv_recording=(ImageView) findViewById(R.id.iv_recording);
		tv_titles.setText("稿件");
		tv_cancel_new_audio = (ImageView)findViewById(R.id.tv_cancel_new_audio);
		tv_cancel_new_audio.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		iv_doc.setOnClickListener(this);
		iv_image.setOnClickListener(this);
		iv_camera.setOnClickListener(this);
		iv_mic.setOnClickListener(this);
		iv_save.setOnClickListener(this);
		tv_category.setOnClickListener(this);
		iv_locaiton_reset.setOnClickListener(this);
		tv_location.setOnClickListener(this);
		viewMask.setOnClickListener(this);
		tv_send.setOnClickListener(this);
		
//		if(IsNonEmptyUtils.isString(application.getLocation())){
//			tv_location.setText(application.getLocation());
//		}

		gv=(GridView) findViewById(R.id.gv);
		gv.setAdapter(new ScriptAdapter(NewScriptActivity.this,Bimp.tempSelectBitmap,NewScriptActivity.this));
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
		        ItemImage  image=	Bimp.tempSelectBitmap.get(position);
		        if(image!=null){
		        	String filepath=Bimp.tempSelectBitmap.get(position).getFilepath();
		        	 if(filepath.endsWith(".mp4")){
		        		 Intent intentvideo=new Intent(NewScriptActivity.this, ShowVideoActivity.class);
		        		 intentvideo.putExtra("playpath", filepath);
		        		 startActivity(intentvideo);
		             }else if(filepath.endsWith(".mp3")){
		            	//开始实质播放
			            MediaManager.playLocationMp3(filepath,new MediaPlayer.OnCompletionListener() {
			                  @Override
			                  public void onCompletion(MediaPlayer mp) {
			                      Toast.makeText(NewScriptActivity.this, "播放完成", 0).show();
//			                      animationDrawable.selectDrawable(0);//显示动画第一帧
//			                      animationDrawable.stop();
//			                      //播放完毕，当前播放索引置为-1。
//			                      pos = -1;
			                   }
			             });

		             }else{
		            	 Intent intentImg=new Intent(NewScriptActivity.this, ShowImageActivity.class);
		            	 intentImg.putExtra(GlobalContants.NEWSHOWIMAGE, filepath);
		            	 startActivity(intentImg);
		             }
		        }
			}
		});
		mRl_isShow_voice = (RelativeLayout) findViewById(R.id.rl_isShow_voice);
		mRl_isShow_voice = (RelativeLayout) findViewById(R.id.rl_isShow_voice);
		mSl_screen = (ScrollView) findViewById(R.id.sl_screen);
		//按着说话按钮
		mIv_voice = (ImageView) findViewById(R.id.iv_voice);
		mIv_voice.setOnClickListener(this);
		
		et_title=(EditText) findViewById(R.id.et_title);
		et_content=(EditText) findViewById(R.id.et_content);
		et_title.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				String content=et_title.getText().toString();
				if(IsNonEmptyUtils.isString(content)){
					if(content.length()>=30){
						UserToast.toSetToast(NewScriptActivity.this, "标题请在30个字以内");
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
//		et_content.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,int count) {
//				String content=et_content.getText().toString();
//				if(IsNonEmptyUtils.isString(content)){
//					if(content.length()>=300){
//						UserToast.toSetToast(NewScriptActivity.this, "内容请在300个字以内");
//					}
//				}
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
//			}
//			@Override
//			public void afterTextChanged(Editable s) {
//			}
//		});
		luying();
		
		mHandler.sendEmptyMessage(11);
	}
	
	/**
	 * 初始化从其他页面传递过来的数据并显示到该页面上
	 */
	private void initViewData(){
		if(tag.endsWith("details")){//表示从详情页面传递
			mNewUploadinglistBean=(UploadingDetailsBean) getIntent().getSerializableExtra("details");
			if(mNewUploadinglistBean!=null){
				et_title.setText(mNewUploadinglistBean.getTitle());
				et_content.setText(mNewUploadinglistBean.getContent());
				List<ResourcesBean> list=mNewUploadinglistBean.getResources();
				if(IsNonEmptyUtils.isList(list)){
					for(int i=0;i<list.size();i++){
						ItemImage selectImage=new ItemImage();
//						selectImage.setBitmap(BitmapUtils.returnBitMapImg(list.get(i).getPreviewUrl()));
						selectImage.setFilepath(list.get(i).getPreviewUrl());
						selectImage.setResult(true);
						Bimp.tempSelectBitmap.add(0,selectImage);
					}
				}
				
				TypeBean bean=new TypeBean();
				bean.setCategoryName(mNewUploadinglistBean.getCategoryName());
				bean.setId(mNewUploadinglistBean.getCategoryId());
				mTypeBean=bean;
				tv_category.setText(mTypeBean.getCategoryName());
				initList();
			}
		}else if(tag.equals("detailsnoup")){
			mNewUploadinglistBean=(UploadingDetailsBean) getIntent().getSerializableExtra("details");
			locationNoUpBean=(LocationNoUploadBean) getIntent().getSerializableExtra("nouploadBean");
			
			if(mNewUploadinglistBean!=null){
				et_title.setText(mNewUploadinglistBean.getTitle());
				et_content.setText(mNewUploadinglistBean.getContent());
				List<ResourcesBean> list=mNewUploadinglistBean.getResources();
				if(IsNonEmptyUtils.isList(list)){
					for(int i=0;i<list.size();i++){
						ItemImage selectImage=new ItemImage();
//						selectImage.setBitmap(BitmapUtils.returnBitMapImg(list.get(i).getPreviewUrl()));
						selectImage.setFilepath(list.get(i).getPreviewUrl());
						selectImage.setResult(true);
						Bimp.tempSelectBitmap.add(0,selectImage);
					}
				}
				
				TypeBean bean=new TypeBean();
				bean.setCategoryName(mNewUploadinglistBean.getCategoryName());
				bean.setId(mNewUploadinglistBean.getCategoryId());
				mTypeBean=bean;
				tv_location.setText(locationNoUpBean.getLocation());
				tv_category.setText(mTypeBean.getCategoryName());
				initList();
			}
			
		}else if(tag.equals("resources")){
			String videoPath=getIntent().getStringExtra("resourcespath");
			ItemImage selectImage=new ItemImage();
//			selectImage.setBitmap(BitmapUtils.returnBitMapImg(list.get(i).getPreviewUrl()));
			selectImage.setFilepath(videoPath);
			selectImage.setResult(true);
			Bimp.tempSelectBitmap.add(0,selectImage);
			mHandler.sendEmptyMessageDelayed(9, 1000);
//			initList();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
			Bimp.tempSelectBitmap.clear();
		}
	}
	
	/**
	 * 将新建的稿件保存到本地
	 */
	private void saveScript(){
		if(mLoginBean!=null){
			title=et_title.getText().toString();
			content=et_content.getText().toString();
			
			if(title.isEmpty()){//标题
				UserToast.toSetToast(NewScriptActivity.this, "请输入标题");
				return ;
			}
			
			if(content.isEmpty()){//正文内容
				UserToast.toSetToast(NewScriptActivity.this, "请输入正文内容");
				return ;
			}
			
			if(!IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
				UserToast.toSetToast(NewScriptActivity.this, "请选择上传资源");
				return;
			}
			
			if(mTypeBean==null){
				UserToast.toSetToast(NewScriptActivity.this, "请选择所属类型");
				return;
			}
			
			LocationNoUploadBean locationnouploadbean=new LocationNoUploadBean();
			locationnouploadbean.setContent(content);
			
			StringBuffer sb=new StringBuffer();
			if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
				for(int i=0;i<Bimp.tempSelectBitmap.size()-1;i++){//最后一个是空白的加好所以size-1
					String imgPath=Bimp.tempSelectBitmap.get(i).getFilepath();
					if(i==(Bimp.tempSelectBitmap.size()-2)){//表示最后一个
						sb.append(imgPath);
					}else{
						sb.append(imgPath+",");
					}
				}
				locationnouploadbean.setImgpath(sb.toString());
			}
			
			locationnouploadbean.setLatitude(application.getLatitude()+"");
//			locationnouploadbean.setLocation(application.getLocation());
			locationnouploadbean.setLocation(tv_location.getText().toString());
			locationnouploadbean.setLongitude(application.getLongitude()+"");
			locationnouploadbean.setTitle(title);
			locationnouploadbean.setTypeId(mTypeBean.getId());
			locationnouploadbean.setTypeName(mTypeBean.getCategoryName());
			locationnouploadbean.setUserId(mLoginBean.getUserID());
			locationnouploadbean.setUserName(mLoginBean.getUsername());
			locationnouploadbean.setUuid(Utils.getUUID());
			locationnouploadbean.setSaveTime(TimeUtil.getDate5());
			locationnouploadbean.setSaveTime1(TimeUtil.getDate6());
			if(tag.equals("detailsnoup")){//表示是从未上传页面编辑过来的在次保存时直接更新
				locationNoUpBean.setContent(content);
				locationNoUpBean.setImgpath(sb.toString());
				locationNoUpBean.setLatitude(application.getLatitude()+"");
				locationNoUpBean.setLocation(tv_location.getText().toString());
//				locationNoUpBean.setLocation(application.getLocation());
				locationNoUpBean.setLongitude(application.getLongitude()+"");
				locationNoUpBean.setTitle(title);
				locationNoUpBean.setTypeId(mTypeBean.getId());
				locationNoUpBean.setTypeName(mTypeBean.getCategoryName());
				locationNoUpBean.setUserId(mLoginBean.getUserID());
				locationNoUpBean.setUserName(mLoginBean.getUsername());
				locationNoUpBean.setUuid(Utils.getUUID());
				locationNoUpBean.setSaveTime(TimeUtil.getDate5());
				locationNoUpBean.setSaveTime1(TimeUtil.getDate6());
				noUpLoadtask.saveOrUpdateTask(locationNoUpBean);
			}else{
				noUpLoadtask.saveOrUpdateTask(locationnouploadbean);
			}
			NewScriptActivity.this.finish();
			UserToast.toSetToast(NewScriptActivity.this, "保存成功");
		}
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.tv_send://发送按钮
			if(mLoginBean!=null){
				title=et_title.getText().toString();
				content=et_content.getText().toString();
				
				if(title.isEmpty()){//标题
					UserToast.toSetToast(NewScriptActivity.this, "请输入标题");
					return ;
				}
				
				if(content.isEmpty()){//正文内容
					UserToast.toSetToast(NewScriptActivity.this, "请输入正文内容");
					return ;
				}
				
				if(mTypeBean==null){
					UserToast.toSetToast(NewScriptActivity.this, "请选择所属类型");
					return;
				}
				
				if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
//					if(Bimp.tempSelectBitmap.size()<=1){
//						UserToast.toSetToast(NewScriptActivity.this, "请选择上传资源");
//						return;
//					}
				}
				
				if(tag.equals("detailsnoup")){//表示是从未上传页面编辑过来的在次保存时直接更新
					com.jwzt.caibian.application.Configs.isUpload=true;
					noUpLoadtask.deletePerson(locationNoUpBean);
				}
				
				/***文稿上传第一次请求接口*/
				Configs.firstUploadUrl=com.jwzt.caibian.application.Configs.ICON_URL+"/bvCaster_converge/phone/media/upload.jspx?requestType=getFileSize&uuid=%s&fileName=%s";
				/***文稿上传第二次请求接口*/
				Configs.SecondUploadUrl=com.jwzt.caibian.application.Configs.ICON_URL+"/bvCaster_converge/phone/media/upload.jspx?requestType=uploadFile&uuid=%s&fileName=%s&beginPosition=%s";
				/***文稿上传第三次请求接口*/
				Configs.ThirdUploadUrl=com.jwzt.caibian.application.Configs.ICON_URL+"/bvCaster_converge/phone/media/upload.jspx?requestType=finishNotify&uuid=%s&fileName=%s&fileTotalSize=%s&fileMD5=%s&infoType=%s";
				
				Intent intent_back=new Intent(NewScriptActivity.this,BackTransferActivity.class);
				intent_back.putExtra("title", title);
				intent_back.putExtra("content", content);
				intent_back.putExtra("type", mTypeBean);
//				intent_back.putExtra("imglist", selectImage);
				intent_back.putExtra("tag", "NewScriptSend");
				intent_back.putExtra("infotype", "1");
				startActivityForResult(intent_back,BACKRSSENDESULT);
				this.finish();
			}
			break;
		case R.id.iv_back://返回按钮
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		case R.id.tv_location://位置
			Intent intent=new Intent(NewScriptActivity.this,LocationActivity.class);
			startActivityForResult(intent, CODE_LOCATION);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.view_bar:
			tv_location.setText("您的坐标?");
			tv_location.setBackgroundResource(R.drawable.circle_border);
			tv_location.setBackgroundResource(R.drawable.selector_circle_border);
			break;
		case R.id.iv_locaiton_reset:
			tv_location.setText("您的坐标?");
			tv_location.setBackgroundResource(R.drawable.selector_circle_border);
			iv_locaiton_reset.setVisibility(View.GONE);
			break;
		case R.id.tv_category://分类
			Intent intent_category=new Intent(NewScriptActivity.this,CategoryActivity.class);
			startActivityForResult(intent_category, CODE_CATEGORY);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.viewMask:
			AnimationUtils.hideAlpha(viewMask);
			break;
		case R.id.iv_doc://文档
			//TODO
//			saveSelectImg=Bimp.tempSelectBitmap;
			Intent intent_doc=new Intent(NewScriptActivity.this,SelectFootageActivity.class);
			intent_doc.putExtra("saveselect", Bimp.tempSelectBitmap);
			startActivityForResult(intent_doc, SELECTRESOURCE);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			break;
		case R.id.iv_image:
			if(Bimp.tempSelectBitmap.size()>=10){
				UserToast.toSetToast(NewScriptActivity.this, "已到选择上线");
			}else{
				Intent intent_paizhao = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				IMG_SAVE_NAME = Utils.getUUID() + ".png";
				intent_paizhao.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(FileUtil.getFolderPath(),IMG_SAVE_NAME)));
				startActivityForResult(intent_paizhao, PHOTOHRAPH);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			}
			
			viewMask.setFocusable(true);
			viewMask.setClickable(true);
			break;
		case R.id.iv_camera:
//			selectVideoPopupWindow = PhotoChangeUtil.getVideoPopupWindow(NewScriptActivity.this, NewScriptActivity.this, root);
//			AnimationUtils.showAlpha(viewMask);
			Intent intent_Paishe = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			// 设置视频大小
			intent_Paishe.putExtra(MediaStore.EXTRA_SIZE_LIMIT,768000);
			// 设置视频时间，毫秒
			intent_Paishe.putExtra(MediaStore.EXTRA_DURATION_LIMIT,450000);
			intent_Paishe.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			startActivityForResult(intent_Paishe, VIDEOPAISHE);
			
			viewMask.setFocusable(true);
			viewMask.setClickable(true);
			break;
		case R.id.iv_mic:
			if (isVisibility ) {
				mRl_isShow_voice.setVisibility(View.GONE);
				isVisibility = false;
			}else {
				mRl_isShow_voice.setVisibility(View.VISIBLE);
				isVisibility = true;
				UIUtils.getHandler().post(new Runnable() {
					
					@Override
					public void run() {
						mSl_screen.fullScroll(ScrollView.FOCUS_DOWN);
					}
				});
			}
			break;
		case R.id.iv_save:
			saveScript();
			break;
		case R.id.tv_take_new://拍照
			if(Bimp.tempSelectBitmap.size()>=100){
				UserToast.toSetToast(NewScriptActivity.this, "已到选择上线");
			}else{
				Intent intent_paizhao = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				IMG_SAVE_NAME = Utils.getUUID() + ".png";
				intent_paizhao.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(new File(FileUtil.getFolderPath(),IMG_SAVE_NAME)));
				startActivityForResult(intent_paizhao, PHOTOHRAPH);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				if(selectPhotoPopupWindow!=null){
					selectPhotoPopupWindow.dismiss();
					selectPhotoPopupWindow=null;
				}
				AnimationUtils.hide(viewMask);
				viewMask.setFocusable(false);
				viewMask.setClickable(false);
			}
			break;
		case R.id.tv_from_album://本地选择图片
			if(Bimp.tempSelectBitmap.size()>=100){
				UserToast.toSetToast(NewScriptActivity.this, "已到选择上线");
			}else{
				Intent intent1 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);   //, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				String path = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
				Uri uri = Uri.fromFile(new File(path));
				intent1.setData(uri);
				this.sendBroadcast(intent1);
				Intent intent_select = new Intent(NewScriptActivity.this, PhotoPickerActivity.class);
				intent_select.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, false);
				intent_select.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, 1);
				intent_select.putExtra(PhotoPickerActivity.ALL_TYPE, PhotoPickerActivity.ALL_PHOTO);
				intent_select.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, (100-Bimp.tempSelectBitmap.size()));
		        startActivityForResult(intent_select, TAKE_PICTURE);
				overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				if(selectPhotoPopupWindow!=null){
					selectPhotoPopupWindow.dismiss();
					selectPhotoPopupWindow=null;
				}
				AnimationUtils.hide(viewMask);
				viewMask.setFocusable(false);
				viewMask.setClickable(false);
			}
			break;
		case R.id.tv_cancel://图片取消
			if(selectPhotoPopupWindow!=null){
				selectPhotoPopupWindow.dismiss();
				selectPhotoPopupWindow=null;
			}
			AnimationUtils.hide(viewMask);
			viewMask.setFocusable(false);
			viewMask.setClickable(false);
			break;
		case R.id.tv_take_video://视频拍摄
			Intent intentVideoPaishe = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			// 设置视频大小
			intentVideoPaishe.putExtra(MediaStore.EXTRA_SIZE_LIMIT,768000);
			// 设置视频时间，毫秒
			intentVideoPaishe.putExtra(MediaStore.EXTRA_DURATION_LIMIT,450000);
			intentVideoPaishe.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			startActivityForResult(intentVideoPaishe, VIDEOPAISHE);
			if(selectVideoPopupWindow!=null){
				selectVideoPopupWindow.dismiss();
				selectVideoPopupWindow=null;
			}
			AnimationUtils.hide(viewMask);
			viewMask.setFocusable(false);
			viewMask.setClickable(false);
			break;
		case R.id.tv_from_video://本地选择视频
	        Intent intentVideo = new Intent();  
	        intentVideo.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）  
	        intentVideo.setAction(Intent.ACTION_GET_CONTENT);  
	        startActivityForResult(intentVideo, VIDEOSELECT);
	        if(selectVideoPopupWindow!=null){
				selectVideoPopupWindow.dismiss();
				selectVideoPopupWindow=null;
			}
			AnimationUtils.hide(viewMask);
			viewMask.setFocusable(false);
			viewMask.setClickable(false);
			break;
		case R.id.tv_cancel_video://录像取消
			if(selectVideoPopupWindow!=null){
				selectVideoPopupWindow.dismiss();
				selectVideoPopupWindow=null;
			}
			AnimationUtils.hide(viewMask);
			viewMask.setFocusable(false);
			viewMask.setClickable(false);
			break;
		case R.id.tv_cancel_audio://录音取消按钮
			if(selectAudioPopupWindow!=null){
				selectAudioPopupWindow.dismiss();
				selectAudioPopupWindow=null;
			}
			AnimationUtils.hide(viewMask);
			viewMask.setFocusable(false);
			viewMask.setClickable(false);
			break;
		case R.id.tv_cancel_new_audio://消息发送取消按钮
			mRl_isShow_voice.setVisibility(View.GONE);
			isVisibility = false;
			break;
		}
		
	}
	
	/**
	 * 录音
	 */
	private void luying() {
		mIv_voice.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 按下事件
					mIv_voice.setBackgroundResource(R.drawable.new_voiceluyin);
					//标记为没有滑离按钮
					if (null != recorder) {
						start_record=System.currentTimeMillis();
						recorder.prepare();
						recorder.startRecording();
						recorder.setmVolumeListener(NewScriptActivity.this);
					} else {
						recorder = new MyAudioRecorder("voice_luyin");
						recorder.prepare();
						recorder.startRecording();
					}
					
					// 显示“录音中”的提示框
					rl_recording.setVisibility(View.VISIBLE);
					
					//开始计时
					bool = true;
					luYinTime=0;
					mHandler.postDelayed(task, 1000);
					iv_recording.setImageResource(R.drawable.record01);
					//开始计时
					bool = true;
					luYinTime=0;
					mHandler.postDelayed(task, 1000);
					break;
				case MotionEvent.ACTION_UP:// 抬起
					mIv_voice.setBackgroundResource(R.drawable.new_voiceluyinwai);
					LuYinTaiQi();
					mRl_isShow_voice.setVisibility(View.GONE);
					isVisibility = false;
					break;
//				case MotionEvent.ACTION_MOVE:
//					System.out.println("Y值"+event.getY()+"X值"+event.getX());
//					if(event.getY()<-100){//如果偏离超过100像素，视为已经滑离按钮
//						//表明用户的手指已经滑离按钮
//						isSlideOut=true;
//						//显示“松开取消发送”的提示
//						rl_cancel.setVisibility(View.VISIBLE);
//						rl_recording.setVisibility(View.INVISIBLE);
//						rl_short.setVisibility(View.INVISIBLE);
//					}else{
//						//表明用户的手指没有滑离按钮
//						isSlideOut=false;
//						rl_recording.setVisibility(View.VISIBLE);
//						rl_cancel.setVisibility(View.INVISIBLE);
//						rl_short.setVisibility(View.INVISIBLE);
//					}
//					break;
//				case MotionEvent.ACTION_CANCEL:
//					isSlideOut=false;
//					if(recorder!=null){
//						recorder.stopRecording();// 停止录音
//						recorder.release();  
//					}
//					break;
				}
				return true;
			}
		});
	}
	
	/**
	 * 录音抬起方法
	 */
	private void LuYinTaiQi(){
		bool = false;
		end_record=System.currentTimeMillis();
		//如果录音时间大于3秒并且没有滑离按钮
		if(end_record-start_record>=1500){
//			// 隐藏“录音中”的提示框
			rl_recording.setVisibility(View.GONE);
			// 释放
			mp3Path = recorder.stopRecording();// 停止录音
			recorder.release();
			ItemImage image=new ItemImage();
			image.setBitmap(null);
			image.setFilepath(mp3Path);
			image.setResult(true);
			Bimp.tempSelectBitmap.add(0,image);
			mHandler.sendEmptyMessage(9);
		}else if(end_record-start_record<1500){//手指没有滑离但是录音时间小于3秒
			rl_recording.setVisibility(View.GONE);
			recorder.stopRecording();// 停止录音
			recorder.release();
			//500毫秒之后隐藏“录音时间太短”的提示
			mHandler.sendEmptyMessageDelayed(30, 500);
		}
		

		
	//	System.out.println("mp3Pathmp3Pathmp3Path"+mp3Path);
	}
	
	//录音计时
	private Runnable task = new Runnable() {
		public void run() {
			if (bool) {
				++luYinTime;
				Message message=new Message();
				message=mHandler.obtainMessage(35, luYinTime);
				mHandler.sendMessage(message);
				mHandler.postDelayed(this, 1000);
			}
		}
	};
	private RelativeLayout mRl_isShow_voice;
	private ScrollView mSl_screen;
	private ImageView mIv_voice;
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==TAKE_PICTURE) {//本地选择图片
			if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
				mHandler.sendEmptyMessage(9);
			}
		}else if(requestCode == PHOTOHRAPH){//拍照
			final File picture = new File(FileUtil.getFolderPath(), IMG_SAVE_NAME);
			Uri localUri = Uri.fromFile(picture);
			Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
			sendBroadcast(localIntent);
			int degree = FileUtil.readPictureDegree(picture.getAbsolutePath());  
			BitmapFactory.Options opts=new BitmapFactory.Options();//获取缩略图显示到屏幕上
			opts.inSampleSize=2;
			Bitmap cbitmap=BitmapFactory.decodeFile(picture.getAbsolutePath(),opts);
			if(cbitmap!=null){//把图片旋转为正的方向 
               Bitmap bm = FileUtil.rotaingImageView(degree, cbitmap);    //控制旋转角度
           	   ItemImage image=new ItemImage();
    		   image.setBitmap(null);
    		   image.setFilepath("file:///"+picture.getAbsolutePath());
    		   image.setResult(true);
    		   Bimp.tempSelectBitmap.add(0,image);
    		   mHandler.sendEmptyMessage(9);
           }
		}else if(requestCode == VIDEOSELECT){//本地选择视频
			if (data != null) {
				Uri uri = data.getData();
				if(uri!=null){
					String picturePath = GETImageUntils.getPath(NewScriptActivity.this, uri);
					ItemImage image=new ItemImage();
		    		image.setBitmap(null);
		    		image.setFilepath(picturePath);
		    		image.setResult(true);
		    		Bimp.tempSelectBitmap.add(0,image);
					mHandler.sendEmptyMessage(9);
				}
			}
		}else if(requestCode == VIDEOPAISHE){//本地拍摄视频
			if (data != null) {
				Uri originalUri = data.getData();
				if (originalUri != null) {
					String[] proj = { MediaStore.Images.Media.DATA };
					Cursor cursor = managedQuery(originalUri, proj, null, null,null);
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					String path = cursor.getString(column_index);
					ItemImage image=new ItemImage();
		    		image.setBitmap(null);
		    		image.setFilepath(path);
		    		image.setResult(true);
		    		Bimp.tempSelectBitmap.add(0,image);
					mHandler.sendEmptyMessage(9);
				}
			}
		}else if(requestCode==CODE_CATEGORY){//分类
			if(data!=null){
				mTypeBean=(TypeBean) data.getSerializableExtra("tag");
				if (mTypeBean !=null) {
					if(!TextUtils.isEmpty(mTypeBean.getCategoryName())){
						tv_category.setText(mTypeBean.getCategoryName());
					}
				}
			}
		}else if(requestCode==CODE_LOCATION){//定位
			if(data!=null){
				String info = data.getStringExtra("info");
				if(!TextUtils.isEmpty(info)){
					tv_location.setText(info);
					tv_location.setBackgroundResource(R.drawable.selector_circle_border_left);
					iv_locaiton_reset.setVisibility(View.VISIBLE);
				}
			}
		}else if (requestCode==BACKRSSENDESULT) {
			System.out.println(" 发布 BACKRSSENDESULT    回调");
			Bimp.tempSelectBitmap.clear();
//			selectImage.clear();
			Bimp.tempSelectBitmap.add(null);
			//scriptAdapter.notifyDataSetChanged();
			gv.setAdapter(new ScriptAdapter(NewScriptActivity.this,Bimp.tempSelectBitmap,NewScriptActivity.this));
		}else if(requestCode==SELECTRESOURCE){//从选择素材库返回
			//TODO
			if(IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)){
				if(data!=null){
					ArrayList<ItemImage> saveSelect=(ArrayList<ItemImage>) data.getSerializableExtra("saveselect");
					ArrayList<ItemImage> saveSelectss=saveSelect(saveSelect,Bimp.tempSelectBitmap);
					Bimp.tempSelectBitmap=saveSelectss;
					mHandler.sendEmptyMessage(9);
				}
			}
		}
	}
	
	/**
	 * 将新选择的资源附加到原有的数据上
	 * @param saveSelect 跳转到素材库页面前已选的数据
	 * @param selectimg 从素材库页面返回后所选的素材
	 * @return
	 */
	private ArrayList<ItemImage> saveSelect(ArrayList<ItemImage> saveSelectimg,ArrayList<ItemImage> selectimg){
		if(IsNonEmptyUtils.isList(saveSelectimg)){//跳转页面之前保存的数据
			if(IsNonEmptyUtils.isList(selectimg)){//从素材库页面返回时选择的数据
				for(int i=0;i<selectimg.size();i++){
					if(selectimg.get(i)!=null){
						ItemImage itemImage=new ItemImage();
						itemImage.setBitmap(null);
						String filepath=selectimg.get(i).getFilepath();
						if(isTaskGroup(filepath, saveSelectimg)){//表示集合中已存在
							
						}else{//表示集合中不存在
							itemImage.setFilepath(filepath);
							itemImage.setResult(true);
							saveSelectimg.add(0,itemImage);
						}
					}
				}
				return saveSelectimg;
			}
		}
		if(IsNonEmptyUtils.isList(saveSelectimg)){
			
		}else{
			saveSelectimg=new ArrayList<ItemImage>();
		}
		saveSelectimg.add(null);
		return saveSelectimg;
	}
	
	/**
	 * 判断改任务是否存在群组交流列表中
	 * @return
	 */
	private boolean isTaskGroup(String filepath,ArrayList<ItemImage> saveSelectimg){
		if(IsNonEmptyUtils.isList(saveSelectimg)){
			for(int i=0;i<saveSelectimg.size();i++){
				if(saveSelectimg.get(i)!=null){
					if(filepath.equals(saveSelectimg.get(i).getFilepath())){
						return true;
					}
				}else{
					return false;
				}
			}
		}else{
			return false;
		}
		return false;
	}
	
	@Override
	public void setCallbackAdapter() {
		if(Bimp.tempSelectBitmap.size()>=100){
			UserToast.toSetToast(NewScriptActivity.this, "已到选择上线");
		}else{
			Intent intent1 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);   //, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
			String path = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
			Uri uri = Uri.fromFile(new File(path));
			intent1.setData(uri);
			this.sendBroadcast(intent1);

			Intent intent_select = new Intent(NewScriptActivity.this, PhotoPickerActivity.class);
			intent_select.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, false);
			intent_select.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, 1);
			intent_select.putExtra(PhotoPickerActivity.ALL_TYPE, PhotoPickerActivity.ALL_PHOTO);
			intent_select.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, (100-Bimp.tempSelectBitmap.size()));
	        startActivityForResult(intent_select, TAKE_PICTURE);
			overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
			if(selectPhotoPopupWindow!=null){
				selectPhotoPopupWindow.dismiss();
				selectPhotoPopupWindow=null;
			}
			AnimationUtils.hide(viewMask);
			viewMask.setFocusable(false);
			viewMask.setClickable(false);
		}
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			title=et_title.getText().toString();
			content=et_content.getText().toString();
			if(!title.isEmpty()||!content.isEmpty()||mTypeBean!=null||IsNonEmptyUtils.isList(Bimp.tempSelectBitmap)&&Bimp.tempSelectBitmap.size()>1){//标题
				showTip();
			}else{
				return super.onKeyDown(keyCode, event);
			}
        }
		
        return false;
	}
	
	/**
	 *退出页面时询问是否保存
	 */
	private void showTip() {
		if (alertDialog == null) {
			alertDialog = new AlertDialog.Builder(NewScriptActivity.this).create();
			OnKeyListener keylistener = new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
						return true;
					} else {
						return false;
					}
				}
			};
			alertDialog.setOnKeyListener(keylistener);// 保证按返回键的时候alertDialog也不会消失
		}
		alertDialog.show();
		View tipView = View.inflate(NewScriptActivity.this, R.layout.edit_alert_layout, null);
		TextView tv_tip = (TextView) tipView.findViewById(R.id.tv_tip);
		tv_tip.setText("有资源未保存或上传，确认退出页面");
		View tv_yes = (TextView) tipView.findViewById(R.id.tv_yes);
		tv_yes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				NewScriptActivity.this.finish();
			}
		});
		tipView.findViewById(R.id.tv_no).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		alertDialog.setContentView(tipView);
	}

	@Override
	public void onCurrentVoice(int currentVolume) {
		mHandler.sendEmptyMessage(currentVolume);
	}

	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
	}
	@Override
	protected void initRequestOnStart(String result, int code, int biaoshi) {
	}
	@Override
	protected void initRequestOnFailure(String failure, int code, int biaoshi) {
	}
	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
	}
	
	
	
}
