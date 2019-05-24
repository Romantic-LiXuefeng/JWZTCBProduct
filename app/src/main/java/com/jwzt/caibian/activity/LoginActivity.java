package com.jwzt.caibian.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.receiver.TagAliasOperatorHelper;
import com.jwzt.caibian.receiver.TagAliasOperatorHelper.TagAliasBean;
import com.jwzt.caibian.util.BDLocationUtils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.SharePreferenceUtils;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;

/**
 * 登录的类
 * 
 * @author jwzt
 *
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	private View tv_login;
	private EditText et_account, et_pwd;
	private CbApplication application;
	private LoginBean loginbean;
	private String message;
	private long mExitTime;
	String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
	Pattern p = Pattern.compile(regEx);
	public static int sequence = 1;

	private final int PERMISSION_REQUEST_CODE = 1;
	private int mNoPermissionIndex = 0;
	private final String[] permissionManifest = {
			Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION
	};

	private final int[] noPermissionTip = {
			R.string.no_camera_permission,
            R.string.no_record_audio_permission,
			R.string.no_read_phone_state_permission,
			R.string.no_write_external_storage_permission,
			R.string.no_read_external_storage_permission,
            R.string.no_location_permission
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (IsNonEmptyUtils.isString(message)) {
					UserToast.toSetToast(LoginActivity.this, message);
				} else {
					UserToast.toSetToast(LoginActivity.this, "登录失败");
				}
				break;
			case 1:
				// 登录成功保存用户名和密码
				save();
				UserToast.toSetToast(LoginActivity.this, "登录成功");
				activityJump();
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);
		application = (CbApplication) getApplication();
		if (!permissionCheck()) {
			if (Build.VERSION.SDK_INT >= 23) {
				ActivityCompat.requestPermissions(this, permissionManifest, PERMISSION_REQUEST_CODE);
			} else {
				showNoPermissionTip(getString(noPermissionTip[mNoPermissionIndex]));
				finish();
			}
		}


		findView();
	}

	private void findView() {
		tv_login = findViewById(R.id.tv_login);
		tv_login.setOnClickListener(this);
		et_account = (EditText) findViewById(R.id.et_account);
		et_pwd = (EditText) findViewById(R.id.et_pwd);

		et_account.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String content = et_account.getText().toString();
				System.out.println("contentcontent:" + content);
				if (IsNonEmptyUtils.isString(content)) {
					content.replaceAll("\n", "");
					Matcher m = p.matcher(content);
					if (m.find()) {
						et_account.setText(content.substring(0,
								content.length() - 1));
						et_account.setSelection(content.length() - 1);
						Toast.makeText(LoginActivity.this, "不允许输入特殊符号！",
								Toast.LENGTH_LONG).show();
					} else {
						if (content.length() >= 10) {
							UserToast
									.toSetToast(LoginActivity.this, "请在10个字以内");
						}
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		SharedPreferences preferences = getSharedPreferences("user",Context.MODE_PRIVATE);
		String name = preferences.getString("name", "");
		String pwd = preferences.getString("pwd", "");
		if(IsNonEmptyUtils.isString(name)&&IsNonEmptyUtils.isString(pwd)){
			et_account.setText(name);
			et_pwd.setText(pwd);
//			login(name, pwd);
		}
		
	}

	private void save() {
		SharedPreferences preferences = getSharedPreferences("user",Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		String name = et_account.getText().toString();
		String pwd = et_pwd.getText().toString();
		editor.putString("name", name);
		editor.putString("pwd", pwd);
		editor.commit();
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(LoginActivity.this, "再按一次退出采编",
						Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
		// if(keyCode==event.KEYCODE_BACK){
		// if(Configs.isExitLogin){
		// Configs.isExitLogin=false;
		// // finish();
		// System.exit(0);
		// android.os.Process.killProcess(android.os.Process.myPid());
		// // ActivityManager manager =
		// (ActivityManager)getSystemService(ACTIVITY_SERVICE); //获取应用程序管理器
		// // manager.killBackgroundProcesses(getPackageName()); //强制结束当前应用程序
		// // return false;
		// }else{
		// return super.onKeyDown(keyCode, event);
		// }
		// }
		// return false;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_login:
			String username = et_account.getText().toString().trim();
			String password = et_pwd.getText().toString().trim();
			if (username.isEmpty()) {
				UserToast.toSetToast(LoginActivity.this, "请输入用户名");
				return;
			}

			if (password.isEmpty()) {
				UserToast.toSetToast(LoginActivity.this, "请输入密码");
				return;
			}

			login(username, password);
			break;
		}
	}

	/**
	 * 登录成功后跳转到主页面
	 */
	private void activityJump() {
		String username = et_account.getText().toString().trim();
		String password = et_pwd.getText().toString().trim();
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		SharePreferenceUtils.mSharedPreferences = null;
		CbApplication.urerName = username;
		loginbean.setPassword(password);
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

	private void login(String username, String password) {
		if (CbApplication.getNetType() != -1) {
			showLoadingDialog(LoginActivity.this, "", "");
			String loginUrl = String.format(Configs.loginUrl, username.trim()
					.replaceAll("\n", ""), password);
			System.out.println("loginUrl:" + loginUrl);
			RequestData(loginUrl, Configs.loginCode, -1);
		} else {
			UserToast.toSetToast(UIUtils.getContext(),
					getString(R.string.please_check_net));
		}
	}

	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		System.out.println("loginresult:" + result);
		initDataParse(result, code);
	}

	@Override
	protected void initRequestOnStart(String result, int code, int biaoshi) {
	}

	@Override
	protected void initRequestOnFailure(String failure, int code, int biaoshi) {
		System.out.println();
	}

	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
		System.out.println("loginresult:" + result);
		// initDataParse(result, code);
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


	/**
	 * 权限检查（适配6.0以上手机）
	 */
	private boolean permissionCheck() {
		int permissionCheck = PackageManager.PERMISSION_GRANTED;
		String permission;
		for (int i = 0; i < permissionManifest.length; i++) {
			permission = permissionManifest[i];
			mNoPermissionIndex = i;
			if (PermissionChecker.checkSelfPermission(this, permission)
					!= PackageManager.PERMISSION_GRANTED) {
				permissionCheck = PackageManager.PERMISSION_DENIED;
			}
		}
		if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 没有权限的提醒
	 *
	 * @param tip
	 */
	private void showNoPermissionTip(String tip) {
		Toast.makeText(this, tip, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case PERMISSION_REQUEST_CODE:
				for (int i = 0; i < permissions.length; i++) {
					if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
						int toastTip = noPermissionTip[i];
						mNoPermissionIndex = i;
						if (toastTip != 0) {
							showToast(LoginActivity.this, toastTip);
							finish();
						}
					}
				}
				break;
		}
	}

	public static void showToast(final Context context, final int resID) {
		if (context != null) {
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(context, context.getString(resID), Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}
