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
import team.idealstate.hyper.rpc.api.service.AbstractServiceManager;
import team.idealstate.hyper.rpc.api.service.InvokeInformationHelper;
import team.idealstate.hyper.rpc.api.service.entity.ActualInvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.ActualInvokeResult;
import team.idealstate.hyper.rpc.api.service.entity.InvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.InvokeResult;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

/**
 * <p>ManualRegistrationServiceManager</p>
 *
 * <p>
 * 已弃用，请使用 {@link StdServiceManager}
 * </p>
 *
 * <p>创建于 2024/2/6 13:32</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.0
 */
@Deprecated
public final class ManualRegistrationServiceManager extends AbstractServiceManager {

    private static final Logger logger = LogManager.getLogger(ManualRegistrationServiceManager.class);

    public ManualRegistrationServiceManager(@NotNull InvokeInformationHelper invokeInformationHelper, @NotNull ExecutorService executorService) {
        super(invokeInformationHelper, executorService);
    }

    @Override
    protected <T> @NotNull T newServiceInstance(@NotNull Class<T> serviceInterface) {
        throw new UnsupportedOperationException("该服务管理器不支持自动构造服务实例，请使用 ServiceManager#register(Class, Object) 方法手动注册服务实例");
    }

    @Override
    protected void callDetail(@NotNull InvokeDetail invokeDetail) throws Throwable {
        ClassLoader classLoader = getClassLoader();
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
}
