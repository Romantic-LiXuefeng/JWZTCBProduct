package com.jwzt.caibian.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.util.BitmapUtils;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ShowImageActivity extends Activity implements OnClickListener {
	/**图片显示的控件*/
	private ImageView iv_show_image;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	/**保存图片的按钮*/
	private TextView tv_pic_sav;
	private String path;
	private String filename;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				ShowImageActivity.this.finish();
				break;
			case 1:
				UIUtils.showToast("图片已保存至"+BitmapUtils.imagepath + filename);
				//UserToast.toSetToast(ShowImageActivity.this, "图片已保存至"+filename);
				break;
			case 2:
				UIUtils.showToast("保存失败");
				//UserToast.toSetToast(ShowImageActivity.this, "保存失败");
			
				break;
			}
		};
	};
	private Runnable saveFileRunnable = new Runnable() {

		@Override
		public void run() {
			try {
				Bitmap bitmap = BitmapUtils.returnBitMap(path);
				filename = path.hashCode() + "";
				BitmapUtils.saveImageToGallery(getApplicationContext(), bitmap,filename);
				Message message = mHandler.obtainMessage();
				message.what = 1;
				mHandler.sendMessage(message);
			} catch (Exception e) {
				Message message = mHandler.obtainMessage();
				message.what = 2;
				mHandler.sendMessage(message);
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_image);
		initView();
		initData();
	}

	private void initData() {
		options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.replace) // 设置图片下载期间显示的图片  
        .showImageForEmptyUri(R.drawable.replace) // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.replace) // 设置图片加载或解码过程中发生错误显示的图片  
        .cacheInMemory(false) // 设置下载的图片是否缓存在内存中  
        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中 
        .bitmapConfig(Config.RGB_565)
//        .displayer(new FadeInBitmapDisplayer(100))
        .build(); // 构建完成  
	    imageLoader = ImageLoader.getInstance();
		Intent intent = getIntent();
		path = intent.getStringExtra(GlobalContants.NEWSHOWIMAGE);
		imageLoader.displayImage(path, iv_show_image, options);
	}

	private void initView() {
		iv_show_image = (ImageView) findViewById(R.id.iv_show_image);
		tv_pic_sav = (TextView) findViewById(R.id.tv_pic_sav);
		iv_show_image.setOnClickListener(this);
		tv_pic_sav.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_show_image:
			this.finish();
			break;
		case R.id.tv_pic_sav:
			if(path.startsWith("http://")){
				saveImage();
			}else{
				UserToast.toSetToast(ShowImageActivity.this, "本机图片不能再次保存");
			}
			break;
		}
	}
	
	
	
	
	
	
	
	private void saveImage() {
		//UserToast.toSetToast(this, "保存");
		String filename = path;
		boolean is = BitmapUtils.isPicExist(filename);
		if (is) {
			UserToast.toSetToast(this, "已经保存");
			
		} else {
			new Thread(saveFileRunnable).start();
		}
	}






	

}
