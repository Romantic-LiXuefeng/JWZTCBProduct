package com.jwzt.caibian.bean;
/**
 * 经过编辑的图片的路径信息类
 * @author howie
 *
 */
public class EditPicMessage {
	private String path;

	public EditPicMessage(String path) {
		super();
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
}
