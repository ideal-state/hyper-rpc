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

package team.idealstate.hyper.rpc.api.test;

import team.idealstate.hyper.common.MethodUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * <p>Test</p>
 *
 * <p>创建于 2024/2/10 15:45</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.2
 */
public class Test {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException {
        StringBuilder sout;
        Method method = Test.class.getDeclaredMethod("a", int.class, Long.class, boolean[].class, List.class);
        String desc = MethodUtils.getDesc(method);
        System.out.println(desc);
        sout = new StringBuilder("[");
        for (Class<?> paramType : MethodUtils.getParamTypes(desc)) {
            sout.append(paramType.getName()).append(',').append(' ');
        }
        sout.append("]");
        System.out.println(sout);
        System.out.println(MethodUtils.getReturnType(desc).getName());
        System.out.println("\n\n");
        method = Test.class.getDeclaredMethod("b", int.class);
        desc = MethodUtils.getDesc(method);
        System.out.println(desc);
        sout = new StringBuilder("[");
        for (Class<?> paramType : MethodUtils.getParamTypes(desc)) {
            sout.append(paramType.getName()).append(',').append(' ');
        }
        sout.append("]");
        System.out.println(sout);
        System.out.println(MethodUtils.getReturnType(desc).getName());
        System.out.println("\n\n");
        method = Test.class.getDeclaredMethod("c", Long.class, boolean[].class);
        desc = MethodUtils.getDesc(method);
        System.out.println(desc);
        sout = new StringBuilder("[");
        for (Class<?> paramType : MethodUtils.getParamTypes(desc)) {
            sout.append(paramType.getName()).append(',').append(' ');
        }
        sout.append("]");
        System.out.println(sout);
        System.out.println(MethodUtils.getReturnType(desc).getName());
        System.out.println("\n\n");
        method = Test.class.getDeclaredMethod("d", boolean[].class, List.class);
        desc = MethodUtils.getDesc(method);
        System.out.println(desc);
        sout = new StringBuilder("[");
        for (Class<?> paramType : MethodUtils.getParamTypes(desc)) {
            sout.append(paramType.getName()).append(',').append(' ');
        }
        sout.append("]");
        System.out.println(sout);
        System.out.println(MethodUtils.getReturnType(desc).getName());
        System.out.println("\n\n");
        method = Test.class.getDeclaredMethod("e", List.class);
        desc = MethodUtils.getDesc(method);
        System.out.println(desc);
        sout = new StringBuilder("[");
        for (Class<?> paramType : MethodUtils.getParamTypes(desc)) {
            sout.append(paramType.getName()).append(',').append(' ');
        }
        sout.append("]");
        System.out.println(sout);
        System.out.println(MethodUtils.getReturnType(desc).getName());
    }

    public static String[] a(int a, Long b, boolean[] c, List<?> d) {
        return null;
    }

    public static void b(int a) {
        return;
    }

    public static List<Void> c(Long b, boolean[] c) {
        return null;
    }

    public static Void d(boolean[] c, List<?> d) {
        return null;
    }

    public static Void e(List<?> d) {
        return null;
    }
}
