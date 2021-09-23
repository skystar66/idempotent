package com.tbex.idmpotent.server.core.process;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.msg.enums.ResponseCode;
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

import static com.tbex.idmpotent.netty.msg.MessageConstants.STATE_OK;
import static com.tbex.idmpotent.netty.msg.enums.ResponseCode.ID_DUPLICATE;
import static com.tbex.idmpotent.netty.msg.enums.ResponseCode.ID_SUCCESS;
import static com.tbex.idmpotent.server.utils.MySeqIdGen.default_seq_prefix;

/**
 * 处理业务
 */
@Component
@Slf4j
public class ExcutingProcess implements IdpChecker {


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
            //校验幂等
            boolean validate = commonValidate.checkCommonValidate(rpcCmd);
            if (validate) {
                channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ResponseCode.ID_DUPLICATE_PERIOD));
                return;
            }
            //校验该幂等id 是否是当前服务处理
            String id = rpcCmd.getMsg().getIdempotentId();
            //业务线类型
            String bussinessType = rpcCmd.getType();
            //获取幂等id 的 节点id
            String nodeId = id.substring(0, 2);
            //获取幂等缓存key
            String cacheKey = CacheKeyUtil.getIdpKeyById(bussinessType, id);
            //添加幂等状态
            Set<KeyState> keyStates = new HashSet<>();
            keyStates.add(KeyState.EXECUTING);
            IdpKey idpKey = null;
            if (nodeId.equals(idmpotentServerConfig.getNodeId())) {
                //获取id生成时间
                String timestamp = id.substring((default_seq_prefix + MySeqIdGen.node).length() - 1, id.length() - idmpotentServerConfig.getCountnum());
                //校验该时间是否在10分钟内,这里减1  主要为啦 解决时间边界问题
                /**10分钟概念：正常来说 每个请求的耗时 不会超过10分钟，所以设置10分钟的含义在于，1，减轻redis压力，例如：一个请求在1s
                 * 就已经处理完啦，此时本地缓存已经清除掉啦，这时就需要查询redis 来获取数据 进行校验判断，但是这个查询redis 是个无用的查询，白白增加啦啊redis的压力，如果本地缓存失效，
                 * 就证明该请求已经成功处理完毕，不需要再查询redis。
                 * 1，可以用这种方式来减轻redis的交互，但是还有一个场景，例如 1，生成啦一个幂等id 为1，2，它处理完啦整个流程 耗时 1分钟，缓存被清空，3，过啦3分钟 再次查询，发现 id
                 * 缓存不存在，并且 它的耗时已超过 3分钟，此时就可以认为 这个请求是个重复的请求，直接返回幂等拦截，重复请求，也就是说 一个幂等id的过期时间在 3分钟。      ？？？
                 * */
                if (DateUtil.timeDifferenceMinutes(Long.valueOf(timestamp)) < idmpotentServerConfig.getPeriodTime() - 1) {
                    //获取本地缓存
                    Object cacheObj = GuavaCacheUtil.get(cacheKey);
                    if (cacheObj == null) {
                        //查询redis
                        idpKey = idKeyStore.getCacheIdpKey(id, bussinessType, keyStates);
                    } else {
                        idpKey = (IdpKey) cacheObj;
                    }
                } else {
                    //非10分钟以内的, 直接返回幂等失效，重新获取
                    channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ID_DUPLICATE));
                    return;
                }
            } else {
                //表示 该幂等id不是本服务的，是切换过来的，处理不是该服务处理的幂等,获取redis/mysql
                //如果存在的话，直接返回 幂等拦截,幂等重复,直接返回调用端
                idpKey = idKeyStore.getCacheIdpKey(id, bussinessType, keyStates);
                process(idpKey, channel, rpcCmd, keyStates);
                return;
            }
            process(idpKey, channel, rpcCmd, keyStates);
            GuavaCacheUtil.put(cacheKey, idpKey);
        } catch (Exception ex) {
            log.error("Executing  is error：{}", ex);
        }
    }


    public void process(IdpKey idpKey, Channel channel, RpcCmd rpcCmd, Set<KeyState> keyStates) {
        if (idpKey.getKeyState() == KeyState.INIT) {
            //更新执行中
            idpKey.setKeyState(KeyState.EXECUTING);
            //表示新的幂等情求 异步处理redis/mysql
            idmpotentStoreService.asyncUpdateCacheAndDB(idpKey, keyStates);
        } else {
            /**走幂等校验*/
            IdpCheckerStateHelper.getInstance().checkIdpKeyStatus(idpKey, channel, rpcCmd);
        }
        channel.writeAndFlush(MessageCreator.okResponse(rpcCmd, STATE_OK));
    }

}
