package com.jwzt.caibian.view;



import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;
/**
 * 可以下拉刷新的scrollView
 * @author howie
 *
 */
public class PullableScrollView extends ScrollView implements Pullable {
	private PullableScrollViewListener scrollViewListener = null;
	private boolean isPullDownable=true;// 是否可以下拉刷新
	private boolean isPullUpable=true;//是否可以上拉刷新
	
	

	public void setPullUpable(boolean isPullUpable) {
		this.isPullUpable = isPullUpable;
	}


	public void setPullDownable(boolean isPullable) {
		this.isPullDownable = isPullable;
	}
	public PullableScrollView(Context context) {
		super(context);
	}

	public PullableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setScrollViewListener(PullableScrollViewListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		setPullDownable(true);
		
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
		}
	}

	@Override
	public boolean canPullDown() {  
		if (getScrollY() == 0&&isPullDownable) {//如果滑动y=0处并且isPullable为true就允许下拉刷新
			return true;
		}else {
			return false;
		}
	}

	@Override
	public boolean canPullUp() {
		if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight())&&isPullUpable)
			return true;
		else
			return false;
	}

}
