/*
 *    Copyright 2023 ideal-state
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package team.idealstate.hyper.rpc.api;

import java.util.Collection;
import java.util.Map;

/**
 * <p>AssertUtils</p>
 *
 * <p>创建于 2024/2/4 14:43</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes"})
public abstract class AssertUtils {

    public static void isTrue(Boolean expression, String message) {
        if (!Boolean.TRUE.equals(expression)) {
            if (message == null) {
                throw new IllegalArgumentException();
            }
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNull(Object object, String message) {
        isTrue(object == null, message);
    }

    public static void notNull(Object object, String message) {
        isTrue(object != null, message);
    }

    public static void isEmpty(Map map, String message) {
        isTrue(map == null || map.isEmpty(), message);
    }

    public static void notEmpty(Map map, String message) {
        isTrue(map != null && !map.isEmpty(), message);
    }

    public static void isEmpty(Collection collection, String message) {
        isTrue(collection == null || collection.isEmpty(), message);
    }

    public static void notEmpty(Collection collection, String message) {
        isTrue(collection != null && !collection.isEmpty(), message);
    }

    public static void notBlank(CharSequence string, String message) {
        isTrue(!StringUtils.isBlank(string), message);
    }

    public static void isNumeric(CharSequence string, String message) {
        isTrue(StringUtils.isNumeric(string), message);
    }

    public static void isIntegral(CharSequence string, String message) {
        isTrue(StringUtils.isIntegral(string), message);
    }
}
