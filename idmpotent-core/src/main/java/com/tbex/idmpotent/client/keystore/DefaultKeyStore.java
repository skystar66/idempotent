package com.tbex.idmpotent.client.keystore;

import com.tbex.idmpotent.client.IdpKey;
import com.tbex.idmpotent.client.KeyState;
import com.tbex.idmpotent.client.Pair;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @ClassName: DefaultKeyStore
 * @Description: todo 默认在线程隔离变量中保存,只适用于服务器只有单线程且采取BIO的通信模型，因为实际中重试不一定发生在同一个线程内
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:36
 * @Version: 1.0
 */
public class DefaultKeyStore implements KeyStore {

    private ConcurrentMap<String, IdpKey> keys = new ConcurrentHashMap<>();

    @Override
    public void replace(IdpKey k) throws KeyStoreException {
        keys.put(k.getId(), k);
    }

    @Override
    public Pair putIfAbsent(IdpKey k) {
        IdpKey idpKey = keys.get(k.getId());
        if (null == idpKey) {
            keys.put(k.getId(), k);
            return new Pair(k, 0);
        }
        return new Pair(idpKey, 1);
    }

    @Override
    public Pair putIfAbsentOrInStates(IdpKey k, Set<KeyState> states) throws KeyStoreException {
        IdpKey oldK = keys.get(k.getId());
        if (null != states && states.contains(oldK.getKeyState())) {
            keys.put(k.getId(), k);
            return new Pair(k, 0);
        }
        return new Pair(oldK, 1);
    }

    @Override
    public void remove(String id) {
        keys.remove(id);
    }

}
