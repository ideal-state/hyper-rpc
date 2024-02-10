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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.idealstate.hyper.common.AssertUtils;
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
import team.idealstate.hyper.rpc.impl.service.annotation.Implementation;
import team.idealstate.hyper.rpc.impl.service.annotation.RemoteService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>StdServiceManager</p>
 *
 * <p>创建于 2024/2/9 8:56</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @see Implementation
 * @see RemoteService
 * @since 1.0.2
 */
public class StdServiceManager extends AbstractServiceManager {

    private static final Logger logger = LogManager.getLogger(StdServiceManager.class);
    private final AtomicReference<ServiceInvoker> serviceInvoker = new AtomicReference<>(null);
    private final AtomicInteger timeout = new AtomicInteger(Future.DEFAULT_TIMEOUT);

    public StdServiceManager(@NotNull ExecutorService executorService) {
        this(new JacksonInvokeInformationHelper(), executorService);
    }

    public StdServiceManager(@NotNull InvokeInformationHelper invokeInformationHelper, @NotNull ExecutorService executorService) {
        super(invokeInformationHelper, executorService);
    }

    @Override
    protected <T> @NotNull T newServiceInstance(@NotNull Class<T> serviceInterface) {
        ClassLoader classLoader = getClassLoader();
        T instance = newLocalServiceInstance(serviceInterface, classLoader);
        if (instance == null) {
            instance = newRemoteServiceInstance(serviceInterface, classLoader);
        }
        AssertUtils.notNull(instance, "无效的服务接口实例");
        return instance;
    }

    @Nullable
    protected <T> T newLocalServiceInstance(@NotNull Class<T> serviceInterface, @NotNull ClassLoader classLoader) {
        Implementation implementation = serviceInterface.getDeclaredAnnotation(Implementation.class);
        if (implementation != null) {
            String implClassName = implementation.value();
            AssertUtils.notBlank(implClassName, "无效的实现类全类名");
            try {
                Class<? extends T> implClass = Class.forName(implClassName, true,
                        classLoader
                ).asSubclass(serviceInterface);
                return implClass.getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                logger.debug("已标记实现类但未找到实现类（如已标记为远程服务，可忽视该项）：{}", e.getMessage());
            } catch (ClassCastException e) {
                throw new IllegalStateException("无效的实现类全类名", e);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new IllegalStateException("无效的实现类", e);
            }
        }
        return null;
    }

    @NotNull
    @SuppressWarnings({"unchecked"})
    protected <T> T newRemoteServiceInstance(@NotNull Class<T> serviceInterface, @NotNull ClassLoader classLoader) {
        RemoteService remoteService = serviceInterface.getDeclaredAnnotation(RemoteService.class);
        if (remoteService == null || !remoteService.value()) {
            throw new IllegalStateException("无法自动构造实现接口 '" + serviceInterface.getTypeName() +
                    "' 的实例：未指定接口的实现方式或实现类（手动注册请使用 ServiceManager#register(Class, Object) 方法）");
        }
        return (T) Proxy.newProxyInstance(
                getClassLoader(),
                new Class[]{serviceInterface, RemoteProxyService.class},
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
        ClassLoader classLoader = getClassLoader();
        Class<?> serviceInterface = invokeInformationHelper.getServiceInterface(invokeDetail, classLoader);
        if (get(serviceInterface) instanceof RemoteProxyService) {
            callRemoteDetail(invokeDetail, classLoader);
        } else {
            callLocalDetail(invokeDetail, classLoader);
        }
    }

    protected void callLocalDetail(@NotNull InvokeDetail invokeDetail, @NotNull ClassLoader classLoader) throws Throwable {
        final ActualInvokeDetail actualInvokeDetail = invokeInformationHelper.convert(invokeDetail, classLoader);
        final Object serviceInstance = get(actualInvokeDetail.getServiceInterface());
        final Method method = actualInvokeDetail.getMethod();
        final ActualInvokeResult actualInvokeResult = new ActualInvokeResult();
        final String id = actualInvokeDetail.getId();
        actualInvokeResult.setId(id);
        try {
            final Object returnVal = method.invoke(serviceInstance, actualInvokeDetail.getArgumentObjects());
            actualInvokeResult.setCode(InvokeResult.Code.SUCCESS);
            actualInvokeResult.setDataType(method.getReturnType());
            actualInvokeResult.setData(returnVal);
        } catch (Throwable e) {
            actualInvokeResult.setCode(InvokeResult.Code.FAIL);
            actualInvokeResult.setDataType(String.class);
            actualInvokeResult.setData("服务调用过程中抛出异常（详细信息请见服务提供方的日志）");
            logger.catching(e);
        } finally {
            final InvokeResult invokeResult = invokeInformationHelper.convert(actualInvokeResult, classLoader);
            callback(invokeResult);
        }
    }

    protected void callRemoteDetail(@NotNull InvokeDetail invokeDetail, @NotNull ClassLoader classLoader) throws Throwable {
        ServiceInvoker serviceInvoker = this.serviceInvoker.get();
        if (serviceInvoker != null) {
            serviceInvoker.invoke(invokeDetail);
        } else {
            throw new UnsupportedOperationException("服务调用器不可用（未设置）");
        }
    }

    public int getTimeout() {
        return timeout.get();
    }

    public void setTimeout(int timeout) {
        this.timeout.set(timeout);
    }

    public ServiceInvoker getServiceInvoker() {
        return serviceInvoker.get();
    }

    public void setServiceInvoker(ServiceInvoker serviceInvoker) {
        this.serviceInvoker.set(serviceInvoker);
    }

    private interface RemoteProxyService {
    }
}
