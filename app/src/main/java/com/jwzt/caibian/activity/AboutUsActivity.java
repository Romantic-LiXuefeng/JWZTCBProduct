package com.jwzt.caibian.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jwzt.caibian.application.BaseActivity;
import com.jwzt.cb.product.R;
/**
 * 关于我们
 * @author howie
 *
 */
public class AboutUsActivity extends BaseActivity implements OnClickListener {
	private View iv_back;
	/**标题*/
	private TextView tv_titles;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		iv_back=findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		tv_titles=(TextView) findViewById(R.id.tv_titles);
		tv_titles.setText("关于我们");
		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.iv_back://返回按钮
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
