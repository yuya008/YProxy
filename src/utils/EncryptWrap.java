package utils;

import java.nio.ByteBuffer;

public interface EncryptWrap {
    void wrap(ByteBuffer buffer, int size);
}
