package com.jwzt.caibian.bean;
/**
 * 用于通过eventBus发送消息使加号按钮可见的类
 * @author howie
 *
 */
public class ShowPlusMessage {
	private boolean isVisible;
	
	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public ShowPlusMessage(boolean isVisible) {
		super();
		this.isVisible = isVisible;
	}
	
}
