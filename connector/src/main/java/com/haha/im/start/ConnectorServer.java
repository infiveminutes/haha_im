package com.haha.im.start;

import com.haha.im.codec.MsgDecoder;
import com.haha.im.codec.MsgEncoder;
import com.haha.im.handler.ConnectorClientHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConnectorServer {
    private static final Logger logger = LoggerFactory.getLogger(ConnectorServer.class);

    public static void start(int port, ApplicationContext applicationContext) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        // codec
                        pipeline.addLast(new MsgDecoder());
                        pipeline.addLast(new MsgEncoder());
                        // message handler
                        pipeline.addLast(applicationContext.getBean(ConnectorClientHandler.class));
                    }
                });

        ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("[connector] start successfully at port {}, waiting for clients to connect...", port);
            } else {
                throw new Exception("[connector] start failed");
            }
        });

        try {
            f.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("bind timeout", e);
        }
    }
}