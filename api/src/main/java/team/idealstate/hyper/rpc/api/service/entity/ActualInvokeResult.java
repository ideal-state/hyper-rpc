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

/**
 * <p>ActualInvokeResult</p>
 *
 * <p>创建于 2024/2/4 15:25</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class ActualInvokeResult {

    private String id;
    private InvokeResult.Code code;
    private Class<?> dataType;
    private Object data;

    public ActualInvokeResult() {
    }

    public ActualInvokeResult(String id, InvokeResult.Code code, Class<?> dataType, Object data) {
        this.id = id;
        this.code = code;
        this.dataType = dataType;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InvokeResult.Code getCode() {
        return code;
    }

    public void setCode(InvokeResult.Code code) {
        this.code = code;
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public void setDataType(Class<?> dataType) {
        this.dataType = dataType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActualInvokeResult)) {
            return false;
        }
        final ActualInvokeResult that = (ActualInvokeResult) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        if (getCode() != that.getCode()) {
            return false;
        }
        if (getDataType() != null ? !getDataType().equals(that.getDataType()) : that.getDataType() != null) {
            return false;
        }
        return getData() != null ? getData().equals(that.getData()) : that.getData() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getCode() != null ? getCode().hashCode() : 0);
        result = 31 * result + (getDataType() != null ? getDataType().hashCode() : 0);
        result = 31 * result + (getData() != null ? getData().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ActualInvokeResult{" +
                "id='" + id + '\'' +
                ", code=" + code +
                ", dataType=" + dataType +
                ", data=" + data +
                '}';
    }
}
