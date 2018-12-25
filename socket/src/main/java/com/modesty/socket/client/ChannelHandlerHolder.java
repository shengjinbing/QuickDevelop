package com.modesty.socket.client;


import io.netty.channel.ChannelHandler;

/**
 * Client-side interface to collect all {@link ChannelHandler}. By doing so, we can conveniently
 * use {@link ChannelHandlerHolder#handlers()} methods to reconnect to server.
 *
 * @author wangzhiyuan
 * @since 2018/6/25
 */
interface ChannelHandlerHolder {

    /**
     * Collection of {@link ChannelHandler} used for {@link io.netty.bootstrap.Bootstrap#handler(ChannelHandler)}
     * to connect server.
     *
     * @return Collection of {@link ChannelHandler}
     */
    ChannelHandler[] handlers();
}
