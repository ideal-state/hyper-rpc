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

package team.idealstate.hyper.rpc.api.future;

import team.idealstate.hyper.rpc.api.future.exception.InvalidFutureException;
import team.idealstate.hyper.rpc.api.future.exception.TimeoutException;

/**
 * <p>Future</p>
 *
 * <p>创建于 2024/1/27 2:08</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class Future<V> {

    public static final int DEFAULT_TIMEOUT = 5;
    private final Object lock = new Object();
    private volatile boolean invalid = false;
    private volatile boolean completed = false;
    private volatile V value = null;

    public void invalidate() {
        this.invalid = true;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void complete(V value) throws InvalidFutureException {
        if (!isCompleted()) {
            synchronized (lock) {
                if (!isCompleted()) {
                    this.value = value;
                    this.completed = true;
                }
            }
        }
    }

    @SuppressWarnings({"all"})
    public boolean isCompleted() throws InvalidFutureException {
        if (invalid) {
            throw new InvalidFutureException();
        }
        return completed;
    }

    public V get() throws InvalidFutureException, InterruptedException {
        do {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        } while (!isCompleted());
        return value;
    }

    public V get(int timeout) throws InvalidFutureException, TimeoutException, InterruptedException {
        timeout = timeout <= 0 ? DEFAULT_TIMEOUT : timeout;
        final long timeout0 = System.currentTimeMillis() + (timeout * 1000L);
        while (!isCompleted()) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            if (timeout0 < System.currentTimeMillis()) {
                throw new TimeoutException();
            }
        }
        return value;
    }

    public V getNow() {
        return value;
    }
}
