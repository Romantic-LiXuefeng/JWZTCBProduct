package com.jwzt.caibian.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 分类数据bean对象
 * @author pc
 *
 */
public class TypeBean implements Serializable{
	private String id;
	private String categoryName;
	private String hasChild;
	private String childList;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getHasChild() {
		return hasChild;
	}
	public void setHasChild(String hasChild) {
		this.hasChild = hasChild;
	}
	public String getChildList() {
		return childList;
	}
	public void setChildList(String childList) {
		this.childList = childList;
	}
	
	
}
