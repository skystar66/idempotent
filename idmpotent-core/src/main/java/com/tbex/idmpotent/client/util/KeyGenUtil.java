package com.tbex.idmpotent.client.util;

import com.tbex.idmpotent.client.IdpKey;
import com.tbex.idmpotent.client.KeyState;
import com.tbex.idmpotent.client.keyprovider.KeyGenException;

import java.io.IOException;
import java.util.Base64;

import static com.tbex.idmpotent.client.Msgs.IDPKEY_COMPRESS_EXCEPTION;

/**
 * @ClassName: KeyGenUtil
 * @Description: todo key生成处理类
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:00
 * @Version: 1.0
 */

public class KeyGenUtil {

    public static byte[] serialize(Object res) throws IOException {
        String serialized = JsonUtil.write(res);
        byte[] compressed = CompressUtil.gzip(serialized);
        return Base64.getEncoder().encode(compressed);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        byte[] decoded = Base64.getDecoder().decode(bytes);
        String uncompressed = CompressUtil.unGZIP(decoded);
        try {
            return JsonUtil.read(uncompressed, type);
        } catch (IOException e) {
            throw new IOException("反序列化失败：" + new String(bytes) + " -> " + type.getName(), e);
        }
    }


    /**
     * @Author xuliang
     * @Description //TODO 处理成功保存结果
     * @Date 下午5:01 2020/4/17
     * @Param id res
     * @return idkey
     **/
    public static IdpKey newSuccess(String id, Object res) throws KeyGenException {
        try {
            byte[] serialized = serialize(res);
            return new IdpKey()
                    .setId(id)
                    .setKeyState(KeyState.SUCCESS)
                    .setContent(serialized);
        } catch (IOException e) {
            throw new KeyGenException(IDPKEY_COMPRESS_EXCEPTION, e);
        }
    }

    public static IdpKey newExecuting(String id) {
        return new IdpKey()
                .setId(id)
                .setKeyState(KeyState.EXECUTING);
    }

}
