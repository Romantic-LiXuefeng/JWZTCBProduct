package com.jwzt.caibian.listener;

import com.jwzt.caibian.widget.WheelView;

/**
 * Created by 我的电脑 on 2019/1/12.
 */

public interface  OnWheelClickedListener {
    /**
     * Callback method to be invoked when current item clicked
     * @param wheel the wheel view
     * @param itemIndex the index of clicked item
     */
    void onItemClicked(WheelView wheel, int itemIndex);
}
