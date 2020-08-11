package com.tbex.idmpotent.client.idpchecker;

import com.tbex.idmpotent.client.IdpKey;
import com.tbex.idmpotent.client.KeyState;
import com.tbex.idmpotent.client.MethodSignatureWrapper;
import com.tbex.idmpotent.client.keyprovider.KeyGenException;
import com.tbex.idmpotent.client.keyprovider.KeyProvider;
import com.tbex.idmpotent.client.keyprovider.TraceIdPoolProvider;
import com.tbex.idmpotent.client.keystore.KeyStore;
import com.tbex.idmpotent.client.keystore.KeyStoreException;
import com.tbex.idmpotent.client.util.KeyGenUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import static com.tbex.idmpotent.client.Msgs.IDPKEY_COMPRESS_EXCEPTION;

/**
 * @ClassName: BaseIdpChecker
 * @Description: todo java类作用描述
 * @Author: xuliang
 * @Date: 2020/4/17$ 下午4:52$
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public abstract class BaseIdpChecker implements IdpChecker {


    protected KeyProvider keyProvider;

    protected KeyStore keyStore;

    @Override
    public void onException(Throwable cause) throws KeyStoreException, KeyGenException {
        try {
            IdpKey idpKey = new IdpKey()
                    .setId(TraceIdPoolProvider.get());
            if (RuntimeException.class.isAssignableFrom(cause.getClass())) {
                idpKey.setKeyState(KeyState.RUNTIME_FAIL);
                idpKey.setContent(KeyGenUtil.serialize("程序RuntimeException异常"));
                keyStore.replace(idpKey);
            } else if (Exception.class.isAssignableFrom(cause.getClass())) {
                idpKey.setKeyState(KeyState.FAIL);
                idpKey.setContent(KeyGenUtil.serialize("程序Exception异常"));
                System.out.println("出现异常 更新 fail");
                keyStore.replace(idpKey);
            } else {
                // 不支持的异常处理
                throw new IllegalStateException(cause);
            }
        } catch (Exception ex) {
            throw new KeyGenException(IDPKEY_COMPRESS_EXCEPTION, ex);
        }

    }
}
