package com.jwzt.caibian.bean;
/**
 * 用于通知拍照的消息类
 * @author howie
 *
 */
public class NewMessage {
	public static final int TYPE_VIDEO=0;
	public static final int TYPE_AUDIO=1;
	public static final int TYPE_IMAGE=2;
	
	public NewMessage(int type) {   
		super();
		this.type = type;
	}

	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
