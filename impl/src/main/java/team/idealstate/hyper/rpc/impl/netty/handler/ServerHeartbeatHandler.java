package team.idealstate.hyper.rpc.impl.netty.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.rpc.impl.JacksonUtils;
import team.idealstate.hyper.rpc.impl.netty.entity.Heartbeat;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * <p>ServerHeartbeatHandler</p>
 *
 * <p>Created on 2024/1/26 22:27</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class ServerHeartbeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(ServerHeartbeatHandler.class);
    private final boolean respond;

    public ServerHeartbeatHandler() {
        this(true);
    }

    public ServerHeartbeatHandler(boolean respond) {
        this.respond = respond;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("[{}] 有新的远程服务客户端连接", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            try {
                Heartbeat heartbeat = JacksonUtils.toBean(((ByteBuf) msg).toString(StandardCharsets.UTF_8), Heartbeat.class);
                ReferenceCountUtil.release(msg);
                Date now = new Date();
                SocketAddress remoteAddress = ctx.channel().remoteAddress();
                if (heartbeat.getTimestamp() == null || new Date(heartbeat.getTimestamp()).after(now)) {
                    logger.error("[{}] 非法的心跳数据包：{}", remoteAddress, heartbeat);
                    return;
                }
                logger.trace("[{}] 接收到来自远程服务客户端的心跳包", remoteAddress);
                if (respond) {
                    heartbeat.setTimestamp(now.getTime());
                    byte[] data = JacksonUtils.toJson(heartbeat).getBytes(StandardCharsets.UTF_8);
                    ByteBuf buf = ctx.alloc().buffer(data.length);
                    buf.writeBytes(data);
                    ctx.writeAndFlush(buf);
                    logger.trace("[{}] 已向远程服务客户端回复心跳包", remoteAddress);
                }
                return;
            } catch (JsonProcessingException ignored) {
            }
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        logger.info("[{}] 远程服务客户端已离线", remoteAddress);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (IdleState.WRITER_IDLE.equals(((IdleStateEvent) evt).state())) {
                SocketAddress remoteAddress = ctx.channel().remoteAddress();
                logger.info("[{}] 远程服务客户端已持续 60s 空闲状态", remoteAddress);
                ctx.close();
                logger.info("[{}] 已断开远程服务客户端", remoteAddress);
                return;
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
