package com.jwzt.caibian.bean;

import java.io.Serializable;

public class ChuanlIanListBean  implements Serializable {
	private String id;
	private String title;
	private String createUserId;
	private String contentNewsIds;
	private String state;
	private String deleted;
	private String createTime;
	private String contentNewsList;
	private String contentMessageList;
	private String programmeCheck1Id;
	private String programmeCheck2Id;
	private String author;
	private String reButton;
	private String newsCount;

	public String getNewsCount() {
		return newsCount;
	}

	public void setNewsCount(String newsCount) {
		this.newsCount = newsCount;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public String getContentNewsIds() {
		return contentNewsIds;
	}
	public void setContentNewsIds(String contentNewsIds) {
		this.contentNewsIds = contentNewsIds;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getContentNewsList() {
		return contentNewsList;
	}
	public void setContentNewsList(String contentNewsList) {
		this.contentNewsList = contentNewsList;
	}
	public String getContentMessageList() {
		return contentMessageList;
	}
	public void setContentMessageList(String contentMessageList) {
		this.contentMessageList = contentMessageList;
	}
	public String getProgrammeCheck1Id() {
		return programmeCheck1Id;
	}
	public void setProgrammeCheck1Id(String programmeCheck1Id) {
		this.programmeCheck1Id = programmeCheck1Id;
	}
	public String getProgrammeCheck2Id() {
		return programmeCheck2Id;
	}
	public void setProgrammeCheck2Id(String programmeCheck2Id) {
		this.programmeCheck2Id = programmeCheck2Id;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getReButton() {
		return reButton;
	}
	public void setReButton(String reButton) {
		this.reButton = reButton;
	}

}
