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

package team.idealstate.hyper.common;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>ClassUtils</p>
 *
 * <p>创建于 2024/2/4 14:43</p>
 *
 * @author ketikai
 * @version 1.0.3
 * @since 1.0.0
 */
public abstract class ClassUtils {

    private static final Set<String> PRIMITIVE_CLASS_NAME_SET;
    private static final Set<String> PRIMITIVE_TYPE_DESC_SET;

    static {
        PRIMITIVE_CLASS_NAME_SET = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(
                        "void", "boolean", "char", "byte", "short", "int", "float", "long", "double"
                ))
        );
        PRIMITIVE_TYPE_DESC_SET = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(
                        "V", "Z", "C", "B", "S", "I", "F", "J", "D"
                ))
        );
    }

    @NotNull
    public static Class<?> forName(@NotNull String name) throws ClassNotFoundException {
        AssertUtils.notBlank(name, "无效的类名称");
        return forName(name, true, null);
    }

    @NotNull
    public static Class<?> forName(@NotNull String name, boolean initialize, ClassLoader classLoader) throws ClassNotFoundException {
        AssertUtils.notBlank(name, "无效的类名称");
        if (isPrimitiveName(name)) {
            switch (name) {
                case "void":
                    return void.class;
                case "boolean":
                    return boolean.class;
                case "char":
                    return char.class;
                case "byte":
                    return byte.class;
                case "short":
                    return short.class;
                case "int":
                    return int.class;
                case "float":
                    return float.class;
                case "long":
                    return long.class;
                case "double":
                    return double.class;
            }
        }
        return Class.forName(name, initialize, classLoader);
    }

    @NotNull
    public static Class<?> forDesc(@NotNull String desc) throws ClassNotFoundException {
        AssertUtils.notBlank(desc, "无效的类型描述");
        return forDesc(desc, true, null);
    }

    @NotNull
    public static Class<?> forDesc(@NotNull String desc, boolean initialize, ClassLoader classLoader) throws ClassNotFoundException {
        AssertUtils.notBlank(desc, "无效的类型描述");
        if (isPrimitiveDesc(desc)) {
            switch (desc) {
                case "V":
                    return void.class;
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
            }
        }
        if (desc.startsWith("L")) {
            return Class.forName(desc.substring(1, desc.length() - 1), initialize, classLoader);
        }
        return Class.forName(desc, initialize, classLoader);
    }

    @NotNull
    public static String getName(@NotNull Class<?> cls) {
        AssertUtils.notNull(cls, "无效的类");
        return cls.getName();
    }

    @NotNull
    public static String getDesc(@NotNull Class<?> cls) {
        AssertUtils.notNull(cls, "无效的类");
        if (isPrimitiveClass(cls)) {
            switch (cls.getName()) {
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
        if (isArrayClass(cls)) {
            return cls.getName();
        }
        return "L" + cls.getName() + ";";
    }

    public static boolean isArrayClass(Class<?> cls) {
        AssertUtils.notNull(cls, "无效的类");
        return cls.isArray();
    }

    public static boolean isPrimitiveClass(Class<?> cls) {
        AssertUtils.notNull(cls, "无效的类");
        return PRIMITIVE_CLASS_NAME_SET.contains(cls.getName());
    }

    public static boolean isPrimitiveName(String name) {
        AssertUtils.notBlank(name, "无效的类名称");
        return PRIMITIVE_CLASS_NAME_SET.contains(name);
    }

    public static boolean isPrimitiveDesc(String desc) {
        AssertUtils.notBlank(desc, "无效的类型描述");
        return PRIMITIVE_TYPE_DESC_SET.contains(desc);
    }
}
