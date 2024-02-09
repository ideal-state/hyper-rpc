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

package team.idealstate.hyper.rpc.example.common.service;

import team.idealstate.hyper.rpc.impl.service.annotation.Implementation;
import team.idealstate.hyper.rpc.impl.service.annotation.RemoteService;

/**
 * <p>CalcService</p>
 *
 * <p>创建于 2024/2/9 13:23</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.2
 */
@RemoteService
@Implementation("team.idealstate.hyper.rpc.example.server.service.CalcServiceImpl")
public interface CalcService {

    long sum(int a, int b);
}
