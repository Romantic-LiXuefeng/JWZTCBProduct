package com.jwzt.caibian.util;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jwzt.caibian.application.CbApplication;


/**
 * 工具类, 专门处理UI相关的逻辑
 * 
 * @author Kevin
 * 
 */
public class UIUtils {

	public static Context getContext() {
		return CbApplication.getContext();
	}

	public static int getMainThreadId() {
		return CbApplication.getMainThreadId();
	}

	public static Handler getHandler() {
		return CbApplication.getHandler();
	}

	/**
	 * 根据id获取字符串
	 */
	public static String getString(int id) {
		return getContext().getResources().getString(id);
	}

	/**
	 * 根据id获取图片
	 */
	public static Drawable getDrawable(int id) {
		return getContext().getResources().getDrawable(id);
	}

	/**
	 * 根据id获取颜色值
	 */
	public static int getColor(int id) {
		return getContext().getResources().getColor(id);
	}

	/**
	 * 获取颜色状态集合
	 */
	public static ColorStateList getColorStateList(int id) {
		return getContext().getResources().getColorStateList(id);
	}

	/**
	 * 根据id获取尺寸
	 */
	public static int getDimen(int id) {
		return getContext().getResources().getDimensionPixelSize(id);
	}

	/**
	 * 根据id获取字符串数组
	 */
	public static String[] getStringArray(int id) {
		return getContext().getResources().getStringArray(id);
	}

	/**
	 * dp转px
	 */
	public static int dip2px(float dp) {
		float density = getContext().getResources().getDisplayMetrics().density;
		return (int) (density * dp + 0.5);
	}

	/**
	 * px转dp
	 */
	public static float px2dip(float px) {
		float density = getContext().getResources().getDisplayMetrics().density;
		return px / density;
	}

	/**
	 * 加载布局文件
	 */
	public static View inflate(int layoutId) {
		return View.inflate(getContext(), layoutId, null);
	}

	/**
	 * 判断当前是否运行在主线程
	 * 
	 * @return
	 */
	public static boolean isRunOnUiThread() {
		return getMainThreadId() == android.os.Process.myTid();
	}

	/**
	 * 保证当前的操作运行在UI主线程
	 * 
	 * @param runnable
	 */
	public static void runOnUiThread(Runnable runnable) {
		if (isRunOnUiThread()) {
			runnable.run();
		} else {
			getHandler().post(runnable);
		}
	}
	public static Resources getResources() {
		return getContext().getResources();
	}
	public static int getScreenWidth() {
		DisplayMetrics dm = UIUtils.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}
	public static int dp2Px(int dip) {
		//        dp<-->px
		//1. px/dp = density
		//2. px / (ppi/160) = dp;

		float density = UIUtils.getResources().getDisplayMetrics().density;
		int px = (int) (dip * density + .5f);
		return px;
	}
	////////////////////////////////////////////////////////
	public static byte[] Bitmap2Bytes(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	//////////////////////////////////////////////////////////
	public static Bitmap Bytes2Bimap(byte[] b){
		if(b.length!=0){
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		}
		else {
			return null;
		}
	}

	////////////////////////////////获取电池的电量///////////////

	public static int getBatteryManager(){
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = getContext().registerReceiver(null, ifilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		level = (int)(level/10.0);
		return level;
	}
	//Android中判断SD卡是否存在，并且可以进行写操作，可以使用如下代码
	//////////////////////////////////////////////////////////////////////////
	public static boolean isHaveSdcard(){
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}else {
			return false;
		}
	}
	////////////////////////////////获取sdcard文件的缓存目录////////////////////
	public static String getSdCardCatchDir(){
		return getContext().getExternalCacheDir().getPath();
	}

	////////////////////////////////获取sdcard文件私有目录////////////////
	public static String getMySdCardPrivatelyDir(){
		return getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
	}
	////////////////////////////////获取sdcard文件的缓存目录////////////////////
	public static String getCatchDir(){
		return getContext().getCacheDir().getPath();
	}

	////////////////////////////////获取sdcard文件私有目录////////////////
	public static String getMyPrivatelyDir(){
		return getContext().getFilesDir().getPath();
	}

	//////////////////////////把bitmap保存到sd卡中////////////////////////////////////////////////
	public void saveMyBitmap(Bitmap mBitmap,String bitName,String filePath)  {
		File f = new File(filePath);
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	////////////////////////显示toast的utils/////////////////
	public static void showToast(String showContent){
		Toast.makeText(getContext(),showContent,Toast.LENGTH_SHORT).show();
	}

	/**
	 * 将toast封装起来，连续点击时可以覆盖上一个
	 */
	public static void ShowUpToast(String text){
		Toast mToast = CbApplication.getToast();
		if (mToast == null) {
			mToast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
			mToast.show();
		}else {
			mToast.setText(text);
			mToast.show();
		}
	}
	
	//////////////////////////显示提示////////////////////////////////
	public static void showHint(final TextView textView,Activity mActivity,String showHint){
		boolean isShow = true;
		if (isShow) {
			textView.setText(showHint);
			textView.setVisibility(View.VISIBLE);
			UIUtils.getHandler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					textView.setVisibility(View.GONE);
				}
			}, 2000);
		}
	}
	
	//////////////////////根据条件显示toast////////////////////////////
	
	public static void isShowToast(String showContent){
		Boolean isShowToast = false;
		if (isShowToast) {
			
			UIUtils.showToast(showContent);
		}
		
	}


	// ///////////////////////////////获取视频缩略图////////////////////////
		public static Bitmap getVideoThumbnail(String filePath) {
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


	
}
