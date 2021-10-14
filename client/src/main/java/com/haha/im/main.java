package com.haha.im;

import com.haha.im.client.UserClient;
import com.haha.im.codec.MsgDecoder;
import com.haha.im.codec.MsgEncoder;
import com.haha.im.handler.ClientConnectorHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class main {
    public static void main(String[] args) {
        UserClient userClient = new UserClient(3, 3, 300000L);
        ClientConnectorHandler clientConnectorHandler = new ClientConnectorHandler(userClient);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            // codec
                            pipeline.addLast(new MsgDecoder());
                            pipeline.addLast(new MsgEncoder());
                            // message handler
                            pipeline.addLast(clientConnectorHandler);
                        }
                    });

            // Start the connection attempt.
            Channel ch = b.connect("localhost", 9099).sync().channel();

            userClient.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            // login
            System.out.println("please enter userName:");
            String userName = in.readLine();
            System.out.println("please enter userName:");
            String passwd = in.readLine();
            if(!userClient.login(userName, passwd)) {
                throw new Exception("login fail");
            }
            if(!userClient.connect()) {
                throw new Exception("connect fail");
            }
            for (; ; ) {
                String line = in.readLine();
                if (line == null) {
                    continue;
                }
                userClient.sendMsg(2L, line);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
