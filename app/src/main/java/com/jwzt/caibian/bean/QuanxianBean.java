package com.jwzt.caibian.bean;

/**
 * Created by 我的电脑 on 2019/1/23.
 */

public class QuanxianBean {

    /**
     * data : {"programmeFirstCheck":true,"programmeSecondCheck":true}
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
         * programmeFirstCheck : true
         * programmeSecondCheck : true
         */

        private boolean programmeFirstCheck;
        private boolean programmeSecondCheck;

        public boolean isProgrammeFirstCheck() {
            return programmeFirstCheck;
        }

        public void setProgrammeFirstCheck(boolean programmeFirstCheck) {
            this.programmeFirstCheck = programmeFirstCheck;
        }

        public boolean isProgrammeSecondCheck() {
            return programmeSecondCheck;
        }

        public void setProgrammeSecondCheck(boolean programmeSecondCheck) {
            this.programmeSecondCheck = programmeSecondCheck;
        }
    }
}
