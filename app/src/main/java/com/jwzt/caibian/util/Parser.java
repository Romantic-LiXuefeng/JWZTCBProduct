package com.jwzt.caibian.util;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jwzt.caibian.bean.MessageAllBean;

public class Parser {
	/**
	 * @param jsondata
	 * @return
	 */
	public static List<MessageAllBean> parserData(String jsondata) {
		List<MessageAllBean> messagelist = new ArrayList<MessageAllBean>();
		try {
			JSONObject jsonobject = JSONObject.parseObject(jsondata);
			String redata = jsonobject.getString("data");
			JSONArray array = JSONArray.parseArray(redata);
			for (int i = 0; i < array.size(); i++) {
				MessageAllBean messagebean = new MessageAllBean();
				JSONObject jsonobjexty = (JSONObject) array.get(i);
				String ids = jsonobjexty.getString("id");
				String parentId = jsonobjexty.getString("parentId");
				String type = jsonobjexty.getString("type");
				String parentType = jsonobjexty.getString("parentType");
				String state = jsonobjexty.getString("state");
				String parentMessageId = jsonobjexty
						.getString("parentMessageId");
				String sender = jsonobjexty.getString("sender");
				String recipient = jsonobjexty.getString("recipient");
				String createTime = jsonobjexty.getString("createTime");
				String content = jsonobjexty.getString("content");
				String messageDescribe = jsonobjexty
						.getString("messageDescribe");
				String senderName = jsonobjexty.getString("senderName");
				String recipientName = jsonobjexty.getString("recipientName");
				String title = jsonobjexty.getString("title");
				String author = jsonobjexty.getString("author");
				String replyState = jsonobjexty.getString("replyState");
				String list = jsonobjexty.getString("list");
				if (!list.equals("") && !list.equals("[]")) {
					JSONArray arrayre = JSONArray.parseArray(list);
					List<MessageAllBean> messagelists = new ArrayList<MessageAllBean>();
					for (int k = 0; k < arrayre.size(); k++) {
						MessageAllBean messagebeanto = new MessageAllBean();
						JSONObject jsonobjextyto = (JSONObject) arrayre.get(k);
						String idst = jsonobjextyto.getString("id");
						String parentIds = jsonobjextyto.getString("parentId");
						String types = jsonobjextyto.getString("type");
						String parentTypes = jsonobjextyto
								.getString("parentType");
						String states = jsonobjextyto.getString("state");
						String parentMessageIds = jsonobjextyto
								.getString("parentMessageId");
						String senders = jsonobjextyto.getString("sender");
						String recipients = jsonobjextyto
								.getString("recipient");
						String createTimes = jsonobjextyto
								.getString("createTime");
						String contents = jsonobjextyto.getString("content");
						String messageDescribes = jsonobjextyto
								.getString("messageDescribe");
						String senderNames = jsonobjextyto
								.getString("senderName");
						String recipientNames = jsonobjextyto
								.getString("recipientName");
						String titles = jsonobjextyto.getString("title");
						String authors = jsonobjextyto.getString("author");
						String replyStates = jsonobjextyto
								.getString("replyState");
						messagebeanto.setAuthor(authors);
						messagebeanto.setContent(contents);
						messagebeanto.setCreateTime(createTimes);
						messagebeanto.setMessageDescribe(messageDescribes);
						messagebeanto.setParentId(parentIds);
						messagebeanto.setParentMessageId(parentMessageIds);
						messagebeanto.setParentType(parentTypes);
						messagebeanto.setRecipient(recipientNames);
						messagebeanto.setRecipientName(recipientNames);
						messagebeanto.setReplyState(replyStates);
						messagebeanto.setSender(senders);
						messagebeanto.setSenderName(senderNames);
						messagebeanto.setState(states);
						messagebeanto.setTitle(titles);
						messagebeanto.setType(types);
						messagebeanto.setId(idst);
						messagebeanto.setRecipient(recipients);
						messagelists.add(messagebeanto);
					}
					messagebean.setList(messagelists);
				}

				messagebean.setAuthor(author);
				messagebean.setContent(content);
				messagebean.setCreateTime(createTime);
				messagebean.setId(ids);
				messagebean.setMessageDescribe(messageDescribe);
				messagebean.setParentId(parentId);
				messagebean.setParentMessageId(parentMessageId);
				messagebean.setParentType(parentType);
				messagebean.setRecipient(recipient);
				messagebean.setRecipientName(recipientName);
				messagebean.setReplyState(replyState);
				messagebean.setSender(sender);
				messagebean.setSenderName(senderName);
				messagebean.setState(state);
				messagebean.setTitle(title);
				messagebean.setType(type);
				messagelist.add(messagebean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return messagelist;

	}

	public static MessageAllBean parserDataBean(String jsondata) {
		MessageAllBean messagebean = new MessageAllBean();
		try {
			JSONObject jsonobject = JSONObject.parseObject(jsondata);
			String redata = jsonobject.getString("data");
			JSONObject array = JSONObject.parseObject(redata);		
			String ids = array.getString("id");
			String parentId = array.getString("parentId");
			String type = array.getString("type");
			String parentType = array.getString("parentType");
			String state = array.getString("state");
			String parentMessageId = array.getString("parentMessageId");
			String sender = array.getString("sender");
			String recipient = array.getString("recipient");
			String createTime = array.getString("createTime");
			String content = array.getString("content");
			String messageDescribe = array.getString("messageDescribe");
			String senderName = array.getString("senderName");
			String recipientName = array.getString("recipientName");
			String title = array.getString("title");
			String author = array.getString("author");
			String replyState = array.getString("replyState");
			String list = array.getString("list");
			if (!list.equals("") && !list.equals("[]")) {
				JSONArray arrayre = JSONArray.parseArray(list);
				List<MessageAllBean> messagelists = new ArrayList<MessageAllBean>();
				for (int k = 0; k < arrayre.size(); k++) {
					MessageAllBean messagebeanto = new MessageAllBean();
					JSONObject jsonobjextyto = (JSONObject) arrayre.get(k);
					String idst = jsonobjextyto.getString("id");
					String parentIds = jsonobjextyto.getString("parentId");
					String types = jsonobjextyto.getString("type");
					String parentTypes = jsonobjextyto.getString("parentType");
					String states = jsonobjextyto.getString("state");
					String parentMessageIds = jsonobjextyto
							.getString("parentMessageId");
					String senders = jsonobjextyto.getString("sender");
					String recipients = jsonobjextyto.getString("recipient");
					String createTimes = jsonobjextyto.getString("createTime");
					String contents = jsonobjextyto.getString("content");
					String messageDescribes = jsonobjextyto
							.getString("messageDescribe");
					String senderNames = jsonobjextyto.getString("senderName");
					String recipientNames = jsonobjextyto
							.getString("recipientName");
					String titles = jsonobjextyto.getString("title");
					String authors = jsonobjextyto.getString("author");
					String replyStates = jsonobjextyto.getString("replyState");
					messagebeanto.setAuthor(authors);
					messagebeanto.setContent(contents);
					messagebeanto.setCreateTime(createTimes);
					messagebeanto.setMessageDescribe(messageDescribes);
					messagebeanto.setParentId(parentIds);
					messagebeanto.setParentMessageId(parentMessageIds);
					messagebeanto.setParentType(parentTypes);
					messagebeanto.setRecipient(recipientNames);
					messagebeanto.setRecipientName(recipientNames);
					messagebeanto.setReplyState(replyStates);
					messagebeanto.setSender(senders);
					messagebeanto.setSenderName(senderNames);
					messagebeanto.setState(states);
					messagebeanto.setTitle(titles);
					messagebeanto.setType(types);
					messagebeanto.setId(idst);
					messagebeanto.setRecipient(recipients);
					messagelists.add(messagebeanto);
				}
				messagebean.setList(messagelists);
			}
			messagebean.setAuthor(author);
			messagebean.setContent(content);
			messagebean.setCreateTime(createTime);
			messagebean.setId(ids);
			messagebean.setMessageDescribe(messageDescribe);
			messagebean.setParentId(parentId);
			messagebean.setParentMessageId(parentMessageId);
			messagebean.setParentType(parentType);
			messagebean.setRecipient(recipient);
			messagebean.setRecipientName(recipientName);
			messagebean.setReplyState(replyState);
			messagebean.setSender(sender);
			messagebean.setSenderName(senderName);
			messagebean.setState(state);
			messagebean.setTitle(title);
			messagebean.setType(type);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return messagebean;

	}
	
	public static MessageAllBean parserDataBeandetail(String jsondata) {
		MessageAllBean messagebean = new MessageAllBean();
		try {
			JSONObject jsonobject = JSONObject.parseObject(jsondata);
			String redata = jsonobject.getString("data");
			JSONObject array = JSONObject.parseObject(redata);		
			String ids = array.getString("id");
			String parentId = array.getString("parentId");
			String type = array.getString("type");
			String parentType = array.getString("parentType");
			String state = array.getString("state");
			String parentMessageId = array.getString("parentMessageId");
			String sender = array.getString("sender");
			String recipient = array.getString("recipient");
			String createTime = array.getString("createTime");
			String content = array.getString("content");
			String messageDescribe = array.getString("messageDescribe");
			String senderName = array.getString("senderName");
			String recipientName = array.getString("recipientName");
			String title = array.getString("title");
			String author = array.getString("author");
			String replyState = array.getString("replyState");
			messagebean.setAuthor(author);
			messagebean.setContent(content);
			messagebean.setCreateTime(createTime);
			messagebean.setId(ids);
			messagebean.setMessageDescribe(messageDescribe);
			messagebean.setParentId(parentId);
			messagebean.setParentMessageId(parentMessageId);
			messagebean.setParentType(parentType);
			messagebean.setRecipient(recipient);
			messagebean.setRecipientName(recipientName);
			messagebean.setReplyState(replyState);
			messagebean.setSender(sender);
			messagebean.setSenderName(senderName);
			messagebean.setState(state);
			messagebean.setTitle(title);
			messagebean.setType(type);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return messagebean;

	}


}
