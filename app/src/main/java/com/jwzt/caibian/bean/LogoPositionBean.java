package com.jwzt.caibian.bean;
/**
 * logo位置设置用到的bean
 * @author howie
 *
 */
public class LogoPositionBean {
	
	private boolean isSelected;

	public LogoPositionBean(boolean isSelected) {
		super();
		this.isSelected = isSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
}
