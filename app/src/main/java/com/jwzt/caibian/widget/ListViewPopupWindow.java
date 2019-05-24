package com.jwzt.caibian.widget;

import java.util.List;

import com.jwzt.cb.product.R;
import com.jwzt.caibian.adapter.AlarmClockPopListViewAdapter;
import com.jwzt.caibian.interfaces.OnAlarmSettingPopItemClickListener;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 更改闹钟声及个人设置界面用到的弹出listView的popupWindow
 * @author howie
 *
 */
public class ListViewPopupWindow extends PopupWindow {
	
	private ListView lv;
	/**
	 * 
	 * @param context
	 * @param onClickListener
	 * @param list
	 * @param isCentered listView中文字的对齐方式，false代表左对齐，true代表居中对齐
	 * @param onAlarmSettingPopItemClickListener 回调点击的索引号position的监听
	 */
	public ListViewPopupWindow(Context context,
			OnClickListener onClickListener,List<String> list, boolean isCentered, final OnAlarmSettingPopItemClickListener onAlarmSettingPopItemClickListener) {
		
		View contentView = View.inflate(context, R.layout.alarm_clock_layout,
				null);  

		this.setContentView(contentView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		/** 点击popupWindow范围以外的地方,让popupWindow消失 */
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());
		// 找到对应的控件
		lv=(ListView) contentView.findViewById(R.id.lv);
		lv.setAdapter(new AlarmClockPopListViewAdapter(context,list,isCentered));
		//限制listView的最大高度，最多显示5条
		/*//if(list.size()>5){
			RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) lv.getLayoutParams();
			layoutParams.height=DensityUtil.dip2px(context, 200);
			lv.setLayoutParams(layoutParams);
		//}
*/		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				onAlarmSettingPopItemClickListener.onItemClick(position);
			}
		});
		//解决弹出的选择列表在某些机型上不能点击的问题功能适配问题
		setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);

	}


}
