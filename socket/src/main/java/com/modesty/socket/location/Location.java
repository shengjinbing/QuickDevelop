package com.modesty.socket.location;

import java.io.Serializable;

/**
 * @author wangzhiyuan
 * @since 2018/6/28
 */

public class Location implements Serializable {
    public double lat;
    public double lng;
    public float bearing;
    public double altitude;
    public float accuracy;
    public String address;
    public String provider;
    public String adCode;
    public String cityCode;
    public int locType;
    public float speed;

    @Override
    public String toString() {
        return "Location{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", bearing=" + bearing +
                ", altitude=" + altitude +
                ", accuracy=" + accuracy +
                ", address='" + address + '\'' +
                ", provider='" + provider + '\'' +
                ", adCode='" + adCode + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", locType=" + locType +
                ", speed=" + speed +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(obj instanceof Location && obj.toString().equals(toString())){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
