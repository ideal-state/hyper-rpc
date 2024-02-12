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

package team.idealstate.hyper.rpc.api.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.common.AssertUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Watchdog</p>
 *
 * <p>创建于 2024/2/5 23:58</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public final class Watchdog {
    private static final Logger logger = LogManager.getLogger(Watchdog.class);
    private static final AtomicInteger IDS = new AtomicInteger(0);
    private static final int DEFAULT_MAXIMUM_RETRY = 10;
    private final Thread watchdogThread;
    private final int maximumRetry;
    private final ServiceStarter target;
    private final AtomicInteger retryCount = new AtomicInteger(0);

    public Watchdog(@NotNull ServiceStarter target) {
        this(target, DEFAULT_MAXIMUM_RETRY);
    }

    public Watchdog(@NotNull ServiceStarter target, int maximumRetry) {
        AssertUtils.notNull(target, "目标实例不允许为 null");
        this.target = target;
        this.maximumRetry = Math.max(DEFAULT_MAXIMUM_RETRY, maximumRetry);
        this.watchdogThread = new Thread(() -> {
            final ServiceStarter starter = Watchdog.this.target;
            while (true) {
                if (Thread.interrupted()) {
                    try {
                        starter.shutdown();
                    } catch (Throwable e) {
                        logger.error(e.getMessage());
                        logger.debug("catching", e);
                    } finally {
                        retryCount.set(0);
                    }
                    break;
                }
                if (starter.isNeedClose()) {
                    starter.close();
                } else if (!starter.isActive()) {
                    try {
                        starter.startup();
                    } catch (Throwable e) {
                        logger.error(e.getMessage());
                        logger.debug("catching", e);
                        try {
                            starter.close();
                        } catch (Throwable ex) {
                            logger.error(ex.getMessage());
                            logger.debug("catching", ex);
                        }
                        if (retryCount.incrementAndGet() >= Watchdog.this.maximumRetry) {
                            logger.warn("连续重试次数已达 {} 次，{} 即将关闭",
                                    Watchdog.this.maximumRetry, Thread.currentThread().getName());
                            Thread.currentThread().interrupt();
                            continue;
                        }
                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            logger.info("{} 已关闭", Thread.currentThread().getName());
        }, "Watchdog-" + IDS.getAndIncrement());
    }

    public void startup() {
        if (!watchdogThread.isAlive()) {
            watchdogThread.start();
        }
    }

    public void shutdown() {
        if (watchdogThread.isAlive()) {
            watchdogThread.interrupt();
            while (watchdogThread.isAlive()) {
                logger.info("等待 {} 关闭", watchdogThread.getName());
                try {
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException ignored) {
                }
            }
        }
        if (target.isActive()) {
            target.shutdown();
        }
    }

    public boolean isAlive() {
        return watchdogThread.isAlive();
    }

    public boolean isActive() {
        return target.isActive();
    }
}
