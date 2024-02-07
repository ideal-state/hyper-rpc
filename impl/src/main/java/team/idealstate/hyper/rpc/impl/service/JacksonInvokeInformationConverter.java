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
import org.objectweb.asm.Type;
import team.idealstate.hyper.rpc.api.TypeUtils;
import team.idealstate.hyper.rpc.api.service.InvokeInformationConverter;
import team.idealstate.hyper.rpc.api.service.entity.ActualInvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.ActualInvokeResult;
import team.idealstate.hyper.rpc.api.service.entity.InvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.InvokeResult;
import team.idealstate.hyper.rpc.api.service.exception.InvokeInformationConvertException;
import team.idealstate.hyper.rpc.impl.JacksonUtils;

/**
 * <p>JacksonInvokeInformationConverter</p>
 *
 * <p>创建于 2024/2/4 16:34</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public final class JacksonInvokeInformationConverter implements InvokeInformationConverter {

    private static Class<?>[] getArgumentClasses(Type[] argumentTypes) throws ClassNotFoundException {
        Class<?>[] argumentClasses = new Class<?>[argumentTypes.length];
        for (int i = 0; i < argumentTypes.length; i++) {
            Type argumentType = argumentTypes[i];
            String actualClassName = TypeUtils.getActualClassName(argumentType);
            switch (actualClassName) {
                case "Z":
                    argumentClasses[i] = boolean.class;
                    break;
                case "C":
                    argumentClasses[i] = char.class;
                    break;
                case "B":
                    argumentClasses[i] = byte.class;
                    break;
                case "S":
                    argumentClasses[i] = short.class;
                    break;
                case "I":
                    argumentClasses[i] = int.class;
                    break;
                case "F":
                    argumentClasses[i] = float.class;
                    break;
                case "J":
                    argumentClasses[i] = long.class;
                    break;
                case "D":
                    argumentClasses[i] = double.class;
                    break;
                case "V":
                    argumentClasses[i] = Void.class;
                    break;
                default:
                    argumentClasses[i] = Class.forName(actualClassName.replace("[V", "[Ljava.lang.Void;"));
            }
        }
        return argumentClasses;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object[] getArgumentObjects(Class<?>[] argumentClasses, String[] arguments) throws JsonProcessingException {
        Object[] argumentObjects = new Object[argumentClasses.length];
        for (int i = 0; i < argumentObjects.length; i++) {
            argumentObjects[i] = JacksonUtils.toBean(arguments[i], (Class) argumentClasses[i]);
        }
        return argumentObjects;
    }

    @Override
    public @NotNull ActualInvokeDetail convert(@NotNull InvokeDetail invokeDetail) throws InvokeInformationConvertException {
        ActualInvokeDetail actualInvokeDetail = new ActualInvokeDetail();
        actualInvokeDetail.setId(invokeDetail.getId());
        final Class<?>[] argumentClasses;
        try {
            final Class<?> serviceInterface = TypeUtils.getActualClass(invokeDetail.getService());
            actualInvokeDetail.setServiceInterface(serviceInterface);
            argumentClasses = getArgumentClasses(Type.getArgumentTypes(invokeDetail.getDescription()));
            actualInvokeDetail.setMethod(serviceInterface.getMethod(invokeDetail.getMethod(), argumentClasses));
            actualInvokeDetail.setArgumentObjects(getArgumentObjects(argumentClasses, invokeDetail.getArguments()));
        } catch (ClassNotFoundException | NoSuchMethodException | JsonProcessingException e) {
            throw new InvokeInformationConvertException(e);
        }
        return actualInvokeDetail;
    }

    @Override
    public @NotNull InvokeDetail convert(@NotNull ActualInvokeDetail actualInvokeDetail) throws InvokeInformationConvertException {
        InvokeDetail invokeDetail = new InvokeDetail();
        invokeDetail.setId(actualInvokeDetail.getId());
        invokeDetail.setService(TypeUtils.getActualClassName(Type.getType(actualInvokeDetail.getServiceInterface())));
        invokeDetail.setMethod(actualInvokeDetail.getMethod().getName());
        invokeDetail.setDescription(Type.getMethodDescriptor(actualInvokeDetail.getMethod()));
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
    public @NotNull ActualInvokeResult convert(@NotNull InvokeResult invokeResult) throws InvokeInformationConvertException {
        ActualInvokeResult actualInvokeResult = new ActualInvokeResult();
        actualInvokeResult.setId(invokeResult.getId());
        actualInvokeResult.setCode(InvokeResult.Code.codeOf(invokeResult.getCode()));
        Class<?> dataType;
        try {
            dataType = TypeUtils.getActualClass(invokeResult.getDataType());
            actualInvokeResult.setData(JacksonUtils.toBean(invokeResult.getData(), dataType));
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new InvokeInformationConvertException(e);
        }
        actualInvokeResult.setDataType(dataType);
        return actualInvokeResult;
    }

    @Override
    public @NotNull InvokeResult convert(@NotNull ActualInvokeResult actualInvokeResult) throws InvokeInformationConvertException {
        InvokeResult invokeResult = new InvokeResult();
        invokeResult.setId(actualInvokeResult.getId());
        invokeResult.setCode(actualInvokeResult.getCode().getCode());
        invokeResult.setDataType(TypeUtils.getActualClassName(Type.getType(actualInvokeResult.getDataType())));
        try {
            invokeResult.setData(JacksonUtils.toJson(actualInvokeResult.getData()));
        } catch (JsonProcessingException e) {
            throw new InvokeInformationConvertException(e);
        }
        return invokeResult;
    }
}
