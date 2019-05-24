package com.jwzt.caibian.bean;

import java.io.Serializable;

/**
 * 任务列表中resourceList字段中ResourceFile字段
 * @author pc
 *
 */
@SuppressWarnings("serial")
public class TaskListResourceListResourceFileBean implements Serializable{
	private String id;
	private String uploadServerId;
	private String parentFileId;
	private String fileName;
	private String resourceType;
	private String filePath;
	private String localPath;
	private String status;
	private String fileSize;
	private String preViewImg;
	private String uploadTime;
	private String uploadUserId;
	private String uploadUserName;
	private HandlerBean handler;
	private String duration;

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	private HibernateLazyInitializerBean hibernateLazyInitializer;
	private String fileRealPath;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUploadServerId() {
		return uploadServerId;
	}
	public void setUploadServerId(String uploadServerId) {
		this.uploadServerId = uploadServerId;
	}
	public String getParentFileId() {
		return parentFileId;
	}
	public void setParentFileId(String parentFileId) {
		this.parentFileId = parentFileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getPreViewImg() {
		return preViewImg;
	}
	public void setPreViewImg(String preViewImg) {
		this.preViewImg = preViewImg;
	}
	public String getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}
	public String getUploadUserId() {
		return uploadUserId;
	}
	public void setUploadUserId(String uploadUserId) {
		this.uploadUserId = uploadUserId;
	}
	public String getUploadUserName() {
		return uploadUserName;
	}
	public void setUploadUserName(String uploadUserName) {
		this.uploadUserName = uploadUserName;
	}
	public HandlerBean getHandler() {
		return handler;
	}
	public void setHandler(HandlerBean handler) {
		this.handler = handler;
	}
	public HibernateLazyInitializerBean getHibernateLazyInitializer() {
		return hibernateLazyInitializer;
	}
	public void setHibernateLazyInitializer(
			HibernateLazyInitializerBean hibernateLazyInitializer) {
		this.hibernateLazyInitializer = hibernateLazyInitializer;
	}
	public String getFileRealPath() {
		return fileRealPath;
	}
	public void setFileRealPath(String fileRealPath) {
		this.fileRealPath = fileRealPath;
	}
	
	
}
