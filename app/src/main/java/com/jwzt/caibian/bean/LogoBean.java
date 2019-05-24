package com.jwzt.caibian.bean;
/**
 * 图标
 * @author howie
 *
 */
public class LogoBean {
	private String logopath;
	private boolean isSelected;
	
//	public LogoBean(boolean isSelected) {
//		super();
//		this.isSelected = isSelected;
//	}
	public String getLogopath() {
		return logopath;
	}
	public void setLogopath(String logopath) {
		this.logopath = logopath;
	}
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
}
