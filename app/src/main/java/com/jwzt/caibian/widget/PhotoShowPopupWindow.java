package com.jwzt.caibian.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.GridAdapter;

public class PhotoShowPopupWindow extends PopupWindow {
	private TextView  tv_cancel;//发送
	/**取消按钮**/
	private TextView tv_no_send;//取消
    private GridView view;
	public PhotoShowPopupWindow(Context context, OnClickListener onClickListener) {
		View contentView = View.inflate(context, R.layout.take_photo_popup_show, null);
		
		this.setContentView(contentView);
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		/**点击popupWindow范围以外的地方,让popupWindow消失*/
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());
		//找到对应的控件
		view =  (GridView) contentView.findViewById(R.id.noScrollgridview);
		tv_cancel =  (TextView) contentView.findViewById(R.id.tv_cancel_show);
		tv_no_send=(TextView) contentView.findViewById(R.id.tv_no);
		view.setSelector(new ColorDrawable(Color.TRANSPARENT));
		GridAdapter	adapter = new GridAdapter(context);
		//adapter.update();
		view.setAdapter(adapter);
		
		//view.setOnClickListener(onClickListener);
		//tv_cancel.setOnClickListener(onClickListener);
		tv_cancel.setOnClickListener(onClickListener);
		tv_no_send.setOnClickListener(onClickListener);
		//加入动画
		this.setAnimationStyle(R.style.AnimBottom);

	}
	
	
	

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}
	
}
