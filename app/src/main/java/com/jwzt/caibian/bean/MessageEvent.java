package com.jwzt.caibian.bean;
/**
 * 用于通信的类
 * @author howie
 *
 */
public class MessageEvent {
	/**是否处于编辑状态*//*
	private boolean isEditing;*/
	private int which;
	
	public MessageEvent(int which) {
		super();
		this.which = which;
	}

/*	public MessageEvent(boolean isEditing) {
		super();
		this.isEditing = isEditing;
	}

	public boolean isEditing() {
		return isEditing;
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}*/

	public int getWhich() {
		return which;
	}

	public void setWhich(int which) {
		this.which = which;
	}
	
	
}
