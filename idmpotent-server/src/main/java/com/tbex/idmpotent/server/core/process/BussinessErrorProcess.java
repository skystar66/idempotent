package com.tbex.idmpotent.server.core.process;

import com.tbex.idmpotent.netty.msg.dto.RpcCmd;
import com.tbex.idmpotent.server.core.IdpChecker;
import com.tbex.idmpotent.server.utils.Constants;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

@Component(Constants.IDP_CHECK_PROCESS_BUSSINESS_ERROR)
public class BussinessErrorProcess implements IdpChecker {


    @Override
    public void process(Channel channel, RpcCmd rpcCmd) {





    }
}
