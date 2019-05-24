package com.jwzt.caibian.bean;

/**
 * 任务列表的Bean对象
 * @author pc
 */
public class TaskListBean {
	private String id;// 任务ID
	private String creatorId; // 负责人ID
	private String creator; // 负责人姓名
	private String creatorPhone;// 负责人电话
	private String title;// 任务标题
	private String status;// 审核状态 1:待提审2 审核中 3 审核通过(进行中) 4 审核不通过
	private String createTime;// 创建时间
	private String endTime;// 任务截止时间
	private String updateTime;//更新时间
	private String approverId;// 审核人ID
	private String approver;// 审核人
	private String clueAddress;// 任务地址
	private String lng; // 任务位置经度
	private String lat; // 任务位置纬度
	private String instruction;// 任务说明
	private String beizhu;//备注
	private String taskStatus;//任务状态
	private String resourceList;//资源集合
	private String clueList;//意义不明确（暂时没用）
	private String userIds;//意义不明确（暂时没用）
	private String userList;//用户组
	private String users;//参与人员
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCreatorPhone() {
		return creatorPhone;
	}
	public void setCreatorPhone(String creatorPhone) {
		this.creatorPhone = creatorPhone;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getApproverId() {
		return approverId;
	}
	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}
	public String getApprover() {
		return approver;
	}
	public void setApprover(String approver) {
		this.approver = approver;
	}
	public String getClueAddress() {
		return clueAddress;
	}
	public void setClueAddress(String clueAddress) {
		this.clueAddress = clueAddress;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	public String getBeizhu() {
		return beizhu;
	}
	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}
	public String getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	public String getClueList() {
		return clueList;
	}
	public void setClueList(String clueList) {
		this.clueList = clueList;
	}
	public String getResourceList() {
		return resourceList;
	}
	public void setResourceList(String resourceList) {
		this.resourceList = resourceList;
	}
	public String getUserIds() {
		return userIds;
	}
	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}
	public String getUserList() {
		return userList;
	}
	public void setUserList(String userList) {
		this.userList = userList;
	}
	public String getUsers() {
		return users;
	}
	public void setUsers(String users) {
		this.users = users;
	}
}
