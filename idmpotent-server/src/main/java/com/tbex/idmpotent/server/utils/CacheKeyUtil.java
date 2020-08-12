package com.tbex.idmpotent.server.utils;

/**
 * @ClassName: CacheKeyUtil
 * @Description: todo java类作用描述
 * @Author: xuliang
 * @Date: 2020/4/26$ 下午3:17$
 * @Version: 1.0
 */
public class CacheKeyUtil {


    public static final String prefix = "idp:";


    /**
     * 获取幂等信息 key
     */
    public static String getIdpKeyById(String bussinessType, String id) {
        return String.format((prefix + bussinessType + "%s"), id);
    }


}
