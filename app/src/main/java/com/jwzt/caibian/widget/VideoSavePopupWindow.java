package com.jwzt.caibian.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.jwzt.cb.product.R;
/**
 * 保存录制的视频的时候弹出的填写视频信息的PopupWindow
 * @author howie
 *
 */
public class VideoSavePopupWindow extends PopupWindow {
	
	public VideoSavePopupWindow(Context context) {
		View contentView = View.inflate(context, R.layout.layout_save_video_info,null);  
		this.setContentView(contentView);
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		/** 点击popupWindow范围以外的地方,让popupWindow消失 */
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());
		// 找到对应的控件
		
		//解决弹出的选择列表在某些机型上不能点击的问题功能适配问题
		setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);

	}


}
