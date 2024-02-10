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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.common.ClassUtils;
import team.idealstate.hyper.common.MethodUtils;
import team.idealstate.hyper.rpc.api.service.InvokeInformationHelper;
import team.idealstate.hyper.rpc.api.service.entity.ActualInvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.ActualInvokeResult;
import team.idealstate.hyper.rpc.api.service.entity.InvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.InvokeResult;
import team.idealstate.hyper.rpc.api.service.exception.InvokeInformationConvertException;
import team.idealstate.hyper.rpc.impl.JacksonUtils;

/**
 * <p>JacksonInvokeInformationHelper</p>
 *
 * <p>创建于 2024/2/4 16:34</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.0
 */
public final class JacksonInvokeInformationHelper implements InvokeInformationHelper {

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object[] getArgumentObjects(Class<?>[] argumentClasses, String[] arguments) throws JsonProcessingException {
        Object[] argumentObjects = new Object[argumentClasses.length];
        for (int i = 0; i < argumentObjects.length; i++) {
            argumentObjects[i] = JacksonUtils.toBean(arguments[i], (Class) argumentClasses[i]);
        }
        return argumentObjects;
    }

    @Override
    public @NotNull Class<?> getServiceInterface(@NotNull InvokeDetail invokeDetail, @NotNull ClassLoader classLoader) throws ClassNotFoundException {
        return ClassUtils.forName(invokeDetail.getService(), false, classLoader);
    }

    @Override
    public @NotNull ActualInvokeDetail convert(@NotNull InvokeDetail invokeDetail, @NotNull ClassLoader classLoader) throws InvokeInformationConvertException {
        ActualInvokeDetail actualInvokeDetail = new ActualInvokeDetail();
        actualInvokeDetail.setId(invokeDetail.getId());
        final Class<?>[] argumentClasses;
        try {
            final Class<?> serviceInterface = ClassUtils.forName(invokeDetail.getService(), false, classLoader);
            actualInvokeDetail.setServiceInterface(serviceInterface);
            argumentClasses = MethodUtils.getParamTypes(invokeDetail.getDescription(), false, classLoader);
            actualInvokeDetail.setMethod(serviceInterface.getMethod(invokeDetail.getMethod(), argumentClasses));
            actualInvokeDetail.setArgumentObjects(getArgumentObjects(argumentClasses, invokeDetail.getArguments()));
        } catch (ClassNotFoundException | NoSuchMethodException | JsonProcessingException e) {
            throw new InvokeInformationConvertException(e);
        }
        return actualInvokeDetail;
    }

    @Override
    public @NotNull InvokeDetail convert(@NotNull ActualInvokeDetail actualInvokeDetail, @NotNull ClassLoader classLoader) throws InvokeInformationConvertException {
        InvokeDetail invokeDetail = new InvokeDetail();
        invokeDetail.setId(actualInvokeDetail.getId());
        invokeDetail.setService(ClassUtils.getDesc(actualInvokeDetail.getServiceInterface()));
        invokeDetail.setMethod(actualInvokeDetail.getMethod().getName());
        invokeDetail.setDescription(MethodUtils.getDesc(actualInvokeDetail.getMethod()));
        Object[] argumentObjects = actualInvokeDetail.getArgumentObjects();
        argumentObjects = argumentObjects == null ? new Object[0] : argumentObjects;
        String[] arguments = new String[argumentObjects.length];
        try {
            for (int i = 0; i < argumentObjects.length; i++) {
                arguments[i] = JacksonUtils.toJson(argumentObjects[i]);
            }
        } catch (JsonProcessingException e) {
            throw new InvokeInformationConvertException(e);
        }
        invokeDetail.setArguments(arguments);
        return invokeDetail;
    }

    @Override
    public @NotNull ActualInvokeResult convert(@NotNull InvokeResult invokeResult, @NotNull ClassLoader classLoader) throws InvokeInformationConvertException {
        ActualInvokeResult actualInvokeResult = new ActualInvokeResult();
        actualInvokeResult.setId(invokeResult.getId());
        actualInvokeResult.setCode(InvokeResult.Code.codeOf(invokeResult.getCode()));
        Class<?> dataType;
        try {
            dataType = ClassUtils.forDesc(invokeResult.getDataType(), false, classLoader);
            actualInvokeResult.setData(JacksonUtils.toBean(invokeResult.getData(), dataType));
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new InvokeInformationConvertException(e);
        }
        actualInvokeResult.setDataType(dataType);
        return actualInvokeResult;
    }

    @Override
    public @NotNull InvokeResult convert(@NotNull ActualInvokeResult actualInvokeResult, @NotNull ClassLoader classLoader) throws InvokeInformationConvertException {
        InvokeResult invokeResult = new InvokeResult();
        invokeResult.setId(actualInvokeResult.getId());
        invokeResult.setCode(actualInvokeResult.getCode().getCode());
        invokeResult.setDataType(ClassUtils.getDesc(actualInvokeResult.getDataType()));
        try {
            invokeResult.setData(JacksonUtils.toJson(actualInvokeResult.getData()));
        } catch (JsonProcessingException e) {
            throw new InvokeInformationConvertException(e);
        }
        return invokeResult;
    }
}
