package team.idealstate.hyper.rpc.impl.service.annotation;

import java.lang.annotation.*;

/**
 * <p>Implementation</p>
 *
 * <p>
 * 用于标记接口的本地实现
 * </p>
 *
 * <p>创建于 2024/2/9 9:07</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Implementation {

    /**
     * @return 实现当前接口的类的全类名
     */
    String value();
}
