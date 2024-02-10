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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>MethodUtils</p>
 *
 * <p>创建于 2024/2/10 14:30</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.2
 */
public abstract class MethodUtils {

    @NotNull
    public static String getDesc(@NotNull Method method) {
        AssertUtils.notNull(method, "无效的方法");
        Class<?>[] parameterTypes = method.getParameterTypes();
        StringBuilder stringBuilder = new StringBuilder(parameterTypes.length * 10);
        stringBuilder.append('(');
        for (Class<?> parameterType : parameterTypes) {
            stringBuilder.append(ClassUtils.getDesc(parameterType));
        }
        stringBuilder.append(')');
        stringBuilder.append(ClassUtils.getDesc(method.getReturnType()));
        return stringBuilder.toString();
    }

    public static Class<?>[] getParamTypes(@NotNull String methodDesc) throws IllegalArgumentException, ClassNotFoundException {
        return getParamTypes(methodDesc, true, null);
    }

    @NotNull
    public static Class<?>[] getParamTypes(@NotNull String methodDesc, boolean initialize, ClassLoader classLoader) throws IllegalArgumentException, ClassNotFoundException {
        AssertUtils.notBlank(methodDesc, "无效的方法描述");
        int length = methodDesc.length();
        START:
        if (length >= 3) {
            if (methodDesc.charAt(0) == '(') {
                Integer arrayDescHeader = null;
                Integer arrayDescFooter = null;
                Integer refTypeHeader = null;
                Integer lastParamTypeFooter = null;
                Integer returnTypeHeader = null;
                List<Class<?>> paramTypes = new ArrayList<>(8);
                Class<?> returnType = null;
                for (int i = 1; i < length; i++) {
                    if (returnType != null) {
                        break START;
                    }
                    char c = methodDesc.charAt(i);
                    switch (c) {
                        case '[':
                            if (arrayDescHeader == null) {
                                arrayDescHeader = i;
                                arrayDescFooter = i;
                            } else if (refTypeHeader != null || arrayDescFooter == null || arrayDescFooter + 1 != i) {
                                break START;
                            } else {
                                arrayDescFooter = i;
                            }
                            break;
                        case 'L':
                            if (refTypeHeader == null) {
                                if (i != 1 && arrayDescFooter == null && lastParamTypeFooter == null) {
                                    break START;
                                }
                                refTypeHeader = i;
                            }
                            break;
                        case ';':
                            if (refTypeHeader == null) {
                                break START;
                            }
                            if (arrayDescHeader == null) {
                                if (returnTypeHeader == null) {
                                    paramTypes.add(ClassUtils.forDesc(methodDesc.substring(refTypeHeader + 1, i), initialize, classLoader));
                                } else {
                                    returnType = ClassUtils.forDesc(methodDesc.substring(refTypeHeader + 1, i), initialize, classLoader);
                                }
                            } else {
                                if (returnTypeHeader == null) {
                                    paramTypes.add(ClassUtils.forDesc(methodDesc.substring(arrayDescHeader, i + 1), initialize, classLoader));
                                } else {
                                    returnType = ClassUtils.forDesc(methodDesc.substring(arrayDescHeader, i + 1), initialize, classLoader);
                                }
                            }
                            lastParamTypeFooter = i;
                            arrayDescHeader = null;
                            arrayDescFooter = null;
                            refTypeHeader = null;
                            break;
                        case ')':
                            if (returnTypeHeader != null) {
                                break START;
                            }
                            returnTypeHeader = i + 1;
                            if (returnTypeHeader > length) {
                                break START;
                            }
                            break;
                        default:
                            if (refTypeHeader != null) {
                                continue;
                            }
                            String desc = String.valueOf(c);
                            if (!ClassUtils.isPrimitiveDesc(desc)) {
                                break START;
                            }
                            if (arrayDescHeader == null) {
                                if (returnTypeHeader == null) {
                                    paramTypes.add(ClassUtils.forDesc(desc));
                                } else {
                                    returnType = ClassUtils.forDesc(desc);
                                }
                            } else {
                                if (returnTypeHeader == null) {
                                    paramTypes.add(ClassUtils.forDesc(methodDesc.substring(arrayDescHeader, i + 1), initialize, classLoader));
                                } else {
                                    returnType = ClassUtils.forDesc(methodDesc.substring(arrayDescHeader, i + 1), initialize, classLoader);
                                }
                            }
                            lastParamTypeFooter = i;
                            arrayDescHeader = null;
                            arrayDescFooter = null;
                    }
                }
                if (returnType == null) {
                    break START;
                }
                return paramTypes.toArray(new Class[0]);
            }
        }
        throw new IllegalArgumentException("无效的方法描述 '" + methodDesc + "'");
    }

    @NotNull
    public static Class<?> getReturnType(@NotNull String methodDesc) throws IllegalArgumentException, ClassNotFoundException {
        return getReturnType(methodDesc, true, null);
    }

    @NotNull
    public static Class<?> getReturnType(@NotNull String methodDesc, boolean initialize, ClassLoader classLoader) throws IllegalArgumentException, ClassNotFoundException {
        AssertUtils.notBlank(methodDesc, "无效的方法描述");
        int length = methodDesc.length();
        START:
        if (length >= 3) {
            if (methodDesc.charAt(0) == '(') {
                Integer arrayDescHeader = null;
                Integer arrayDescFooter = null;
                Integer refTypeHeader = null;
                Integer lastParamTypeFooter = null;
                Integer returnTypeHeader = null;
                List<Class<?>> paramTypes = new ArrayList<>(8);
                Class<?> returnType = null;
                for (int i = 1; i < length; i++) {
                    if (returnType != null) {
                        break START;
                    }
                    char c = methodDesc.charAt(i);
                    switch (c) {
                        case '[':
                            if (arrayDescHeader == null) {
                                arrayDescHeader = i;
                                arrayDescFooter = i;
                            } else if (refTypeHeader != null || arrayDescFooter == null || arrayDescFooter + 1 != i) {
                                break START;
                            } else {
                                arrayDescFooter = i;
                            }
                            break;
                        case 'L':
                            if (refTypeHeader == null) {
                                if (i != 1 && arrayDescFooter == null && lastParamTypeFooter == null) {
                                    break START;
                                }
                                refTypeHeader = i;
                            }
                            break;
                        case ';':
                            if (refTypeHeader == null) {
                                break START;
                            }
                            if (arrayDescHeader == null) {
                                if (returnTypeHeader == null) {
                                    paramTypes.add(ClassUtils.forDesc(methodDesc.substring(refTypeHeader + 1, i), initialize, classLoader));
                                } else {
                                    returnType = ClassUtils.forDesc(methodDesc.substring(refTypeHeader + 1, i), initialize, classLoader);
                                }
                            } else {
                                if (returnTypeHeader == null) {
                                    paramTypes.add(ClassUtils.forDesc(methodDesc.substring(arrayDescHeader, i + 1), initialize, classLoader));
                                } else {
                                    returnType = ClassUtils.forDesc(methodDesc.substring(arrayDescHeader, i + 1), initialize, classLoader);
                                }
                            }
                            lastParamTypeFooter = i;
                            arrayDescHeader = null;
                            arrayDescFooter = null;
                            refTypeHeader = null;
                            break;
                        case ')':
                            if (returnTypeHeader != null) {
                                break START;
                            }
                            returnTypeHeader = i + 1;
                            if (returnTypeHeader > length) {
                                break START;
                            }
                            break;
                        default:
                            if (refTypeHeader != null) {
                                continue;
                            }
                            String desc = String.valueOf(c);
                            if (!ClassUtils.isPrimitiveDesc(desc)) {
                                break START;
                            }
                            if (arrayDescHeader == null) {
                                if (returnTypeHeader == null) {
                                    paramTypes.add(ClassUtils.forDesc(desc, initialize, classLoader));
                                } else {
                                    returnType = ClassUtils.forDesc(desc, initialize, classLoader);
                                }
                            } else {
                                if (returnTypeHeader == null) {
                                    paramTypes.add(ClassUtils.forDesc(methodDesc.substring(arrayDescHeader, i + 1), initialize, classLoader));
                                } else {
                                    returnType = ClassUtils.forDesc(methodDesc.substring(arrayDescHeader, i + 1), initialize, classLoader);
                                }
                            }
                            lastParamTypeFooter = i;
                            arrayDescHeader = null;
                            arrayDescFooter = null;
                    }
                }
                if (returnType == null) {
                    break START;
                }
                return returnType;
            }
        }
        throw new IllegalArgumentException("无效的方法描述 '" + methodDesc + "'");
    }
}
