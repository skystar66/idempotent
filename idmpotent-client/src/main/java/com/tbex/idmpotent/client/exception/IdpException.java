package com.tbex.idmpotent.client.exception;


/**
 * @ClassName: IdpException
 * @Description: todo 幂等性服务异常
 * @Author: xuliang
 * @Date: 2020/4/17 下午4:51
 * @Version: 1.0
 */
public class IdpException extends RuntimeException {

    public IdpException(String msg) {
        super(msg);
    }

    public IdpException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
