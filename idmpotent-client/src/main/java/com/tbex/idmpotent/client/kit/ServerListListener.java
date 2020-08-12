package com.tbex.idmpotent.client.kit;

import com.tbex.idmpotent.client.config.IdmpomtentClientConfig;
import com.tbex.idmpotent.client.utils.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Function:
 *
 * @author xuliang
 * Date: 2018/12/23 00:35
 * @since JDK 1.8
 */
public class ServerListListener implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ServerListListener.class);

    private ZKit zkUtil;

    private IdmpomtentClientConfig appConfiguration;


    public ServerListListener() {
        zkUtil = SpringBeanFactory.getBean(ZKit.class);
        appConfiguration = SpringBeanFactory.getBean(IdmpomtentClientConfig.class);
    }

    @Override
    public void run() {
        //注册监听服务
        zkUtil.subscribeEvent(appConfiguration.getZkRoot());

    }
}
