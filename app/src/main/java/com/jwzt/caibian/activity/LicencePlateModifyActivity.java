package com.jwzt.caibian.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.util.AnimationUtils;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UIUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.widget.LicencePickPopupWindow;

/**
 * 个人设置跳转的修改用户车牌号的Activity
 * 
 * @author howie
 * 
 */
public class LicencePlateModifyActivity extends BaseActivity implements
		OnClickListener {

	/**车牌号的前2位*/
	private String  licenceInfo="请选择";
	/**显示车牌号前两位的textview*/
	private TextView tv_car_number;
	/**popupWindow弹出来的时候背景变暗的view*/
	private View view_mask;
	/**布局最外层的根布局*/
	private RelativeLayout rl_root;
	/** 标题 **/
	private TextView tv_name;
	/** 右上角的文字 **/
	private TextView tv_edit;
	/** 修改个人信息的输入框 **/
	private EditText et_input;
	/** 返回按钮 **/
	private ImageView iv_back;
	/**用户输入的车牌号**/
	private String carnum,startCarNum,input_licence;
	/**选择车牌号后5位的popupWindow**/
	private LicencePickPopupWindow popwindow;
	/**车牌号的textview*/
	private TextView ftv_car_number;
	CbApplication	alication;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.licenceplate_modify_layout);
		alication = (CbApplication) getApplication();
		carnum=getIntent().getStringExtra("carnum");
		
		findView();
		
		

	}
	
	private void findView(){
		// 头部的标题
		tv_name = (TextView) findViewById(R.id.tv_name);
		// 右上角的“保存”
		tv_edit = (TextView) findViewById(R.id.tv_edit);
		// 修改车牌号后五位的输入框
		et_input = (EditText) findViewById(R.id.et_input);
		if(IsNonEmptyUtils.isString(carnum)){
			et_input.setText(carnum);
		}
		
		//车牌号的textview
		ftv_car_number=(TextView) findViewById(R.id.ftv_car_number);
		ftv_car_number.setOnClickListener(this);
		//显示车牌号前2位的textview
		tv_car_number=(TextView) findViewById(R.id.tv_car_num);
		rl_root=(RelativeLayout) findViewById(R.id.rl_root);
		//popupWindow弹出来的时候背景变暗的view
		view_mask=findViewById(R.id.view_mask);
		// 传过来的车牌号
		tv_name.setText("修改车牌号");
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
			input_licence = et_input.getText().toString().trim();
			startCarNum=tv_car_number.getText().toString().trim();
			// 判断车牌号是否为空
			if (TextUtils.isEmpty(input_licence)) {
				UserToast.toSetToast(LicencePlateModifyActivity.this, "车牌号不能为空！");
			} else if(input_licence.length()<5){
				UserToast.toSetToast(LicencePlateModifyActivity.this, "车牌号尾号不足5位！");
			}else{
				String carNumString=startCarNum+" "+input_licence;
				if (!TextUtils.isEmpty(carNumString)) {
					   Intent in = new Intent();  
		               in.putExtra("carnum",carNumString);  
		               setResult(7, in);  
		               this.finish();
				}
			}
			break;
		// 返回按钮
		case R.id.iv_back:
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
			//选择车牌号的前两位
		case R.id.ftv_car_number:
			if(popwindow!=null&&popwindow.isShowing()){
				popwindow.dismiss();
				AnimationUtils.hideAlpha(view_mask);
			}else{
				//隐藏输入法
				InputMethodManager inputManager =  
	            (InputMethodManager)et_input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(et_input.getWindowToken(), 0);
				
				if(popwindow==null){
					popwindow=new LicencePickPopupWindow(LicencePlateModifyActivity.this, LicencePlateModifyActivity.this);
				}
				popwindow.showAtLocation(rl_root, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				AnimationUtils.showAlpha(view_mask);
				popwindow.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						AnimationUtils.hideAlpha(view_mask);
					}
				});
			}
			
			break;
			//弹出的popupWindow里面的“确定”按钮
		case R.id.ftv_place_confirm:
			licenceInfo = popwindow.getLicenceInfo();
			if(licenceInfo.contains("请选择")){
				UserToast.toSetToast(LicencePlateModifyActivity.this, "请选择准确的车牌前两位号码");
				popwindow.dismiss();
				return;
			}
			tv_car_number.setText(licenceInfo);
			popwindow.dismiss();
			break;
			//弹出的popupWindow里面的“取消”按钮
		case R.id.ftv_place_cancel:
			popwindow.dismiss();
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
