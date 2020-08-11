package com.tbex.idmpotent.client.keystore.redis;

import com.google.common.collect.Lists;
import com.tbex.idmpotent.client.IdpKey;
import com.tbex.idmpotent.client.KeyState;
import com.tbex.idmpotent.client.Msgs;
import com.tbex.idmpotent.client.Pair;
import com.tbex.idmpotent.client.keystore.KeyStore;
import com.tbex.idmpotent.client.keystore.KeyStoreException;
import com.tbex.idmpotent.client.util.FileUtil;
import com.tbex.idmpotent.client.util.JsonUtil;
import com.tbex.idmpotent.client.util.StringJoinerUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 */
@Data
@Slf4j
@Accessors(chain = true)
public class RedisKeyStore implements KeyStore {

    private RedisClient redisClient;

    private static final String KEY_PREFIX = "idp-";

    private static final String PUTIFABSENT_SCRIPT_PATH = "/lua/putIfAbsent.lua";

    private static volatile String PUTIFABSENT_SCRIPT_SHA;

    private static final String PUTIFABSENTORINSTATES_SCRIPT_PATH = "/lua/putIfAbsentOrInStates.lua";

    private static volatile String PUTIFABSENTORINSTATES_SCRIPT_SHA;

    private String loadScript(String fileName) {
        String content = FileUtil.readExternalResFile(fileName);
        return redisClient.loadScript(content);
    }

    private String serJson(Object obj) throws KeyStoreException {
        try {
            return JsonUtil.write(obj);
//            return FastJsonUtil.bean2Json(obj);
        } catch (Exception e) {
            throw new KeyStoreException(StringJoinerUtil.join(Msgs.IDPKEY_KEYSTORE_SERIALIZING_EXCEPTION, obj.toString()), e);
        }
    }

    private <T> T deserJson(String json, Class<T> type) throws KeyStoreException {
        try {
            return JsonUtil.read(json, type);
        } catch (Exception e) {
            throw new KeyStoreException(StringJoinerUtil.join(Msgs.IDPKEY_KEYSTORE_SERIALIZING_EXCEPTION, json), e);
        }
    }

    /**
     * 使用Redis执行Lua脚本
     */
    private Pair executeScript(String sha, String... params) throws KeyStoreException {
        List<String> paramList = Lists.newArrayList();
        if (params != null) {
            paramList.addAll(Arrays.asList(params));
        }
//        System.out.println(sha);
        String res = redisClient.executeLua(sha, Lists.newArrayList(), paramList);
//        if (!(res instanceof String)) {
//            return null;
//        }
//        System.out.println("start 返回结果 :" + res.replace("'\'",""));
        Pair pair = deserJson(res, Pair.class);
//        System.out.println("end 返回结果 :" + pair);
        return pair;
    }

    @PostConstruct
    public void init() {
        PUTIFABSENT_SCRIPT_SHA = loadScript(PUTIFABSENT_SCRIPT_PATH);
        PUTIFABSENTORINSTATES_SCRIPT_SHA = loadScript(PUTIFABSENTORINSTATES_SCRIPT_PATH);
    }

    @Override
    public void replace(IdpKey k) throws KeyStoreException {
        String json = serJson(k);
        log.info("更新 idpkey  id：{} json:{} ", k.getId(), json);
        redisClient.setex(KEY_PREFIX + k.getId(), (int) EXPIRE_TIME, json);
    }

    @Override
    public void remove(String id) throws KeyStoreException {
        redisClient.del(id);
    }

    @Override
    public Pair putIfAbsent(IdpKey k) throws KeyStoreException {
        String kJson = serJson(k);
        String expireTime = Long.toString(EXPIRE_TIME);
        return executeScript(PUTIFABSENT_SCRIPT_SHA, kJson, expireTime);
    }

    @Override
    public Pair putIfAbsentOrInStates(IdpKey k, Set<KeyState> states)
            throws KeyStoreException {
        String kJson = serJson(k);
        String statesJson = serJson(states);
        String expireTime = Long.toString(EXPIRE_TIME);
        return executeScript(PUTIFABSENTORINSTATES_SCRIPT_SHA, kJson, statesJson, expireTime);
    }

    @Override
    public KeyState getStatus(String id) throws KeyStoreException {
        String keyStoreJson = redisClient.get(id);
        try {
            IdpKey idpKey = JsonUtil.read(keyStoreJson, IdpKey.class);
            return idpKey.getKeyState();
        } catch (IOException ex) {
            throw new KeyStoreException(StringJoinerUtil.join(Msgs.IDPKEY_KEYSTORE_SERIALIZING_EXCEPTION, keyStoreJson), ex);

        }
    }
}
