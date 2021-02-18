package com.tbex.idmpotent.server.server.manager;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.msg.enums.EventType;
import com.tbex.idmpotent.server.config.IdmpotentServerConfig;
import com.tbex.idmpotent.server.core.IdpChecker;
import com.tbex.idmpotent.server.core.process.ServiceProcessInitializer;
import com.tbex.idmpotent.server.core.store.FastStorage;
import com.tbex.idmpotent.server.core.store.IDKeyStore;
import com.tbex.idmpotent.server.core.store.JdbcKeyStore;
import com.tbex.idmpotent.server.utils.*;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: IdmpotentManager
 * @Description: todo java类作用描述
 * @Author: xuliang
 * @Date: 2020/4/22$ 下午5:09$
 * @Version: 1.0
 */
@Slf4j
@Component
public class IdmpotentManager {

    @Autowired
    IdmpotentServerConfig idmpotentServerConfig;

    @Autowired
    FastStorage fastStorage;

    @Autowired
    IDKeyStore idKeyStore;

    @Autowired
    JdbcKeyStore jdbcKeyStore;

    /**
     * @return
     * @Author xuliang
     * @Description //TODO 流程节点：执行中
     * @Date 下午3:52 2020/4/26
     * @Param channel, rpcCmd
     **/
    public void execute(Channel channel, RpcCmd rpcCmd) {
        try {
            IdpChecker idpChecker
                    = ServiceProcessInitializer.getInstance().getProcessService(EventType.valueOf(rpcCmd.getEvent()));
            idpChecker.process(channel, rpcCmd);
        } catch (Exception ex) {
            log.error("幂等服务异常，execute errorMsg：{}", ex);
        }
    }
}
