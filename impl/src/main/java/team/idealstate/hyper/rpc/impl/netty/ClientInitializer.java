package team.idealstate.hyper.rpc.impl.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.rpc.api.AssertUtils;
import team.idealstate.hyper.rpc.api.service.ServiceManager;
import team.idealstate.hyper.rpc.api.service.ServiceStarter;
import team.idealstate.hyper.rpc.impl.netty.handler.*;

import java.security.Key;

/**
 * <p>ClientInitializer</p>
 *
 * <p>创建于 2024/2/5 20:23</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
final class ClientInitializer extends ChannelInitializer<SocketChannel> {
    private final ServiceStarter serviceStarter;
    private final Key key;
    private final ServiceManager serviceManager;

    /**
     * @param key RSA 密钥
     */
    public ClientInitializer(@NotNull ServiceStarter serviceStarter, @NotNull Key key, @NotNull ServiceManager serviceManager) {
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
                .addLast(new IdleStateHandler(60, 0, 0))
                .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2, true))
                .addLast(new LengthFieldPrepender(2, 0, false))
                .addLast(new SafeMessageCodec(key))
                .addLast(new ClientHeartbeatHandler(serviceStarter))
                .addLast(new OutboundMessageChecker())
                .addLast(new InvokeDetailCodec())
                .addLast(new InvokeResultCodec())
                .addLast(new InboundMessageChecker())
                .addLast(new ServiceManagerAdapter(serviceManager))
                .addLast(exceptionHandler);
    }
}
