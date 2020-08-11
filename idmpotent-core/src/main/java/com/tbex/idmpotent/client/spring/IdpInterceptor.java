package com.tbex.idmpotent.client.spring;

import com.tbex.idmpotent.client.IdpException;
import com.tbex.idmpotent.client.MethodSignatureWrapper;
import com.tbex.idmpotent.client.Msgs;
import com.tbex.idmpotent.client.idpchecker.IdpChecker;
import com.tbex.idmpotent.client.idpchecker.RejectException;
import com.tbex.idmpotent.client.keyprovider.KeyGenException;
import com.tbex.idmpotent.client.keystore.KeyStoreException;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @ClassName: IdpInterceptor
 * @Description: todo 用于定义幂等切面
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:38
 * @Version: 1.0
 */
@Slf4j
@Accessors(chain = true)
@Data
public class IdpInterceptor {

    private IdpChecker idpChecker;

    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignatureWrapper wrapper = new MethodSignatureWrapper(pjp);
        String methodFullName = wrapper.getMethodSignature();
        if (DebugConfig.getMode() == DebugConfig.Mode.DEBUG) {
        log.info(">> idempotent intercept method {}", methodFullName);
        }
        Object res;
        try {
            res = idpChecker.onCheck(wrapper);
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
        log.info("<< idempotent intercept method {}", methodFullName);
        }
        return res;
    }

}
