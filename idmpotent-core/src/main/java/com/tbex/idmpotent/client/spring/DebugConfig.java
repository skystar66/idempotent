package com.tbex.idmpotent.client.spring;


/**
 * @ClassName: DebugConfig
 * @Description: todo debug 日志开关配置
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:15
 * @Version: 1.0
 */
public class DebugConfig {

    public enum Mode {

        /**
         * 正式的
         */
        PROD,
        /**
         * 测试模式，打印日志
         */
        DEBUG

    }

    private static Mode mode = Mode.PROD;

    public static void toggleDebugMode() {
        mode = Mode.DEBUG;
    }

    public static Mode getMode() {
        return mode;
    }

}
