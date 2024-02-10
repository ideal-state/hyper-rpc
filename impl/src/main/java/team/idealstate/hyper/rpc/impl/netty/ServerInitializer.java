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

package team.idealstate.hyper.rpc.impl.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.common.AssertUtils;
import team.idealstate.hyper.rpc.api.service.ServiceManager;
import team.idealstate.hyper.rpc.api.service.ServiceStarter;
import team.idealstate.hyper.rpc.impl.netty.handler.*;

import java.security.Key;

/**
 * <p>ServerInitializer</p>
 *
 * <p>创建于 2024/2/5 20:23</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.0
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    private final ServiceStarter serviceStarter;
    private final Key key;
    private final ServiceManager serviceManager;
    private final ChannelGroup channelGroup;

    /**
     * @param key RSA 密钥
     */
    public ServerInitializer(@NotNull ServiceStarter serviceStarter, @NotNull Key key, @NotNull ServiceManager serviceManager, ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
        AssertUtils.notNull(serviceStarter, "服务启动器不允许为 null");
        AssertUtils.notNull(key, "消息密钥不允许为 null");
        AssertUtils.notNull(serviceManager, "服务管理器不允许为 null");
        this.serviceStarter = serviceStarter;
        this.key = key;
        this.serviceManager = serviceManager;
    }

    @Override
    protected void initChannel(@NotNull SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        final ExceptionHandler exceptionHandler = new ExceptionHandler();
        pipeline.addLast(exceptionHandler)
                .addLast(new IdleStateHandler(0, 60, 0))
                .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2, true))
                .addLast(new LengthFieldPrepender(2, 0, false))
                .addLast(new SafeMessageCodec(key))
                .addLast(new OutboundMessageChecker())
                .addLast(new ServerHeartbeatHandler(channelGroup))
                .addLast(new InvokeDetailCodec())
                .addLast(new InvokeResultCodec())
                .addLast(new InboundMessageChecker())
                .addLast(new ServiceManagerAdapter(serviceManager))
                .addLast(exceptionHandler);
    }
}
