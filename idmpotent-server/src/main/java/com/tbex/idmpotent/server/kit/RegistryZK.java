//package com.tbex.idmpotent.server.kit;
//
//import com.tbex.idmpotent.server.config.IdmpotentServerConfig;
//import com.tbex.idmpotent.server.utils.SpringUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * Function:
// *
// * @author xuliang
// * Date: 2018/8/24 01:37
// * @since JDK 1.8
// */
//public class RegistryZK implements Runnable {
//
//    private static Logger logger = LoggerFactory.getLogger(RegistryZK.class);
//
//    private ZKit zKit;
//
//    private IdmpotentServerConfig appConfiguration;
//
//    private String ip;
//    private int cimServerPort;
//    private int httpPort;
//
//    public RegistryZK(String ip, int cimServerPort, int httpPort) {
//        this.ip = ip;
//        this.cimServerPort = cimServerPort;
//        this.httpPort = httpPort;
//        zKit = SpringUtil.getBean(ZKit.class);
//        appConfiguration = SpringUtil.getBean(IdmpotentServerConfig.class);
//    }
//
//    @Override
//    public void run() {
//
//        //创建父节点
//        zKit.createRootNode();
//
//        //是否要将自己注册到 ZK
//        if (appConfiguration.isZkSwitch()) {
//            String path = appConfiguration.getZkRoot() + "/" + appConfiguration.getNodeId() + "-" + ip + ":" + cimServerPort+":"+"1";
//            zKit.createNode(path);
//            logger.info("Registry zookeeper success, msg=[{}]", path);
//        }
//
//
//    }
//}