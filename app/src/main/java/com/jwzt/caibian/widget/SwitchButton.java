package com.jwzt.caibian.widget;

import com.jwzt.cb.product.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SwitchButton extends RelativeLayout {

	public SwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	private void init() {
		// TODO Auto-generated method stub
		View.inflate(getContext(), R.layout.custom_layout, this);
//		view_slide=findViewById(R.id.view_slide);
//		tv_left=(TextView) findViewById(R.id.tv_left);
//		tv_right=(TextView) findViewById(R.id.tv_right);
	}

}
