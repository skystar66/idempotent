package com.tbex.idmpotent.client;

/**
 * @ClassName: Msgs
 * @Description: todo 幂等性消息提示
 * @Author: xuliang
 * @Date: 2020/4/17 下午4:50
 * @Version: 1.0
 */
public class Msgs {


    public static final String IDPKEY_STORE_EXCEPTION = "[幂等性组件错误]：IdpKey持久化失败";

    public static final String IDPKEY_KEYSTORE_SAVE_EXCEPTION = "[幂等性组件错误]：IdpKey保存失败";

    public static final String IDPKEY_KEYSTORE_QUERY_EXCEPTION = "[幂等性组件错误]：IdpKey查询失败";

    public static final String IDPKEY_KEYSTORE_DELETE_EXCEPTION = "[幂等性组件错误]：IdpKey删除失败";

    public static final String IDPKEY_KEYSTORE_CLEANUP_EXCEPTION = "[幂等性组件错误]：IdpKey清理出错";

    public static final String IDPKEY_KEYSTORE_SERIALIZING_EXCEPTION = "[幂等性组件错误]：IdpKey序列化出错";

    public static final String IDPKEY_GEN_EXCEPTION = "[幂等性组件错误]：IdpKey生成失败";

    public static final String IDP_BLOCKINGCHECK_EXCEPTION = "[幂等性组件错误]：阻塞检查时被意外中断";

    public static final String IDPKEY_COMPRESS_EXCEPTION = "[幂等性组件错误]：IdpKey压缩出错";

    public static final String IDP_EXCEPTION = "[幂等性拦截]";

    public static final String IDP_REJECT_EXCEPTION = "[幂等性拦截]：IdpKey被重复调用，且未成功结束";

    public static final String IDP_MULTI_INVOKE = "[幂等性拦截]：IdpKey被重复调用，直接返回旧值";

    public static final String IDP_RETRYLIMIT_EXCEPTION = "[幂等性拦截]：阻塞重试次数达阈值";
}
