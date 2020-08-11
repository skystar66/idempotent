package com.tbex.idmpotent.client.annotation;

import java.lang.annotation.*;

/**
 * 需要幂等性实现的注册该接口
 *
 * @author xuliang
 * @date:2020-04-17
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EnableIdp {


    /**
     * key的超时时间 默认300s
     * 设置超时时间的目的 在于 清理数据
     *
     * @return event
     * @see
     */
    long expireTime() default 300;


}
