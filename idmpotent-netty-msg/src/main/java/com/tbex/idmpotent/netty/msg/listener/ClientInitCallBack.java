package com.tbex.idmpotent.netty.msg.listener;


/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/29
 *
 * @author xuliang
 */
public interface ClientInitCallBack {


    /**
     * 初始化连接成功回调
     *
     * @param remoteKey 远程调用唯一key
     */
    void connected(String remoteKey);

    /**
     * 连接失败回调
     *
     * @param remoteKey 远程调用唯一key
     */
    void connectFail(String remoteKey);


}
