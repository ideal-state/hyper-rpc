package team.idealstate.hyper.rpc.example.server.service;

import team.idealstate.hyper.rpc.example.common.service.CalcService;

/**
 * <p>CalcServiceImpl</p>
 *
 * <p>创建于 2024/2/9 13:23</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.2
 */
public class CalcServiceImpl implements CalcService {
    @Override
    public long sum(int a, int b) {
        return a + b;
    }
}
