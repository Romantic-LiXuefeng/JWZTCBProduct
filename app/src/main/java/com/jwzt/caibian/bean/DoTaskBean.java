package com.jwzt.caibian.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 发送的任务列表bean对象
 * @author awq
 */
@DatabaseTable(tableName = "tasks")
public class DoTaskBean {
	@DatabaseField(generatedId = true)
	private int id;  //自动增长id
	@DatabaseField(canBeNull = true)
	private int  taskid;     //任务id
	@DatabaseField(canBeNull = true)
	private String videopath;    //视频地址
	@DatabaseField(canBeNull = true)
	private String sendTime;     //发送时间
	@DatabaseField(canBeNull = true)
	private String thumbImages;  //资源地址，如果是图片则以，分割 
	@DatabaseField(canBeNull = true)
	private String sucaiContent;   //发送内容  如果是问题则取这个字段
	@DatabaseField(canBeNull = true)
	private int sucaiType;      //发送类型   0：文字，1：图片  2：音频  3：视频
	@DatabaseField(canBeNull = true)
	private String sucaiId;    //发送内容自动生成的ID  根据uuid生成
	@DatabaseField(canBeNull = true)
	private int readerStatue;   //用来标识任务是否发送 0已经发送  1未发送
	@DatabaseField(canBeNull = true)
	private int userid;   //用户id
	@DatabaseField(canBeNull = true)
	private double Longitude;//经度 
	@DatabaseField(canBeNull = true)
	private double Latitude;//纬度
	@DatabaseField(canBeNull = true)
	private int timeLength;//录音时长
	
	
	public int getTimeLength() {
		return timeLength;
	}
	public void setTimeLength(int timeLength) {
		this.timeLength = timeLength;
	}
	public double getLongitude() {
		return Longitude;
	}
	public void setLongitude(double longitude) {
		Longitude = longitude;
	}
	public double getLatitude() {
		return Latitude;
	}
	public void setLatitude(double latitude) {
		Latitude = latitude;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTaskid() {
		return taskid;
	}
	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}
	public String getVideopath() {
		return videopath;
	}
	public void setVideopath(String videopath) {
		this.videopath = videopath;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getThumbImages() {
		return thumbImages;
	}
	public void setThumbImages(String thumbImages) {
		this.thumbImages = thumbImages;
	}
	
	public int getSucaiType() {
		return sucaiType;
	}
	public void setSucaiType(int sucaiType) {
		this.sucaiType = sucaiType;
	}
	
	public int getReaderStatue() {
		return readerStatue;
	}
	public void setReaderStatue(int readerStatue) {
		this.readerStatue = readerStatue;
	}
	public String getSucaiContent() {
		return sucaiContent;
	}
	public void setSucaiContent(String sucaiContent) {
		this.sucaiContent = sucaiContent;
	}
	public String getSucaiId() {
		return sucaiId;
	}
	public void setSucaiId(String sucaiId) {
		this.sucaiId = sucaiId;
	}
	
	
    

}
