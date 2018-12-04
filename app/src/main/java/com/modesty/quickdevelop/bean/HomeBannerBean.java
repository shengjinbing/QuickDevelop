package com.modesty.quickdevelop.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/11/26 0026.
 */

public class HomeBannerBean {
    /**
     * code : 0000
     * message : 成功
     * t : 1510902829273
     * data : [{"id":26,"title":"永邦专属b","startTime":1494936000000,"endTime":1504096740000,"image":"http://img.traveler99.com/image/2017/5/18/1495102603387.png","link":"http://www.traveler99.com","type":3,"status":1,"adSort":26,"notice":null,"businessCodes":"FjXsMktM","isDelete":0,"createUserId":3,"createUserName":"wenshuang","createTime":1494938415000}]
     */

    private String code;
    private String message;
    private long t;
    /**
     * id : 26
     * title : 永邦专属b
     * startTime : 1494936000000
     * endTime : 1504096740000
     * image : http://img.traveler99.com/image/2017/5/18/1495102603387.png
     * link : http://www.traveler99.com
     * type : 3
     * status : 1
     * adSort : 26
     * notice : null
     * businessCodes : FjXsMktM
     * isDelete : 0
     * createUserId : 3
     * createUserName : wenshuang
     * createTime : 1494938415000
     */

    private List<DataBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getT() {
        return t;
    }

    public void setT(long t) {
        this.t = t;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HomeBannerBean{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", t=" + t +
                ", data=" + data +
                '}';
    }

    public static class DataBean {
        private int id;
        private String title;
        private long startTime;
        private long endTime;
        private String image;
        private String link;
        private int type;
        private int status;
        private int adSort;
        private String notice;
        private String businessCodes;
        private int isDelete;
        private int createUserId;
        private String createUserName;
        private long createTime;

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

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getAdSort() {
            return adSort;
        }

        public void setAdSort(int adSort) {
            this.adSort = adSort;
        }

       public String tNotice() {
            return notice;
        }

        public void setNotice(String notice) {
            this.notice = notice;
        }

        public String getBusinessCodes() {
            return businessCodes;
        }

        public void setBusinessCodes(String businessCodes) {
            this.businessCodes = businessCodes;
        }

        public int getIsDelete() {
            return isDelete;
        }

        public void setIsDelete(int isDelete) {
            this.isDelete = isDelete;
        }

        public int getCreateUserId() {
            return createUserId;
        }

        public void setCreateUserId(int createUserId) {
            this.createUserId = createUserId;
        }

        public String getCreateUserName() {
            return createUserName;
        }

        public void setCreateUserName(String createUserName) {
            this.createUserName = createUserName;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }
}
