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

package team.idealstate.hyper.rpc.api.service.spi;

import org.apache.logging.log4j.core.config.Order;
import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.rpc.api.AssertUtils;
import team.idealstate.hyper.rpc.api.service.ServiceManager;

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * <p>ServiceManagerProvider</p>
 *
 * <p>创建于 2024/2/4 11:16</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ServiceManagerProvider {

    @NotNull
    static ServiceManagerProvider getInstance() {
        ServiceManagerProvider current = ProviderSingleton.serviceManagerProvider;
        if (current == null) {
            synchronized (ServiceManagerProvider.class) {
                current = ProviderSingleton.serviceManagerProvider;
                if (current == null) {
                    final Iterator<ServiceManagerProvider> serviceLoader = ServiceLoader.load(
                            ServiceManagerProvider.class,
                            Optional.ofNullable(Thread.currentThread().getContextClassLoader())
                                    .orElse(ServiceManagerProvider.class.getClassLoader())
                    ).iterator();
                    Integer orderVal = null;
                    while (serviceLoader.hasNext()) {
                        final ServiceManagerProvider next = serviceLoader.next();
                        final Order order = next.getClass().getDeclaredAnnotation(Order.class);
                        final int nextOrderVal = order == null ? 0 : order.value();
                        if (orderVal == null || nextOrderVal > orderVal) {
                            current = next;
                            orderVal = nextOrderVal;
                        }
                    }
                    ProviderSingleton.serviceManagerProvider = current;
                    AssertUtils.notNull(current, "未找到 ServiceManagerProvider 的具体实现");
                    assert current != null;
                }
            }
        }
        return current;
    }

    @NotNull
    ServiceManager get();
}
