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

package team.idealstate.hyper.rpc.example.common;

import team.idealstate.hyper.rpc.impl.RsaUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * <p>KeyUtils</p>
 *
 * <p>创建于 2024/2/6 21:41</p>
 *
 * @author ketikai
 * @version 1.0.2
 * @since 1.0.0
 */
public abstract class KeyUtils {

    public static PublicKey loadPublicKey() throws IOException {
        final File keyFile = new File("./key");
        final Path keyPath = keyFile.toPath();
        if (!keyFile.exists()) {
            try (InputStream inputStream = KeyUtils.class.getResourceAsStream("/assets/hyper-rpc/client/public")) {
                if (inputStream == null) {
                    throw new FileNotFoundException("消息密钥文件不存在");
                }
                final byte[] data = new byte[inputStream.available()];
                //noinspection ResultOfMethodCallIgnored
                inputStream.read(data);
                Files.write(keyPath, data);
            }
        }
        return RsaUtils.generatePublicKey(RsaUtils.importKey(Files.readAllBytes(keyPath)));
    }

    public static PrivateKey loadPrivateKey() throws IOException {
        final File keyFile = new File("./key");
        final Path keyPath = keyFile.toPath();
        if (!keyFile.exists()) {
            try (InputStream inputStream = KeyUtils.class.getResourceAsStream("/assets/hyper-rpc/server/private")) {
                if (inputStream == null) {
                    throw new FileNotFoundException("消息密钥文件不存在");
                }
                final byte[] data = new byte[inputStream.available()];
                //noinspection ResultOfMethodCallIgnored
                inputStream.read(data);
                Files.write(keyPath, data);
            }
        }
        return RsaUtils.generatePrivateKey(RsaUtils.importKey(Files.readAllBytes(keyPath)));
    }
}
