package com.tbex.idmpotent.server.utils;

import com.tbex.idmpotent.server.core.IdpKey;
import com.tbex.idmpotent.server.core.enums.KeyState;
import com.tbex.idmpotent.server.exception.KeyGenException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.Date;

import static com.tbex.idmpotent.server.utils.Constants.IDPKEY_COMPRESS_EXCEPTION;


/**
 *
 */
public class IDKeyGenUtil {

    public static byte[] serialize(Object res) throws IOException {
        String serialized = JsonUtils.toJSONString(res);
        byte[] compressed = CompressUtil.gzip(serialized);
        return Base64.getEncoder().encode(compressed);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        byte[] decoded = Base64.getDecoder().decode(bytes);
        String uncompressed = CompressUtil.unGZIP(decoded);
        return JsonUtils.parseString(uncompressed, type);

    }

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

    /**
     * 幂等服务初始化
     */
    public static IdpKey newExecuting(String id) {
        return new IdpKey()
                .setId(id)
                .setCreatedTime(new Date())
                .setKeyState(KeyState.EXECUTING);

    }

    /**
     * 程序业务异常
     */
    public static IdpKey newBussinessException(String id) {
        return new IdpKey()
                .setId(id)
                .setCreatedTime(new Date())
                .setKeyState(KeyState.BUSSINESS_EXCEPTION);

    }


    /**
     * 程序异常
     */
    public static IdpKey newException(String id) {
        return new IdpKey()
                .setId(id)
                .setCreatedTime(new Date())
                .setKeyState(KeyState.EXECUTING);

    }


    public static void main(String[] args) {

        String msg = "阿速达海曙敌我急迫急迫极品家丁hiuhdashdashdadniasuhdoiajdoiasodsdoshadon pOhdahoiqhoidhaoihodhadhadhosad哈搜定好后ID啊后的哈数好哦呵呵耦合红豆红Ohdo后和OH哦后哦后的厚爱HO大红色的挥洒的hi算的hIUASFHIU爱好哦哦";

        System.out.println("字节长度：" + msg.length());
        try {
            byte[] zipLength = IDKeyGenUtil.serialize(msg);

            System.out.println("压缩后的长度：" + zipLength.length);
        } catch (Exception ex) {

        }


    }


    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

}
