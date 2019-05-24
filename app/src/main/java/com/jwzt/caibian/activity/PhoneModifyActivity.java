package com.jwzt.caibian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UIUtils;

/**
 * 个人设置跳转的修改手机号码的Activity
 * 
 * @author howie
 * 
 */
public class PhoneModifyActivity extends BaseActivity implements OnClickListener {
	/** 标题 **/
	private TextView tv_name;
	/** 右上角的文字 **/
	private TextView tv_edit;
	/** 修改手机号的输入框 **/
	private EditText et_input;
	/** 返回按钮 **/
	private ImageView iv_back;
	private String phonenum,input_phonenum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_modify_layout);
		//从上个界面传过来的手机号
		phonenum=getIntent().getStringExtra("phonenum");
		
		findView();
	}
	
	private void findView(){
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_edit = (TextView) findViewById(R.id.tv_edit);
		et_input = (EditText) findViewById(R.id.et_input);
		if(IsNonEmptyUtils.isString(phonenum)){
			et_input.setText(phonenum);
		}
		tv_name.setText("修改手机号");
		tv_edit.setText("保存");
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		tv_edit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_edit:// 保存按钮
			input_phonenum = et_input.getText().toString().trim();
			if (!TextUtils.isEmpty(input_phonenum)) {
				   Intent in = new Intent();  
	               in.putExtra("phonenum",input_phonenum);  
	               setResult(6, in);  
				this.finish();
			}else {
				UIUtils.showToast("用户名不能为空");
			}
			break;
		case R.id.iv_back:// 返回按钮
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		}
	}

	@Override
	protected void initRequestOnSuccess(String result, int code, int biaoshi) {
	}
	@Override
	protected void initRequestOnStart(String result, int code, int biaoshi) {
	}
	@Override
	protected void initRequestOnFailure(String failure, int code, int biaoshi) {
	}
	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
	}
}
