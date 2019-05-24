package com.jwzt.caibian.activity;

import java.util.ArrayList;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.FragmentTabAdapter;
import com.jwzt.caibian.fragment.UploadedFragment;
import com.jwzt.caibian.fragment.UploadingFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.RadioGroup;
/**
 * �ϴ�
 * @author howie
 *
 */
public class UploadActivity extends FragmentActivity {
	private ArrayList<Fragment> fragments;
	private RadioGroup rg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		rg=(RadioGroup) findViewById(R.id.rg);
		rg.check(R.id.rb0);
		fragments=new ArrayList<Fragment>();
//		fragments.add(new UploadingFragment());
		fragments.add(new UploadedFragment());
		new FragmentTabAdapter(UploadActivity.this, fragments, R.id.fl, rg);
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			
			
			finish();
			overridePendingTransition(R.anim.push_left_out,
					R.anim.push_right_out);
			return false;
		}
		return false;
	}
}
