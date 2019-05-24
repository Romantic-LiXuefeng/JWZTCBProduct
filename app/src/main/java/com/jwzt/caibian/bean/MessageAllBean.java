package com.jwzt.caibian.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author awq
 */
@SuppressWarnings("serial")
public class MessageAllBean implements Serializable {
	private String id;
	private String parentId;
	private String type;
	private String parentType;
	private String state;
	private String parentMessageId;
	private String sender;
	private String recipient;
	private String createTime;
	private String content;
	private String messageDescribe;
	private String senderName;
	private String recipientName;
	private String replyState;
	private String author;
	private String title;
	private List<MessageAllBean> list;
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<MessageAllBean> getList() {
		return list;
	}
	public void setList(List<MessageAllBean> list) {
		this.list = list;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getParentType() {
		return parentType;
	}
	public void setParentType(String parentType) {
		this.parentType = parentType;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getParentMessageId() {
		return parentMessageId;
	}
	public void setParentMessageId(String parentMessageId) {
		this.parentMessageId = parentMessageId;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMessageDescribe() {
		return messageDescribe;
	}
	public void setMessageDescribe(String messageDescribe) {
		this.messageDescribe = messageDescribe;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getRecipientName() {
		return recipientName;
	}
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}
	public String getReplyState() {
		return replyState;
	}
	public void setReplyState(String replyState) {
		this.replyState = replyState;
	}
	
	

}
