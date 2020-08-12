package com.tbex.idmpotent.server.core.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum KeyState {


    /**
     * 成功
     */
    SUCCESS(1),
    /**
     * 执行中
     */
    EXECUTING(2),

    /**
     * 程序异常，不允许重试
     */
    EXCEPTION(3),
    /**
     * 业务执行失败，允许重试
     */
    BUSSINESS_EXCEPTION(4);

    int value;

    public int getValue() {
        return value;
    }

}
