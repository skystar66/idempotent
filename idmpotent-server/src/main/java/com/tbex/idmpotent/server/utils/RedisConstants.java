package com.tbex.idmpotent.server.utils;

public class RedisConstants {


    //redis pub到之后调用的方法名
    public static final String NOTIFY = "notify";


    /**接口幂等校验过期时间：5分钟*/
    public static final int default_request_time_out=5*60;


    //设置幂等请求的幂等服务
    public static String idp_server_node_prefix = "idp_server_node:%s";




}
