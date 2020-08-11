package com.tbex.idmpotent.client.keyprovider;

/**
 * @ClassName: RpcException
 * @Description: todo id生成异常
 * @Author: xuliang
 * @Date: 2020/4/17 下午4:59
 * @Version: 1.0
 */
public class RpcException extends Exception {

    public RpcException(String msg) {
        super(msg);
    }

    public RpcException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
