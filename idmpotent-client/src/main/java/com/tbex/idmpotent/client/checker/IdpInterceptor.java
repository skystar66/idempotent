package com.tbex.idmpotent.client.checker;

import com.tbex.idmpotent.client.client.MessageCreator;
import com.tbex.idmpotent.client.client.ReqRpcClient;
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
import java.util.concurrent.Executor;

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
    ReqRpcClient reqRpcClient;

    @Autowired
    Executor taskExecutor;

    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
        //获取header中的 全局唯一id
        String traceId = getHeaderTraceId();
        //获取接口路径
        String uri = getUri();
        //获取参数
        Object[] args = pjp.getArgs();
        log.info(">> idempotent intercept traceId {} | uri:{} | args:{}", traceId,uri,args);
        Object res;
        try {
            //获取header中的 幂等服务id
            MessageDto messageDtoValidate = reqRpcClient.request(MessageCreator.serverExcuting(traceId,
                    args,uri));
            log.info("excuting id response msg : {}", messageDtoValidate);
            if (messageDtoValidate.getState() == MessageConstants.STATE_OK) {
                //执行业务
                res = pjp.proceed();
            } else {
                //幂等拦截
                throw new RejectException(Msgs.IDP_EXCEPTION, messageDtoValidate.loadBean(String.class));
            }
            if (res != null) {
                //执行成功后 通知 幂等服务端处理，此处可使用线程池处理，防止幂等端掉不通情况
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            reqRpcClient.request(MessageCreator.serverSuccess(traceId));
                            log.info("业务执行成功，traceId：{}", traceId);
                        } catch (Exception ex) {
                            log.error("调用幂等服务失败，通知幂等服务该请求已成功结束！ error:{}", ex);
                        }
                    }
                });
            }
        } catch (RpcException cause) {
            throw new IdpException(Msgs.IDPKEY_GEN_EXCEPTION, cause);
        } catch (RejectException cause) {
            throw new IdpException(Msgs.IDP_EXCEPTION, cause);
        } catch (InterruptedException cause) {
            throw new IdpException(Msgs.IDP_BLOCKINGCHECK_EXCEPTION, cause);
        } catch (Throwable cause) {
            //全局异常处理,业务执行异常
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
     * @Description //TODO 获取http header 头中的全局唯一id
     * @Date 下午4:27 2020/4/22
     * @Param
     **/
    private String getUri() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getRequestURI()+":"+request.getServletPath();
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
                MessageDto messageDtoRunExe = reqRpcClient.request(MessageCreator.serverBussinessRuntimeException(traceId));
                log.info("runtime exception response msg:{}", messageDtoRunExe);
            } else if (Exception.class.isAssignableFrom(cause.getClass())) {
                MessageDto messageDtoRunExe = reqRpcClient.request(MessageCreator.serverBussinessException(traceId));
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
