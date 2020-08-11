package com.tbex.idmpotent.client.idpchecker.nonblocking;


import com.tbex.idmpotent.client.KeyState;

/**
 * @ClassName: FastPassIdpChecker
 * @Description: todo EXECUTING状态也会继续执行
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:30
 * @Version: 1.0
 */
public class FastPassIdpChecker extends NonblockingIdpChecker {

    public FastPassIdpChecker() {
        rejectStateSet.add(KeyState.SUCCESS);
        rejectStateSet.add(KeyState.EXECUTING);
    }
}
