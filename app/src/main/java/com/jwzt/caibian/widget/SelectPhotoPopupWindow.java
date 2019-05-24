package com.jwzt.caibian.widget;


import com.jwzt.cb.product.R;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SelectPhotoPopupWindow extends PopupWindow {
	private TextView tv_take_new, tv_from_album, tv_cancel;//“拍照”，“来自相册”和“取消”

	public SelectPhotoPopupWindow(Context context, OnClickListener onClickListener) {
		View contentView = View.inflate(context, R.layout.photo_popup_layout, null);
		
		this.setContentView(contentView);
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		/**点击popupWindow范围以外的地方,让popupWindow消失*/
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());
		//找到对应的控件
		tv_take_new =  (TextView) contentView.findViewById(R.id.tv_take_new);
		tv_from_album =  (TextView) contentView.findViewById(R.id.tv_from_album);
		tv_cancel =  (TextView) contentView.findViewById(R.id.tv_cancel);
		
		
		tv_take_new.setOnClickListener(onClickListener);
		tv_from_album.setOnClickListener(onClickListener);
		tv_cancel.setOnClickListener(onClickListener);
		//加入动画
		this.setAnimationStyle(R.style.AnimBottom);

	}
	
	
	
	
	
}
