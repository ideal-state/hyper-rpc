package team.idealstate.hyper.rpc.impl;

import java.util.Base64;

/**
 * <p>Base64Utils</p>
 *
 * <p>创建于 2024/1/22 16:25</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Base64Utils {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    public static byte[] encode(byte[] data) {
        return ENCODER.encode(data);
    }

    public static byte[] decode(byte[] data) {
        return DECODER.decode(data);
    }
}
