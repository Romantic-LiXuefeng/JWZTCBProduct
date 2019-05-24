package com.jwzt.caibian.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;



import com.jwzt.cb.product.R;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UIUtils;


/**
 * 个人设置跳转的修改用户的年龄的Activity
 * 
 * @author howie
 * 
 */
public class AgeModifyActivity extends Activity implements
		OnClickListener {
	/** 标题 **/
	private TextView tv_name;
	/** 右上角的文字 **/
	private TextView tv_edit;
	/** 修改个人信息的输入框 **/
	private EditText et_input;
	/** 返回按钮 **/
	private ImageView iv_back;
	/**用户输入的年龄**/
	private String age,input_age;
	

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.age_modify_layout);
		age=getIntent().getStringExtra("age");
		
		findView();
	}
	
	private void findView(){
		// 头部的标题
		tv_name = (TextView) findViewById(R.id.tv_name);
		// 右上角的“保存”
		tv_edit = (TextView) findViewById(R.id.tv_edit);
		// 修改用户年龄的输入框
		et_input = (EditText) findViewById(R.id.et_input);
		if(IsNonEmptyUtils.isString(age)){
			et_input.setText(age);
		}
		
		tv_name.setText("修改年龄");
		tv_edit.setText("保存");
		// 返回
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		tv_edit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 右上角的“保存”按钮
		case R.id.tv_edit:
			input_age = et_input.getText().toString().trim();
			if (!TextUtils.isEmpty(input_age)) {// 判断用户输入的年龄是否为空
				   Intent in = new Intent();  
	               in.putExtra("age",input_age);  
	               setResult(8, in);  
	               this.finish();
			}else {
				UIUtils.showToast("年龄不能为空");
			}
			break;
		// 返回按钮
		case R.id.iv_back:
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		}

	}

	

	

	
	
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	
}
