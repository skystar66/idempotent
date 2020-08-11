package com.tbex.idmpotent.client.idpchecker.blocking;

import com.tbex.idmpotent.client.*;
import com.tbex.idmpotent.client.idpchecker.BaseIdpChecker;
import com.tbex.idmpotent.client.idpchecker.RejectException;
import com.tbex.idmpotent.client.keyprovider.KeyProvider;
import com.tbex.idmpotent.client.keyprovider.TraceIdPoolProvider;
import com.tbex.idmpotent.client.keystore.KeyStore;
import com.tbex.idmpotent.client.util.KeyGenUtil;
import com.tbex.idmpotent.client.util.StringJoinerUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName: BlockingIdpChecker
 * @Description: todo  当上一个请求在进行中EXECUTING时，其它请求进行重试，并保证一次成功，当上一个请求执行失败 FAIL时，进行调用重试，并保证成功一次，
 * @Author: xuliang
 * @Date: 2020/4/17$ 下午5:09$
 * @Version: 1.0
 */
@Slf4j
public class BlockingIdpChecker extends BaseIdpChecker {


    /*拒绝策略*/
    Set<KeyState> rejectStateSet;

    //允许策略
    Set<KeyState> passStateSet;


    public BlockingIdpChecker() {
        rejectStateSet = new HashSet<>();
        rejectStateSet.add(KeyState.SUCCESS);
        //此处passStateSet 状态集合为FAIL 允许 重试
        passStateSet = new HashSet<>();
    }

    //休眠时间
    private static final int INIT_RETRY_INTERVAL_TIME = 50;

    //重试次数
    private static final int MAX_RETRY_COUNT = 3;


    @Override
    public Object onCheck(MethodSignatureWrapper target) throws Throwable {

        //获取全局唯一id
        String id = TraceIdPoolProvider.get();
        //构造执行幂等服务数据结构 状态为 执行中 EXECUTING
        IdpKey newK = KeyGenUtil.newExecuting(id);
        IdpKey oldK = null;
        int intervalTime = INIT_RETRY_INTERVAL_TIME;
        int retryCount = 0;
        while (retryCount++ < MAX_RETRY_COUNT) {
            //执行lua  passStateSet：FAIL
            Pair pair = keyStore.putIfAbsentOrInStates(newK, passStateSet);
            log.info("putIfAbsentOrInStates返回: " + pair);
            oldK = pair.getIdpKey();
            //成功数量 有且只能唯一
            Integer updatedCount = pair.getCount();
            if (KeyState.EXECUTING == oldK.getKeyState() && updatedCount == 1) {
                //状态为执行中，并且只有当前线程执行成功，退出循环,保存结果success
                log.info("当前线程执行成功 ========= ");
                break;
            } else if (passStateSet.contains(oldK.getKeyState())) {
                // 存在其他线程执行失败，且这时保存FAIL成功，说明命中了可以继续执行的状态,继续执行
                log.info("存在其他线程执行失败，且这时保存成功，说明命中了可以继续执行的状态===========");



                continue;
            } else if (rejectStateSet.contains(oldK.getKeyState())) {
                // 其他线程执行成功，且这时保存失败，将旧结果直接返回
                log.info(Msgs.IDP_MULTI_INVOKE, oldK);
                log.info("其他线程执行成功，且这时保存失败，将旧结果直接返回 content:{} type : {}===========", oldK.getContent(), target.getReturnType());
                return KeyGenUtil.deserialize(oldK.getContent(), target.getReturnType());
            }

            // 还有一种情况，其他线程执行中，需要阻塞当前线程、重试固定次数，此时KeyState.EXECUTING == oldK.getKeyState() && updatedCount == 0
            Thread.sleep(intervalTime);
            intervalTime *= 2;
            log.info(StringJoinerUtil.join(">> 重试 idpKey=", newK.toString(), " count=",
                    Integer.toString(retryCount)));
        }


        if (retryCount >= MAX_RETRY_COUNT) {
            log.info("超过阈值");
            // 重试间隔时间超过阈值
            throw new RejectException(Msgs.IDP_RETRYLIMIT_EXCEPTION, oldK);
        }
        // 调用目标方法
        Object res = target.invoke();
        // 若调用成功没有抛出异常，将调用结果保存到 KeyStore
        IdpKey idpKey = KeyGenUtil.newSuccess(id, res);
        log.info("replace预存最终结果：" + idpKey);
        keyStore.replace(idpKey);
        return res;
    }

    @Override
    public boolean onCheckBussines(String id) throws Throwable {

        //构造执行幂等服务数据结构 状态为 执行中 EXECUTING
        IdpKey newK = KeyGenUtil.newExecuting(id);
        IdpKey oldK = null;
        int intervalTime = INIT_RETRY_INTERVAL_TIME;
        int retryCount = 0;
        while (retryCount++ < MAX_RETRY_COUNT) {
            //执行lua  passStateSet：FAIL
            Pair pair = keyStore.putIfAbsentOrInStates(newK, passStateSet);
            log.info("putIfAbsentOrInStates返回: " + pair);
            oldK = pair.getIdpKey();
            //成功数量 有且只能唯一
            Integer updatedCount = pair.getCount();
            if (KeyState.EXECUTING == oldK.getKeyState() && updatedCount == 1) {
                //状态为执行中，并且只有当前线程执行成功，退出循环,保存结果success
                log.info("当前线程执行成功 ========= ");
                break;
            } else if (passStateSet.contains(oldK.getKeyState())) {
                // 存在其他线程执行失败，且这时保存FAIL成功，说明命中了可以继续执行的状态,继续执行
                log.info("存在其他线程执行失败，且这时保存成功，说明命中了可以继续执行的状态===========");
                break;
            } else if (rejectStateSet.contains(oldK.getKeyState())) {
                // 其他线程执行成功，且这时保存失败，将旧结果直接返回
                log.info(Msgs.IDP_MULTI_INVOKE, oldK);
                log.info("其他线程执行成功，且这时保存失败，将旧结果直接返回 content:{} type : {}===========", oldK.getContent(), String.class);
                return false;
            }

            // 还有一种情况，其他线程执行中，需要阻塞当前线程、重试固定次数，此时KeyState.EXECUTING == oldK.getKeyState() && updatedCount == 0
            Thread.sleep(intervalTime);
            intervalTime *= 2;
            log.info(StringJoinerUtil.join(">> 重试 idpKey=", newK.toString(), " count=",
                    Integer.toString(retryCount)));
        }


        if (retryCount >= MAX_RETRY_COUNT) {
            log.info("超过重试阀值：{}", Msgs.IDP_RETRYLIMIT_EXCEPTION, oldK);
            // 重试间隔时间超过阈值
            return false;
        }
        // 若调用成功没有抛出异常，将调用结果保存到 KeyStore
        IdpKey idpKey = KeyGenUtil.newSuccess(id, "success");
        log.info("replace预存最终结果：" + idpKey);
        keyStore.replace(idpKey);
        return true;
    }
}
