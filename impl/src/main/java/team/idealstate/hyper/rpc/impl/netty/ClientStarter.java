package team.idealstate.hyper.rpc.impl.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.rpc.api.AssertUtils;
import team.idealstate.hyper.rpc.api.service.ServiceInvoker;
import team.idealstate.hyper.rpc.api.service.ServiceManager;
import team.idealstate.hyper.rpc.api.service.ServiceStarter;
import team.idealstate.hyper.rpc.api.service.entity.InvokeDetail;

import java.net.SocketAddress;
import java.security.Key;
import java.util.concurrent.TimeUnit;

/**
 * <p>ClientStarter</p>
 *
 * <p>创建于 2024/2/5 20:44</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ClientStarter implements ServiceStarter, ServiceInvoker {
    private static final Logger logger = LogManager.getLogger(ClientStarter.class);
    private final SocketAddress connectAddress;
    private final Key key;
    private final ServiceManager serviceManager;
    private final EventLoopGroup workerGroup;
    private final Bootstrap bootstrap;
    private final Object lock = new Object();
    private volatile boolean active = false;
    private volatile Channel connectChannel = null;
    private volatile boolean needClose = false;
    /**
     * @param key      RSA 密钥
     * @param nThreads 工作线程数
     */
    public ClientStarter(@NotNull SocketAddress connectAddress, @NotNull Key key, @NotNull ServiceManager serviceManager, int nThreads) {
        AssertUtils.notNull(connectAddress, "客户端连接地址不允许为 null");
        AssertUtils.notNull(key, "消息密钥不允许为 null");
        AssertUtils.notNull(serviceManager, "服务管理器不允许为 null");
        this.connectAddress = connectAddress;
        this.key = key;
        this.serviceManager = serviceManager;
        this.workerGroup = new NioEventLoopGroup(nThreads);
        this.bootstrap = new Bootstrap().group(workerGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(connectAddress)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 6000)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_SNDBUF, 65535)
                .option(ChannelOption.SO_RCVBUF, 65535)
                .handler(new ClientInitializer(this, this.key, this.serviceManager));
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
            ChannelFuture channelFuture = bootstrap.connect();
            try {
                channelFuture.sync();
            } catch (InterruptedException e) {
                logger.error("[{}] 线程中断，客户端未能启动完成", connectAddress);
                return;
            }
            this.needClose = false;
            this.active = true;
            this.connectChannel = channelFuture.channel();
            logger.info("[{}] 客户端启动完成", connectAddress);
        }
    }

    @Override
    public void close() {
        Channel channel = connectChannel;
        if (channel != null) {
            synchronized (lock) {
                channel = connectChannel;
                if (channel != null) {
                    try {
                        channel.close().sync();
                    } catch (InterruptedException e) {
                        logger.error("[{}] 线程中断，通道未能完成关闭", connectAddress);
                        return;
                    }
                    this.needClose = false;
                    this.active = false;
                    this.connectChannel = null;
                }
            }
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
    public void shutdown() {
        if (workerGroup.isTerminated()) {
            return;
        }
        synchronized (lock) {
            if (workerGroup.isTerminated()) {
                return;
            }
            this.needClose = false;
            this.active = false;
            this.connectChannel = null;
            serviceManager.shutdown();
            workerGroup.shutdownGracefully();
            while (true) {
                if (workerGroup.isTerminated()) {
                    logger.info("[{}] WorkerGroup 已关闭", connectAddress);
                    break;
                }
                logger.info("[{}] 等待 WorkerGroup 关闭", connectAddress);
                try {
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void invoke(@NotNull InvokeDetail invokeDetail) {
        AssertUtils.notNull(invokeDetail, "服务调用细节不允许为 null");
        Channel channel = connectChannel;
        if (!active || channel == null || !channel.isActive()) {
            throw new UnsupportedOperationException("服务调用器尚未准备完成");
        }
        channel.writeAndFlush(invokeDetail);
    }
}
