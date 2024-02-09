/*
 * Copyright 2024 ideal-state
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import team.idealstate.hyper.rpc.api.service.entity.InvokeDetail;
import team.idealstate.hyper.rpc.impl.JacksonUtils;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>InvokeDetailCodec</p>
 *
 * <p>Created on 2024/1/24 18:14</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class InvokeDetailCodec extends MessageToMessageCodec<ByteBuf, InvokeDetail> {

    private static final Logger logger = LogManager.getLogger(InvokeDetailCodec.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, InvokeDetail msg, List<Object> out) throws Exception {
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
            out.add(JacksonUtils.toBean(msg.toString(StandardCharsets.UTF_8), InvokeDetail.class));
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
