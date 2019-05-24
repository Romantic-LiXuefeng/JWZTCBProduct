package com.jwzt.caibian.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.util.FileOperateUtil;
import com.jwzt.caibian.widget.CameraContainer;
import com.jwzt.caibian.widget.CameraContainer.TakePictureListener;
import com.jwzt.caibian.widget.CameraView.FlashMode;
import com.jwzt.caibian.widget.FilterImageView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;


/** 
 * @ClassName: CameraAty 
 * @Description:  自定义照相机类
 *  
 */
public class CameraAty extends Activity implements View.OnClickListener,TakePictureListener{
	public final static String TAG="CameraAty";
	private boolean mIsRecordMode=false;
	private String mSaveRoot;
	private CameraContainer mContainer;
	private FilterImageView mThumbView;
	private ImageButton mCameraShutterButton;
	private ImageButton mRecordShutterButton;
	private ImageView mFlashView;
	//private ImageButton mSwitchModeButton;
	private ImageView mSwitchCameraView;
	private ImageView mSettingView;
	private ImageView mVideoIconView;
	private View mHeaderBar;
	private boolean isRecording=false;
	private ImageView mTitleBack;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera);

		ImageButton btn_switch_mode = findViewById(R.id.btn_switch_mode);
		btn_switch_mode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<String> picPathList = mContainer.getPicPathList();
				Intent intent=new Intent();
				intent.putExtra("picPathList", picPathList);
				CameraAty.this.setResult(2, intent);//2任意
				CameraAty.this.finish();
			}
		});
		mHeaderBar=findViewById(R.id.camera_header_bar);
		mContainer=(CameraContainer)findViewById(R.id.container);
		mThumbView=(FilterImageView)findViewById(R.id.btn_thumbnail);
		mVideoIconView=(ImageView)findViewById(R.id.videoicon);
		mCameraShutterButton=(ImageButton)findViewById(R.id.btn_shutter_camera);
		mRecordShutterButton=(ImageButton)findViewById(R.id.btn_shutter_record);
		mSwitchCameraView=(ImageView)findViewById(R.id.btn_switch_camera);
		mFlashView=(ImageView)findViewById(R.id.btn_flash_mode);
	//	mSwitchModeButton=(ImageButton)findViewById(R.id.btn_switch_mode);
		mSettingView=(ImageView)findViewById(R.id.btn_other_setting);
		mTitleBack = (ImageView)findViewById(R.id.btn_take_picture_back);
		mThumbView.setOnClickListener(this);
		mCameraShutterButton.setOnClickListener(this);
		mRecordShutterButton.setOnClickListener(this);
		mFlashView.setOnClickListener(this);
	//	mSwitchModeButton.setOnClickListener(this);
		mSwitchCameraView.setOnClickListener(this);
		mSettingView.setOnClickListener(this);
		mTitleBack.setOnClickListener(this);

		mSaveRoot="test";
		mContainer.setRootPath(mSaveRoot);
		initThumbnail();
	}


	/**
	 * 加载缩略图
	 */
	private void initThumbnail() {
		String thumbFolder=FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, mSaveRoot);
		List<File> files=FileOperateUtil.listFiles(thumbFolder, ".jpg");
		if(files!=null&&files.size()>0){
			Bitmap thumbBitmap=BitmapFactory.decodeFile(files.get(0).getAbsolutePath());
			if(thumbBitmap!=null){
				mThumbView.setImageBitmap(thumbBitmap);
				//视频缩略图显示播放图案
				if(files.get(0).getAbsolutePath().contains("video")){
					mVideoIconView.setVisibility(View.GONE);
				}else {
					mVideoIconView.setVisibility(View.GONE);
				}
			}
		}else {
			mThumbView.setImageBitmap(null);
			mVideoIconView.setVisibility(View.GONE);
		}

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_shutter_camera:
			mCameraShutterButton.setClickable(false);
			mContainer.takePicture(this);
			break;
		case R.id.btn_thumbnail:
			//打开缩略图预览界面，并关闭当前的页面
//			startActivity(new Intent(this,AlbumAty.class));
//			finish();
			break;
		case R.id.btn_flash_mode:
			if(mContainer.getFlashMode()==FlashMode.ON){
				mContainer.setFlashMode(FlashMode.OFF);
				mFlashView.setImageResource(R.drawable.btn_flash_off);
			}else if (mContainer.getFlashMode()==FlashMode.OFF) {
				mContainer.setFlashMode(FlashMode.AUTO);
				mFlashView.setImageResource(R.drawable.btn_flash_auto);
			}
			else if (mContainer.getFlashMode()==FlashMode.AUTO) {
				mContainer.setFlashMode(FlashMode.TORCH);
				mFlashView.setImageResource(R.drawable.btn_flash_torch);
			}
			else if (mContainer.getFlashMode()==FlashMode.TORCH) {
				mContainer.setFlashMode(FlashMode.ON);
				mFlashView.setImageResource(R.drawable.btn_flash_on);
			}
			break;
//		case R.id.btn_switch_mode:
////			if(mIsRecordMode){
////				mSwitchModeButton.setImageResource(R.drawable.ic_switch_camera);
////				mCameraShutterButton.setVisibility(View.VISIBLE);
////				mRecordShutterButton.setVisibility(View.GONE);
////				//拍照模式下显示顶部菜单
////				mHeaderBar.setVisibility(View.VISIBLE);
////				mIsRecordMode=false;
////				mContainer.switchMode(0);
////				stopRecord();
////			}
////			else {
////				mSwitchModeButton.setImageResource(R.drawable.ic_switch_video);
////				mCameraShutterButton.setVisibility(View.GONE);
////				mRecordShutterButton.setVisibility(View.VISIBLE);
////				//录像模式下隐藏顶部菜单
////				mHeaderBar.setVisibility(View.GONE);
////				mIsRecordMode=true;
////				mContainer.switchMode(5);
////			}
//			break;
		case R.id.btn_shutter_record:
			if(!isRecording){
				isRecording=mContainer.startRecord();
				if (isRecording) {
					mRecordShutterButton.setBackgroundResource(R.drawable.btn_shutter_recording);
				}
			}else {
				stopRecord();	
			}
			break;
		case R.id.btn_switch_camera:
			mContainer.switchCamera();
			break;
		case R.id.btn_other_setting:
			mContainer.setWaterMark();
			break;
		case R.id.btn_take_picture_back:
			//TODO 需要返回拍照的集合
			ArrayList<String> picPathList = mContainer.getPicPathList();
			Intent intent=new Intent();
			intent.putExtra("picPathList", picPathList);
			setResult(2, intent);//2任意
			CameraAty.this.finish();
			break;
		default:
			break;
		}
	}


	private void stopRecord() {
		mContainer.stopRecord(this);
		isRecording=false;
		mRecordShutterButton.setBackgroundResource(R.drawable.btn_shutter_record);
	}
	
	@Override
	public void onTakePictureEnd(Bitmap thumBitmap) {
		mCameraShutterButton.setClickable(true);	
	}

	@Override
	public void onAnimtionEnd(Bitmap bm,boolean isVideo) {
		if(bm!=null){
			//生成缩略图
			Bitmap thumbnail=ThumbnailUtils.extractThumbnail(bm, 213, 213);
			mThumbView.setImageBitmap(thumbnail);
			if(isVideo)
				mVideoIconView.setVisibility(View.GONE);
			else {
				mVideoIconView.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void onResume() {		
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {//当返回按键被按下
			ArrayList<String> picPathList = mContainer.getPicPathList();
			Intent intent=new Intent();
			intent.putExtra("picPathList", picPathList);
			setResult(2, intent);//2任意
			CameraAty.this.finish();
			 }
			 return false;

	}
}