package team.idealstate.hyper.rpc.impl.service;

import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.rpc.api.future.Future;
import team.idealstate.hyper.rpc.api.service.AbstractServiceManager;
import team.idealstate.hyper.rpc.api.service.InvokeInformationConverter;
import team.idealstate.hyper.rpc.api.service.ServiceInvoker;
import team.idealstate.hyper.rpc.api.service.entity.ActualInvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.ActualInvokeResult;
import team.idealstate.hyper.rpc.api.service.entity.InvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.InvokeResult;
import team.idealstate.hyper.rpc.api.service.exception.ServiceInvocationFailureException;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>JdkProxyServiceManager</p>
 *
 * <p>创建于 2024/2/6 11:48</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public final class JdkProxyServiceManager extends AbstractServiceManager {

    private final AtomicReference<ServiceInvoker> serviceInvoker = new AtomicReference<>(null);
    private final int timeout;

    public JdkProxyServiceManager(
            @NotNull InvokeInformationConverter invokeInformationConverter,
            @NotNull ExecutorService executorService
    ) {
        this(invokeInformationConverter, executorService, Future.DEFAULT_TIMEOUT);
    }

    public JdkProxyServiceManager(
            @NotNull InvokeInformationConverter invokeInformationConverter,
            @NotNull ExecutorService executorService,
            int timeout
    ) {
        super(invokeInformationConverter, executorService);
        this.timeout = timeout;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    protected <T> @NotNull T newServiceInstance(@NotNull Class<T> serviceInterface) {
        return (T) Proxy.newProxyInstance(
                Optional.ofNullable(Thread.currentThread().getContextClassLoader())
                        .orElse(JdkProxyServiceManager.class.getClassLoader()),
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
                    InvokeDetail invokeDetail = invokeInformationConverter.convert(actualInvokeDetail);
                    Future<InvokeResult> resultFuture = call(invokeDetail);
                    final Class<?> returnType = method.getReturnType();
                    InvokeResult invokeResult = resultFuture.get(getTimeout());
                    final Class<?> dataType = Class.forName(invokeResult.getDataType());
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
                    ActualInvokeResult actualInvokeResult = invokeInformationConverter.convert(invokeResult);
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
