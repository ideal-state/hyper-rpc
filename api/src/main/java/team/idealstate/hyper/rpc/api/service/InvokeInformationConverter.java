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

import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.rpc.api.service.entity.ActualInvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.ActualInvokeResult;
import team.idealstate.hyper.rpc.api.service.entity.InvokeDetail;
import team.idealstate.hyper.rpc.api.service.entity.InvokeResult;
import team.idealstate.hyper.rpc.api.service.exception.InvokeInformationConvertException;

/**
 * <p>InvokeInformationConverter</p>
 *
 * <p>创建于 2024/2/4 13:57</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public interface InvokeInformationConverter {

    @NotNull
    ActualInvokeDetail convert(@NotNull InvokeDetail invokeDetail) throws InvokeInformationConvertException;

    @NotNull
    InvokeDetail convert(@NotNull ActualInvokeDetail actualInvokeDetail) throws InvokeInformationConvertException;

    @NotNull
    ActualInvokeResult convert(@NotNull InvokeResult invokeResult) throws InvokeInformationConvertException;

    @NotNull
    InvokeResult convert(@NotNull ActualInvokeResult actualInvokeResult) throws InvokeInformationConvertException;
}
