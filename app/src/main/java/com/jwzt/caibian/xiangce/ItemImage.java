package com.jwzt.caibian.xiangce;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author AnYufan
 *
 */
public class ItemImage implements Serializable{
	private String filepath; //图片路径
	private boolean result;  //用来表示是不是拍照
	private Bitmap bitmap;   //如果是拍照则用来存储
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}
