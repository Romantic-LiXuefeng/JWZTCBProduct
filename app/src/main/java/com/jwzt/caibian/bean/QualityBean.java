package com.jwzt.caibian.bean;
/**
 * 质量
 * @author howie
 *
 */
public class QualityBean {
	
	public QualityBean(boolean isSelected, String quality) {
		super();
		this.isSelected = isSelected;
		this.quality = quality;
	}
	private boolean isSelected;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	/**
	 * 质量
	 */
	private String quality;

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}
	
	
}
