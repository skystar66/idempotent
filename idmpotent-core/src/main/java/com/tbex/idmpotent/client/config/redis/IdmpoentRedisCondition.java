package com.tbex.idmpotent.client.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * @ClassName: IdmpoentCondition
 * @Description: todo idmpotent 配置文件开关
 * @Author: xuliang
 * @Date: 2020/4/20 上午10:52
 * @Version: 1.0
 */
@Slf4j
public class IdmpoentRedisCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        //判断当前环境开关是否开启
        String isRedisOnOff = context.getEnvironment().getProperty("idmpotent.isRedisOnOff");

        if (!StringUtils.isEmpty(isRedisOnOff) && isRedisOnOff.equalsIgnoreCase("onn")) {
            log.info("redis onn");
            return true;
        }
        return false;
    }

}
