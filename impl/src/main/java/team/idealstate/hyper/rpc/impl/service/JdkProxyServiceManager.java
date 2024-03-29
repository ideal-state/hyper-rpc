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

package team.idealstate.hyper.rpc.impl.service;

import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.common.ClassUtils;
import team.idealstate.hyper.rpc.api.future.Future;
import team.idealstate.hyper.rpc.api.service.AbstractServiceManager;
import team.idealstate.hyper.rpc.api.service.InvokeInformationHelper;
import team.idealstate.hyper.rpc.api.service.ServiceInvoker;
import team.idealstate.hyper.rpc.api.service.entity.ActualInvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.ActualInvokeResult;
import team.idealstate.hyper.rpc.api.service.entity.InvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.InvokeResult;
import team.idealstate.hyper.rpc.api.service.exception.ServiceInvocationFailureException;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>JdkProxyServiceManager</p>
 *
 * <p>
 * 已弃用，请使用 {@link StdServiceManager}
 * </p>
 *
 * <p>创建于 2024/2/6 11:48</p>
 *
 * @author ketikai
 * @version 1.0.1
 * @since 1.0.0
 */
@Deprecated
public final class JdkProxyServiceManager extends AbstractServiceManager {

    private final AtomicReference<ServiceInvoker> serviceInvoker = new AtomicReference<>(null);
    private final int timeout;

    public JdkProxyServiceManager(
            @NotNull InvokeInformationHelper invokeInformationHelper,
            @NotNull ExecutorService executorService
    ) {
        this(invokeInformationHelper, executorService, Future.DEFAULT_TIMEOUT);
    }

    public JdkProxyServiceManager(
            @NotNull InvokeInformationHelper invokeInformationHelper,
            @NotNull ExecutorService executorService,
            int timeout
    ) {
        super(invokeInformationHelper, executorService);
        this.timeout = timeout;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    protected <T> @NotNull T newServiceInstance(@NotNull Class<T> serviceInterface) {
        ClassLoader classLoader = getClassLoader();
        return (T) Proxy.newProxyInstance(
                classLoader,
                new Class[]{serviceInterface},
                (proxy, method, args) -> {
                    if (Object.class.equals(method.getDeclaringClass())) {
                        switch (method.getName()) {
                            case "hashCode":
                                return System.identityHashCode(proxy);
                            case "equals":
                                return Objects.equals(proxy, args[0]);
                            case "toString":
                                return proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy));
                        }
                    }
                    ActualInvokeDetail actualInvokeDetail = new ActualInvokeDetail();
                    actualInvokeDetail.setId(UUID.randomUUID().toString());
                    actualInvokeDetail.setServiceInterface(serviceInterface);
                    actualInvokeDetail.setMethod(method);
                    actualInvokeDetail.setArgumentObjects(args);
                    InvokeDetail invokeDetail = invokeInformationHelper.convert(actualInvokeDetail, classLoader);
                    Future<InvokeResult> resultFuture = call(invokeDetail);
                    final Class<?> returnType = method.getReturnType();
                    InvokeResult invokeResult = resultFuture.get(getTimeout());
                    final Class<?> dataType = ClassUtils.forDesc(invokeResult.getDataType(), false, classLoader);
                    if (invokeResult.getCode() == InvokeResult.Code.SUCCESS.getCode()) {
                        if (returnType.isAssignableFrom(dataType)) {
                            if (void.class.isAssignableFrom(returnType) || Void.class.isAssignableFrom(returnType)) {
                                return null;
                            }
                        } else {
                            throw new ServiceInvocationFailureException("无效的服务调用结果类型（不一致）：" +
                                    returnType.getTypeName() + " 与 " + dataType.getTypeName());
                        }
                    }
                    ActualInvokeResult actualInvokeResult = invokeInformationHelper.convert(invokeResult, classLoader);
                    switch (actualInvokeResult.getCode()) {
                        case SUCCESS:
                            return actualInvokeResult.getData();
                        case FAIL:
                            if (Throwable.class.isAssignableFrom(dataType)) {
                                throw new ServiceInvocationFailureException((Throwable) actualInvokeResult.getData());
                            }
                            throw new ServiceInvocationFailureException(actualInvokeResult.getData());
                        default:
                            throw new ServiceInvocationFailureException("无效的服务调用结果代码：" + invokeResult.getCode());
                    }
                }
        );
    }

    @Override
    protected void callDetail(@NotNull InvokeDetail invokeDetail) throws Throwable {
        ServiceInvoker serviceInvoker = this.serviceInvoker.get();
        if (serviceInvoker != null) {
            serviceInvoker.invoke(invokeDetail);
        } else {
            throw new UnsupportedOperationException("请先设置服务调用器实例");
        }
    }

    public int getTimeout() {
        return timeout;
    }

    public void setServiceInvoker(ServiceInvoker serviceInvoker) {
        this.serviceInvoker.set(serviceInvoker);
    }
}
