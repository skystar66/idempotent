package com.tbex.idmpotent.server.token;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: TraceIdPoolProvider
 * @Description: todo java类作用描述
 * @Author: xuliang
 * @Date: 2020/4/20$ 上午11:35$
 * @Version: 1.0
 */
public class TokenProvider {


    private static ConcurrentHashMap<String, String> tokenConcurrent = new ConcurrentHashMap<>();

    public static void put(String token) {
        tokenConcurrent.put("token", token);
    }

    public static void remove() {
        tokenConcurrent.remove("token");
    }


    public static String get() {
        return tokenConcurrent.get("token");
    }


}
