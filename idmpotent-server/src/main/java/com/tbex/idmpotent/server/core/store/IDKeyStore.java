package com.tbex.idmpotent.server.core.store;

import com.google.common.collect.Lists;
import com.tbex.idmpotent.server.core.IdpKey;
import com.tbex.idmpotent.server.core.Pair;
import com.tbex.idmpotent.server.core.enums.KeyState;
import com.tbex.idmpotent.server.exception.KeyStoreException;
import com.tbex.idmpotent.server.utils.FileUtil;
import com.tbex.idmpotent.server.utils.JsonUtils;
import com.tbex.idmpotent.server.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 */
@Component
public class IDKeyStore {


    @Autowired
    FastStorage fastStorage;

    @Autowired
    JdbcKeyStore jdbcKeyStore;

    long EXPIRE_TIME = 1000;


    public static final String PUTIFABSENTORINSTATES_SCRIPT_PATH = "/lua/putIfAbsentOrInStates.lua";

    public static volatile String PUTIFABSENTORINSTATES_SCRIPT_SHA;

    @PostConstruct
    public void init() {
        String content = FileUtil.readExternalResFile(PUTIFABSENTORINSTATES_SCRIPT_PATH);
        PUTIFABSENTORINSTATES_SCRIPT_SHA = fastStorage.loadScript(content);
    }


    public Pair putIfAbsentOrInStates(IdpKey k, Set<KeyState> states) throws KeyStoreException {
        String kJson = JsonUtils.toJSONString(k);
        String statesJson = JsonUtils.toJSONString(states);
        String expireTime = Long.toString(EXPIRE_TIME);
        return executeScript(PUTIFABSENTORINSTATES_SCRIPT_SHA, kJson, statesJson, expireTime);
    }

    /**
     * 使用Redis执行Lua脚本
     */
    private Pair executeScript(String... params) throws KeyStoreException {
        List<String> paramList = Lists.newArrayList();
        if (params != null) {
            paramList.addAll(Arrays.asList(params));
        }
        String res = fastStorage.executeLua(PUTIFABSENTORINSTATES_SCRIPT_SHA, Lists.newArrayList(), paramList);
        Pair pair = JsonUtils.parseString(res, Pair.class);
        return pair;
    }

    /**
     * 获取idpKey
     */
    public IdpKey getCacheIdpKey(String id, String bussinessType,
                                  Set<KeyState> keyStates) throws KeyStoreException {

        IdpKey idpKey = null;
        String cacheStr = fastStorage.get(RedisKeyUtil.getIdpKeyById(bussinessType, id));
        if (StringUtils.isEmpty(cacheStr)) {
            idpKey = jdbcKeyStore.get(id, keyStates);
            fastStorage.set(RedisKeyUtil.getIdpKeyById(bussinessType, id), JsonUtils.toJSONString(idpKey));
        } else {
            idpKey = JsonUtils.parseString(cacheStr, IdpKey.class);
        }
        return idpKey;
    }


}
