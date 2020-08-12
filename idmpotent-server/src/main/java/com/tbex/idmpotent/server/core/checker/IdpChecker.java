package com.tbex.idmpotent.server.core.checker;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.server.cache.GuavaCacheUtil;
import com.tbex.idmpotent.server.config.IdmpotentServerConfig;
import com.tbex.idmpotent.server.core.IdpKey;
import com.tbex.idmpotent.server.core.Pair;
import com.tbex.idmpotent.server.core.enums.KeyState;
import com.tbex.idmpotent.server.core.store.FastStorage;
import com.tbex.idmpotent.server.core.store.IDKeyStore;
import com.tbex.idmpotent.server.core.store.JdbcKeyStore;
import com.tbex.idmpotent.server.exception.KeyStoreException;
import com.tbex.idmpotent.server.server.MessageCreator;
import com.tbex.idmpotent.server.utils.DateUtil;
import com.tbex.idmpotent.server.utils.IDKeyGenUtil;
import com.tbex.idmpotent.server.utils.CacheKeyUtil;
import com.tbex.idmpotent.server.utils.MySeqIdGen;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static com.tbex.idmpotent.netty.msg.MessageConstants.STATE_OK;
import static com.tbex.idmpotent.netty.msg.enums.ResponseCode.ID_DUPLICATE;
import static com.tbex.idmpotent.netty.msg.enums.ResponseCode.ID_NOT_EXIST;
import static com.tbex.idmpotent.netty.msg.enums.ResponseCode.ID_SUCCESS;
import static com.tbex.idmpotent.server.utils.Constants.IDPKEY_KEYSTORE_SAVE_EXCEPTION;
import static com.tbex.idmpotent.server.utils.MySeqIdGen.default_seq_prefix;

/**
 * @ClassName: IdpChecker
 * @Description: todo java类作用描述
 * @Author: xuliang
 * @Date: 2020/4/26$ 下午4:40$
 * @Version: 1.0
 */
@Component
@Slf4j
public class IdpChecker {


    @Autowired
    IdmpotentServerConfig idmpotentServerConfig;

    @Autowired
    FastStorage fastStorage;

    @Autowired
    IDKeyStore idKeyStore;

    @Autowired
    JdbcKeyStore jdbcKeyStore;

    @Resource(name = "saveStore")
    private ExecutorService saveStore;


    /**
     * @return
     * @Author xuliang
     * @Description //TODO 幂等性 executing状态
     * @Date 下午3:55 2020/4/27
     * @Param channel, rpcCmd
     **/
    public void executing(Channel channel, RpcCmd rpcCmd) {
        try {
            //校验该幂等id 是否是当前服务处理
            String id = rpcCmd.getMsg().loadBean(String.class);
            //业务线类型
            String bussinessType = rpcCmd.getType();
            //获取幂等id 的 节点id
            String nodeId = id.substring(0, 2);
            String cacheKey = CacheKeyUtil.getIdpKeyById(bussinessType, id);
            Set<KeyState> keyStates = new HashSet<>();
            keyStates.add(KeyState.EXECUTING);
            if (nodeId.equals(idmpotentServerConfig.getNodeId())) {
                //获取id生成时间
                String timestamp = id.substring((default_seq_prefix + MySeqIdGen.node).length() - 1, id.length() - idmpotentServerConfig.getCountnum());
                //校验该时间是否在10分钟内,这里减1  主要为啦 解决时间边界问题
                if (DateUtil.timeDifferenceMinutes(Long.valueOf(timestamp)) < idmpotentServerConfig.getPeriodTime() - 1) {
                    //获取本地缓存
                    Object cacheObj = GuavaCacheUtil.get(cacheKey);
                    if (cacheObj == null) {
                        //存储本地缓存
                        GuavaCacheUtil.put(cacheKey, IDKeyGenUtil.newExecuting(id));
                        //表示新的幂等情求 异步处理redis/mysql
                        asyncSaveCacheAndDB(id, keyStates);
                        channel.writeAndFlush(MessageCreator.okResponse(rpcCmd, STATE_OK));
                        return;
                    } else {
                        //校验是否状态已完成
                        IdpKey idpKey = (IdpKey) cacheObj;
                        checkIdpKeyExecuting(idpKey, channel, rpcCmd);
                    }
                } else {
                    //非10分钟以内的
                    //如果存在的话，直接返回 幂等拦截,幂等重复,直接返回调用端
                    IdpKey idpKey = idKeyStore.getCacheIdpKey(id, bussinessType, keyStates);
                    checkIdpKeyExecuting(idpKey, channel, rpcCmd);
                }
            } else {
                //表示 该幂等id不是本服务的，是切换过来的，处理不是该服务处理的幂等,获取redis/mysql
                //如果存在的话，直接返回 幂等拦截,幂等重复,直接返回调用端
                IdpKey idpKey = idKeyStore.getCacheIdpKey(id, bussinessType, keyStates);
                checkIdpKeyExecuting(idpKey, channel, rpcCmd);
            }
        } catch (Exception ex) {
            log.error("Executing  is error：{}", ex);
        }
    }


    /**
     * @return
     * @Author xuliang
     * @Description //TODO 处理成功
     * @Date 下午4:27 2020/4/27
     * @Param
     **/
    public void success(Channel channel, RpcCmd rpcCmd) {
        try {
            //校验该幂等id 是否是当前服务处理
            String id = rpcCmd.getMsg().loadBean(String.class);
            //业务线类型
            String bussinessType = rpcCmd.getType();
            //获取幂等id 的 节点id
            String nodeId = id.substring(0, 2);
            String cacheKey = CacheKeyUtil.getIdpKeyById(bussinessType, id);
            Set<KeyState> keyStates = new HashSet<>();
            keyStates.add(KeyState.SUCCESS);
            if (nodeId.equals(idmpotentServerConfig.getNodeId())) {
                //获取id生成时间
                String timestamp = id.substring((default_seq_prefix + MySeqIdGen.node).length() - 1, id.length() - idmpotentServerConfig.getCountnum());
                //校验该时间是否在10分钟内
                if (DateUtil.timeDifferenceMinutes(Long.valueOf(timestamp)) < idmpotentServerConfig.getPeriodTime() - 1) {
                    //获取本地缓存
                    Object cacheObj = GuavaCacheUtil.get(cacheKey);
                    if (cacheObj == null) {
                        channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ID_NOT_EXIST));
                        return;
                    } else {
                        //校验是否状态已完成
                        IdpKey idpKey = (IdpKey) cacheObj;
                        boolean checkSuccess = checkRedisOrMysqlIdpKeySuccess(idpKey, channel, rpcCmd);
                        if (!checkSuccess) {
                            return;
                        }
                        idpKey = IDKeyGenUtil.newSuccess(idpKey.getId(), rpcCmd.getMsg().getData());
                        //更新本地缓存
                        GuavaCacheUtil.put(cacheKey, idpKey);
                        //异步更新redis/mysql
                        asyncUpdateCacheAndDB(idpKey, keyStates);
                    }
                } else {
                    //非10分钟以内的
                    //如果存在的话，直接返回 幂等拦截,幂等重复,直接返回调用端
                    IdpKey idpKey = idKeyStore.getCacheIdpKey(id, bussinessType, keyStates);
                    boolean checkSuccess = checkRedisOrMysqlIdpKeySuccess(idpKey, channel, rpcCmd);
                    if (!checkSuccess) {
                        return;
                    }
                    idpKey = IDKeyGenUtil.newSuccess(idpKey.getId(), rpcCmd.getMsg().getData());
                    //更新本地缓存
                    GuavaCacheUtil.put(cacheKey, idpKey);
                    //异步更新redis/mysql
                    asyncUpdateCacheAndDB(idpKey, keyStates);
                }
            } else {
                //表示 该幂等id不是本服务的，是切换过来的，处理不是该服务处理的幂等,获取redis/mysql
                //如果存在的话，直接返回 幂等拦截,幂等重复,直接返回调用端
                IdpKey idpKey = idKeyStore.getCacheIdpKey(id, bussinessType, keyStates);
                boolean checkSuccess = checkRedisOrMysqlIdpKeySuccess(idpKey, channel, rpcCmd);
                if (!checkSuccess) {
                    return;
                }
                idpKey = IDKeyGenUtil.newSuccess(idpKey.getId(), rpcCmd.getMsg().getData());
                //更新本地缓存
                GuavaCacheUtil.put(cacheKey, idpKey);
                //异步更新redis/mysql
                asyncUpdateCacheAndDB(idpKey, keyStates);
            }
        } catch (Exception ex) {
            log.error("Executing  is error：{}", ex);
        }
    }


    /**
     * @return
     * @Author xuliang
     * @Description //TODO 处理业务异常
     * @Date 下午4:27 2020/8/12
     * @Param
     **/
    public void bussinesException(Channel channel, RpcCmd rpcCmd) {
        try {
            //校验该幂等id 是否是当前服务处理
            String id = rpcCmd.getMsg().loadBean(String.class);
            //业务线类型
            String bussinessType = rpcCmd.getType();
            //获取幂等id 的 节点id
            String nodeId = id.substring(0, 2);
            String cacheKey = CacheKeyUtil.getIdpKeyById(bussinessType, id);
            Set<KeyState> keyStates = new HashSet<>();
            keyStates.add(KeyState.BUSSINESS_EXCEPTION);
            if (nodeId.equals(idmpotentServerConfig.getNodeId())) {
                //获取id生成时间
                String timestamp = id.substring((default_seq_prefix + MySeqIdGen.node).length() - 1, id.length() - idmpotentServerConfig.getCountnum());
                //校验该时间是否在10分钟内
                if (DateUtil.timeDifferenceMinutes(Long.valueOf(timestamp)) < idmpotentServerConfig.getPeriodTime() - 1) {
                    //获取本地缓存
                    Object cacheObj = GuavaCacheUtil.get(cacheKey);
                    if (cacheObj == null) {
                        channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ID_NOT_EXIST));
                        return;
                    } else {
                        //校验是否状态已完成
                        IdpKey idpKey = (IdpKey) cacheObj;
                        boolean checkResult = checkIdpKeyBussinessException(idpKey, channel, rpcCmd);
                        if (!checkResult) {
                            return;
                        }

                        idpKey = IDKeyGenUtil.newBussinessException(idpKey.getId());
                        //更新本地缓存
                        GuavaCacheUtil.put(cacheKey, idpKey);
                        //异步更新redis/mysql
                        asyncUpdateCacheAndDB(idpKey, keyStates);
                    }
                } else {
                    //非10分钟以内的
                    //如果存在的话，直接返回 幂等拦截,幂等重复,直接返回调用端
                    IdpKey idpKey = idKeyStore.getCacheIdpKey(id, bussinessType, keyStates);
                    boolean checkResult = checkIdpKeyBussinessException(idpKey, channel, rpcCmd);
                    if (!checkResult) {
                        return;
                    }
                    idpKey = IDKeyGenUtil.newBussinessException(idpKey.getId());
                    //异步更新redis/mysql
                    asyncUpdateCacheAndDB(idpKey, keyStates);
                }
            } else {
                //表示 该幂等id不是本服务的，是切换过来的，处理不是该服务处理的幂等,获取redis/mysql
                //如果存在的话，直接返回 幂等拦截,幂等重复,直接返回调用端
                IdpKey idpKey = idKeyStore.getCacheIdpKey(id, bussinessType, keyStates);
                boolean checkResult = checkIdpKeyBussinessException(idpKey, channel, rpcCmd);
                if (!checkResult) {
                    return;
                }
                idpKey = IDKeyGenUtil.newBussinessException(idpKey.getId());
                //异步更新redis/mysql
                asyncUpdateCacheAndDB(idpKey, keyStates);
            }
        } catch (Exception ex) {
            log.error("bussiness exception  is error：{}", ex);
        }

    }


    /**
     * @return
     * @Author xuliang
     * @Description //TODO 处理异常
     * @Date 下午4:27 2020/8/12
     * @Param
     **/
    public void exception(Channel channel, RpcCmd rpcCmd) {
        try {
            //校验该幂等id 是否是当前服务处理
            String id = rpcCmd.getMsg().loadBean(String.class);
            //业务线类型
            String bussinessType = rpcCmd.getType();
            //获取幂等id 的 节点id
            String nodeId = id.substring(0, 2);
            String cacheKey = CacheKeyUtil.getIdpKeyById(bussinessType, id);
            Set<KeyState> keyStates = new HashSet<>();
            keyStates.add(KeyState.EXCEPTION);
            if (nodeId.equals(idmpotentServerConfig.getNodeId())) {
                //获取id生成时间
                String timestamp = id.substring((default_seq_prefix + MySeqIdGen.node).length() - 1, id.length() - idmpotentServerConfig.getCountnum());
                //校验该时间是否在10分钟内
                if (DateUtil.timeDifferenceMinutes(Long.valueOf(timestamp)) < idmpotentServerConfig.getPeriodTime() - 1) {
                    //获取本地缓存
                    Object cacheObj = GuavaCacheUtil.get(cacheKey);
                    if (cacheObj == null) {
                        channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ID_NOT_EXIST));
                        return;
                    } else {
                        //todo 如果出现异常 直接更新本地缓存 状态为异常，不关心程序执行到哪里，该幂等id作废
                        IdpKey idpKey = (IdpKey) cacheObj;
                        idpKey = IDKeyGenUtil.newExecuting(idpKey.getId());
                        //更新本地缓存
                        GuavaCacheUtil.put(cacheKey, idpKey);
                        //异步更新redis/mysql
                        asyncUpdateCacheAndDB(idpKey, keyStates);
                    }
                } else {
                    //非10分钟以内的
                    //如果存在的话，直接返回 幂等拦截,幂等重复,直接返回调用端
                    IdpKey idpKey = idKeyStore.getCacheIdpKey(id, bussinessType, keyStates);
                    //todo 如果出现异常 直接更新本地缓存 状态为异常，不关心程序执行到哪里，该幂等id作废
                    idpKey = IDKeyGenUtil.newExecuting(idpKey.getId());
                    //更新本地缓存
                    GuavaCacheUtil.put(cacheKey, idpKey);
                    //异步更新redis/mysql
                    asyncUpdateCacheAndDB(idpKey, keyStates);
                }
            } else {
                //表示 该幂等id不是本服务的，是切换过来的，处理不是该服务处理的幂等,获取redis/mysql
                //如果存在的话，直接返回 幂等拦截,幂等重复,直接返回调用端
                IdpKey idpKey = idKeyStore.getCacheIdpKey(id, bussinessType, keyStates);
                //todo 如果出现异常 直接更新本地缓存 状态为异常，不关心程序执行到哪里，该幂等id作废
                idpKey = IDKeyGenUtil.newExecuting(idpKey.getId());
                //更新本地缓存
                GuavaCacheUtil.put(cacheKey, idpKey);
                //异步更新redis/mysql
                asyncUpdateCacheAndDB(idpKey, keyStates);
            }
        } catch (Exception ex) {
            log.error("bussiness exception  is error：{}", ex);
        }

    }


    /**
     * @return
     * @Author xuliang
     * @Description //TODO 处理redis or mysql idpKey 数据
     * @Date 下午4:22 2020/4/27
     * @Param
     **/
    public void checkIdpKeyExecuting(IdpKey idpKey, Channel channel, RpcCmd rpcCmd) {

        try {
            if (idpKey != null) {
                if (idpKey.getKeyState() == KeyState.SUCCESS) {
                    //幂等处理成功,直接返回调用端
                    channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ID_SUCCESS));
                    return;
                }else if (idpKey.getKeyState() == KeyState.EXCEPTION) {
                    //该幂等 上次请求出现程序异常，此次不让处理 ，直接返回失败
                    channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ID_DUPLICATE));
                    return;
                }else if (idpKey.getKeyState() == KeyState.BUSSINESS_EXCEPTION) {
                    //该幂等 上次请求出现程序业务异常，过啦一会，数据修复 已经好啦，此时 允许客户端再次请求
                    channel.writeAndFlush(MessageCreator.okResponse(rpcCmd, STATE_OK));
                    return;
                }
                //幂等拦截
                channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ID_DUPLICATE));
                return;
            }
        } catch (Exception ex) {
            log.error("doRedisOrMysqlIdpKey id error : {}", ex);
        }
    }

    /**
     * @return
     * @Author xuliang
     * @Description //TODO 处理redis or mysql idpKey 数据
     * @Date 下午4:22 2020/4/27
     * @Param
     **/
    public boolean checkRedisOrMysqlIdpKeySuccess(IdpKey idpKey, Channel channel, RpcCmd rpcCmd) {

        try {
            if (idpKey != null) {
                if (idpKey.getKeyState() == KeyState.SUCCESS) {
                    //幂等处理成功,直接返回调用端
                    channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ID_SUCCESS));
                    return false;
                }
                return true;
            }
        } catch (Exception ex) {
            log.error("doRedisOrMysqlIdpKey id error : {}", ex);
        }
        return false;
    }


    /**
     * @return
     * @Author xuliang
     * @Description //TODO 处理redis or mysql idpKey 数据
     * @Date 下午4:22 2020/4/27
     * @Param
     **/
    public boolean checkIdpKeyBussinessException(IdpKey idpKey, Channel channel, RpcCmd rpcCmd) {

        try {
            if (idpKey != null) {
                if (idpKey.getKeyState() == KeyState.SUCCESS) {
                    //幂等处理成功,直接返回调用端
                    channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ID_SUCCESS));
                    return false;
                }
                return true;
            }
        } catch (Exception ex) {
            log.error("doRedisOrMysqlIdpKey id error : {}", ex);
        }
        return false;
    }


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
                Pair pair = idKeyStore.putIfAbsentOrInStates(IDKeyGenUtil.newExecuting(id), keyStates);
                /**此处使用乐观锁 类似于 CAS操作*/
                if (pair.getIdpKey().getKeyState() == KeyState.EXECUTING && pair.getCount() == 1) {
                    log.info("save redis 数据成功！ id:{}", id);
                    //处理成功,存储mysql
                    Pair pairDB = jdbcKeyStore.putIfAbsentOrInStates(IDKeyGenUtil.newExecuting(id), keyStates);
                    if (pair.getIdpKey().getKeyState() == KeyState.EXECUTING && pair.getCount() == 1) {
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
}
