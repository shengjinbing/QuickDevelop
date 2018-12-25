package com.modesty.socket.server;



import com.modesty.socket.model.CollectServerProtobuf;
import com.modesty.socket.utils.SocketConstants;
import com.modesty.socket.utils.Utils;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

/**
 * @author wangzhiyuan
 * @since 2017/4/12
 */
final class ServerSocketManager {
    private final HashedWheelTimer hashedWheelTimer = new HashedWheelTimer();
    private final ServerBootstrap bootstrap = new ServerBootstrap();

    private final int port;
    private Channel channel;

    public static ServerSocketManager getInstance(){
        return InstanceHolder.serverSocketManager;
    }

    private static class InstanceHolder{
        private static final ServerSocketManager serverSocketManager = new ServerSocketManager(SocketConstants.DEFAULT_PORT);
    }

    private ServerSocketManager(int port) {
        this.port = port;
        initialize();
    }

    private void initialize(){
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.group(boss, worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 128);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();

                //decode
                p.addLast(new ProtobufVarint32FrameDecoder());
                p.addLast(new ProtobufDecoder(CollectServerProtobuf.CollectMessage.getDefaultInstance()));

                //encode
                p.addLast(new ProtobufVarint32LengthFieldPrepender());
                p.addLast(new ProtobufEncoder());

                p.addLast(new ServerChannelHandler());
            }
        });

    }

    public synchronized void start(){
        hashedWheelTimer.newTimeout(new TimerTask() {

            @Override
            public void run(Timeout timeout) throws Exception {
                final ChannelFuture f = bootstrap.bind(port).sync();
                channel = f.channel();

                if (f.isSuccess()) {
                    Utils.log("Server---server start successfully");
                } else {
                    Utils.log("Server---server start failed");
                }
            }
        },0, TimeUnit.MILLISECONDS);
    }

    private synchronized void close() {
        if (channel != null && (channel.isActive() || channel.isOpen())) {
            channel.close();
        }
    }
}
