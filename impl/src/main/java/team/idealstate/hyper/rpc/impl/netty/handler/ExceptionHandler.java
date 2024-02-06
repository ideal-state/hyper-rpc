package team.idealstate.hyper.rpc.impl.netty.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * <p>ExceptionHandler</p>
 *
 * <p>创建于 2024/2/6 22:32</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelDuplexHandler {

    private static final Logger logger = LogManager.getLogger(ExceptionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            logger.debug("catching", cause);
            return;
        }
        super.exceptionCaught(ctx, cause);
    }
}
