package com.modesty.socket.client;



import com.modesty.socket.utils.Utils;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wangzhiyuan
 * @since 2018/6/22
 */
@Sharable
abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements ChannelHandlerHolder{

    ConnectionWatchdog() {}

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Utils.log("channel is active");
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Utils.log("channel is inactive, it depends on client's choice whether to reconnect or not.");
        ClientSocketManager.getInstance().onConnectionLost();
        ctx.fireChannelInactive();
    }
}
