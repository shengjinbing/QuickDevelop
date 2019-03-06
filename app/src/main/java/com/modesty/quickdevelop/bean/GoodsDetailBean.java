package com.modesty.quickdevelop.bean;

import java.util.List;

/**
 * Created by wangtianxiang on 2019/3/4
 */
public class GoodsDetailBean {
    /**
     * data : {"socre":0,"record":[],"goods":{"off_time":"2019-03-22 00:00:00","goods_name":"手机","goods_intro":"","market_value":800,"market_unit":"积分","goods_simple_intro":"","need_unit":"积分","goods_id":96,"need_value":700,"vip_Discount":"暂无折扣","goods_type":5,"stock":1000,"pics":{"banner":["attachment/uploads/appshinepic/2019/03/03/1551571187app_banner_img0.jpg","attachment/uploads/appshinepic/2019/03/03/1551571187app_banner_img1.jpg"],"info":["attachment/uploads/appshinepic/2019/03/03/1551571187img_detail_app0.png","attachment/uploads/appshinepic/2019/03/03/1551571187img_detail_app1.png"]}},"verify":{"msg":"","receive":-1}}
     * ret_msg :
     * ret_code : 0
     */
    private DataEntity data;
    private String ret_msg;
    private int ret_code;

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public DataEntity getData() {
        return data;
    }

    public String getRet_msg() {
        return ret_msg;
    }

    public int getRet_code() {
        return ret_code;
    }

    public class DataEntity {
        /**
         * socre : 0
         * record : []
         * goods : {"off_time":"2019-03-22 00:00:00","goods_name":"手机","goods_intro":"","market_value":800,"market_unit":"积分","goods_simple_intro":"","need_unit":"积分","goods_id":96,"need_value":700,"vip_Discount":"暂无折扣","goods_type":5,"stock":1000,"pics":{"banner":["attachment/uploads/appshinepic/2019/03/03/1551571187app_banner_img0.jpg","attachment/uploads/appshinepic/2019/03/03/1551571187app_banner_img1.jpg"],"info":["attachment/uploads/appshinepic/2019/03/03/1551571187img_detail_app0.png","attachment/uploads/appshinepic/2019/03/03/1551571187img_detail_app1.png"]}}
         * verify : {"msg":"","receive":-1}
         */
        private int socre;
        private List<?> record;
        private GoodsEntity goods;
        private VerifyEntity verify;

        public void setSocre(int socre) {
            this.socre = socre;
        }

        public void setRecord(List<?> record) {
            this.record = record;
        }

        public void setGoods(GoodsEntity goods) {
            this.goods = goods;
        }

        public void setVerify(VerifyEntity verify) {
            this.verify = verify;
        }

        public int getSocre() {
            return socre;
        }

        public List<?> getRecord() {
            return record;
        }

        public GoodsEntity getGoods() {
            return goods;
        }

        public VerifyEntity getVerify() {
            return verify;
        }

        public class GoodsEntity {
            /**
             * off_time : 2019-03-22 00:00:00
             * goods_name : 手机
             * goods_intro :
             * market_value : 800
             * market_unit : 积分
             * goods_simple_intro :
             * need_unit : 积分
             * goods_id : 96
             * need_value : 700
             * vip_Discount : 暂无折扣
             * goods_type : 5
             * stock : 1000
             * pics : {"banner":["attachment/uploads/appshinepic/2019/03/03/1551571187app_banner_img0.jpg","attachment/uploads/appshinepic/2019/03/03/1551571187app_banner_img1.jpg"],"info":["attachment/uploads/appshinepic/2019/03/03/1551571187img_detail_app0.png","attachment/uploads/appshinepic/2019/03/03/1551571187img_detail_app1.png"]}
             */
            private String off_time;
            private String goods_name;
            private String goods_intro;
            private int market_value;
            private String market_unit;
            private String goods_simple_intro;
            private String need_unit;
            private int goods_id;
            private int need_value;
            private String vip_Discount;
            private int goods_type;
            private int stock;
            private PicsEntity pics;

            public void setOff_time(String off_time) {
                this.off_time = off_time;
            }

            public void setGoods_name(String goods_name) {
                this.goods_name = goods_name;
            }

            public void setGoods_intro(String goods_intro) {
                this.goods_intro = goods_intro;
            }

            public void setMarket_value(int market_value) {
                this.market_value = market_value;
            }

            public void setMarket_unit(String market_unit) {
                this.market_unit = market_unit;
            }

            public void setGoods_simple_intro(String goods_simple_intro) {
                this.goods_simple_intro = goods_simple_intro;
            }

            public void setNeed_unit(String need_unit) {
                this.need_unit = need_unit;
            }

            public void setGoods_id(int goods_id) {
                this.goods_id = goods_id;
            }

            public void setNeed_value(int need_value) {
                this.need_value = need_value;
            }

            public void setVip_Discount(String vip_Discount) {
                this.vip_Discount = vip_Discount;
            }

            public void setGoods_type(int goods_type) {
                this.goods_type = goods_type;
            }

            public void setStock(int stock) {
                this.stock = stock;
            }

            public void setPics(PicsEntity pics) {
                this.pics = pics;
            }

            public String getOff_time() {
                return off_time;
            }

            public String getGoods_name() {
                return goods_name;
            }

            public String getGoods_intro() {
                return goods_intro;
            }

            public int getMarket_value() {
                return market_value;
            }

            public String getMarket_unit() {
                return market_unit;
            }

            public String getGoods_simple_intro() {
                return goods_simple_intro;
            }

            public String getNeed_unit() {
                return need_unit;
            }

            public int getGoods_id() {
                return goods_id;
            }

            public int getNeed_value() {
                return need_value;
            }

            public String getVip_Discount() {
                return vip_Discount;
            }

            public int getGoods_type() {
                return goods_type;
            }

            public int getStock() {
                return stock;
            }

            public PicsEntity getPics() {
                return pics;
            }

            public class PicsEntity {
                /**
                 * banner : ["attachment/uploads/appshinepic/2019/03/03/1551571187app_banner_img0.jpg","attachment/uploads/appshinepic/2019/03/03/1551571187app_banner_img1.jpg"]
                 * info : ["attachment/uploads/appshinepic/2019/03/03/1551571187img_detail_app0.png","attachment/uploads/appshinepic/2019/03/03/1551571187img_detail_app1.png"]
                 */
                private List<String> banner;
                private List<String> info;

                public void setBanner(List<String> banner) {
                    this.banner = banner;
                }

                public void setInfo(List<String> info) {
                    this.info = info;
                }

                public List<String> getBanner() {
                    return banner;
                }

                public List<String> getInfo() {
                    return info;
                }
            }
        }

        public class VerifyEntity {
            /**
             * msg :
             * receive : -1
             */
            private String msg;
            private int receive;

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public void setReceive(int receive) {
                this.receive = receive;
            }

            public String getMsg() {
                return msg;
            }

            public int getReceive() {
                return receive;
            }
        }
    }
}
