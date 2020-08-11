package com.tbex.idmpotent.client.keystore;

import com.tbex.idmpotent.client.IdpKey;
import com.tbex.idmpotent.client.util.StringJoinerUtil;

/**
 * @ClassName: KeyStoreException
 * @Description: todo 数据持久化异常
 * @Author: xuliang
 * @Date: 2020/4/17 下午4:57
 * @Version: 1.0
 */
public class KeyStoreException extends Exception {

    public KeyStoreException(String msg, IdpKey k) {
        super(StringJoinerUtil.join(msg, k.toString()));
    }

    public KeyStoreException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public KeyStoreException(String msg, String id, Throwable cause) {
        super(StringJoinerUtil.join(msg, " id=", id), cause);
    }

    public KeyStoreException(String msg, IdpKey k, Throwable cause) {
        super(StringJoinerUtil.join(msg, " ", k.toString()), cause);
    }

}
