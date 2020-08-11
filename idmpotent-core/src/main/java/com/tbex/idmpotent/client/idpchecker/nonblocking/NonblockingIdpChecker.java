package com.tbex.idmpotent.client.idpchecker.nonblocking;


import com.tbex.idmpotent.client.*;
import com.tbex.idmpotent.client.idpchecker.BaseIdpChecker;
import com.tbex.idmpotent.client.idpchecker.RejectException;
import com.tbex.idmpotent.client.keyprovider.TraceIdPoolProvider;
import com.tbex.idmpotent.client.util.KeyGenUtil;
import com.tbex.idmpotent.client.util.StringJoinerUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;


/**
 * @ClassName: NonblockingIdpChecker
 * @Description: todo 非阻塞，即使别的线程正在执行调用（EXECUTING），也直接拦截
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:31
 * @Version: 1.0
 */
@Slf4j
public abstract class NonblockingIdpChecker extends BaseIdpChecker {

    Set<KeyState> rejectStateSet;

    public NonblockingIdpChecker() {
        rejectStateSet = new HashSet<>();
        rejectStateSet.add(KeyState.SUCCESS);
    }

    @Override
    public Object onCheck(MethodSignatureWrapper target) throws Throwable {
        String id = TraceIdPoolProvider.get();
        IdpKey newK = KeyGenUtil.newExecuting(id);
        Pair pair = keyStore.putIfAbsent(newK);
        IdpKey oldK = pair.getIdpKey();
        if (rejectStateSet.contains(oldK.getKeyState()) && pair.getCount() ==0) {
            throw new RejectException(StringJoinerUtil
                    .join(Msgs.IDP_REJECT_EXCEPTION, ":", oldK.toString()));
        }
        // 调用目标方法
        Object res = target.invoke();
        // 若调用成功没有抛出异常，将调用结果保存到 KeyStore
        IdpKey idpKey = KeyGenUtil.newSuccess(id, res);
        keyStore.replace(idpKey);
        return res;
    }

    @Override
    public boolean onCheckBussines(String traceId) throws Throwable {
//        String id = keyProvider.get();
        IdpKey newK = KeyGenUtil.newExecuting(traceId);
        Pair pair = keyStore.putIfAbsent(newK);
        IdpKey oldK = pair.getIdpKey();
        if (rejectStateSet.contains(oldK.getKeyState())) {
//            throw new RejectException(StringJoinerUtil
//                    .join(Msgs.IDP_REJECT_EXCEPTION, ":", oldK.toString()));
            log.info("reject exception :{}", (Msgs.IDP_REJECT_EXCEPTION + ":" + oldK.toString()));
            return false;
        }

        // 若调用成功没有抛出异常，将调用结果保存到 KeyStore
        IdpKey idpKey = KeyGenUtil.newSuccess(traceId, "success");
        keyStore.replace(idpKey);
        return true;
    }
}
