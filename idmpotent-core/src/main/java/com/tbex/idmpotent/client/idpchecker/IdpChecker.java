package com.tbex.idmpotent.client.idpchecker;

import com.tbex.idmpotent.client.MethodSignatureWrapper;
import com.tbex.idmpotent.client.keyprovider.KeyGenException;
import com.tbex.idmpotent.client.keystore.KeyStoreException;


/**
 * @ClassName: IdpChecker
 * @Description: todo 幂等性检查器
 * @Author: xuliang
 * @Date: 2020/4/17 下午4:53
 * @Version: 1.0
 */
public interface IdpChecker {


    /**
     * @return Object
     * @Author xuliang
     * @Description //TODO 幂等性检查器
     * @Date 下午4:56 2020/4/17
     * @Param wrapper
     **/
    Object onCheck(MethodSignatureWrapper wrapper) throws Throwable;

    /**
     * @return Object
     * @Author xuliang
     * @Description //TODO bussines幂等性检查器
     * @Date 下午4:56 2020/4/17
     * @Param wrapper
     **/
    boolean onCheckBussines(String traceId) throws Throwable;

    void onException(Throwable cause) throws KeyStoreException, KeyGenException;


}
