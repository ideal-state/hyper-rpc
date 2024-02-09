package team.idealstate.hyper.rpc.impl.service.annotation;

import java.lang.annotation.*;

/**
 * <p>RemoteService</p>
 *
 * <p>
 * 用于标记接口是否为远程服务；
 * </p>
 * <p>
 * 注意：当接口同时标记 {@link Implementation} 且其值有效时，
 * 当前接口应被视为本地服务
 * </p>
 *
 * <p>创建于 2024/2/9 9:03</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RemoteService {

    boolean value() default true;
}
