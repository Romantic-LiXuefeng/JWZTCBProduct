package com.jwzt.caibian.bean;

import android.graphics.Bitmap;

public class BitmapBean {
	public static final int TYPE_NORMAL=0;
	public static final int TYPE_UNNORMAL=1;
	/**每一个bean对应的开始的时间戳*/
	private int timeStamp;
	
	public int getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}
	private Bitmap bitmap;
	private int type;
	private int imgWidth;
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getImgWidth() {
		return imgWidth;
	}
	public void setImgWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}
	public BitmapBean(Bitmap bitmap, int type) {
		super();
		this.bitmap = bitmap;
		this.type = type;
	}
	
	
}
