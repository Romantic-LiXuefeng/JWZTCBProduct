package com.jwzt.caibian.interfaces;


import com.jwzt.caibian.view.PullableScrollView;

/**
 * pullableScrollView快速滑动监听
 * @author hly
 *
 */
public interface PullableScrollViewListener {
	void onScrollChanged(PullableScrollView scrollView, int x, int y, int oldx, int oldy);
}
