package com.tbex.idmpotent.client.idpchecker.blocking;


import com.tbex.idmpotent.client.KeyState;

/**
 * @ClassName: DefaultIdpChecker
 * @Description: todo EXECUTING阻塞，FAIL放行，其他拒绝
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:29
 * @Version: 1.0
 */
public class DefaultIdpChecker extends BlockingIdpChecker {

    public DefaultIdpChecker() {
        rejectStateSet.add(KeyState.SUCCESS);
        rejectStateSet.add(KeyState.RUNTIME_FAIL);
        passStateSet.add(KeyState.FAIL);
    }
}
