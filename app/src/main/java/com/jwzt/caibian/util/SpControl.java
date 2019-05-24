package com.jwzt.caibian.util;

import android.content.Context;

	public class SpControl {
		
		private SharePreferenceUtils mPreferenceUtils;
		Context ctx ;
		private static SpControl mSpControl = null;
		public SpControl(){
			mPreferenceUtils = new SharePreferenceUtils();
			ctx = UIUtils.getContext();
		}
		
		public static SpControl getSpControl(){
			if (mSpControl == null) {
				mSpControl = new SpControl();
			}
			return mSpControl;
		}
		
		public void putBoolean(String key, boolean value) {
				mPreferenceUtils.putBoolean(ctx, key, value);
		}
		
		public boolean getBoolean(String key,
				boolean defaultValue) {
				return mPreferenceUtils.getBoolean(ctx, key, defaultValue);
			}
		
		public void putString(String key, String value) {
				mPreferenceUtils.putString(ctx, key, value);
			}
		
		public String getString(String key, String defaultValue) {
				return mPreferenceUtils.getString(ctx, key, defaultValue);
			}
		
		public void putInt(String key, int value) {
				mPreferenceUtils.putInt(ctx, key, value);
			}
		
		public int getInt(String key, int defaultValue) {
				return mPreferenceUtils.getInt(ctx, key, defaultValue);
			}
	
	
}


















	