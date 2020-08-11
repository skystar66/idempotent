package com.tbex.idmpotent.client.keyprovider;

/**
 * @ClassName: KeyGenException
 * @Description: todo id生成异常
 * @Author: xuliang
 * @Date: 2020/4/17 下午4:59
 * @Version: 1.0
 */
public class KeyGenException extends Exception {

    public KeyGenException(String msg) {
        super(msg);
    }

    public KeyGenException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
