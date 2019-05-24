package com.jwzt.caibian.bean;

import java.io.Serializable;

/**
 * 封装视频的分辨率信息的bean
 * @author howie
 *
 */
public class ResolutionBean implements Serializable{
	private String  width;
	private String height;
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public ResolutionBean(String width, String height) {
		super();
		this.width = width;
		this.height = height;
	}
	
}
