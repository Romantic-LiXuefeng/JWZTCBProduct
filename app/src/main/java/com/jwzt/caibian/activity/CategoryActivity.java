package com.jwzt.caibian.activity;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.CategoryAdapter;
import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.caibian.application.CbApplication;
import com.jwzt.caibian.application.Configs;
import com.jwzt.caibian.bean.LoginBean;
import com.jwzt.caibian.bean.TypeBean;
import com.jwzt.caibian.util.IsNonEmptyUtils;
import com.jwzt.caibian.util.UserToast;
import com.jwzt.caibian.util.UIUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
/**
 * 分类界面
 * @author howie
 *
 */
public class CategoryActivity extends BaseActivity implements OnClickListener {
	private ListView lv;
	private View iv_back;
	private TextView tv_titles;
	private CbApplication application;
	private LoginBean mLoginBean;
	private List<TypeBean> mList;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				initView();
				break;
			}
		};
	};
	private CategoryAdapter mCategoryAdapter;
	private int listPosion = -1;
	private TextView tv_confirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		application=(CbApplication) getApplication();
		mLoginBean=application.getmLoginBean();
		showLoadingDialog(this, null, null);
		findViews();
		initData();
	}
	
	private void initData(){
		if(CbApplication.getNetType()!=-1){
			listPosion = -1;
			String typeUrl=String.format(Configs.typeUrl, mLoginBean.getUserID());
			System.out.println("typeUrl"+typeUrl);
			RequestData(typeUrl, Configs.typeCode, -1);
		}else{
			UserToast.toSetToast(UIUtils.getContext(), getString(R.string.please_check_net));
		}
	}
	
	private void initView(){
		tv_confirm = (TextView) findViewById(R.id.tv_confirm);
		final CategoryAdapter mCategoryAdapter = new CategoryAdapter(this,mList);
		lv.setAdapter(mCategoryAdapter);
		tv_confirm.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
							Intent intent=new Intent();
							intent.putExtra("tag", mCategoryAdapter.getCategoryName());
							setResult(5, intent);//4任意 
							finish();
					}
				});
		dismisLoadingDialog();
	}
	
	private void findViews() {
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("选择标签");
		iv_back=findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		lv=(ListView) findViewById(R.id.lv);
		lv.setOnItemClickListener(new OnItemClickListener() {

			

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3) {
				listPosion = position;
				// TODO Auto-generated method stub
				/*if(position!=0){//如果点击的不是其他
				 * 
*/				
				
				/*String tag="";
					tag = mList.get(position).getCategoryName();
					
					Intent intent=new Intent();
					intent.putExtra("tag", mList.get(position));
					setResult(5, intent);//4任意 
					finish();*/
					/*}else{//如果点击的是“其他”
					Intent intent_tag=new Intent(CategoryActivity.this,SelectTagActivity.class);
					startActivityForResult(intent_tag, 1);
					overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
				}
				*/
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(data!=null){
			setResult(resultCode, data);
			finish();
		}
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.iv_back:
			finish();
			overridePendingTransition(R.anim.push_left_out,R.anim.push_right_out);
			break;
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(CategoryActivity.this, NewScriptActivity.class);
			setResult(RESULT_OK, intent);
			finish();
			overridePendingTransition(R.anim.push_left_out,
					R.anim.push_right_out);
			return false;
		}
		return false;
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
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				dismisLoadingDialog();
			}
		});
		
	}
	@Override
	protected void initRequestOnCache(String result, int code, int biaoshi) {
		// TODO Auto-generated method stub
//		initDataParse(result, code);
	}
	
	/**
	 * 处理网络返回的数据并解析json
	 */
	private void initDataParse(String result, int code){
		if(code==Configs.typeCode){//分类
			JSONObject jsonObject=JSON.parseObject(result);
			String status=jsonObject.getString("status");
			if(status.equals("100")){//表示获取成功
				String data=jsonObject.getString("data");
				mList=JSON.parseArray(data,TypeBean.class);
				if(IsNonEmptyUtils.isList(mList)){
					mHandler.sendEmptyMessage(1);
				}
			}
		}
	}
}
