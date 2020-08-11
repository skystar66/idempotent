package com.tbex.idmpotent.client.idpchecker;


import com.tbex.idmpotent.client.IdpKey;
import com.tbex.idmpotent.client.util.StringJoinerUtil;

/**
 * @ClassName: RejectException
 * @Description: todo 拒绝异常处理
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:27
 * @Version: 1.0
 */
public class RejectException extends Exception {

    public RejectException(String msg) {
        super(msg);
    }

    public RejectException(String msg, IdpKey idpKey) {
        super(StringJoinerUtil.join(msg, " ", idpKey == null ? "" : idpKey.toString()));
    }

    public RejectException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RejectException(String msg, IdpKey idpKey, Throwable cause) {
        super(StringJoinerUtil.join(msg, " ", idpKey.toString()), cause);
    }
}
