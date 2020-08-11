package com.tbex.idmpotent.client.keyprovider;


import com.tbex.idmpotent.client.util.MySeqIdGen;

/**
 * @ClassName: DefaultKeyProvider
 * @Description: todo 生成idpKey的id,Default在每次接收到请求都生成一个新的id，所以幂等性检查不会生效
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:32
 * @Version: 1.0
 */
public class DefaultKeyProvider implements KeyProvider {


//
    @Override
    public String get() {
        return MySeqIdGen.getUsdtId();
    }


}
