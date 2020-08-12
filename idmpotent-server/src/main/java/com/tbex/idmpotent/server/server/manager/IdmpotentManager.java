package com.tbex.idmpotent.server.server.manager;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.netty.msg.enums.ResponseCode;
import com.tbex.idmpotent.server.cache.GuavaCacheUtil;
import com.tbex.idmpotent.server.cache.LocalCacheUtils;
import com.tbex.idmpotent.server.config.IdmpotentServerConfig;
import com.tbex.idmpotent.server.core.IdpKey;
import com.tbex.idmpotent.server.core.Pair;
import com.tbex.idmpotent.server.core.checker.IdpChecker;
import com.tbex.idmpotent.server.core.enums.KeyState;
import com.tbex.idmpotent.server.core.store.FastStorage;
import com.tbex.idmpotent.server.core.store.IDKeyStore;
import com.tbex.idmpotent.server.core.store.JdbcKeyStore;
import com.tbex.idmpotent.server.core.store.impl.RedisStorage;
import com.tbex.idmpotent.server.server.MessageCreator;
import com.tbex.idmpotent.server.token.TokenProvider;
import com.tbex.idmpotent.server.utils.*;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.tbex.idmpotent.netty.msg.MessageConstants.STATE_OK;
import static com.tbex.idmpotent.server.utils.TopicConstants.USER_NAME;
import static com.tbex.idmpotent.server.utils.TopicConstants.USER_PWD;

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

    @Autowired
    IdpChecker idpChecker;

    /**
     * 用户登录
     */
    public void handleLogin(Channel channel, RpcCmd rpcCmd) {
        String token = TokenProvider.get();
        if (StringUtils.isEmpty(token)) {

            String userNameAndPwd = rpcCmd.getMsg().loadBean(String.class);
            String[] params = userNameAndPwd.split(":");

            String userName = params[0];
            String pwd = params[1];
            //登陆成功
            if (userName.equals(USER_NAME) && pwd.equals(TopicConstants.USER_PWD)) {
                //生成token
                token = UUID.randomUUID().toString();
                TokenProvider.put(token);
            }
            rpcCmd.setToken(token);
            channel.writeAndFlush(MessageCreator.okResponse(rpcCmd, STATE_OK));
            log.info("user login: userName:{} pwd :{} token : {}", userName, pwd, token);
            return;
        }
        rpcCmd.setToken(token);
        log.info("user login: userName:{} pwd :{} token : {}", USER_NAME, USER_PWD, token);
        channel.writeAndFlush(MessageCreator.okResponse(rpcCmd, STATE_OK));
    }


    /**
     * 用户退出登录
     */
    public void handleLogout(Channel channel, RpcCmd rpcCmd) {
        //校验token
        if (!rpcCmd.getToken().equals(TokenProvider.get())) {
            channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ResponseCode.NOT_LOGIN));
            return;
        }
        //移除token
        TokenProvider.remove();
        channel.writeAndFlush((MessageCreator.okResponse(rpcCmd, STATE_OK)));
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
        rpcCmd.getMsg().setData(id);
        channel.writeAndFlush((MessageCreator.okResponse(rpcCmd, STATE_OK)));
    }


    /**
     * @return
     * @Author xuliang
     * @Description //TODO 流程节点：执行中
     * @Date 下午3:52 2020/4/26
     * @Param channel, rpcCmd
     **/
    public void executing(Channel channel, RpcCmd rpcCmd) {
        try {
            idpChecker.executing(channel, rpcCmd);
        } catch (Exception ex) {
            log.error("幂等服务异常，executing errorMsg：{}", ex);
        }
    }


    /**
     * @return
     * @Author xuliang
     * @Description //TODO 流程节点：执行成功
     * @Date 下午3:52 2020/4/26
     * @Param channel, rpcCmd
     **/
    public void success(Channel channel, RpcCmd rpcCmd) {
        try {
            idpChecker.success(channel, rpcCmd);
        } catch (Exception ex) {
            log.error("幂等服务异常，executing errorMsg：{}", ex);
        }
    }

    /**
     * @return
     * @Author xuliang
     * @Description //TODO 流程节点：业务处理异常
     * @Date 下午3:52 2020/8/12
     * @Param channel, rpcCmd
     **/
    public void bussinesException(Channel channel, RpcCmd rpcCmd) {
        try {
            idpChecker.bussinesException(channel, rpcCmd);
        } catch (Exception ex) {
            log.error("幂等服务异常，executing errorMsg：{}", ex);
        }
    }


    /**
     * @return
     * @Author xuliang
     * @Description //TODO 流程节点：程序异常
     * @Date 下午3:52 2020/8/12
     * @Param channel, rpcCmd
     **/
    public void exception(Channel channel, RpcCmd rpcCmd) {
        try {
            idpChecker.exception(channel, rpcCmd);
        } catch (Exception ex) {
            log.error("幂等服务异常，executing errorMsg：{}", ex);
        }
    }








}
