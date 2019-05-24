package com.jwzt.caibian.bean;

import java.io.Serializable;

public class ResourcesBean implements Serializable {
	private String id;
	private String infoId;
	private String fileId;
	private String resourceName;
	private String resourceType; // 文件类型，1：视频、2：音频、3：图片
	private String createTime;
	private String fileSize;
	private String fileRealPath;
	private String previewUrl;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInfoId() {
		return infoId;
	}
	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileRealPath() {
		return fileRealPath;
	}
	public void setFileRealPath(String fileRealPath) {
		this.fileRealPath = fileRealPath;
	}
	public String getPreviewUrl() {
		return previewUrl;
	}
	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}
}
