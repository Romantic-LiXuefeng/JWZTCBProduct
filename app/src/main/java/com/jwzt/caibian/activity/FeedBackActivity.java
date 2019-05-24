package com.jwzt.caibian.activity;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.application.Configs;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class FeedBackActivity extends Activity implements OnClickListener {

	private EditText suggestionContent;
    private String userid;
    private String messageid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);	
		initView();
		initData();
	}

	
	
	private void initData() {
		userid=getIntent().getStringExtra("userid");
		messageid=getIntent().getStringExtra("messageid");
		
	}

	
	
	private void initView() {
		TextView title=(TextView)this.findViewById(R.id.tv_name);
		title.setText("回复");
		TextView commit=(TextView)this.findViewById(R.id.tv_edit);
		commit.setText("提交");
		commit.setOnClickListener(this);
		suggestionContent = (EditText)this.findViewById(R.id.et_user_suggestion);
		this.findViewById(R.id.iv_back).setOnClickListener(this);
	}



	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.iv_back:
			this.finish();
			break;
		case R.id.tv_edit:
			String content=suggestionContent.getText().toString();
			String infoListUrl=String.format(Configs.huifuUrl,userid,messageid,content);
			if (content!=null&&!"".equals(content)){
				XutilsGet(infoListUrl);
			}else{
				Toast.makeText(this,"请输入您的回复！", Toast.LENGTH_SHORT).show();
			}

			break;

		default:
			break;
		}
		
		
	}



	private void XutilsGet( String url){
		RequestParams params = new RequestParams();
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(1000); //设置超时时间   10s
		http.send(HttpRequest.HttpMethod.GET, url ,params,new RequestCallBack<String>(){
			@Override
			public void onLoading(long total, long current, boolean isUploading) {

			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Toast.makeText(FeedBackActivity.this,"回复提交成功！",Toast.LENGTH_SHORT).show();
				finish();
			}

			@Override
			public void onStart() {
				
			}

			@Override
			public void onFailure(HttpException error, String msg) {

			}
		});
	}





}
