package com.tbex.idmpotent.client.util;

import java.util.Arrays;

/**
 * @ClassName: StringJoinerUtil
 * @Description: todo string字符串拼接处理
 * @Author: xuliang
 * @Date: 2020/4/17 下午4:58
 * @Version: 1.0
 */
public class StringJoinerUtil {

    public static String join(String var1, String var2, String... others) {
        StringBuilder sb = new StringBuilder();
        sb.append(var1).append(var2);
        if (null != others) {
            Arrays.stream(others).forEach(sb::append);
        }
        return sb.toString();
    }

}
