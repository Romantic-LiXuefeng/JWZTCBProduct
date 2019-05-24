package com.jwzt.caibian.util;

import java.io.File;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.jwzt.caibian.application.Configs;

/**
 * 
 * 修改头像时调用拍照和从相册选择的工具类
 * @author hly
 *
 */
public class CreateBmpFactory {

	// ----------相机图片的业务相关
	private static final int PHOTO_REQUEST_CAREMA = 3;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 4;// 从相册中选择

	private Fragment fragment;
	private Activity activity;
	private Context mContext;
	
	public CreateBmpFactory(Context context,Fragment fragment) {
		super();
		this.mContext=context;
		this.fragment = fragment;
		WindowManager wm = (WindowManager) fragment.getActivity().getSystemService("window");
		windowHeight = wm.getDefaultDisplay().getHeight();
		windowWidth = wm.getDefaultDisplay().getWidth();
	}

	int windowHeight;
	int windowWidth;

	public CreateBmpFactory(Context context,Activity activity) {
		super();
		this.mContext=context;
		this.activity = activity;
		WindowManager wm = (WindowManager) activity.getSystemService("window");
		windowHeight = wm.getDefaultDisplay().getHeight();
		windowWidth = wm.getDefaultDisplay().getWidth();
	}

	//从本地相册中选择
	public void OpenGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		if (fragment != null) {
			fragment.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
		} else {
			activity.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
		}
	}

	//调用系统相机拍照
	public void OpenCamera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		if (hasSdcard()) {
			Configs.tempFile = new File(Environment.getExternalStorageDirectory(), UUID.randomUUID().toString() + ".jpg");
			Uri uri = Uri.fromFile(Configs.tempFile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		}
		if (fragment != null) {
			fragment.startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
		} else {
			activity.startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
		}
	}

	public String getBitmapFilePath(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			String picturePath;
			if (data != null) {
				Uri uri = data.getData();
				if(uri!=null){
					String[] filePathColumn = { MediaColumns.DATA };
					picturePath = GETImageUntils.getPath(mContext, uri);
					System.out.println("我是选择图片地址："+picturePath);
					return picturePath;
				}
			}
		} else if (requestCode == PHOTO_REQUEST_CAREMA) {
			if (hasSdcard()) {
				return Configs.tempFile.getAbsolutePath();
			} else {
				if (fragment != null) {
					
					UserToast.toSetToast(fragment.getActivity(), "未找到存储卡，无法存储照片！");
				} else {
					UserToast.toSetToast(activity, "未找到存储卡，无法存储照片！");
				}
			}
		}
		return null;
	}
	
	public Bitmap getBitmapByOpt(String picturePath) {
		Options opt = new Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(picturePath, opt);
		int imgHeight = opt.outHeight;
		int imgWidth = opt.outWidth;
		int scaleX = imgWidth / windowWidth;
		int scaleY = imgHeight / windowHeight;
		int scale = 1;
		if (scaleX > scaleY & scaleY >= 1) {
			scale = scaleX;
		}
		if (scaleY > scaleX & scaleX >= 1) {
			scale = scaleY;
		}
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = scale;
		return BitmapFactory.decodeFile(picturePath, opt);
	}

	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
}
