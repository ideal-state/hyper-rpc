package team.idealstate.hyper.rpc.example.client.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.rpc.api.AssertUtils;
import team.idealstate.hyper.rpc.example.common.model.entity.Hello;
import team.idealstate.hyper.rpc.example.common.service.HelloService;

import java.time.Instant;

/**
 * <p>HelloServiceImpl</p>
 *
 * <p>创建于 2024/2/6 21:58</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.0
 */
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LogManager.getLogger(HelloServiceImpl.class);

    @Override
    public @NotNull Hello hello(@NotNull Hello hello) {
        AssertUtils.notNull(hello, "打招呼信息不允许为 null");
        logger.info("[{}]: {}", hello.getTimestamp(), hello.getMessage());
        hello.setMessage("你说啥？没听到！声微饭否？");
        hello.setTimestamp(Instant.now());
        return hello;
    }
}
