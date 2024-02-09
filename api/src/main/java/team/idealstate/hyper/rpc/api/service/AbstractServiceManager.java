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

package team.idealstate.hyper.rpc.api.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.idealstate.hyper.rpc.api.AssertUtils;
import team.idealstate.hyper.rpc.api.future.Future;
import team.idealstate.hyper.rpc.api.future.exception.InvalidFutureException;
import team.idealstate.hyper.rpc.api.service.entity.InvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.InvokeResult;
import team.idealstate.hyper.rpc.api.service.exception.UnregisteredServiceException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>AbstractServiceManager</p>
 *
 * <p>创建于 2024/2/4 12:53</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings({"unchecked"})
public abstract class AbstractServiceManager implements ServiceManager {

    private static final Logger logger = LogManager.getLogger(AbstractServiceManager.class);

    protected final Map<Class<?>, Object> registeredServiceInstanceMap = new ConcurrentHashMap<>(64, 0.6F);
    protected final Map<String, Future<InvokeResult>> resultFutureMap = new ConcurrentHashMap<>(32, 0.6F);
    protected final InvokeInformationHelper invokeInformationHelper;
    protected final ExecutorService executorService;
    protected final Lock lock = new ReentrantLock();
    private final AtomicReference<ClassLoader> classLoader = new AtomicReference<>(
            Optional.ofNullable(Thread.currentThread().getContextClassLoader())
                    .orElse(getClass().getClassLoader())
    );

    protected AbstractServiceManager(@NotNull InvokeInformationHelper invokeInformationHelper, @NotNull ExecutorService executorService) {
        AssertUtils.notNull(invokeInformationHelper, "调用信息转换器不能为 null");
        AssertUtils.notNull(executorService, "执行器服务不能为 null");
        this.invokeInformationHelper = invokeInformationHelper;
        this.executorService = executorService;
    }

    private static void checkServiceInterface(Class<?> serviceInterface) throws IllegalArgumentException {
        AssertUtils.notNull(serviceInterface, "服务接口不能为 null");
        if (!serviceInterface.isInterface()) {
            throw new IllegalArgumentException(serviceInterface.getTypeName() + " 不是个接口");
        }
    }

    private static void checkDetail(InvokeDetail invokeDetail) {
        AssertUtils.notNull(invokeDetail, "服务调用细节不允许为 null");
    }

    private static void checkResult(InvokeResult invokeResult) {
        AssertUtils.notNull(invokeResult, "服务调用结果不允许为 null");
    }

    @NotNull
    protected abstract <T> T newServiceInstance(@NotNull Class<T> serviceInterface);

    @Override
    public <T> @Nullable T register(@NotNull Class<T> serviceInterface) throws IllegalArgumentException {
        checkServiceInterface(serviceInterface);
        lock.lock();
        try {
            final T originalServiceInstance = (T) registeredServiceInstanceMap.get(serviceInterface);
            final T newServiceInstance = newServiceInstance(serviceInterface);
            registeredServiceInstanceMap.put(serviceInterface, newServiceInstance);
            final String serviceInterfaceName = serviceInterface.getTypeName();
            if (originalServiceInstance != null) {
                registeredServiceInstanceMap.remove(serviceInterface);
                logger.debug("已注销服务 {} 的原有实例 {}", serviceInterfaceName, originalServiceInstance);
            }
            logger.info("已注册服务 {} 的实例 {}", serviceInterfaceName, newServiceInstance);
            return originalServiceInstance;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T> @Nullable T register(@NotNull Class<T> serviceInterface, @NotNull T serviceInstance) throws IllegalArgumentException {
        checkServiceInterface(serviceInterface);
        AssertUtils.notNull(serviceInstance, "必须提供一个实现服务接口 %s 的实例");
        lock.lock();
        try {
            final T originalServiceInstance = (T) registeredServiceInstanceMap.get(serviceInterface);
            registeredServiceInstanceMap.put(serviceInterface, serviceInstance);
            final String serviceInterfaceName = serviceInterface.getTypeName();
            if (originalServiceInstance != null) {
                registeredServiceInstanceMap.remove(serviceInterface);
                logger.debug("已注销服务 {} 的原有实例 {}", serviceInterfaceName, originalServiceInstance);
            }
            logger.info("已注册服务 {} 的实例 {}", serviceInterfaceName, serviceInstance);
            return originalServiceInstance;
        } finally {
            lock.unlock();
        }
    }

    private <T> T find0(Class<T> serviceInterface) {
        checkServiceInterface(serviceInterface);
        return (T) registeredServiceInstanceMap.get(serviceInterface);
    }

    @Override
    public <T> @Nullable T find(@NotNull Class<T> serviceInterface) {
        return find0(serviceInterface);
    }

    @Override
    public <T> @NotNull T get(@NotNull Class<T> serviceInterface) throws UnregisteredServiceException {
        final T serviceInstance = find0(serviceInterface);
        if (serviceInstance == null) {
            throw new UnregisteredServiceException(serviceInterface);
        }
        return serviceInstance;
    }

    @Override
    public @NotNull InvokeResult invoke(@NotNull InvokeDetail invokeDetail) throws InvalidFutureException, InterruptedException {
        return call(invokeDetail).get();
    }

    protected abstract void callDetail(@NotNull InvokeDetail invokeDetail) throws Throwable;

    @Override
    public @NotNull Future<InvokeResult> call(@NotNull InvokeDetail invokeDetail) {
        checkDetail(invokeDetail);
        final Future<InvokeResult> resultFuture = new Future<>();
        final String detailId = invokeDetail.getId();
        resultFutureMap.put(detailId, resultFuture);
        executorService.submit(() -> {
            try {
                callDetail(invokeDetail);
            } catch (Throwable e) {
                resultFutureMap.remove(detailId);
                logger.catching(e);
                resultFuture.invalidate();
            }
        });
        return resultFuture;
    }

    @Override
    public void callback(@NotNull InvokeResult invokeResult) {
        checkResult(invokeResult);
        final Future<InvokeResult> resultFuture = resultFutureMap.remove(invokeResult.getId());
        if (resultFuture != null) {
            try {
                resultFuture.complete(invokeResult);
            } catch (InvalidFutureException ignored) {
            }
        }
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                logger.info("服务管理器的附加执行器服务 {} 已关闭", executorService);
                break;
            }
            logger.info("等待服务管理器的附加执行器服务 {} 关闭", executorService);
            try {
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @NotNull
    @Override
    public ClassLoader getClassLoader() {
        return classLoader.get();
    }

    @Override
    public void setClassLoader(@NotNull ClassLoader classLoader) {
        AssertUtils.notNull(classLoader, "无效的类加载器");
        this.classLoader.set(classLoader);
    }
}
