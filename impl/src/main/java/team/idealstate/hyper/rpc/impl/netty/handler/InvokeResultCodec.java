package team.idealstate.hyper.rpc.impl.netty.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.idealstate.hyper.rpc.api.service.entity.InvokeResult;
import team.idealstate.hyper.rpc.impl.JacksonUtils;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>InvokeResultCodec</p>
 *
 * <p>Created on 2024/1/24 18:14</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class InvokeResultCodec extends MessageToMessageCodec<ByteBuf, InvokeResult> {

    private static final Logger logger = LogManager.getLogger(InvokeResultCodec.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, InvokeResult msg, List<Object> out) throws Exception {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        logger.debug("[{}] 开始编码消息：{}", remoteAddress, msg);
        byte[] data = JacksonUtils.toJson(msg).getBytes(StandardCharsets.UTF_8);
        logger.debug("[{}] 已编码消息为：{}", remoteAddress, data.length);
        ByteBuf buf = ctx.alloc().buffer(data.length);
        buf.writeBytes(data);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        try {
            SocketAddress remoteAddress = ctx.channel().remoteAddress();
            logger.debug("[{}] 开始解码消息：{}", remoteAddress, msg.readableBytes());
            out.add(JacksonUtils.toBean(msg.toString(StandardCharsets.UTF_8), InvokeResult.class));
            logger.debug("[{}] 已解码消息为：{}", remoteAddress, out.get(0));
            return;
        } catch (JsonProcessingException ignored) {
        }
        ByteBuf buf = ctx.alloc().buffer(msg.readableBytes());
        buf.writeBytes(msg);
        out.add(buf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof EncoderException || cause instanceof DecoderException) {
            logger.debug("无效的消息", cause);
            return;
        }
        super.exceptionCaught(ctx, cause);
    }
}
