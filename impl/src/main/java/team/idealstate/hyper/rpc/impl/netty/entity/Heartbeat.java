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

package team.idealstate.hyper.rpc.impl.netty.entity;

/**
 * <p>Heartbeat</p>
 *
 * <p>创建于 2024/1/26 22:38</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class Heartbeat {

    private Long timestamp;

    public Heartbeat() {
    }

    public Heartbeat(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Heartbeat)) {
            return false;
        }
        final Heartbeat heartbeat = (Heartbeat) o;

        return getTimestamp() != null ? getTimestamp().equals(heartbeat.getTimestamp()) : heartbeat.getTimestamp() == null;
    }

    @Override
    public int hashCode() {
        return getTimestamp() != null ? getTimestamp().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Heartbeat{" +
                "timestamp=" + timestamp +
                '}';
    }
}
