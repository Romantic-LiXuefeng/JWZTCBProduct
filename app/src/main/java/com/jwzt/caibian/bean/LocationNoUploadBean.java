package com.jwzt.caibian.bean;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 本地保存的未上传的数据
 * @author pc
 *
 */
@DatabaseTable(tableName = "localfiles")
public class LocationNoUploadBean implements Serializable{
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField(canBeNull = true)
	private String uuid;//uuid
	@DatabaseField(canBeNull = true)
	private String title;//标题
	@DatabaseField(canBeNull = true)
	private String content;//内容
	@DatabaseField(canBeNull = true)
	private String location;//当前位置
	@DatabaseField(canBeNull = true)
	private String longitude;//经度
	@DatabaseField(canBeNull = true)
	private String latitude;//纬度
	@DatabaseField(canBeNull = true)
	private String imgpath;//所选图片音频视频的路径集合中间用“，”隔开
	@DatabaseField(canBeNull = true)
	private String userId;//用户id
	@DatabaseField(canBeNull = true)
	private String userName;//用户名称
	@DatabaseField(canBeNull = true)
	private String typeId;//类型id
	@DatabaseField(canBeNull = true)
	private String typeName;//类型名称
	@DatabaseField(canBeNull = true)
	private String saveTime;//保存时间06月14日 20:25格式
	@DatabaseField(canBeNull = true)
	private String saveTime1;//保存时间04-28 12:12格式
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getImgpath() {
		return imgpath;
	}
	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getSaveTime() {
		return saveTime;
	}
	public void setSaveTime(String saveTime) {
		this.saveTime = saveTime;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getSaveTime1() {
		return saveTime1;
	}
	public void setSaveTime1(String saveTime1) {
		this.saveTime1 = saveTime1;
	}
	
}
