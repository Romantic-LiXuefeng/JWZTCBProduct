package com.jwzt.caibian.util;

import com.jwzt.caibian.application.CbApplication;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 专门访问和设置SharePreference的工具类, 保存和配置一些设置信息
 * 
 * @author Kevin
 * 
 */
public class SharePreferenceUtils {

	public static SharedPreferences mSharedPreferences;
	
	public void putBoolean(Context ctx, String key, boolean value) {
		if (mSharedPreferences == null) {
			mSharedPreferences = ctx.getSharedPreferences(getUserName(),
					Context.MODE_PRIVATE);
		}

		mSharedPreferences.edit().putBoolean(key, value).commit();
	}

	public boolean getBoolean(Context ctx, String key,
			boolean defaultValue) {
		if (mSharedPreferences == null) {
			mSharedPreferences = ctx.getSharedPreferences(getUserName(),
					Context.MODE_PRIVATE);
		}

		return mSharedPreferences.getBoolean(key, defaultValue);
	}

	public void putString(Context ctx, String key, String value) {
		if (mSharedPreferences == null) {
			mSharedPreferences = ctx.getSharedPreferences(getUserName(),
					Context.MODE_PRIVATE);
		}

		mSharedPreferences.edit().putString(key, value).commit();
	}

	public String getString(Context ctx, String key, String defaultValue) {
		if (mSharedPreferences == null) {
			mSharedPreferences = ctx.getSharedPreferences(getUserName(),
					Context.MODE_PRIVATE);
		}

		return mSharedPreferences.getString(key, defaultValue);
	}
	
	public void putInt(Context ctx, String key, int value) {
		if (mSharedPreferences == null) {
			mSharedPreferences = ctx.getSharedPreferences(getUserName(),
					Context.MODE_PRIVATE);
		}

		mSharedPreferences.edit().putInt(key, value).commit();
	}

	public int getInt(Context ctx, String key, int defaultValue) {
		if (mSharedPreferences == null) {
			mSharedPreferences = ctx.getSharedPreferences(getUserName(),
					Context.MODE_PRIVATE);
		}

		return mSharedPreferences.getInt(key, defaultValue);
	}
	
	
	//得到用户的信息
		public String getUserName(){
			/*if (mSharedPreferences == null) {
				mSharedPreferences = UIUtils.getContext().getSharedPreferences("userName",Context.MODE_PRIVATE);
			}

			return mSharedPreferences.getString("userName", "textUserName");*/
			if (CbApplication.urerName == null) {
				return "texturerName";
			}else {
				return CbApplication.urerName;
			}
			
		}
		//存进用户的信息
		public void putUserName(Context ctx, String key, String userName){
			if (mSharedPreferences == null) {
				mSharedPreferences = ctx.getSharedPreferences("userName",Context.MODE_PRIVATE);
			}

			mSharedPreferences.edit().putString(key, userName).commit();
		}
		
		


}
