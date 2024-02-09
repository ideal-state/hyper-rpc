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

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;
import team.idealstate.hyper.rpc.api.TypeUtils;
import team.idealstate.hyper.rpc.api.service.ServiceManager;
import team.idealstate.hyper.rpc.api.service.entity.InvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.InvokeResult;

import java.net.SocketAddress;

/**
 * <p>ServiceManagerAdapter</p>
 *
 * <p>Created on 2024/1/24 20:26</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class ServiceManagerAdapter extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(ServiceManagerAdapter.class);

    private final ServiceManager serviceManager;

    public ServiceManagerAdapter(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InboundMessageChecker.check(msg);
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        if (msg instanceof InvokeDetail) {
            logger.debug("[{}] 接收到远程调用服务细节：{}", remoteAddress, msg);
            ctx.executor().submit(() -> {
                InvokeResult result;
                try {
                    result = serviceManager.invoke((InvokeDetail) msg);
                } catch (Throwable e) {
                    logger.catching(e);
                    result = new InvokeResult(
                            ((InvokeDetail) msg).getId(),
                            InvokeResult.Code.FAIL.getCode(),
                            TypeUtils.getActualClassName(Type.getType(String.class)),
                            e.getMessage()
                    );
                }
                ctx.writeAndFlush(result);
            });
            return;
        } else if (msg instanceof InvokeResult) {
            logger.debug("[{}] 接收到远程调用服务结果：{}", remoteAddress, msg);
            serviceManager.callback((InvokeResult) msg);
            return;
        }
        logger.debug("[{}] 未接收到任何远程调用服务数据：{}", remoteAddress, msg);
    }
}
