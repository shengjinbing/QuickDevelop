package com.modesty.socket.server;

/**
 * @author wangzhiyuan
 * @since 2017/4/12
 */

final class ServerTest {

    public static void main(String[] args) throws InterruptedException {
        ServerSocketManager.getInstance().start();
    }

}
