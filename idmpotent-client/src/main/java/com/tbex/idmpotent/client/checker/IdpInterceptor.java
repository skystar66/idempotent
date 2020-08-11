package com.tbex.idmpotent.client.checker;

import com.tbex.idmpotent.client.client.MessageCreator;
import com.tbex.idmpotent.client.client.RpcClient;
import com.tbex.idmpotent.client.exception.IdpException;
import com.tbex.idmpotent.client.exception.Msgs;
import com.tbex.idmpotent.client.exception.RejectException;
import com.tbex.idmpotent.client.keyprovider.RpcException;
import com.tbex.idmpotent.client.utils.Constants;
import com.tbex.idmpotent.netty.msg.MessageConstants;
import com.tbex.idmpotent.netty.msg.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: IdpInterceptor
 * @Description: todo 用于定义幂等切面
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:38
 * @Version: 1.0
 */
@Slf4j
@Component
public class IdpInterceptor {


    @Autowired
    RpcClient rpcClient;

    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
        //获取header中的 全局唯一id
        String traceId = getHeaderTraceId();
        log.info(">> idempotent intercept traceId {}", traceId);
        Object res;
        try {
            //获取header中的token
            log.info(">> idempotent enableidpAspectConfig traceId {}", traceId);
            MessageDto messageDtoValidate = rpcClient.request(MessageCreator.serverValidate(traceId));
            log.info("validate id response msg : {}", messageDtoValidate);
            boolean resValidate = messageDtoValidate.loadBean(Boolean.class);
            if (messageDtoValidate.getState() == MessageConstants.STATE_OK) {
                //执行业务
                res = pjp.proceed();
            } else {
                //幂等拦截
                throw new RejectException(Msgs.IDP_EXCEPTION, messageDtoValidate.loadBean(String.class));
            }
            if (res != null) {
                //执行成功后 通知 幂等服务端处理
                MessageDto messageDtoBussinessSuccess = rpcClient.request(MessageCreator.serverBussinessSuccess(traceId));
                log.info("业务执行成功，traceId：{}", traceId);
            }
            log.info("<< idempotent enableidpAspectConfig traceId {}", traceId);

        } catch (RpcException cause) {
            throw new IdpException(Msgs.IDPKEY_GEN_EXCEPTION, cause);
        } catch (RejectException cause) {
            throw new IdpException(Msgs.IDP_EXCEPTION, cause);
        } catch (InterruptedException cause) {
            throw new IdpException(Msgs.IDP_BLOCKINGCHECK_EXCEPTION, cause);
        } catch (Throwable cause) {
            //全局异常处理
            onException(cause, traceId);
            throw cause;
        }
        log.info("<< idempotent intercept traceId {}", traceId);
        return res;
    }

    /**
     * @return
     * @Author xuliang
     * @Description //TODO 获取http header 头中的全局唯一id
     * @Date 下午4:27 2020/4/22
     * @Param
     **/
    private String getHeaderTraceId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getHeader(Constants.HEADER_KEY_TRACE_ID);
    }


    /**
     * @return
     * @Author xuliang
     * @Description //TODO 处理全局异常
     * @Date 下午4:23 2020/4/22
     * @Param cause
     **/
    public void onException(Throwable cause, String traceId) throws RpcException {
        try {
            if (RuntimeException.class.isAssignableFrom(cause.getClass())) {
                MessageDto messageDtoRunExe = rpcClient.request(MessageCreator.serverBussinessRuntimeException(traceId));
                log.info("runtime exception response msg:{}", messageDtoRunExe);
            } else if (Exception.class.isAssignableFrom(cause.getClass())) {
                MessageDto messageDtoRunExe = rpcClient.request(MessageCreator.serverBussinessException(traceId));
                log.info("exception response msg:{}", messageDtoRunExe);
            } else {
                // 不支持的异常处理
                throw new IllegalStateException(cause);
            }
        } catch (Exception ex) {
            throw new RpcException(Msgs.RPC_EXCEPTION, ex);
        }

    }

}
