package com.tbex.idmpotent.client.keyprovider;

/**
 * @ClassName: TraceIdPoolProvider
 * @Description: todo java类作用描述
 * @Author: xuliang
 * @Date: 2020/4/20$ 上午11:35$
 * @Version: 1.0
 */
public class TraceIdPoolProvider {


    private static ThreadLocal<String> spanIdPool = new ThreadLocal<>();

    public static void put(String spanId) {
        spanIdPool.set(spanId);
    }

    public static void remove() {
        spanIdPool.remove();
    }


    public static String get(){
        return spanIdPool.get();
    }


}
