package com.haha.im.start;

import com.haha.im.codec.MsgDecoder;
import com.haha.im.codec.MsgEncoder;
import com.haha.im.handler.ConnectorClientHandler;
import com.haha.im.handler.ConnectorTransferHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Connect2TransferServer {
    private static final Logger logger = LoggerFactory.getLogger(Connect2TransferServer.class);

    public static void start(ApplicationContext applicationContext) {
        String[] transferUrls = new String[]{"localhost:9199"};
        for (String transferUrl : transferUrls) {
            String[] url = transferUrl.split(":");

            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            ChannelFuture f = b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new MsgDecoder());
                            p.addLast(new MsgEncoder());
                            p.addLast(applicationContext.getBean(ConnectorTransferHandler.class));
                        }
                    }).connect(url[0], Integer.parseInt(url[1]))
                    .addListener((ChannelFutureListener) future -> {
                        if (!future.isSuccess()) {
                            throw new Exception("[connector] connect to transfer failed! transfer url: " + transferUrl);
                        }
                    });

            try {
                f.get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.error("[connector] connect to transfer failed! transfer url: " + transferUrl, e);
            }
        }
    }
}
