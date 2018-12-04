package com.modesty.quickdevelop.bean;

/**
 * Created by Administrator on 2017/11/30 0030.
 */

public class StarListBean {
    /**
     * id : 195
     * code : 100011
     * name : 韩雨洁
     * iconUrl : data/maika/storagedata/maika/storage/star/icon55d1a849-2591-4ed0-bace-c0878b08f5dd.jpg
     * currentPrice : 0.0
     * isActivity : 0
     * point : 1.00%
     */

    private int id;
    private String code;
    private String name;
    private String iconUrl;
    private double currentPrice;
    private int isActivity;
    private String point;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getIsActivity() {
        return isActivity;
    }

    public void setIsActivity(int isActivity) {
        this.isActivity = isActivity;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "StarListBean{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", currentPrice=" + currentPrice +
                ", isActivity=" + isActivity +
                ", point='" + point + '\'' +
                '}';
    }
}
