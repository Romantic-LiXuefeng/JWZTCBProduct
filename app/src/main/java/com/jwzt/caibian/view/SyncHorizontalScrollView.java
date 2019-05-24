package com.jwzt.caibian.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

public class SyncHorizontalScrollView extends HorizontalScrollView {

	private View view;
	private ImageView leftImage;
	private ImageView rightImage;
	private int windowWitdh = 0;
	private Activity mContext;

	public void setSomeParam(View view, ImageView leftImage,
			ImageView rightImage, Activity context) {
		//if(this.mContext!=null){
		this.mContext = context;
		this.view = view;
		this.leftImage = leftImage;
		this.rightImage = rightImage;
		/**获取屏幕大小**/
		DisplayMetrics dm = new DisplayMetrics();
		
		this.mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
		windowWitdh = dm.widthPixels;
		//}
	}

	public SyncHorizontalScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SyncHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	// 显示和隐藏左右两边的箭头
	public void showAndHideArrow() {
		if (!mContext.isFinishing() && view != null) {
			this.measure(0, 0);
			if (windowWitdh >= this.getMeasuredWidth()) {
				leftImage.setVisibility(View.GONE);
				rightImage.setVisibility(View.GONE);
			} else {
				if (this.getLeft() == 0) {
					leftImage.setVisibility(View.GONE);
					rightImage.setVisibility(View.VISIBLE);
				} else if (this.getRight() == this.getMeasuredWidth()
						- windowWitdh) {
					leftImage.setVisibility(View.VISIBLE);
					rightImage.setVisibility(View.GONE);
				} else {
					leftImage.setVisibility(View.VISIBLE);
					rightImage.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (!mContext.isFinishing() && view != null && rightImage != null
				&& leftImage != null) {
			if (view.getWidth() <= windowWitdh) {
				leftImage.setVisibility(View.GONE);
				rightImage.setVisibility(View.GONE);
			} else {
				if (l == 0) {
					leftImage.setVisibility(View.GONE);
					rightImage.setVisibility(View.VISIBLE);
				} else if (view.getWidth() - l == windowWitdh) {
					leftImage.setVisibility(View.VISIBLE);
					rightImage.setVisibility(View.GONE);
				} else {
					leftImage.setVisibility(View.VISIBLE);
					rightImage.setVisibility(View.VISIBLE);
				}
			}
		}
	}
}
