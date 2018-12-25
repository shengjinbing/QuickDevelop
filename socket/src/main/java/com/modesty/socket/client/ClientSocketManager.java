package com.modesty.socket.client;



import com.modesty.socket.ConnectionLifecycleListener;
import com.modesty.socket.SocketConfig;
import com.modesty.socket.location.LocationDelegateManager;
import com.modesty.socket.location.LocationUploadManager;
import com.modesty.socket.model.CollectServerProtobuf;
import com.modesty.socket.utils.LoginStatusUtil;
import com.modesty.socket.utils.RequestUtil;
import com.modesty.socket.utils.SocketConstants;
import com.modesty.socket.utils.Utils;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

/**
 * @author wangzhiyuan
 * @since 2018/6/22
 */
public final class ClientSocketManager {
    private final HashedWheelTimer hashedWheelTimer = new HashedWheelTimer();
    private final Bootstrap bootstrap = new Bootstrap();
    private final NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private volatile boolean canReconnect = true;
    private volatile boolean isConnected = false;

    private Channel channel;
    private ConnectionLifecycleListener connectionLifecycleListener;

    public static ClientSocketManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final ClientSocketManager INSTANCE = new ClientSocketManager();
    }

    private ClientSocketManager() {
        initialize();
    }

    private synchronized void initialize() {
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.group(eventLoopGroup);

        final ConnectionWatchdog watchdog = createWatchdog();

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(watchdog.handlers());
            }
        });
    }

    boolean isConnected(){
        return isConnected;
    }

    boolean canReconnect() {
        return canReconnect;
    }

    private synchronized ConnectionWatchdog createWatchdog() {
        final ConnectionWatchdog watchdog = new ConnectionWatchdog() {
            @Override
            public ChannelHandler[] handlers() {
                return new ChannelHandler[]{
                        this,
                        //decode
                        new ProtobufVarint32FrameDecoder(),
                        new ProtobufDecoder(CollectServerProtobuf.CollectMessage.getDefaultInstance()),
                        //encode
                        new ProtobufVarint32LengthFieldPrepender(),
                        new ProtobufEncoder(),
                        //config idle state
                        new IdleStateHandler(SocketConstants.DEFAULT_READER_IDLE_TIME, SocketConstants.DEFAULT_WRITER_IDLE_TIME, SocketConstants.DEFAULT_READER_WRITER_IDLE_TIME),
                        //send and receive proto-type data.
                        new ClientChannelReader(),
                };
            }
        };

        return watchdog;
    }

    private synchronized void writeAndFlush(Object msg) {
        if(channel == null){
            Utils.log("socket channel is null when write and flush");
            return;
        }

        if(!(msg instanceof CollectServerProtobuf.CollectMessage)){
            Utils.log("msg is not CollectServerProtobuf.CollectMessage type when write and flush.");
            return;
        }

        try{
            channel.writeAndFlush(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized Channel getChannel(){
        return channel;
    }

    public synchronized void start() {
        if(isConnected){
            Utils.log("do not need to start, channel is connected");
            return;
        }
        canReconnect = true;
        isConnected = false;
        LocationDelegateManager.getInstance().loadDelegates();
        resetInetAddress();
        doConnection(SocketConstants.ZERO);
    }

    public synchronized void setConnectionLifecycleListener(ConnectionLifecycleListener connectionLifecycleListener){
        this.connectionLifecycleListener = connectionLifecycleListener;
    }

    public synchronized void reconnect(){
        Utils.log("It is " + (canReconnect ? "" : "not") + " allowed to reconnect");
        if(canReconnect){
            resetInetAddress();
            doConnection(SocketConstants.ZERO);
        }
    }

    private synchronized void resetInetAddress(){
        bootstrap.remoteAddress(SocketConfig.instance().getHost(),SocketConfig.instance().getPort());
    }

    private synchronized void doConnection(long delay){
        if(channel != null && channel.isOpen() && channel.isActive()){
            Utils.log("channel is active, no need to call doConnection(long delay)");
            return;
        }

        hashedWheelTimer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) {
                if(!canReconnect){
                    return;
                }
                final ChannelFuture future = bootstrap.connect();
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future){
                        if (future.isSuccess()) {
                            Utils.log("Client---connecting server succeeds");
                            onConnectSuccess(future.channel());
                        } else {
                            Utils.log("Client---connecting server fails，失败重试");
                            Utils.log(future.cause().getMessage());
                            onConnectFailure();
                        }
                    }
                });
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    private synchronized void onConnectSuccess(Channel channel) {
        this.isConnected = true;
        this.channel = channel;
        RequestUtil.login(channel);
        if(connectionLifecycleListener != null){
            connectionLifecycleListener.onConnectSuccess();
        }
    }

    private synchronized void onConnectFailure(){
        this.isConnected = false;
        if(canReconnect){
            doConnection(5 * SocketConstants.ONE_SECOND);
        }
        if(connectionLifecycleListener != null){
            connectionLifecycleListener.onConnectFailure();
        }
    }

    synchronized void onConnectionLost(){
        if(connectionLifecycleListener != null){
            connectionLifecycleListener.onConnectLost(canReconnect);
        }
    }

    private synchronized void closeChannelSafely(Channel channel){
        try{
            if(channel != null){
                channel.close();
                channel = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void close(){
       close(false);
    }

    public synchronized void close(boolean reconnect){
        //close channel
        closeChannelSafely(channel);
        //reset status
        isConnected = false;
        canReconnect = reconnect;
        LoginStatusUtil.setStatus(false);
        //cancel upload
        LocationUploadManager.getInstance().cancel();
        //cancel heartbeat
        HeartbeatAlarmManager.getInstance().cancel();
        //unload location delegate
        LocationDelegateManager.getInstance().unloadDelegates();
    }
}
