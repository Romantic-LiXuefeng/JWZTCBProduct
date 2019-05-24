package com.jwzt.caibian.bean;

import java.io.Serializable;

/**
 * 任务列表中resourceList字段
 * @author pc
 *
 */
@SuppressWarnings("serial")
public class TaskListResourceListBean implements Serializable{
	private String id;
	private String type;
	private String content;
	private String createTime;
	private String status;
	private String uuid;
	private TaskListResourceListResourceFileBean resourceFile;
	private String style;
	private String username;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public TaskListResourceListResourceFileBean getResourceFile() {
		return resourceFile;
	}
	public void setResourceFile(TaskListResourceListResourceFileBean resourceFile) {
		this.resourceFile = resourceFile;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
