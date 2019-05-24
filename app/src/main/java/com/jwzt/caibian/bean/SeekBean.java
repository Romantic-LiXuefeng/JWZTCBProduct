package com.jwzt.caibian.bean;

import java.io.Serializable;

/**
 * 用于记录当进行了裁剪之后进行预览需要跳过的区间的起始点和结束点
 * 
 * @author howie
 * 
 */
public class SeekBean implements Serializable{
	private int id;
	/** 被裁剪的区间的进度的起始点 */
	private int start;
	/** 被裁剪的区间的进度的终止点 */
	private int end;
	/** 起始位置对应的滚动的距离 */
	private int startDistance;
	/** 终点位置对应的滚动的距离 */
	private int endDistance;

	public int getStartDistance() {
		return startDistance;
	}

	public void setStartDistance(int startDistance) {
		this.startDistance = startDistance;
	}

	public int getEndDistance() {
		return endDistance;
	}

	public void setEndDistance(int endDistance) {
		this.endDistance = endDistance;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	//
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
}
