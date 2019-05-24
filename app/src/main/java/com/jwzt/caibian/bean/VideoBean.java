package com.jwzt.caibian.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;


/**
 * 视频录制存储信息
 * @author afnasdf
 *
 */

@Table(name="jwzt_procaibian_video_info")
public class VideoBean {
	
	@Column(name="id",isId=true,autoGen=true)
	private int id;//视频存储主键
	
	
	@Column(name ="videoName")
	private String videoName;//视频名称
	
	
	
	@Column(name="pointPosition")
	private double pointPosition;//标记位置
	
	@Column(name="videoPath")
	private String videoPath;//视频存储位置
	
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


	public double getPointPosition() {
		return pointPosition;
	}

	public void setPointPosition(double pointPosition) {
		this.pointPosition = pointPosition;
	}

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	@Override
	public String toString() {
		return "VideoBean [id=" + id + ", videoName=" + videoName
				 + ", pointPosition="
				+ pointPosition + ", videoPath=" + videoPath + "]";
	}
	
	
	
	
	
	
	
	
}
