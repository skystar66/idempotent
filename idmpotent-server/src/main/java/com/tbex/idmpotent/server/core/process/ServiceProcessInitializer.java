package com.tbex.idmpotent.server.core.process;

import com.tbex.idmpotent.netty.msg.enums.EventType;
import com.tbex.idmpotent.server.core.IdpChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
/**
 * 初始化 幂等服务Process
 * @author xl
 * @version 2021年1月16日
 */
@Component
public class ServiceProcessInitializer {
    
	private static class InstanceHolder{
		private static ServiceProcessInitializer instance = new ServiceProcessInitializer();
	}
	
	public static ServiceProcessInitializer getInstance() {
		return InstanceHolder.instance;
	}

    private Map<EventType, IdpChecker> serviceProcessMap = new HashMap<>();
    
    @Autowired
    private BussinessErrorProcess  bussinessErrorProcess;
    @Autowired
    private ExceptionProcess exceptionProcess;
    @Autowired
    private ExcutingProcess excutingProcess;
    @Autowired
    private SuccessProcess successProcess;


    @Autowired
    private LoginProcess loginProcess;

    @Autowired
    private LoginOutProcess loginOutProcess;

    @Autowired
    private CreateIdProcess createIdProcess;


    /**
     * 获取Handler
     * @param eventType
     * @return
     */
    public IdpChecker getProcessService(EventType eventType) {
    	return serviceProcessMap.get(eventType);
    }

    /**
     * 初始化handler
     */
    @PostConstruct
    private void init() {

        /**业务执行中处理 process*/
    	getInstance().serviceProcessMap.put(EventType.EXECUTING, excutingProcess);
        /**业务处理成功 process*/
    	getInstance().serviceProcessMap.put(EventType.BUSSINESS_SUCCESS, successProcess);
        /**业务程序性异常处理 process*/
    	getInstance().serviceProcessMap.put(EventType.BUSSINESS_RUNTIMEEXCEPTION_FAIL, bussinessErrorProcess);
        /**业务非程序性异常处理 process*/
    	getInstance().serviceProcessMap.put(EventType.BUSSINESS_FAIL, exceptionProcess);

        /**登录处理 process*/
        getInstance().serviceProcessMap.put(EventType.LOGIN, loginProcess);
        /**登出处理 process*/
        getInstance().serviceProcessMap.put(EventType.LOGOUT, loginOutProcess);
        /**创建幂等id处理 process*/
        getInstance().serviceProcessMap.put(EventType.CREATE_ID, createIdProcess);


    }

}