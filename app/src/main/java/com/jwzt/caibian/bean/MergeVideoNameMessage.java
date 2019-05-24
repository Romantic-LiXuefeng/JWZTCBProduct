package com.jwzt.caibian.bean;
/**
 * 用于通过EventBus传递合并的视频的名字的消息类
 * @author howie
 *
 */
public class MergeVideoNameMessage {
	/**视频的名字*/
	private String videoName;

	public MergeVideoNameMessage(String videoName) {
		super();
		this.videoName = videoName;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}
	
	
}
