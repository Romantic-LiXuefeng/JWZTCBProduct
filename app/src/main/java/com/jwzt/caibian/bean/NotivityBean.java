package com.jwzt.caibian.bean;

import java.util.List;

/**
 * 作者：我的孩子叫好帅 on 2018/8/2 13:35
 * Q Q：779594494
 * 邮箱：17600949389@163.com
 */
public class NotivityBean {

    /**
     * data : {"id":12000,"parentId":5587,"type":9,"parentType":6,"state":1,"parentMessageId":0,"sender":1,"recipient":1031,"createTime":"2019-05-11 14:36:00","content":"","messageDescribe":"您有新的新媒体任务","senderName":"系统管理员","recipientName":"孟宪亮","title":"好方法","author":"","replyState":0,"msgType":"","list":[],"checkPerm":""}
     * message : 成功。
     * status : 100
     */

    private DataBean data;
    private String message;
    private int status;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class DataBean {
        /**
         * id : 12000
         * parentId : 5587
         * type : 9
         * parentType : 6
         * state : 1
         * parentMessageId : 0
         * sender : 1
         * recipient : 1031
         * createTime : 2019-05-11 14:36:00
         * content :
         * messageDescribe : 您有新的新媒体任务
         * senderName : 系统管理员
         * recipientName : 孟宪亮
         * title : 好方法
         * author :
         * replyState : 0
         * msgType :
         * list : []
         * checkPerm :
         */

        private int id;
        private int parentId;
        private int type;
        private int parentType;
        private int state;
        private int parentMessageId;
        private int sender;
        private int recipient;
        private String createTime;
        private String content;
        private String messageDescribe;
        private String senderName;
        private String recipientName;
        private String title;
        private String author;
        private int replyState;
        private String msgType;
        private String checkPerm;
        private List<?> list;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getParentType() {
            return parentType;
        }

        public void setParentType(int parentType) {
            this.parentType = parentType;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getParentMessageId() {
            return parentMessageId;
        }

        public void setParentMessageId(int parentMessageId) {
            this.parentMessageId = parentMessageId;
        }

        public int getSender() {
            return sender;
        }

        public void setSender(int sender) {
            this.sender = sender;
        }

        public int getRecipient() {
            return recipient;
        }

        public void setRecipient(int recipient) {
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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getReplyState() {
            return replyState;
        }

        public void setReplyState(int replyState) {
            this.replyState = replyState;
        }

        public String getMsgType() {
            return msgType;
        }

        public void setMsgType(String msgType) {
            this.msgType = msgType;
        }

        public String getCheckPerm() {
            return checkPerm;
        }

        public void setCheckPerm(String checkPerm) {
            this.checkPerm = checkPerm;
        }

        public List<?> getList() {
            return list;
        }

        public void setList(List<?> list) {
            this.list = list;
        }
    }
}
