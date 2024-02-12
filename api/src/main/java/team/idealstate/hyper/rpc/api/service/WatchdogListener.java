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

/**
 * <p>WatchdogListener</p>
 *
 * <p>创建于 2024/2/12 20:31</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.2
 */
public interface WatchdogListener {

    default void before(When when) {
    }

    default void after(When when) {
    }

    default void exceptionCaught(When when, Throwable e) {
    }

    default void unnaturalDeath() {
    }

    enum When {
        STARTUP,
        CLOSE,
        SHUTDOWN,
    }
}