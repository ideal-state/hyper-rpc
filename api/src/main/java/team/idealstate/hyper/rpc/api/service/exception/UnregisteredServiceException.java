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

package team.idealstate.hyper.rpc.api.service.exception;

import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.common.AssertUtils;

/**
 * <p>UnregisteredServiceException</p>
 *
 * <p>创建于 2024/2/4 13:34</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class UnregisteredServiceException extends Exception {
    private static final long serialVersionUID = -2454848777892199600L;
    private final Class<?> serviceInterface;

    public UnregisteredServiceException(@NotNull Class<?> serviceInterface) {
        super(String.format("未注册的服务接口 %s", checkServiceInterface(serviceInterface).getTypeName()));
        this.serviceInterface = serviceInterface;
    }

    private static Class<?> checkServiceInterface(Class<?> serviceInterface) {
        AssertUtils.notNull(serviceInterface, "服务接口不能为 null");
        if (!serviceInterface.isInterface()) {
            throw new IllegalArgumentException(serviceInterface.getTypeName() + " 不是个接口");
        }
        return serviceInterface;
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }
}
