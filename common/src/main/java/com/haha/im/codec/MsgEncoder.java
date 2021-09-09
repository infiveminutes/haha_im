package com.haha.im.codec;

import com.google.protobuf.Message;
import com.haha.im.model.enums.MsgType;
import com.haha.im.model.protobuf.Msg;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MsgEncoder extends MessageToByteEncoder<Message> {

    private Logger logger = LoggerFactory.getLogger(MsgEncoder.class);

    private static final Map<Class<? extends Message>, MsgType> msg2type;

    static {
        msg2type = new HashMap<>();
        msg2type.put(Msg.AckMsg.class, MsgType.ACK);
        msg2type.put(Msg.ChatMsg.class, MsgType.CHAT);
        msg2type.put(Msg.InternalMsg.class, MsgType.INTERNAL);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        try {
            byte[] bytes = message.toByteArray();
            int code = Optional.ofNullable(msg2type.get(message.getClass()))
                    .orElseThrow(() -> new Exception("can't encode this class"+message.getClass().toString()))
                    .getCode();
            int length = bytes.length;
            // int=4type
            ByteBuf buf = Unpooled.buffer(8 + length);
            buf.writeInt(length);
            buf.writeInt(code);
            buf.writeBytes(bytes);
            byteBuf.writeBytes(buf);
        }catch (Exception e) {
            logger.error("encode error", e);
        }
    }
}
