package com.jwzt.caibian.bean;

import java.io.Serializable;

/**
 * 获取群组列表bean对象
 * @author pc
 *
 */
public class GroupListBean implements Serializable{
	private String groupName;
	private String groupId;
	private String taskId;
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
}
