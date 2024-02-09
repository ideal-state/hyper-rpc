package team.idealstate.hyper.rpc.example.common.service;

import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.rpc.example.common.model.entity.Hello;
import team.idealstate.hyper.rpc.impl.service.annotation.Implementation;
import team.idealstate.hyper.rpc.impl.service.annotation.RemoteService;

/**
 * <p>HelloService</p>
 *
 * <p>创建于 2024/2/6 21:49</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.0
 */
@RemoteService
@Implementation("team.idealstate.hyper.rpc.example.client.service.HelloServiceImpl")
public interface HelloService {

    @NotNull
    Hello hello(@NotNull Hello hello);
}
