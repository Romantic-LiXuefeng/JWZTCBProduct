package com.jwzt.caibian.bean;

import java.io.Serializable;

/**
 * 任务列表中 userList字段
 * @author pc
 *
 */
@SuppressWarnings("serial")
public class TaskListUserListBean implements Serializable{
	private String id;
	private String username;
	private String userImg;
	private String channelName;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserImg() {
		return userImg;
	}
	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	
}
