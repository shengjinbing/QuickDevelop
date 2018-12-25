package com.modesty.socket;

/**
 * @author wangzhiyuan
 * @since 2018/8/8
 */

public abstract class ConnectionLifecycleListener {
    public void onConnectFailure(){}
    public void onConnectSuccess(){}
    public void onConnectLost(boolean reconnect){}
}
