package com.tbex.idmpotent.client.idpchecker.blocking;


import com.tbex.idmpotent.client.KeyState;

/**
 * @ClassName: NoRejectIdpChecker
 * @Description: todo 不拒绝 1. 跟踪idpKey的状态直到EXECUTING状态结束 2. 判断若不是SUCCESS则继续执行
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:29
 * @Version: 1.0
 */
public class NoRejectIdpChecker extends BlockingIdpChecker {

    public NoRejectIdpChecker() {
        rejectStateSet.add(KeyState.SUCCESS);
        passStateSet.add(KeyState.FAIL);
        passStateSet.add(KeyState.RUNTIME_FAIL);
    }

}
