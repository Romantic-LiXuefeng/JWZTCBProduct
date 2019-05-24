package com.jwzt.caibian.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.receiver.TagAliasOperatorHelper;
import com.jwzt.caibian.receiver.TagAliasOperatorHelper.TagAliasBean;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.SharePreferenceUtils;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

/**
 * @author Administrator
 * 启动页面
 *
 */
public class LaunchImageActivity extends BaseActivity {
	
	private CbApplication application;
	private LoginBean loginbean;
	private String name,pwd,message;
	public static int sequence = 1;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Intent intent = new Intent(LaunchImageActivity.this,LoginActivity.class);
				startActivity(intent);
				LaunchImageActivity.this.finish();
				break;
			case 1:
				// 登录成功保存用户名和密码
				save();
				UserToast.toSetToast(LaunchImageActivity.this, "登录成功");
				activityJump();
				break;
			}
		}

	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_launch_image);
		application=(CbApplication)getApplication();
		SharedPreferences preferences = getSharedPreferences("user",Context.MODE_PRIVATE);
		name = preferences.getString("name", "");
		pwd = preferences.getString("pwd", "");
		if(IsNonEmptyUtils.isString(name)&&IsNonEmptyUtils.isString(pwd)){
			login(name, pwd);
		}else{
			UIUtils.getHandler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Intent intent = new Intent(LaunchImageActivity.this,LoginActivity.class);
					startActivity(intent);
					LaunchImageActivity.this.finish();
				}
			}, 3000);
		}
	}
	
	private void login(String username, String password) {
		if (CbApplication.getNetType() != -1) {
			showLoadingDialog(LaunchImageActivity.this, "", "");
			String loginUrl = String.format(Configs.loginUrl, username.trim().replaceAll("\n", ""), password);
			System.out.println("loginUrl:" + loginUrl);
			RequestData(loginUrl, Configs.loginCode, -1);
		} else {
			UserToast.toSetToast(UIUtils.getContext(),getString(R.string.please_check_net));
		}
	}
	
	
	private void save() {
		SharedPreferences preferences = getSharedPreferences("user",Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("name", name);
		editor.putString("pwd", pwd);
		editor.commit();
	};
	
	/**
	 * 登录成功后跳转到主页面
	 */
	private void activityJump() {
		Intent intent = new Intent(LaunchImageActivity.this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		SharePreferenceUtils.mSharedPreferences = null;
		CbApplication.urerName = name;
		loginbean.setPassword(pwd);
		application.setmLoginBean(loginbean);
		boolean isAliasAction = true;
		int action = 2;
		// 设置Alines
		TagAliasBean tagAliasBean = new TagAliasBean();
		tagAliasBean.action = action;
		sequence++;
		tagAliasBean.alias = loginbean.getUserID();
		tagAliasBean.isAliasAction = isAliasAction;
		TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(), sequence, tagAliasBean);

		finish();
	}
	
	/**
	 * 处理网络返回的数据并解析
	 * 
	 * @param result
	 * @param code
	 */
	private void initDataParse(String result, int code) {
		if (code == Configs.loginCode) {
			JSONObject jsonObject = JSON.parseObject(result);
			String status = jsonObject.getString("status");
			if (status.equals("100")) {// 表示登录成功
				String dataresult = jsonObject.getString("data");
				loginbean = JSON.parseObject(dataresult, LoginBean.class);
				if (loginbean != null) {
					mHandler.sendEmptyMessage(1);
				} else {
					mHandler.sendEmptyMessage(0);
				}
			} else {
				message = jsonObject.getString("message");
				mHandler.sendEmptyMessage(0);
			}
		}
		dismisLoadingDialog();
	}
	

	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		initDataParse(result, code);
	}

	@Override
	protected void initRequestOnStart(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void initRequestOnFailure(String failure, int code, int biaoshi) {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(0);
	}

	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		
	}
	
}
