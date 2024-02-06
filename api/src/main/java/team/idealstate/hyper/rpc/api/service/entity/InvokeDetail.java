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

import java.util.Arrays;

/**
 * <p>InvokeDetail</p>
 *
 * <p>创建于 2024/1/24 18:02</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class InvokeDetail {

    private String id;
    private String service;
    private String method;
    private String description;
    private String[] arguments;

    public InvokeDetail() {
    }

    public InvokeDetail(String id, String service, String method, String description, String[] arguments) {
        this.id = id;
        this.service = service;
        this.method = method;
        this.description = description;
        this.arguments = arguments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvokeDetail)) {
            return false;
        }
        final InvokeDetail that = (InvokeDetail) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        if (getService() != null ? !getService().equals(that.getService()) : that.getService() != null) {
            return false;
        }
        if (getMethod() != null ? !getMethod().equals(that.getMethod()) : that.getMethod() != null) {
            return false;
        }
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(getArguments(), that.getArguments());
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getService() != null ? getService().hashCode() : 0);
        result = 31 * result + (getMethod() != null ? getMethod().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + Arrays.hashCode(getArguments());
        return result;
    }

    @Override
    public String toString() {
        return "InvokeDetail{" +
                "id='" + id + '\'' +
                ", service='" + service + '\'' +
                ", method='" + method + '\'' +
                ", description='" + description + '\'' +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
