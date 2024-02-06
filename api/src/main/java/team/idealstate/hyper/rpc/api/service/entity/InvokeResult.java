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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>InvokeResult</p>
 *
 * <p>创建于 2024/1/24 18:09</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class InvokeResult {

    private String id;
    private Integer code;
    private String dataType;
    private String data;

    public InvokeResult() {
    }

    public InvokeResult(String id, Integer code, String dataType, String data) {
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

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvokeResult)) {
            return false;
        }
        final InvokeResult that = (InvokeResult) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        if (getCode() != null ? !getCode().equals(that.getCode()) : that.getCode() != null) {
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
        return "InvokeResult{" +
                "id='" + id + '\'' +
                ", code=" + code +
                ", dataType='" + dataType + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    /**
     * <p>InvokeResultCode</p>
     *
     * <p>创建于 2024/2/4 15:46</p>
     *
     * @author ketikai
     * @version 1.0.0
     * @since 1.0.0
     */
    public enum Code {

        SUCCESS(1),
        FAIL(0);

        private static final Map<Integer, Code> CODE_OF;

        static {
            Map<Integer, Code> map = new HashMap<>();
            map.put(SUCCESS.getCode(), SUCCESS);
            map.put(FAIL.getCode(), FAIL);
            CODE_OF = Collections.unmodifiableMap(map);
        }

        private final int code;

        Code(int code) {
            this.code = code;
        }

        public static Code codeOf(int code) {
            return CODE_OF.get(code);
        }

        public int getCode() {
            return code;
        }
    }
}
