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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

/**
 * <p>Identifier</p>
 *
 * <p>创建于 2024/2/10 13:41</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public final class Identifier {

    private static final MessageDigest MESSAGE_DIGEST;

    static {
        try {
            MESSAGE_DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private final String hashString;

    private Identifier(@NotNull String namespace, @NotNull UUID uuid) {
        AssertUtils.notBlank(namespace, "无效的命名空间");
        AssertUtils.notNull(uuid, "无效的唯一标识");
        byte[] hash = MESSAGE_DIGEST.digest((namespace + "#" + uuid).getBytes(StandardCharsets.UTF_8));
        // 将哈希值转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        this.hashString = hexString.toString();
    }

    public static Identifier from(@NotNull Class<?> namespace, @NotNull UUID uuid) {
        AssertUtils.notNull(namespace, "无效的命名空间");
        AssertUtils.notNull(uuid, "无效的唯一标识");
        return new Identifier(ClassUtils.getDesc(namespace), uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Identifier)) {
            return false;
        }
        final Identifier that = (Identifier) o;

        return Objects.equals(hashString, that.hashString);
    }

    @Override
    public int hashCode() {
        return hashString != null ? hashString.hashCode() : 0;
    }

    @Override
    public String toString() {
        return hashString;
    }
}
