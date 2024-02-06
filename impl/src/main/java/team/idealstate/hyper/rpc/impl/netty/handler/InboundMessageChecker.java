package team.idealstate.hyper.rpc.impl.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.rpc.api.service.entity.InvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.InvokeResult;
import team.idealstate.hyper.rpc.impl.netty.entity.Heartbeat;

import java.util.Arrays;
import java.util.List;

/**
 * <p>InboundMessageChecker</p>
 *
 * <p>Created on 2024/1/24 19:41</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class InboundMessageChecker extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(InboundMessageChecker.class);
    private static final List<Class<?>> MESSAGE_TYPES = Arrays.asList(
            Heartbeat.class, InvokeDetail.class, InvokeResult.class
    );

    public static boolean check(Object msg) {
        if (msg == null || !MESSAGE_TYPES.contains(msg.getClass())) {
            logger.error("入站信息必须是一个包含在限制列表 " + MESSAGE_TYPES + " 内类型的实例");
            return false;
        }
        return true;
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        if (check(msg)) {
            super.channelRead(ctx, msg);
        }
    }
}
