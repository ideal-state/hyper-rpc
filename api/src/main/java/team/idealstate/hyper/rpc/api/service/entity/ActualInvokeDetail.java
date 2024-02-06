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

package team.idealstate.hyper.rpc.api.service.entity;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * <p>ActualInvokeDetail</p>
 *
 * <p>创建于 2024/2/4 15:21</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class ActualInvokeDetail {

    private String id;
    private Class<?> serviceInterface;
    private Method method;
    private Object[] argumentObjects;

    public ActualInvokeDetail() {
    }

    public ActualInvokeDetail(String id, Class<?> serviceInterface, Method method, Object[] argumentObjects) {
        this.id = id;
        this.serviceInterface = serviceInterface;
        this.method = method;
        this.argumentObjects = argumentObjects;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgumentObjects() {
        return argumentObjects;
    }

    public void setArgumentObjects(Object[] argumentObjects) {
        this.argumentObjects = argumentObjects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActualInvokeDetail)) {
            return false;
        }
        final ActualInvokeDetail that = (ActualInvokeDetail) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        if (getServiceInterface() != null ? !getServiceInterface().equals(that.getServiceInterface()) : that.getServiceInterface() != null) {
            return false;
        }
        if (getMethod() != null ? !getMethod().equals(that.getMethod()) : that.getMethod() != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(getArgumentObjects(), that.getArgumentObjects());
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getServiceInterface() != null ? getServiceInterface().hashCode() : 0);
        result = 31 * result + (getMethod() != null ? getMethod().hashCode() : 0);
        result = 31 * result + Arrays.hashCode(getArgumentObjects());
        return result;
    }

    @Override
    public String toString() {
        return "ActualInvokeDetail{" +
                "id='" + id + '\'' +
                ", serviceInterface=" + serviceInterface +
                ", method=" + method +
                ", argumentObjects=" + Arrays.toString(argumentObjects) +
                '}';
    }
}
