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

package team.idealstate.hyper.rpc.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.idealstate.hyper.common.RsaUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyPair;

/**
 * <p>RsaKeyPairGenerator</p>
 *
 * <p>创建于 2024/2/6 18:27</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public final class RsaKeyPairGenerator {

    private static final Logger logger = LogManager.getLogger(RsaKeyPairGenerator.class);

    public static void main(String[] args) throws IOException {
        logger.info("生成 RSA 密钥对");
        final KeyPair keyPair = RsaUtils.generate();
        logger.info("导出公钥");
        Files.write(new File("./public").toPath(), RsaUtils.exportKey(keyPair.getPublic()));
        logger.info("导出私钥");
        Files.write(new File("./private").toPath(), RsaUtils.exportKey(keyPair.getPrivate()));
        logger.info("已完成密钥对生成");
    }
}
