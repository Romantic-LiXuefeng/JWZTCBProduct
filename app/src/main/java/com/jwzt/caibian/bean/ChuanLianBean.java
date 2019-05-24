package com.jwzt.caibian.bean;

/**
 * 作者：我的孩子叫好帅 on 2018/8/2 13:35
 * Q Q：779594494
 * 邮箱：17600949389@163.com
 */
public class ChuanLianBean {

    /**
     * data : {"id":259,"title":"测试","createUserId":1031,"contentNewsIds":"5860,5859,","state":4,"deleted":false,"createTime":"2019-04-30 16:51:44","contentNewsList":"","contentMessageList":"","programmeCheck1Id":1,"programmeCheck2Id":0,"author":"孟宪亮","reButton":false,"programmeFlId":"","newsCount":2}
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
         * id : 259
         * title : 测试
         * createUserId : 1031
         * contentNewsIds : 5860,5859,
         * state : 4
         * deleted : false
         * createTime : 2019-04-30 16:51:44
         * contentNewsList :
         * contentMessageList :
         * programmeCheck1Id : 1
         * programmeCheck2Id : 0
         * author : 孟宪亮
         * reButton : false
         * programmeFlId :
         * newsCount : 2
         */

        private int id;
        private String title;
        private int createUserId;
        private String contentNewsIds;
        private int state;
        private boolean deleted;
        private String createTime;
        private String contentNewsList;
        private String contentMessageList;
        private int programmeCheck1Id;
        private int programmeCheck2Id;
        private String author;
        private boolean reButton;
        private String programmeFlId;
        private int newsCount;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getCreateUserId() {
            return createUserId;
        }

        public void setCreateUserId(int createUserId) {
            this.createUserId = createUserId;
        }

        public String getContentNewsIds() {
            return contentNewsIds;
        }

        public void setContentNewsIds(String contentNewsIds) {
            this.contentNewsIds = contentNewsIds;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getContentNewsList() {
            return contentNewsList;
        }

        public void setContentNewsList(String contentNewsList) {
            this.contentNewsList = contentNewsList;
        }

        public String getContentMessageList() {
            return contentMessageList;
        }

        public void setContentMessageList(String contentMessageList) {
            this.contentMessageList = contentMessageList;
        }

        public int getProgrammeCheck1Id() {
            return programmeCheck1Id;
        }

        public void setProgrammeCheck1Id(int programmeCheck1Id) {
            this.programmeCheck1Id = programmeCheck1Id;
        }

        public int getProgrammeCheck2Id() {
            return programmeCheck2Id;
        }

        public void setProgrammeCheck2Id(int programmeCheck2Id) {
            this.programmeCheck2Id = programmeCheck2Id;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public boolean isReButton() {
            return reButton;
        }

        public void setReButton(boolean reButton) {
            this.reButton = reButton;
        }

        public String getProgrammeFlId() {
            return programmeFlId;
        }

        public void setProgrammeFlId(String programmeFlId) {
            this.programmeFlId = programmeFlId;
        }

        public int getNewsCount() {
            return newsCount;
        }

        public void setNewsCount(int newsCount) {
            this.newsCount = newsCount;
        }
    }
}
