package team.idealstate.hyper.rpc.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
