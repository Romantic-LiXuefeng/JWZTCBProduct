package com.jwzt.caibian.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
/**
 * 用于嵌套在listview中的gridview
 * @author howie
 *
 */
public class NoScrollGridView extends GridView {
	
	public NoScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //核心在此
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec+50);
    }
	

}
