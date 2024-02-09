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
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.rpc.api.AssertUtils;
import team.idealstate.hyper.rpc.api.service.ServiceStarter;
import team.idealstate.hyper.rpc.impl.JacksonUtils;
import team.idealstate.hyper.rpc.impl.netty.entity.Heartbeat;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>ClientHeartbeatHandler</p>
 *
 * <p>创建于 2024/1/27 0:48</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class ClientHeartbeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(ClientHeartbeatHandler.class);

    private final ServiceStarter serviceStarter;
    private final ChannelGroup channelGroup;

    public ClientHeartbeatHandler(@NotNull ServiceStarter serviceStarter, ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
        AssertUtils.notNull(serviceStarter, "服务启动器不允许为 null");
        this.serviceStarter = serviceStarter;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.executor().scheduleAtFixedRate(() -> {
            Heartbeat heartbeat = new Heartbeat();
            heartbeat.setTimestamp(new Date().getTime());
            byte[] data;
            try {
                data = JacksonUtils.toJson(heartbeat).getBytes(StandardCharsets.UTF_8);
            } catch (JsonProcessingException e) {
                logger.catching(e);
                return;
            }
            ctx.writeAndFlush(Unpooled.copiedBuffer(data));
            logger.trace("[{}] 已向远程服务服务端发送心跳包", ctx.channel().remoteAddress());
        }, 0, 15, TimeUnit.SECONDS);
        channelGroup.add(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            try {
                Heartbeat heartbeat = JacksonUtils.toBean(((ByteBuf) msg).toString(StandardCharsets.UTF_8), Heartbeat.class);
                ReferenceCountUtil.release(msg);
                SocketAddress remoteAddress = ctx.channel().remoteAddress();
                if (heartbeat.getTimestamp() == null || new Date(heartbeat.getTimestamp()).after(new Date())) {
                    logger.error("[{}] 无效的心跳数据包：{}", remoteAddress, heartbeat);
                    return;
                }
                logger.trace("[{}] 接收到来自远程服务服务端的心跳包", remoteAddress);
                return;
            } catch (JsonProcessingException ignored) {
            }
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        logger.info("[{}] 远程服务服务端已离线", remoteAddress);
        ctx.close();
        close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (IdleState.READER_IDLE.equals(((IdleStateEvent) evt).state())) {
                SocketAddress remoteAddress = ctx.channel().remoteAddress();
                logger.info("[{}] 远程服务服务端已持续 60s 空闲状态", remoteAddress);
                ctx.close();
                logger.info("[{}] 已断开远程服务服务端", remoteAddress);
                close();
                return;
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    private void close() {
        serviceStarter.needClose();
    }
}
