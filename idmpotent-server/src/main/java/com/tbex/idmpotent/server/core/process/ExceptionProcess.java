package com.tbex.idmpotent.server.core.process;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.server.cache.GuavaCacheUtil;
import com.tbex.idmpotent.server.config.IdmpotentServerConfig;
import com.tbex.idmpotent.server.core.IdpChecker;
import com.tbex.idmpotent.server.core.IdpKey;
import com.tbex.idmpotent.server.core.checker.IdpCheckerStateHelper;
import com.tbex.idmpotent.server.core.enums.KeyState;
import com.tbex.idmpotent.server.core.store.FastStorage;
import com.tbex.idmpotent.server.core.store.IDKeyStore;
import com.tbex.idmpotent.server.core.store.IdmpotentStoreService;
import com.tbex.idmpotent.server.core.store.JdbcKeyStore;
import com.tbex.idmpotent.server.server.MessageCreator;
import com.tbex.idmpotent.server.server.validate.CommonValidate;
import com.tbex.idmpotent.server.utils.*;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static com.tbex.idmpotent.netty.msg.enums.ResponseCode.ID_NOT_EXIST;
import static com.tbex.idmpotent.server.utils.MySeqIdGen.default_seq_prefix;

/**
 * 程序性异常，允许重复请求
 *
 */
@Component
@Slf4j
public class ExceptionProcess implements IdpChecker {



    @Autowired
    IdmpotentServerConfig idmpotentServerConfig;

    @Autowired
    FastStorage fastStorage;

    @Autowired
    IDKeyStore idKeyStore;

    @Autowired
    JdbcKeyStore jdbcKeyStore;

    @Autowired
    CommonValidate commonValidate;

    @Autowired
    IdmpotentStoreService idmpotentStoreService;
    @Override
    public void process(Channel channel, RpcCmd rpcCmd) {
        try {
            //校验该幂等id 是否是当前服务处理
            String id = rpcCmd.getMsg().getIdempotentId();
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
                        idmpotentStoreService.asyncUpdateCacheAndDB(idpKey, keyStates);
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
                    idmpotentStoreService.asyncUpdateCacheAndDB(idpKey, keyStates);
                }
            } else {
                //表示 该幂等id不是本服务的，是切换过来的，处理不是该服务处理的幂等,获取redis/mysql
                //如果存在的话，直接返回 幂等拦截,幂等重复,直接返回调用端
                IdpKey idpKey = idKeyStore.getCacheIdpKey(id, bussinessType, keyStates);
                boolean checkResult = IdpCheckerStateHelper.getInstance().checkIdpKeySuccess(idpKey, channel, rpcCmd);
                if (checkResult) {
                    return;
                }
                //todo 如果出现异常 直接更新本地缓存 状态为异常，不关心程序执行到哪里
                idpKey = IDKeyGenUtil.newExecuting(idpKey.getId());
                //异步更新redis/mysql
                idmpotentStoreService.asyncUpdateCacheAndDB(idpKey, keyStates);
            }
        } catch (Exception ex) {
            log.error("bussiness exception  is error：{}", ex);
        }

    }
}
