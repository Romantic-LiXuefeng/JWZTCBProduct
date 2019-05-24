package com.jwzt.caibian.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;


/**
 * 视频进度条的长度
 * @author afnasdf
 *
 */

@Table(name="jwzt_procaibian_progress_info")
public class ProgressBean {
	
	@Column(name="id",isId=true,autoGen=true)
	private int id;//视频存储主键
	
	
	@Column(name ="videoName")
	private String videoName;//视频名称
	
	
	
	
	@Column(name="progressLength")
	private double progressLength;//进度条当前的长度

	public double getProgressLength() {
		return progressLength;
	}

	public void setProgressLength(double progressLength) {
		this.progressLength = progressLength;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	@Override
	public String toString() {
		return "ProgressBean [id=" + id + ", videoName=" + videoName
				+ ", progressLength=" + progressLength + "]";
	}

	
	
	
}
