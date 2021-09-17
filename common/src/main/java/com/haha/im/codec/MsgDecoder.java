package com.haha.im.codec;

import com.google.protobuf.Message;
import com.haha.im.parse.ParseMsgUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MsgDecoder extends ByteToMessageDecoder {

    /**
     * | --- 4 bytes --- | --- 4 bytes --- | --- length bytes --- |
     * |     length      |  code(msg_type) |       msg            |
     */

    private final ParseMsgUtil parseService = new ParseMsgUtil();

    private static final Logger logger = LoggerFactory.getLogger(MsgDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // todo 对于channel, 代码什么时候会走到这
        // todo 现在的实现是否会堆积消息???
        try{
            byteBuf.markReaderIndex();
            if(byteBuf.readableBytes() < 4) {
                return;
            }
            int length = byteBuf.readInt();
            if(length < 0) {
                channelHandlerContext.close();
                logger.error("decoder length less than 0, close this channel");
                return;
            }
            // already use 4 bytes for length(a int value)
            if(length > byteBuf.readableBytes() - 4) {
                byteBuf.resetReaderIndex();
                return;
            }
            int code = byteBuf.readInt();
            ByteBuf bf = Unpooled.buffer(length);
            byteBuf.readBytes(bf);
            Message msg = parseService.parse(code, bf.array());
            list.add(msg);
        }catch (Exception e) {
            logger.error("decode error", e);
        }
    }
}
