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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.common.AssertUtils;
import team.idealstate.hyper.rpc.api.service.ServiceInvoker;
import team.idealstate.hyper.rpc.api.service.ServiceManager;
import team.idealstate.hyper.rpc.api.service.ServiceStarter;
import team.idealstate.hyper.rpc.api.service.entity.InvokeDetail;

import java.net.SocketAddress;
import java.security.Key;
import java.util.concurrent.TimeUnit;

/**
 * <p>ServerStarter</p>
 *
 * <p>创建于 2024/2/5 20:44</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.0
 */
public class ServerStarter implements ServiceStarter, ServiceInvoker {
    private static final Logger logger = LogManager.getLogger(ServerStarter.class);
    private final SocketAddress bindAddress;
    private final Key key;
    private final ServiceManager serviceManager;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final ServerBootstrap bootstrap;
    private final Object lock = new Object();
    private volatile boolean active = false;
    private volatile Channel bindChannel = null;
    private volatile boolean needClose = false;
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * @param key      RSA 密钥
     * @param nThreads 工作线程数
     */
    public ServerStarter(@NotNull SocketAddress bindAddress, @NotNull Key key, @NotNull ServiceManager serviceManager, int nThreads) {
        AssertUtils.notNull(bindAddress, "服务端监听地址不允许为 null");
        AssertUtils.notNull(key, "消息密钥不允许为 null");
        AssertUtils.notNull(serviceManager, "服务管理器不允许为 null");
        this.bindAddress = bindAddress;
        this.key = key;
        this.serviceManager = serviceManager;
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(nThreads);
        this.bootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(bindAddress)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 6000)
                .option(ChannelOption.SO_RCVBUF, 65535)
                .childOption(ChannelOption.SO_SNDBUF, 65535)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ServerInitializer(this, this.key, this.serviceManager, channelGroup));
    }

    @Override
    public void startup() {
        if (active) {
            return;
        }
        synchronized (lock) {
            if (active) {
                return;
            }
            ChannelFuture channelFuture = bootstrap.bind();
            try {
                channelFuture.sync();
                logger.info("[{}] 服务端启动完成", bindAddress);
            } catch (InterruptedException e) {
                logger.error("[{}] 线程中断，服务端未能启动完成", bindAddress);
                return;
            }
            this.needClose = false;
            this.active = true;
            this.bindChannel = channelFuture.channel();
        }
    }

    @Override
    public void needClose() {
        this.needClose = true;
    }

    @Override
    public boolean isNeedClose() {
        return needClose;
    }

    @Override
    public void close() {
        Channel channel = bindChannel;
        if (channel != null) {
            synchronized (lock) {
                channel = bindChannel;
                if (channel != null) {
                    try {
                        channel.close().sync();
                        channelGroup.clear();
                    } catch (InterruptedException e) {
                        logger.error("[{}] 线程中断，通道未能完成关闭", bindAddress);
                        return;
                    }
                    this.needClose = false;
                    this.active = false;
                    this.bindChannel = null;
                }
            }
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void shutdown() {
        if (bossGroup.isTerminated() && workerGroup.isTerminated()) {
            return;
        }
        synchronized (lock) {
            if (bossGroup.isTerminated() && workerGroup.isTerminated()) {
                return;
            }
            this.needClose = false;
            this.active = false;
            this.bindChannel = null;
            serviceManager.shutdown();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            while (true) {
                if (bossGroup.isTerminated()) {
                    logger.info("[{}] BossGroup 已关闭", bindAddress);
                    break;
                }
                logger.info("[{}] 等待 BossGroup 关闭", bindAddress);
                try {
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException ignored) {
                }
            }
            while (true) {
                if (workerGroup.isTerminated()) {
                    logger.info("[{}] WorkerGroup 已关闭", bindAddress);
                    break;
                }
                logger.info("[{}] 等待 WorkerGroup 关闭", bindAddress);
                try {
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException ignored) {
                }
            }
            channelGroup.clear();
        }
    }

    @Override
    public void invoke(@NotNull InvokeDetail invokeDetail) {
        AssertUtils.notNull(invokeDetail, "服务调用细节不允许为 null");
        channelGroup.writeAndFlush(invokeDetail);
    }
}
