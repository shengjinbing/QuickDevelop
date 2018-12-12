package com.modesty.quickdevelop.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lixiang
 * on 2018/12/12
 */
public class StarListData extends BaseData {
    /**
     * code : 0000
     * t : 1544583929518
     * data : [{"name":"胡大平","starCode":"200002","id":227,"type":2,"point":"0.00%"}]
     */
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * name : 胡大平
         * starCode : 200002
         * id : 227
         * type : 2
         * point : 0.00%
         */

        private String name;
        private String starCode;
        private int id;
        private int type;
        private String point;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStarCode() {
            return starCode;
        }

        public void setStarCode(String starCode) {
            this.starCode = starCode;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getPoint() {
            return point;
        }

        public void setPoint(String point) {
            this.point = point;
        }
    }
}
