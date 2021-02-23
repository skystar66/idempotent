package com.tbex.idmpotent.server.core.checker;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.server.core.IdpKey;
import com.tbex.idmpotent.server.core.enums.KeyState;
import com.tbex.idmpotent.server.server.MessageCreator;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import static com.tbex.idmpotent.netty.msg.MessageConstants.STATE_OK;
import static com.tbex.idmpotent.netty.msg.enums.ResponseCode.ID_DUPLICATE;
import static com.tbex.idmpotent.netty.msg.enums.ResponseCode.ID_SUCCESS;

/**
 * 幂等状态执行校验
 */
@Slf4j
public class IdpCheckerStateHelper {


    public IdpCheckerStateHelper() {
    }

    private static class InstanceHolder {
        public static final IdpCheckerStateHelper instance = new IdpCheckerStateHelper();
    }

    public static IdpCheckerStateHelper getInstance() {
        return InstanceHolder.instance;
    }


    /**
     * @return
     * @Author xuliang
     * @Description // 检查该幂等请求是否处理成功
     * @Date 下午4:22 2020/4/27
     * @Param
     **/
    public boolean checkIdpKeySuccess(IdpKey idpKey, Channel channel, RpcCmd rpcCmd) {

        try {
            if (idpKey != null) {
                if (idpKey.getKeyState() == KeyState.SUCCESS) {
                    //幂等处理成功,直接返回调用端
                    channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ID_SUCCESS));
                    return true;
                }
            }
        } catch (Exception ex) {
            log.error("doRedisOrMysqlIdpKey id error : {}", ex);
        }
        return false;
    }


    /**
     * @return
     * @Author xuliang
     * @Description //检查状态是否已完成
     * @Date 下午4:22 2020/4/27
     * @Param
     **/
    public void checkIdpKeyStatus(IdpKey idpKey, Channel channel, RpcCmd rpcCmd) {
        try {
            if (idpKey != null) {
                if (idpKey.getKeyState() == KeyState.SUCCESS) {
                    //幂等处理成功,直接返回调用端
                    channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ID_SUCCESS));
                    return;
                } else if (idpKey.getKeyState() == KeyState.EXCEPTION) {
                    //该幂等 上次请求出现程序异常，此次不让处理 ，直接返回失败
                    channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ID_DUPLICATE));
                    return;
                } else if (idpKey.getKeyState() == KeyState.BUSSINESS_EXCEPTION) {
                    //该幂等 上次请求出现程序业务异常，过啦一会，数据修复 已经好啦，此时 允许客户端再次请求
                    channel.writeAndFlush(MessageCreator.okResponse(rpcCmd, STATE_OK));
                    return;
                } else if (idpKey.getKeyState() == KeyState.EXECUTING) {
                    //幂等拦截
                    channel.writeAndFlush(MessageCreator.bussinesError(rpcCmd, ID_DUPLICATE));
                    return;
                }
                return;
            }
        } catch (Exception ex) {
            log.error("doRedisOrMysqlIdpKey id error : {}", ex);
        }
    }


    /**
     * @return
     * @Author xuliang
     * @Description //检查状态是否已完成
     * @Date 下午4:22 2020/4/27
     * @Param
     **/
    public RpcCmd checkIdpKeyStatus(IdpKey idpKey,RpcCmd rpcCmd) {
        try {
            if (idpKey != null) {
                if (idpKey.getKeyState() == KeyState.SUCCESS) {
                    //幂等处理成功,直接返回调用端
                    return MessageCreator.bussinesError(rpcCmd, ID_SUCCESS);
                } else if (idpKey.getKeyState() == KeyState.EXCEPTION) {
                    //该幂等 上次请求出现程序异常，此次不让处理 ，直接返回失败
                    return MessageCreator.bussinesError(rpcCmd, ID_DUPLICATE);
                } else if (idpKey.getKeyState() == KeyState.BUSSINESS_EXCEPTION) {
                    //该幂等 上次请求出现程序业务异常，过啦一会，数据修复 已经好啦，此时 允许客户端再次请求
                    return MessageCreator.okResponse(rpcCmd, STATE_OK);
                } else if (idpKey.getKeyState() == KeyState.EXECUTING) {
                    //幂等拦截
                    return MessageCreator.bussinesError(rpcCmd, ID_DUPLICATE);
                }
                return null;
            }
        } catch (Exception ex) {
            log.error("doRedisOrMysqlIdpKey id error : {}", ex);
        }
        return null;
    }
}
