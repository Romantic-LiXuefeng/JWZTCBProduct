package com.jwzt.caibian.bean;
/**
 * 用于通过eventBus传递拾取视频或者音频、图片的消息类
 * @author howie
 *
 */
public class PickMessage {
	
	private int type;
	public static final int TYPE_VIDEO=0;//视频
	public static final int TYPE_AUDIO=1;//音频
	public static final int TYPE_IMAGE=2;//图片
	public PickMessage(int type) {
		super();
		this.type = type;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
