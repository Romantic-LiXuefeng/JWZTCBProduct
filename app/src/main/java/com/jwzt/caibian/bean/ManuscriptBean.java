package com.jwzt.caibian.bean;

/**
 * 文稿上传拼装XML时所选的每个资源对应的资源名称，资源类型，资源路径
 * @author pc
 *
 */
public class ManuscriptBean {
	private String name;
	private String path;
	/***文件类型：1：视频、2：音频、3：图片*/
	private String type;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
