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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.common.AesUtils;
import team.idealstate.hyper.common.AssertUtils;
import team.idealstate.hyper.common.Base64Utils;
import team.idealstate.hyper.common.RsaUtils;
import team.idealstate.hyper.rpc.impl.JacksonUtils;
import team.idealstate.hyper.rpc.impl.netty.entity.SafeMessage;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

/**
 * <p>SafeMessageCodec</p>
 *
 * <p>创建于 2024/2/5 15:07</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class SafeMessageCodec extends MessageToMessageCodec<ByteBuf, ByteBuf> {

    private static final Logger logger = LogManager.getLogger(SafeMessageCodec.class);

    private final Key key;

    /**
     * @param key RSA 密钥
     */
    public SafeMessageCodec(@NotNull Key key) {
        AssertUtils.notNull(key, "消息密钥不允许为 null");
        this.key = key;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        final int readableBytes = msg.readableBytes();
        byte[] data = new byte[readableBytes];
        msg.readBytes(data);
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        logger.trace("[{}] 开始加密消息：{}", remoteAddress, data.length);
        final SafeMessage safeMessage = new SafeMessage();
        final byte[] randomKey = AesUtils.generateRandomKey();
        safeMessage.setKey(RsaUtils.encrypt(key, randomKey));
        safeMessage.setData(AesUtils.encrypt(randomKey, Base64Utils.encode(data)));
        final byte[] encrypted = Base64Utils.encode(JacksonUtils.toJson(safeMessage).getBytes(StandardCharsets.UTF_8));
        logger.trace("[{}] 已加密消息为：{}", remoteAddress, encrypted.length);
        ByteBuf buf = ctx.alloc().buffer(encrypted.length);
        buf.writeBytes(encrypted);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        final int readableBytes = msg.readableBytes();
        byte[] data = new byte[readableBytes];
        msg.readBytes(data);
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        logger.trace("[{}] 开始解密消息：{}", remoteAddress, data.length);
        SafeMessage safeMessage = JacksonUtils.toBean(new String(Base64Utils.decode(data), StandardCharsets.UTF_8), SafeMessage.class);
        final byte[] randomKey = RsaUtils.decrypt(key, safeMessage.getKey());
        final byte[] decrypted = Base64Utils.decode(AesUtils.decrypt(randomKey, safeMessage.getData()));
        logger.trace("[{}] 已解密消息为：{}", remoteAddress, decrypted.length);
        ByteBuf buf = ctx.alloc().buffer(decrypted.length);
        buf.writeBytes(decrypted);
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
