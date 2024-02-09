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

package team.idealstate.hyper.rpc.api;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>TypeUtils</p>
 *
 * <p>创建于 2024/2/4 14:43</p>
 *
 * @author ketikai
 * @version 1.0.3
 * @since 1.0.0
 */
public abstract class TypeUtils {

    private static final Set<String> PRIMITIVE_CLASS_NAME_SET;

    static {
        PRIMITIVE_CLASS_NAME_SET = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(
                        "void", "V", "boolean", "Z", "char", "C", "byte", "B", "short", "S", "int", "I",
                        "float", "F", "long", "L", "double", "D"
                ))
        );
    }

    private static boolean isPrimitive(String className) {
        return PRIMITIVE_CLASS_NAME_SET.contains(className);
    }

    private static String getActualClassName(String className) {
        if (isPrimitive(className)) {
            switch (className) {
                case "void":
                    return "V";
                case "boolean":
                    return "Z";
                case "char":
                    return "C";
                case "byte":
                    return "B";
                case "short":
                    return "S";
                case "int":
                    return "I";
                case "float":
                    return "F";
                case "long":
                    return "J";
                case "double":
                    return "D";
            }
        }
        return className;
    }

    /**
     * @return 返回可用作 {@link Class#forName(String)} 方法参数的类型名称
     */
    @NotNull
    public static String getActualClassName(@NotNull Type type) {
        AssertUtils.notNull(type, "类型实例不能为 null");
        final String className = type.getClassName();
        final int dimensions = StringUtils.countMatches(className, '[');
        if (dimensions != 0) {
            String componentClassName = getActualClassName(className.replace("[]", ""));
            if (!isPrimitive(componentClassName)) {
                componentClassName = "L" + componentClassName + ";";
            }
            StringBuilder sb = new StringBuilder(componentClassName.length() + dimensions);
            for (int i = 0; i < dimensions; i++) {
                sb.append('[');
            }
            sb.append(componentClassName);
            return sb.toString();
        }
        return getActualClassName(className);
    }

    /**
     * @return 返回对应 Class
     */
    @NotNull
    public static Class<?> getActualClass(@NotNull String actualClassName, @NotNull ClassLoader classLoader) throws ClassNotFoundException {
        AssertUtils.notBlank(actualClassName, "无效的实际类型名称");
        switch (actualClassName) {
            case "Z":
                return boolean.class;
            case "C":
                return char.class;
            case "B":
                return byte.class;
            case "S":
                return short.class;
            case "I":
                return int.class;
            case "F":
                return float.class;
            case "J":
                return long.class;
            case "D":
                return double.class;
            case "V":
                return void.class;
            default:
                return Class.forName(
                        actualClassName.replace("[V", "[Ljava.lang.Void;"),
                        false,
                        classLoader
                );
        }
    }
}
