
package com.modesty.quickdevelop.bean;


import java.io.Serializable;

/**
 * @author wangzhiyuan
 * @since 2018/2/5
 */

public class BaseData implements Serializable, Cloneable {
    public int errno ;
    public String errmsg;

    @Override
    public BaseData clone() {
        BaseData obj = null;
        try {
            obj = (BaseData)super.clone();
        } catch (CloneNotSupportedException var3) {
            var3.printStackTrace();
        }
        return obj;
    }
}
