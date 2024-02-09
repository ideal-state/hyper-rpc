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
