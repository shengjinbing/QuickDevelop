package com.modesty.socket.server;



import com.modesty.socket.model.CollectServerProtobuf;
import com.modesty.socket.utils.RequestUtil;
import com.modesty.socket.utils.Utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wangzhiyuan
 * @since 2017/4/12
 */
final class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        // 心跳处理
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        if(object != null && object instanceof CollectServerProtobuf.CollectMessage){
            final CollectServerProtobuf.CollectMessage collectMessage = (CollectServerProtobuf.CollectMessage) object;
            final CollectServerProtobuf.MessageType messageType = collectMessage.getMessageType();

            if(messageType == CollectServerProtobuf.MessageType.LOGIN_REQUEST){
                channelHandlerContext.writeAndFlush(RequestUtil.RequestModelUtil.buildLogin());
                Utils.log("server--->receives login request");
            }
            else if(messageType == CollectServerProtobuf.MessageType.PING){
                Utils.log("server--->receives ping request");
            }
            else if(messageType == CollectServerProtobuf.MessageType.USERINFO){
                Utils.log("server--->receives user info request");
            }

            Utils.log("server receives--->" +object.toString());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        Utils.log("server---> exception occurs");
    }
}
