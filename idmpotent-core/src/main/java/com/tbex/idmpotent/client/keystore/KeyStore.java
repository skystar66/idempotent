package com.tbex.idmpotent.client.keystore;


import com.tbex.idmpotent.client.IdpKey;
import com.tbex.idmpotent.client.KeyState;
import com.tbex.idmpotent.client.Pair;

import java.util.Set;

/**
 * @ClassName: KeyStore
 * @Description: todo 处理幂等性服务的数据存储
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:05
 * @Version: 1.0
 */
public interface KeyStore {

    /**
     * 超时时间，超过则清除 / s
     */
    long EXPIRE_TIME = 300;

    void replace(IdpKey k) throws KeyStoreException;

    default KeyState getStatus(String id) throws KeyStoreException {
        return null;
    }

    void remove(String id) throws KeyStoreException;

    Pair putIfAbsent(IdpKey k) throws KeyStoreException;

    Pair putIfAbsentOrInStates(IdpKey k, Set<KeyState> states) throws KeyStoreException;
}
