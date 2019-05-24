package com.jwzt.caibian.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.GridAdapter;
/**
 * 保存视频信息的popupwindow
 * @author howie
 *
 */
public class SaveVideoPopupWindow extends PopupWindow {
	/*private TextView  tv_cancel;//发送
	*//**取消按钮**//*
	private TextView tv_no_send;//取消
    private GridView view;*/
	/**显示位置信息*/
	private TextView tv_set_location;
	/**视频名称的输入框*/
	private EditText et_video_name;
	public SaveVideoPopupWindow(Context context, OnClickListener onClickListener) {
		View contentView = View.inflate(context, R.layout.layout_save_video_info, null);
		tv_set_location=(TextView) contentView.findViewById(R.id.tv_set_location);
		et_video_name=(EditText) contentView.findViewById(R.id.et_video_name);
		//定位按钮
		contentView.findViewById(R.id.iv_locaiton).setOnClickListener(onClickListener);//定位按钮
//		contentView.findViewById(R.id.tv_set_location).setOnClickListener(onClickListener);
		//保存按钮
		contentView.findViewById(R.id.tv_save).setOnClickListener(onClickListener);
		//上传按钮
		contentView.findViewById(R.id.tv_upload).setOnClickListener(onClickListener);
		this.setContentView(contentView);
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		/**点击popupWindow范围以外的地方,让popupWindow消失*/
//		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());
		//找到对应的控件
	/*	view =  (GridView) contentView.findViewById(R.id.noScrollgridview);
		tv_cancel =  (TextView) contentView.findViewById(R.id.tv_cancel_show);
		tv_no_send=(TextView) contentView.findViewById(R.id.tv_no);
		view.setSelector(new ColorDrawable(Color.TRANSPARENT));
		GridAdapter	adapter = new GridAdapter(context);
		//adapter.update();
		view.setAdapter(adapter);
		
		//view.setOnClickListener(onClickListener);
		//tv_cancel.setOnClickListener(onClickListener);
		tv_cancel.setOnClickListener(onClickListener);
		tv_no_send.setOnClickListener(onClickListener);*/
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
	
	/**
	 * 获取用户输入的视频的名称
	 * @return
	 */
	public String getVideoName(){
		return et_video_name.getText().toString().trim();
	}
	
}
