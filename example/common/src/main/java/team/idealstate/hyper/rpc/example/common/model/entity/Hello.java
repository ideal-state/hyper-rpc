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

package team.idealstate.hyper.rpc.example.common.model.entity;

import java.time.Instant;

/**
 * <p>Hello</p>
 *
 * <p>创建于 2024/2/6 21:47</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class Hello {

    private String message;
    private Instant timestamp;

    public Hello() {
    }

    public Hello(String message, Instant timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Hello)) {
            return false;
        }
        final Hello hello = (Hello) o;

        if (getMessage() != null ? !getMessage().equals(hello.getMessage()) : hello.getMessage() != null) {
            return false;
        }
        return getTimestamp() != null ? getTimestamp().equals(hello.getTimestamp()) : hello.getTimestamp() == null;
    }

    @Override
    public int hashCode() {
        int result = getMessage() != null ? getMessage().hashCode() : 0;
        result = 31 * result + (getTimestamp() != null ? getTimestamp().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Hello{" +
                "message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
