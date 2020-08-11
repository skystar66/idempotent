package com.tbex.idmpotent.client.config.mysql;

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
public class IdmpoentMysqlCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        //判断当前环境开关是否开启
        String isMysqlOnOff = context.getEnvironment().getProperty("idmpotent.isMysqlOnOff");
        //当且仅当值为on时，返回true
        if (!StringUtils.isEmpty(isMysqlOnOff) && isMysqlOnOff.equalsIgnoreCase("onn")) {
            log.info("mysql onn");
            return true;
        }
        return false;
    }

}
