package com.jwzt.caibian.bean;


import java.util.List;

/**
 * Created by Administrator on 2018\4\23 0023.
 */

public class TestGroupBean {
    private String id;
    private String type;
    private String title;
    private String content;
    private String source;
    private String keword;
    private String author;
    private String newsImage;
    private String deleted;
    private String status;
    private String terminalType;
    private String createUserId;
    private String createTime;
    private String updateTime;
    private String propId1;
    private String propId2;
    private String propId3;
    private String prop1;
    private String prop2;
    private String prop3;
    private String newsCheck1Id;
    private String newsCheck2Id;
    private String newsBackId;
    private String contentMessageList;
    private String reButton;
    private String createUserName;
    private String categoryId;//类别
    private String address;
    private String checkLevelUser;

    public String getCheckLevelUser() {
        return checkLevelUser;
    }

    public void setCheckLevelUser(String checkLevelUser) {
        this.checkLevelUser = checkLevelUser;
    }

    public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	private List<TestChildBean> newsFileList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getKeword() {
        return keword;
    }

    public void setKeword(String keword) {
        this.keword = keword;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(String newsImage) {
        this.newsImage = newsImage;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getPropId1() {
        return propId1;
    }

    public void setPropId1(String propId1) {
        this.propId1 = propId1;
    }

    public String getPropId2() {
        return propId2;
    }

    public void setPropId2(String propId2) {
        this.propId2 = propId2;
    }

    public String getPropId3() {
        return propId3;
    }

    public void setPropId3(String propId3) {
        this.propId3 = propId3;
    }

    public String getProp1() {
        return prop1;
    }

    public void setProp1(String prop1) {
        this.prop1 = prop1;
    }

    public String getProp2() {
        return prop2;
    }

    public void setProp2(String prop2) {
        this.prop2 = prop2;
    }

    public String getProp3() {
        return prop3;
    }

    public void setProp3(String prop3) {
        this.prop3 = prop3;
    }

    public String getNewsCheck1Id() {
        return newsCheck1Id;
    }

    public void setNewsCheck1Id(String newsCheck1Id) {
        this.newsCheck1Id = newsCheck1Id;
    }

    public String getNewsCheck2Id() {
        return newsCheck2Id;
    }

    public void setNewsCheck2Id(String newsCheck2Id) {
        this.newsCheck2Id = newsCheck2Id;
    }

    public String getNewsBackId() {
        return newsBackId;
    }

    public void setNewsBackId(String newsBackId) {
        this.newsBackId = newsBackId;
    }

    public String getContentMessageList() {
        return contentMessageList;
    }

    public void setContentMessageList(String contentMessageList) {
        this.contentMessageList = contentMessageList;
    }

    public String getReButton() {
        return reButton;
    }

    public void setReButton(String reButton) {
        this.reButton = reButton;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public List<TestChildBean> getNewsFileList() {
        return newsFileList;
    }

    public void setNewsFileList(List<TestChildBean> newsFileList) {
        this.newsFileList = newsFileList;
    }
}
