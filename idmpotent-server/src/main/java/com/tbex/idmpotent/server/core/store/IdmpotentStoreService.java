package com.tbex.idmpotent.server.core.store;

import com.tbex.idmpotent.server.core.IdpKey;
import com.tbex.idmpotent.server.core.Pair;
import com.tbex.idmpotent.server.core.enums.KeyState;
import com.tbex.idmpotent.server.exception.KeyStoreException;
import com.tbex.idmpotent.server.utils.IDKeyGenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static com.tbex.idmpotent.server.utils.Constants.IDPKEY_KEYSTORE_SAVE_EXCEPTION;
import static com.tbex.idmpotent.server.utils.RedisConstants.idp_server_node_prefix;

@Service
@Slf4j
public class IdmpotentStoreService {
    @Autowired
    IDKeyStore idKeyStore;

    @Autowired
    JdbcKeyStore jdbcKeyStore;

    @Autowired
    FastStorage fastStorage;


    @Resource(name = "saveStore")
    private ExecutorService saveStore;


    /**
     * @return
     * @Author xuliang
     * @Description //TODO 异步处理保存redis mysql
     * @Date 下午4:43 2020/4/26
     * @Param
     **/
    public void asyncSaveCacheAndDB(String id, Set<KeyState> keyStates) {
        saveStore.submit(() -> {
            try {
                Pair pair = idKeyStore.putIfAbsentOrInStates(IDKeyGenUtil.newInit(id), keyStates);
                /**此处使用乐观锁 类似于 CAS操作*/
                if (pair.getIdpKey().getKeyState() == KeyState.EXECUTING && pair.getCount() == 1) {
                    log.info("save redis 数据成功！ id:{}", id);
                    //处理成功,存储mysql
                    Pair pairDB = jdbcKeyStore.putIfAbsentOrInStates(IDKeyGenUtil.newExecuting(id), keyStates);
                    if (pairDB.getIdpKey().getKeyState() == KeyState.EXECUTING && pairDB.getCount() == 1) {
                        //处理成功
                        log.info("save mysql 数据成功！ id:{}", id);
                    } else {
                        //幂等失败
                        log.info("save mysql 数据失败！其它线程已处理 id:{}", id);
                    }
                } else {
                    //幂等失败
                    log.info("save redis 数据失败！其它线程已处理 id:{}", id);
                }
            } catch (KeyStoreException ex) {
                log.error(IDPKEY_KEYSTORE_SAVE_EXCEPTION, ex);
            } catch (Exception ex) {
                log.error("其它异常：{}", ex);
            }
        });
    }

    /**
     * @return
     * @Author xuliang
     * @Description //TODO 异步处理更新redis mysql
     * @Date 下午4:43 2020/4/26
     * @Param
     **/
    public void asyncUpdateCacheAndDB(IdpKey idpKey, Set<KeyState> keyStates) {
        saveStore.submit(() -> {
            try {
                Pair pair = idKeyStore.putIfAbsentOrInStates(idpKey, keyStates);
                if (pair.getIdpKey().getKeyState() == KeyState.SUCCESS && pair.getCount() == 1) {
                    log.info("update redis 数据成功！ id:{}", idpKey.getId());
                    //处理成功,存储mysql
                    jdbcKeyStore.replace(idpKey);
                    //处理成功
                    log.info("update mysql 数据成功！ id:{}", idpKey.getId());
                } else {
                    //幂等失败
                    log.info("update redis 数据失败！其它线程已处理 id:{}", idpKey.getId());
                }
            } catch (KeyStoreException ex) {
                log.error(IDPKEY_KEYSTORE_SAVE_EXCEPTION, ex);
            } catch (Exception ex) {
                log.error("其它异常：{}", ex);
            }
        });
    }


    /**
     * @return
     * @Author xuliang
     * @Description //TODO 标记当前幂等为当前服务node处理
     *
     * ---node
     * -----id
     * @Date 下午4:43 2020/4/26
     * @Param
     **/
    public void setIdpNodeFlag(String node,String id){
        fastStorage.hset(
                String.format(idp_server_node_prefix,node),id,id);
    }

    /**
     * @return
     * @Author xuliang
     * @Description //TODO 标记当前幂等为当前服务node处理
     *
     * ---node
     * -----id
     * @Date 下午4:43 2020/4/26
     * @Param
     **/
    public void delIdpNodeFlag(String node,String id){
        fastStorage.hdel(
                String.format(idp_server_node_prefix,node),id);
    }

}
