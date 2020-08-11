package com.tbex.idmpotent.client.http;

import com.tbex.idmpotent.client.IdpException;
import com.tbex.idmpotent.client.MethodSignatureWrapper;
import com.tbex.idmpotent.client.Msgs;
import com.tbex.idmpotent.client.idpchecker.IdpChecker;
import com.tbex.idmpotent.client.idpchecker.RejectException;
import com.tbex.idmpotent.client.keyprovider.DefaultKeyProvider;
import com.tbex.idmpotent.client.keyprovider.KeyGenException;
import com.tbex.idmpotent.client.keyprovider.KeyProvider;
import com.tbex.idmpotent.client.keyprovider.TraceIdPoolProvider;
import com.tbex.idmpotent.client.keystore.KeyStoreException;
import com.tbex.idmpotent.client.spring.DebugConfig;
import com.tbex.idmpotent.client.util.Constants;
import com.tbex.idmpotent.client.util.MySeqIdGen;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: HttpInterceptor
 * @Description: todo 业务 拦截器使用
 * @Author: xuliang
 * @Date: 2020/4/20 上午10:40
 * @Version: 1.0
 */
@Slf4j
@Accessors(chain = true)
@Data
public class BussinessInterceptor {


    private IdpChecker idpChecker;

    private KeyProvider keyProvider;

    //
//    private String getSpanId(ProceedingJoinPoint pjp) {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        return request.getHeader(Constants.HEADER_KEY_TRACE_ID);
//    }

    public Object intercept() throws Throwable {
        //默认生成的id
        String traceId = keyProvider.get();
        TraceIdPoolProvider.put(traceId);
        if (DebugConfig.getMode() == DebugConfig.Mode.DEBUG) {
            log.info(">> idempotent intercept bussiness traceId {}", traceId);
        }
        Object res;
        try {
            res = idpChecker.onCheckBussines(traceId);
        } catch (KeyStoreException cause) {
            throw new IdpException(Msgs.IDPKEY_STORE_EXCEPTION, cause);
        } catch (KeyGenException cause) {
            throw new IdpException(Msgs.IDPKEY_GEN_EXCEPTION, cause);
        } catch (RejectException cause) {
            throw new IdpException(Msgs.IDP_EXCEPTION, cause);
        } catch (InterruptedException cause) {
            throw new IdpException(Msgs.IDP_BLOCKINGCHECK_EXCEPTION, cause);
        } catch (Throwable cause) {
            idpChecker.onException(cause);
            throw cause;
        }
        if (DebugConfig.getMode() == DebugConfig.Mode.DEBUG) {
            log.info("<< idempotent intercept bussiness traceId {}", traceId);
        }
        TraceIdPoolProvider.remove();
        return res;
    }

    public boolean intercept(String traceId) throws Throwable {
        TraceIdPoolProvider.put(traceId);
        if (DebugConfig.getMode() == DebugConfig.Mode.DEBUG) {
            log.info(">> idempotent intercept bussiness traceId {}", traceId);
        }
        boolean res;
        try {
            res = idpChecker.onCheckBussines(traceId);
        } catch (KeyStoreException cause) {
            throw new IdpException(Msgs.IDPKEY_STORE_EXCEPTION, cause);
        } catch (KeyGenException cause) {
            throw new IdpException(Msgs.IDPKEY_GEN_EXCEPTION, cause);
        } catch (RejectException cause) {
            throw new IdpException(Msgs.IDP_EXCEPTION, cause);
        } catch (InterruptedException cause) {
            throw new IdpException(Msgs.IDP_BLOCKINGCHECK_EXCEPTION, cause);
        } catch (Throwable cause) {
            idpChecker.onException(cause);
            throw cause;
        }
        if (DebugConfig.getMode() == DebugConfig.Mode.DEBUG) {
            log.info("<< idempotent intercept bussiness traceId {}", traceId);
        }
        TraceIdPoolProvider.remove();
        return res;
    }

}