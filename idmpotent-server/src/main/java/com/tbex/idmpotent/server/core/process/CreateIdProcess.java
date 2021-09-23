package com.tbex.idmpotent.server.core.process;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.msg.enums.ResponseCode;
import com.tbex.idmpotent.server.cache.GuavaCacheUtil;
import com.tbex.idmpotent.server.core.IdpChecker;
import com.tbex.idmpotent.server.core.enums.KeyState;
import com.tbex.idmpotent.server.core.store.FastStorage;
import com.tbex.idmpotent.server.core.store.IdmpotentStoreService;
import com.tbex.idmpotent.server.server.MessageCreator;
import com.tbex.idmpotent.server.token.TokenProvider;
import com.tbex.idmpotent.server.utils.IDKeyGenUtil;
import com.tbex.idmpotent.server.utils.MySeqIdGen;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static com.tbex.idmpotent.netty.msg.MessageConstants.STATE_OK;

/**
 * 创建幂等id
 */
@Component
@Slf4j
public class CreateIdProcess implements IdpChecker {


    @Autowired
    FastStorage fastStorage;

    @Autowired
    IdmpotentStoreService idmpotentStoreService;

    @Override
    public void process(Channel channel, RpcCmd rpcCmd) {
        createId(channel, rpcCmd);
    }

    /**
     * 创建唯一id
     */
    public void createId(Channel channel, RpcCmd rpcCmd) {
        String id = MySeqIdGen.getId();
        //校验token
        if (!rpcCmd.getToken().equals(TokenProvider.get())) {
            channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ResponseCode.NOT_LOGIN));
            return;
        }
        rpcCmd.getMsg().setIdempotentId(id);


        //添加幂等状态
        Set<KeyState> keyStates = new HashSet<>();
        keyStates.add(KeyState.INIT);
        //存储本地缓存，默认过期时间10min
        GuavaCacheUtil.put(id, IDKeyGenUtil.newInit(id));
        /**已异步存储redis / mysql*/
        idmpotentStoreService.asyncSaveCacheAndDB(id, keyStates);
        channel.writeAndFlush((MessageCreator.okResponse(rpcCmd, STATE_OK)));
    }


}
