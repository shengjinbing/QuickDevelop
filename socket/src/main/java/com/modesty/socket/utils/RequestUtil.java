package com.modesty.socket.utils;

import android.os.Build;
import android.text.TextUtils;


import com.modesty.socket.SocketConfig;
import com.modesty.socket.location.Location;
import com.modesty.socket.model.CollectServerProtobuf;

import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.Channel;

/**
 * @author wangzhiyuan
 * @since 2018/6/26
 */

public final class RequestUtil {

    private RequestUtil(){}

    public static boolean checkNotNull(Channel channel){
        Utils.log(channel == null ? "checkNotNull(channel), channel is null." : "checkNotNull(channel), channel is not null.");
        return channel != null;
    }

    public static void login(Channel channel){
        if(checkNotNull(channel)){
            channel.writeAndFlush(RequestModelUtil.buildLogin());
            Utils.log("login");
        }
    }

    public static void sendHeartbeat(Channel channel){
        if(checkNotNull(channel)){
            channel.writeAndFlush(RequestModelUtil.buildHeartbeat());
            Utils.log("send heartbeat");
        }
    }

    public static void sendUserInfo(Channel channel){
        if(checkNotNull(channel)){
            channel.writeAndFlush(RequestModelUtil.buildUserInfo());
            Utils.log("send user info");
        }
    }

    public static void sendCoordinates(Channel channel, Iterable<? extends CollectServerProtobuf.CoordinateRequest> coordinateRequests, Location newLocation){
        if(checkNotNull(channel)){
            channel.writeAndFlush(RequestModelUtil.buildCoordinatePackage(coordinateRequests,newLocation));
            Utils.log("send coordinates");
        }
    }

    public static void sendPushMessage(Channel channel, long seqId, String serverId){
        if(checkNotNull(channel)){
            channel.writeAndFlush(RequestModelUtil.buildPushMessage(seqId, serverId));
            Utils.log("send heartbeat");
        }
    }

    public static final class RequestModelUtil{
        private static final AtomicInteger sAtomicInteger = new AtomicInteger(0);

        private RequestModelUtil(){}

        public static CollectServerProtobuf.CollectMessage buildPushMessage(long seqId, String serverId){
            final CollectServerProtobuf.PushMessageRequest pushMessageRequest = CollectServerProtobuf.PushMessageRequest.newBuilder().
                    setSeqId(seqId).
                    setServerId(serverId == null ? "" : serverId).
                    setTimestamp(System.currentTimeMillis()).
                    build();

            final CollectServerProtobuf.CollectMessage collectMessage = CollectServerProtobuf.CollectMessage.newBuilder().
                    setData(pushMessageRequest.toByteString()).
                    setMessageType(CollectServerProtobuf.MessageType.PUSH_MESSAGE_REQUEST).
                    build();

            return collectMessage;
        }

        public static CollectServerProtobuf.CoordinateRequest buildCoordinate(Location location){
            if(location == null){
                Utils.log("buildCoordinate--->location is null.");
                return null;
            }

            final CollectServerProtobuf.CoordinateRequest coordinateRequest = CollectServerProtobuf.CoordinateRequest.newBuilder().
                    setLat(location.lat).
                    setLon(location.lng).
                    setCourse(location.bearing).
                    setSpeed(location.speed).
                    setProvider(location.locType).
                    setTimestamp(System.currentTimeMillis()).
                    setType(CollectServerProtobuf.CoordinateType.SOSO_GCJ_COORDINATE).
                    build();

            return coordinateRequest;
        }

        public static CollectServerProtobuf.CollectMessage buildCoordinatePackage(Iterable<? extends CollectServerProtobuf.CoordinateRequest> coordinateRequests, Location location){
            final String uid = SocketConfig.instance().getUid();
            final String cityCode = (location != null && !TextUtils.isEmpty(location.cityCode) ? location.cityCode : "");

            final CollectServerProtobuf.CoordinatePackageRequest coordinatePackageRequest = CollectServerProtobuf.CoordinatePackageRequest.newBuilder().
                    setTimestamp(System.currentTimeMillis()).
                    setUserId(TextUtils.isEmpty(uid) ? "" : uid).
                    setCityId(cityCode).
                    addAllCoordinate(coordinateRequests).
                    build();

            final CollectServerProtobuf.CollectMessage collectMessage = CollectServerProtobuf.CollectMessage.newBuilder().
                    setMessageType(CollectServerProtobuf.MessageType.COORDINATE_PULL).
                    setData(coordinatePackageRequest.toByteString()).
                    build();

            return collectMessage;
        }

        public static CollectServerProtobuf.CollectMessage buildLogin(){
            final String phone = SocketConfig.instance().getPhone();
            final String uid = SocketConfig.instance().getUid();
            final String token = SocketConfig.instance().getToken();
            final String sign = SignUtil.createSign(token);

            final CollectServerProtobuf.LoginRequest loginRequest = CollectServerProtobuf.LoginRequest.newBuilder().
                    setVersion(SocketConfig.PROTO_VERSION).
                    setPhone(TextUtils.isEmpty(phone) ? "" : phone).
                    setUserId(TextUtils.isEmpty(uid) ? "" : uid).
                    setToken(TextUtils.isEmpty(token) ? "" : token).
                    setSign(TextUtils.isEmpty(sign) ? "" : sign).
                    build();

            final CollectServerProtobuf.CollectMessage collectMessage = CollectServerProtobuf.CollectMessage.newBuilder().
                    setData(loginRequest.toByteString()).
                    setMessageType(CollectServerProtobuf.MessageType.LOGIN_REQUEST).
                    build();

            return collectMessage;
        }

        public static CollectServerProtobuf.CollectMessage buildHeartbeat(){
            final int seqId = sAtomicInteger.getAndIncrement();
            final CollectServerProtobuf.PingMessage pingMessage = CollectServerProtobuf.PingMessage.newBuilder().setSeqId(seqId).build();
            final CollectServerProtobuf.CollectMessage collectMessage = CollectServerProtobuf.CollectMessage.newBuilder().
                    setMessageType(CollectServerProtobuf.MessageType.PING).
                    setData(pingMessage.toByteString()).
                    build();
            return collectMessage;
        }

        public static CollectServerProtobuf.CollectMessage buildUserInfo(){
            final String versionName = Utils.getVersionName(SocketConfig.instance().getAppContext());
            final String networkTypeName = Utils.getNetworkTypeName(SocketConfig.instance().getAppContext());
            final String uid = SocketConfig.instance().getUid();

            final CollectServerProtobuf.UserInfoMessage userInfoMessage = CollectServerProtobuf.UserInfoMessage.newBuilder().
                    setDeviceType(CollectServerProtobuf.OS.ANDROID).
                    setManufacturer(Build.MANUFACTURER).
                    setModel(Build.MODEL).
                    setVersion(SocketConfig.PROTO_VERSION).
                    setAppVersion(TextUtils.isEmpty(versionName) ? "" : versionName).
                    setNettype(TextUtils.isEmpty(networkTypeName) ? "" : networkTypeName).
                    setUserId(TextUtils.isEmpty(uid) ? "" : uid).
                    build();

            final CollectServerProtobuf.CollectMessage collectMessage = CollectServerProtobuf.CollectMessage.newBuilder().
                    setData(userInfoMessage.toByteString()).
                    setMessageType(CollectServerProtobuf.MessageType.USERINFO).
                    build();

            return collectMessage;
        }
    }
}
