package com.modesty.socket.client;



import com.modesty.socket.CallbackManager;
import com.modesty.socket.location.LocationUploadManager;
import com.modesty.socket.model.CollectServerProtobuf;
import com.modesty.socket.utils.LoginStatusUtil;
import com.modesty.socket.utils.RequestUtil;
import com.modesty.socket.utils.SocketConstants;
import com.modesty.socket.utils.Utils;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.HashedWheelTimer;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

/**
 * @author wangzhiyuan
 * @since 2018/6/25
 */
@ChannelHandler.Sharable
final class ClientChannelReader extends ChannelInboundHandlerAdapter {
    private Timer loginTimer;
    private Timer sendUserInfoTimer;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {

            final IdleStateEvent e = (IdleStateEvent) evt;

            switch (e.state()) {
                case WRITER_IDLE:
                    RequestUtil.sendHeartbeat(ctx.channel());
                    Utils.log("client send heartbeat write-idle");
                    break;

                case READER_IDLE:
                    RequestUtil.sendHeartbeat(ctx.channel());
                    Utils.log("client send heartbeat reader-idle");
                    break;

                default:
                    break;
            }
        }else{
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        if (msg == null || !(msg instanceof CollectServerProtobuf.CollectMessage)) {
            Utils.log("msg is null or msg is not CollectServerProtobuf.CollectMessage type---" + (msg == null? "" : msg.toString()));
            return;
        }

        Utils.log("Client---client receive server---" + msg.toString());

        final CollectServerProtobuf.CollectMessage collectMessage = (CollectServerProtobuf.CollectMessage) msg;
        final CollectServerProtobuf.MessageType msgType = collectMessage.getMessageType();

        if(msgType == null){
            System.out.print(SocketConstants.TAG + "WTF!!!, msgType is null.");
            return;
        }

        if (msgType == CollectServerProtobuf.MessageType.LOGIN_RESPONSE) {
            if(collectMessage.getData() == null){
                Utils.log("login response ---> response is null");
                return;
            }

            final CollectServerProtobuf.LoginResponse loginResponse = CollectServerProtobuf.LoginResponse.parseFrom(collectMessage.getData());
            final CollectServerProtobuf.LoginErrorCode code = loginResponse.getErrorCode();

            if(code == CollectServerProtobuf.LoginErrorCode.SUCCESS){
                LoginStatusUtil.setStatus(true);
                onLoginSuccess(channelHandlerContext.channel());
            }else if(code == CollectServerProtobuf.LoginErrorCode.ERROR_RETRY){
                LoginStatusUtil.setStatus(false);
                onRetryLogin(channelHandlerContext.channel());
            }else if(code == CollectServerProtobuf.LoginErrorCode.ERROR_CLOSE){
                Utils.log("error code is LoginErrorCode.ERROR_CLOSE");
                ClientSocketManager.getInstance().close(false);
            }
        }
        else if (msgType == CollectServerProtobuf.MessageType.PONG) {
            Utils.log("Client receives server's heartbeat.");
        }
        else if (msgType == CollectServerProtobuf.MessageType.CLOSECHANNEL){
            if(collectMessage.getData() == null){
                Utils.log("close channel response ---> response is null");
                return;
            }

            final CollectServerProtobuf.CloseChannel closeChannel = CollectServerProtobuf.CloseChannel.parseFrom(collectMessage.getData());
            final CollectServerProtobuf.CloseChannelReason reason = closeChannel.getReason();

            if(reason == CollectServerProtobuf.CloseChannelReason.OTHERLOGIN){
                final JSONObject message = new JSONObject();
                message.put("type","otherLogin");
                message.put("content",closeChannel.getErrorMessage());
                CallbackManager.getInstance().notify(message.toString());
            }else if(reason == CollectServerProtobuf.CloseChannelReason.WRONGTOKEN){
                ClientSocketManager.getInstance().close(false);
            }

            Utils.log(
                    "close channel's err message is " + closeChannel.getErrorMessage() +
                    "close channel's reason is " + closeChannel.getReason().toString()
            );
        }
        else if (msgType == CollectServerProtobuf.MessageType.USERINFORESPONSE){
            if(collectMessage.getData() == null){
                Utils.log("user info response ---> response is null");
                onSendUserInfoFailure(channelHandlerContext.channel());
                return;
            }

            final CollectServerProtobuf.UserInfoResponse userInfoResponse = CollectServerProtobuf.UserInfoResponse.parseFrom(collectMessage.getData());

            if(userInfoResponse.getResult()){
                Utils.log("Client receives server's user info response, result is true.");
            }else{
                onSendUserInfoFailure(channelHandlerContext.channel());
            }
        }
        else if(msgType == CollectServerProtobuf.MessageType.PUSH_MESSAGE_RESPONSE){
            if(collectMessage.getData() == null){
                Utils.log("coordinate push");
                return;
            }
            Utils.log("push message response received");
            final CollectServerProtobuf.PushMessageResponse pushMessageResponse = CollectServerProtobuf.PushMessageResponse.parseFrom(collectMessage.getData());
            final boolean retType = pushMessageResponse.getRetType();
            if(retType){
                RequestUtil.sendPushMessage(channelHandlerContext.channel(),pushMessageResponse.getSeqId(),pushMessageResponse.getServerId());
            }
            final String content = pushMessageResponse.getContent();
            Utils.log(content);
            CallbackManager.getInstance().notify(content);
        }

        ReferenceCountUtil.release(msg);
    }

    private void onSendUserInfoFailure(final Channel channel) {
        if(sendUserInfoTimer == null){
            sendUserInfoTimer = new HashedWheelTimer();
        }
        //retry sending
        loginTimer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                RequestUtil.sendUserInfo(channel);
                Utils.log("Client sending user info fails, retry in 10s");
            }
        }, SocketConstants.ONE_SECOND * 10, TimeUnit.MILLISECONDS);
    }

    private void onRetryLogin(final Channel channel){
        if(loginTimer == null){
            loginTimer = new HashedWheelTimer();
        }
        //login login.
        loginTimer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                RequestUtil.login(channel);
                Utils.log("Client login fails, retry in 5s");
            }
        }, SocketConstants.ONE_SECOND * 5, TimeUnit.MILLISECONDS);
    }

    private void onLoginSuccess(Channel channel){
        Utils.log("Client login succeeds");
        //start upload task
        LocationUploadManager.getInstance().startUploadAtFixedRate();
        //send heartbeat.
        HeartbeatAlarmManager.getInstance().send();
        //send user info
        RequestUtil.sendUserInfo(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
