package com.jwzt.caibian.activity;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UserToast;

/**
 * 个人设置跳转的修改用户的兴趣爱好的Activity
 * 
 * @author howie
 * 
 */
public class HobbyActivity extends BaseActivity implements
		OnClickListener {
	/** 标题 **/
	private TextView tv_name;
	/** 右上角的文字 **/
	private TextView tv_edit;
	/** 修改个人信息的输入框 **/
	private EditText et_input;
	/** 返回按钮 **/
	private ImageView iv_back;
	/**用户输入的兴趣爱好**/
	private String hobby,input_hobby;
	CbApplication	alication;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		alication = (CbApplication) getApplication();
		//获取保存在全局的loginResultBean
		setContentView(R.layout.hobby_modify_layout);
		hobby=getIntent().getStringExtra("hobby");
		
		findView();
		

	}
	
	private void findView(){
		// 头部的标题
		tv_name = (TextView) findViewById(R.id.tv_name);
		// 右上角的“保存”
		tv_edit = (TextView) findViewById(R.id.tv_edit);
		// 修改用户兴趣爱好的输入框
		et_input = (EditText) findViewById(R.id.et_input);
		// 传过来的用户的兴趣爱好
		if(IsNonEmptyUtils.isString(hobby)){
			et_input.setText(hobby);
		}

		tv_name.setText("修改兴趣爱好");
		tv_edit.setText("保存");
		// 返回
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		tv_edit.setOnClickListener(this);
		
		et_input.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				String content=et_input.getText().toString();
				if(IsNonEmptyUtils.isString(content)){
					if(content.length()>=10){
						UserToast.toSetToast(HobbyActivity.this, "爱好请在20个字以内");
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
			input_hobby = et_input.getText().toString().trim();
			// 判断用户输入的兴趣爱好是否为空
			if (TextUtils.isEmpty(input_hobby)) {
				UserToast.toSetToast(HobbyActivity.this, "兴趣爱好不能为空！");
			} else {
				Intent intent = new Intent();
				intent.putExtra("hobby", input_hobby);
				setResult(9, intent);
				finish();
				overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
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
