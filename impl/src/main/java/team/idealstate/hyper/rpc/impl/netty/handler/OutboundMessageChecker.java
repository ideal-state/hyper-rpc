package team.idealstate.hyper.rpc.impl.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>OutboundMessageChecker</p>
 *
 * <p>Created on 2024/1/24 19:37</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class OutboundMessageChecker extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(OutboundMessageChecker.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf) {
            super.write(ctx, msg, promise);
            return;
        }
        logger.error("出站信息必须是一个 ByteBuf 实例");
    }
}
