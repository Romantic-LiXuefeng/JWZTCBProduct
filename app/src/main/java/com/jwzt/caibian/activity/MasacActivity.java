package com.jwzt.caibian.activity;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.util.ImageUtil;
import com.jwzt.caibian.widget.MosaicImageView;

/**
 * 对图片进行编辑的界面
 * 
 **/

public class MasacActivity extends Activity {
	/** 传递动作,为takephoto 表示拍照，否则传递过来的是图片的路径 */
	public static final String FILEPATH = "filepath";
	public static final String ACTION = "action";
	public static final String ACTION_INIT = "action_init";
	public static final String FROM = "from";
	/** 动作 */
	public static final String TAKEPHOTO = "takephoto";
	public static final String REDRAW = "ReDraw";
	/** 涂鸦控件的容器 **/
	public LinearLayout imageContent;
	/** 操纵图片的路径 **/
	private String filePath = "";
	/** 涂鸦控件 **/
	private MosaicImageView touchView;
	/** 完成按钮 **/
	public View overBt;
	/** 返回按钮（左上角）*/
	public ImageButton backIB = null;
	/** 完成按钮（右上角）*/
	public Button finishBtn = null;
	/** 撤销文字 **/
	public View cancelText;
	private GetImage handler;
	private ProgressDialog progressDialog = null;
	/** 是否为涂鸦 如果是涂鸦 不能删除之前的照片 **/
	public boolean isReDraw = false;
	Intent intent = null;
	public Context context;
	public BroadcastReceiver broadcastReceiver = null;

	
	/** 回调接口 */
	private SeekBar seekBar;
	private String new_filePath;
	private String new_filePath_sub;
	private boolean isHave=false;;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_msc);
		initView();
		context = this;
		// 获取传递过来的图片路径
		intent = getIntent();
		broadcastReceiver = new BroadcastReceiver(){
			public void onReceive(Context context, Intent intent) {
				if(progressDialog != null && progressDialog.isShowing()){
					progressDialog.dismiss();
				}
			};
		};
		
		registerReceiver(broadcastReceiver, new IntentFilter(ACTION_INIT));
		filePath = getIntent().getStringExtra("filePath");
		new_filePath = getIntent().getStringExtra("new_filePath");
		new_filePath_sub = getIntent().getStringExtra("new_filePath_sub");
//		if(new File(new_filePath).exists()){
//			filePath=new_filePath;
//			isHave=true;
//		}
		if (!TextUtils.isEmpty(filePath)) {
			ImageThread thread = new ImageThread();
			thread.start();
		}
	}

	

	private void initView() {
		imageContent = (LinearLayout) findViewById(R.id.draw_photo_view);
		handler = new GetImage();
		backIB = (ImageButton) findViewById(R.id.title_bar_left_btn);
		backIB.setVisibility(View.VISIBLE);
		finishBtn = (Button) findViewById(R.id.title_bar_right_btn);
		finishBtn.setBackgroundResource(R.drawable.selector_round_green_btn);
		finishBtn.setVisibility(View.GONE);
		
		
		overBt =  findViewById(R.id.back_apply);
		cancelText = findViewById(R.id.back_return);
		View back_to_main =  findViewById(R.id.back_to_main);
		back_to_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MasacActivity.this.finish();
			}
		});
		
		
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progress = 0;			
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				if(progress > 0 && progress < 12.5){
					seekBar.setProgress(0);
				}else if(progress > 12.5 && progress < 25){
					seekBar.setProgress(25);
				}else if(progress > 25 && progress < 37.5){
					seekBar.setProgress(25);
				}else if(progress > 37.5 && progress < 50){
					seekBar.setProgress(50);
				}else if(progress > 50 && progress < 62.5){
					seekBar.setProgress(50);
				}else if(progress > 62.5 && progress < 75){
					seekBar.setProgress(75);
				}else if(progress > 75 && progress < 87.5){
					seekBar.setProgress(75);
				}else if(progress > 87.5 && progress < 100){
					seekBar.setProgress(100);
				}
				touchView.setStrokeMultiples(1 + (float)(progress / 100.0));
				touchView.removeStrokeView();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				this.progress = progress;
				touchView.setStrokeMultiples(1 + (float)(progress / 100.0));
			}
		});
		backIB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                try {
                    touchView.sourceBitmap.recycle();
                    touchView.sourceBitmapCopy.recycle();
                    touchView.destroyDrawingCache();
                } catch (Exception e) {
                    e.printStackTrace();
                }
				finish();
			}
		});
		finishBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				touchView.sourceBitmap.recycle();
				touchView.sourceBitmapCopy.recycle();
				touchView.destroyDrawingCache();
				finish();
			}
		});
		overBt.setOnClickListener(new OnClickListener() {// 完成编辑按钮
			@Override
			public void onClick(View v) {
				overBt.setEnabled(false);
				// 新建一个文件保存照片
				File f = new File(new_filePath);
				
				try {
					Bitmap saveBitmap = touchView.combineBitmap(touchView.sourceBitmapCopy, touchView.sourceBitmap);
					ImageUtil.saveMyBitmap(f, saveBitmap);// 将图片重新存入SD卡
					ImageUtil.saveMyBitmap(new File(new_filePath_sub), saveBitmap);// 将图片重新存入SD卡
					
					
					if (touchView.sourceBitmapCopy != null) {
						touchView.sourceBitmapCopy.recycle();
					}
					touchView.sourceBitmap.recycle();
					saveBitmap.recycle();
					touchView.destroyDrawingCache();
				} catch (IOException e) {
					e.printStackTrace();
				}
				finish();
			}
		});

		cancelText.setOnClickListener(new OnClickListener() {// 撤销按钮
					@Override
					public void onClick(View v) {
						cancelDrawImage();
					}
				});
	}

	/** 撤销方法 **/
	@SuppressWarnings("deprecation")
	@SuppressLint("HandlerLeak")
	public void cancelDrawImage() {
			touchView.destroyDrawingCache();
			WindowManager manager = MasacActivity.this.getWindowManager();
			int ww = manager.getDefaultDisplay().getWidth();// 这里设置高度
			int hh = manager.getDefaultDisplay().getHeight();// 这里设置宽度为
			touchView.revocation(filePath, ww, hh);
			// OME--
			if(imageContent.getChildCount() == 0){
				imageContent.addView(touchView);
			}
	}

	@SuppressLint("HandlerLeak")
	private class GetImage extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				progressDialog = ProgressDialog.show(MasacActivity.this,
						context.getString(R.string.drawPhoto_actionName),
						context.getString(R.string.drawPhoto_actioning));
			}
				break;
			case 1: {
				if (touchView != null) {
					imageContent.removeView(touchView);
				}
				touchView = (MosaicImageView) msg.obj;
				touchView.destroyDrawingCache();
				imageContent.addView(touchView);
			}
				break;
			case 2: {
				// 获取新的图片路径
				filePath = (String) msg.obj;
				// 开启图片和处理线程
				ImageThread thread = new ImageThread();
				thread.start();
			}
				break;
			case 3: {
				if (progressDialog != null)
					progressDialog.dismiss();
			}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	}

	private class ImageThread extends Thread {
		@SuppressWarnings("deprecation")
		public void run() {
			// 打开进度条
			Message msg = new Message();
			msg.what = 0;
			handler.sendMessage(msg);
			// 获取屏幕大小
			WindowManager manager = MasacActivity.this.getWindowManager();
			int ww = manager.getDefaultDisplay().getWidth();// 这里设置高度
			int hh = manager.getDefaultDisplay().getHeight();// 这里设置宽度为
			// 生成画图视图
			touchView = new MosaicImageView(MasacActivity.this, null, filePath, ww, hh);
			Message msg1 = new Message();
			msg1.what = 1;
			msg1.obj = touchView;
			handler.sendMessage(msg1);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(broadcastReceiver != null){
			unregisterReceiver(broadcastReceiver);
		}
	}
}

