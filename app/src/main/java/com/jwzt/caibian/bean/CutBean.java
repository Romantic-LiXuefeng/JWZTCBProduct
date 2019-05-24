package com.jwzt.caibian.bean;
/**
 * 用来记录视频裁剪的位置的bean
 * @author howie
 *
 */
public class CutBean {
	/**半个屏幕的距离加上滑动的距离*/
	private float cut;
	/**打切点的时刻的播放进度*/
	private int progress;
	/***切点所在的item*/
	private int item;
	public float getCut() {
		return cut;
	}
	public void setCut(float cut) {
		this.cut = cut;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public int getItem() {
		return item;
	}
	public void setItem(int item) {
		this.item = item;
	}
	public CutBean(int scrollDistance, int progress) {
		super();
		this.cut = scrollDistance;
		this.progress = progress;
	}
}
