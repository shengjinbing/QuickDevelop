package com.modesty.socket.utils;

/**
 * @author wangzhiyuan
 */

public interface SocketConstants {
    String TAG = "com.elegant.socket--->";

    /*unit is second*/
    int DEFAULT_READER_IDLE_TIME = 0;
    /*unit is second*/
    int DEFAULT_WRITER_IDLE_TIME = 0;
    /*unit is second*/
    int DEFAULT_READER_WRITER_IDLE_TIME = 60 * 3;

    /*unit is ms*/
    long ZERO = 0L;
    /*unit is ms*/
    long ONE_SECOND = 1000L;
    /*unit is ms*/
    long ONE_MINUTE = 60 * ONE_SECOND;

    int DEFAULT_PORT = 9999;
    String DEFAULT_HOST = "140.143.202.53";

}
