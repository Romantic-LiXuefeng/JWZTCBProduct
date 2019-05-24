package com.jwzt.caibian.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwzt.caibian.application.GlobalContants;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.StringUtils;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;


/**
 * 个人设置跳转的修改用户名信息的Activity
 * 
 * @author howie
 * 
 */
public class UserNameModifyActivity extends BaseActivity implements OnClickListener {
	/**用户名是否已经存在*/
	private static final int IS_NAME_EXIST=100;
	/** 标题 **/
	private TextView tv_name;
	/** 右上角的文字 **/
	private TextView tv_edit;
	/** 修改个人信息的输入框 **/
	private EditText et_input;
	/** 返回按钮 **/
	private ImageView iv_back;
	/**用户输入的用户名**/
	private String username,input_userName;
	private CbApplication amapps;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//获取保存在全局的loginResultBean
		amapps=(CbApplication)getApplication();
		setContentView(R.layout.username_modify_layout);
		username=getIntent().getStringExtra("username");
		
		findView();
		
	}
	
	private void findView(){
		tv_name = (TextView) findViewById(R.id.tv_name);// 头部的标题
		tv_edit = (TextView) findViewById(R.id.tv_edit);// 右上角的“保存”
		et_input = (EditText) findViewById(R.id.et_input);// 修改用户名的输入框
		iv_back = (ImageView) findViewById(R.id.iv_back);// 返回
		if(IsNonEmptyUtils.isString(username)){// 传过来的用户名
			et_input.setText(username);
		}
		
		tv_name.setText("修改用户名");
		tv_edit.setText("保存");
		
		iv_back.setOnClickListener(this);
		tv_edit.setOnClickListener(this);
		
		et_input.requestFocus();
		et_input.setFocusable(true);  
		et_input.setFocusableInTouchMode(true);  
		InputMethodManager inputManager = (InputMethodManager)et_input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
		inputManager.showSoftInputFromInputMethod(et_input.getWindowToken(), 0);
		
		et_input.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				String content=et_input.getText().toString();
				if(IsNonEmptyUtils.isString(content)){
					if(content.length()>=10){
						UserToast.toSetToast(UserNameModifyActivity.this, "用户名请在10个字以内");
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 右上角的“保存”按钮
		case R.id.tv_edit:
			input_userName = et_input.getText().toString().trim();
			if (!TextUtils.isEmpty(input_userName)) {
				mSharePreferenceUtils.putString(getApplicationContext(), GlobalContants.USERNAMENICKNAME, input_userName);
				   Intent in = new Intent();  
	               in.putExtra("username",input_userName);  
	               setResult(5, in);  
				this.finish();
			}else {
				UIUtils.showToast("用户名不能为空");
			}
			break;
		// 返回按钮
		case R.id.iv_back:
			finish();
			overridePendingTransition(R.anim.push_left_out,
					R.anim.push_right_out);
			break;
		}

	}



	

	
	
	
	

	@Override
	protected void onResume() {
		super.onResume();
		amapps.setContext(this);

	}

	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initRequestOnStart(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initRequestOnFailure(String failure, int code, int biaoshi) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
		
	}

}
