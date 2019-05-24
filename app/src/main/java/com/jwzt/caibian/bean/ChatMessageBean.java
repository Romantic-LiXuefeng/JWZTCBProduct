package com.jwzt.caibian.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 聊天消息的bean对象
 * @author pc
 *
 */
@DatabaseTable(tableName = "chats")
public class ChatMessageBean {
	@DatabaseField(generatedId = true)
	private int id;    //自动增长id
	@DatabaseField(canBeNull = true)
	private String msgId; // 消息ID
	@DatabaseField(canBeNull = true)
	private String groupId; // 群组ID
	@DatabaseField(canBeNull = true)
	private String senderId;// 发送人ID 即userId
	@DatabaseField(canBeNull = true)
	private String senderName; // 发送人名称
	@DatabaseField(canBeNull = true)
	private String senderImage;// 发送人头像
	@DatabaseField(canBeNull = true)
	private String senderDepartmentName; // 发送人所在部门名称
	@DatabaseField(canBeNull = true)
	private String msgType;// 消息类型：1：文字、2：图片、3：音频、4：视频
	@DatabaseField(canBeNull = true)
	private String createTime;// 发送时间
	@DatabaseField(canBeNull = true)
	private String fontColor;
	@DatabaseField(canBeNull = true)
	private String width;
	@DatabaseField(canBeNull = true)
	private String height;
	@DatabaseField(canBeNull = true)
	private String duration;
	@DatabaseField(canBeNull = true)
	private String previewUrl;
	@DatabaseField(canBeNull = true)
	private String update;
	@DatabaseField(canBeNull = true)
	private String content; // 消息内容
	@DatabaseField(canBeNull = true)
	private String orginContent;
	@DatabaseField(canBeNull = true)
	private String images;
	@DatabaseField(canBeNull = true)
	private String imei;
	@DatabaseField(canBeNull = true)
	private String videopath;
	@DatabaseField(canBeNull = true)
	private String userid;
	@DatabaseField(canBeNull = true)
	private String audiopath;//录音地址
	@DatabaseField(canBeNull = true)
	private int second;//录音时长
	@DatabaseField(canBeNull = true)
	private int isPlayed;//是否已经播放过0表示未播放，1表示已播放
	@DatabaseField(canBeNull = true)
	private int isPlaying;//是否正在播放0表示正在播放，1表示不在播放
	@DatabaseField(canBeNull = true)
	private String isRead;//是否已读0表示已读，1表示未读
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public int getId() {
		return id;
	}
	public void setIds(int id) {
		this.id = id;
	}
	public String getSenderDepartmentName() {
		return senderDepartmentName;
	}
	public void setSenderDepartmentName(String senderDepartmentName) {
		this.senderDepartmentName = senderDepartmentName;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getSenderImage() {
		return senderImage;
	}
	public void setSenderImage(String senderImage) {
		this.senderImage = senderImage;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getFontColor() {
		return fontColor;
	}
	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getUpdate() {
		return update;
	}
	public void setUpdate(String update) {
		this.update = update;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getOrginContent() {
		return orginContent;
	}
	public void setOrginContent(String orginContent) {
		this.orginContent = orginContent;
	}
	public String getImages() {
		return images;
	}
	public void setImages(String images) {
		this.images = images;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getVideopath() {
		return videopath;
	}
	public void setVideopath(String videopath) {
		this.videopath = videopath;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getPreviewUrl() {
		return previewUrl;
	}
	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}
	public String getAudiopath() {
		return audiopath;
	}
	public void setAudiopath(String audiopath) {
		this.audiopath = audiopath;
	}
	public int getSecond() {
		return second;
	}
	public void setSecond(int second) {
		this.second = second;
	}
	public int getIsPlayed() {
		return isPlayed;
	}
	public void setIsPlayed(int isPlayed) {
		this.isPlayed = isPlayed;
	}
	public int getIsPlaying() {
		return isPlaying;
	}
	public void setIsPlaying(int isPlaying) {
		this.isPlaying = isPlaying;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIsRead() {
		return isRead;
	}
	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}
}
