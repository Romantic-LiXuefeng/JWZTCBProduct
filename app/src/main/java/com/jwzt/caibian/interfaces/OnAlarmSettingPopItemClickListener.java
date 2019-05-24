package com.jwzt.caibian.interfaces;

/**
 * 闹钟设置界面点击“闹钟声”、“播放电台”、“重复”弹出的PopupWindow，
 * 当点击了listView的某一项的时候的回调接口
 * @author howie
 * 
 */
public interface OnAlarmSettingPopItemClickListener {
	/**
	 * 点击的索引号
	 * @param position
	 */
	void onItemClick(int position);
}
