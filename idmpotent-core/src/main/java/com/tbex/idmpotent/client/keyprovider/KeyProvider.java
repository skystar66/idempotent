package com.tbex.idmpotent.client.keyprovider;


/**
 * @ClassName: KeyProvider
 * @Description: todo 全局id生成器
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:03
 * @Version: 1.0
 */

public interface KeyProvider {

    String get() throws KeyGenException;

}
