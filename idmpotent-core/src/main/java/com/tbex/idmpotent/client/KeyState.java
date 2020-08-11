package com.tbex.idmpotent.client;

import lombok.AllArgsConstructor;

/**
 * @ClassName: KeyState
 * @Description: todo 执行状态
 * @Author: xuliang
 * @Date: 2020/4/17 下午4:50
 * @Version: 1.0
 */
@AllArgsConstructor
public enum KeyState {

    /**
     * 目标未执行（不应该出现）
     */
    NONE(1),
    /**
     * 目标得到执行并成功
     */
    SUCCESS(2),
    /**
     * 目标执行中（如果）
     */
    EXECUTING(4),
    /**
     * 目标执行失败且可再次执行（抛出Exception）
     */
    FAIL(8),
    /**
     * 目标执行失败且不可再次执行（抛出RuntimeException或Error）
     */
    RUNTIME_FAIL(16);

    int value;

    public int getValue() {
        return value;
    }

}
