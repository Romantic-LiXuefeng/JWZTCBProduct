package com.jwzt.caibian.activity;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.util.UserToast;

/**
 * 绑定手机号码的界面
 * 
 * @author howie
 * 
 */
public class BindPhoneActivity extends BaseActivity implements OnClickListener {
	private String code;
	/**验证码是否正确*/
	private static final int IS_CODE_RIGHT=900;
	/**发送验证码*/
	private static final int SEND_VERIFY_CODE=800;
	/** 返回按钮 **/
	private ImageView iv_back;
	/** 头部标题 **/
	private TextView tv_name;
	/** 右上角的编辑按钮（在这个页面需要设置为隐藏） **/
	private TextView tv_edit;
	/** 手机号输入框 **/
	private EditText edit_phone;
	/** 验证码输入框 **/
	private EditText et_code;
	/** “发送验证码”的按钮 **/
	private TextView tv_send;
	/** 是否可以再次发送（在倒数计时期间为false） **/
	private boolean canSendAgain = true;
	/** 用户输入的电话号码的文本 **/
	private String textphone;
	/** 是否已经发送过验证码,true代表已经发送，false代表还没发送过 **/
	private boolean isCodeSent;
	/** 点击“发送验证码”之后距离下次可以发送的时间间隔 **/
	private static final int TIME_REMAINED = 60;
	/** 当成功发送验证码之后发送验证码按钮上显示的距离下次可以发送验证码的时间，随时间递减，60s,59s,58s。。。。 **/
	private int seconds_remained = TIME_REMAINED;
	/** 绑定手机号的按钮 **/
	private TextView tv_modify;


	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bind_layout);
		
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_edit = (TextView) findViewById(R.id.tv_edit);
		tv_name.setText("绑定手机号");
		iv_back.setOnClickListener(this);
		tv_edit.setVisibility(View.INVISIBLE);
		tv_send = (TextView) findViewById(R.id.tv_send);
		tv_send.setOnClickListener(this);
		// 手机号输入框
		edit_phone = (EditText) findViewById(R.id.et_phone_num);
		// 验证码输入框
		et_code = (EditText) findViewById(R.id.et_code);
		// 绑定手机号的按钮
		tv_modify = (TextView) findViewById(R.id.tv_modify);
		tv_modify.setOnClickListener(this);
		// 给手机号输入框添加监听
		edit_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 只有在输入完11位手机号之后“发送验证码”才变为可用的红色可点击状态
				tv_send.setBackgroundResource(s.length() == 11 && canSendAgain ? R.drawable.corner_5
						: R.drawable.corner_5_red);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		// 给验证码输入框添加监听
		et_code.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				// 如果已经发送过验证码，并且输入文字长度不为0
				if (isCodeSent && s.length() != 0) {
					// 设置为红色背景
					tv_modify.setBackgroundResource(R.drawable.corner_5);
				} else {
					// 设置为灰色背景
					tv_modify.setBackgroundResource(R.drawable.corner_5_red);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// 返回按钮
		case R.id.iv_back:
			
			finish();
			break;
		// 发送验证码
		case R.id.tv_send:
			
			
			break;
		// “立即绑定”按钮
		case R.id.tv_modify:
			textphone = edit_phone.getText().toString().trim();
			
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
