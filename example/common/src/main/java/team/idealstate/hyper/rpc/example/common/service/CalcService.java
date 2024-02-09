package team.idealstate.hyper.rpc.example.common.service;

import team.idealstate.hyper.rpc.impl.service.annotation.Implementation;
import team.idealstate.hyper.rpc.impl.service.annotation.RemoteService;

/**
 * <p>CalcService</p>
 *
 * <p>创建于 2024/2/9 13:23</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.2
 */
@RemoteService
@Implementation("team.idealstate.hyper.rpc.example.server.service.CalcServiceImpl")
public interface CalcService {

    long sum(int a, int b);
}
