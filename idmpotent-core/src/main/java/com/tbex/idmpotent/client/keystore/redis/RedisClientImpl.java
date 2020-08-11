package com.tbex.idmpotent.client.keystore.redis;

import com.google.common.collect.Lists;
import com.tbex.idmpotent.client.util.JsonUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * @ClassName: RedisClientImpl
 * @Description: todo redis实现类
 * @Author: xuliang
 * @Date: 2020/4/20 上午10:47
 * @Version: 1.0
 */

@Component
@Data
@Slf4j
@Accessors(chain = true)
public class RedisClientImpl implements RedisClient {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void set(String key, String value) {
        ValueOperations<String, String> vo = redisTemplate.opsForValue();
        vo.set(key, value);
    }

    @Override
    public void setex(String key, int seconds, String value) {
        ValueOperations<String, String> vo = redisTemplate.opsForValue();
        vo.set(key, value, seconds, TimeUnit.SECONDS);
    }

    @Override
    public void setexnx(String key, int seconds, String value) {
        ValueOperations<String, String> vo = redisTemplate.opsForValue();
        vo.setIfAbsent(key, value);
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    @Override
    public void hset(String hkey, String key, String value) {
        HashOperations<String, String, String> ho = redisTemplate.opsForHash();
        ho.put(hkey, key, value);
    }

    @Override
    public long incr(String key) {
        ValueOperations<String, String> vo = redisTemplate.opsForValue();
        return Optional.ofNullable(vo.increment(key,1)).orElse(0L);
    }

    @Override
    public long decr(String key) {
        ValueOperations<String, String> vo = redisTemplate.opsForValue();
        return Optional.ofNullable(vo.increment(key,-1)).orElse(0L);
    }

    @Override
    public boolean del(String key) {
        return Optional.ofNullable(redisTemplate.delete(key)).orElse(false);
    }

    @Override
    public long hdel(String hkey, String key) {
        HashOperations<String, Object, Object> ho = redisTemplate.opsForHash();
        return ho.delete(hkey, key);
    }

    @Override
    public String get(String key) {
        ValueOperations<String, String> vo = redisTemplate.opsForValue();
        return vo.get(key);
    }

    @Override
    public String hget(String hkey, String key) {
        HashOperations<String, String, String> ho = redisTemplate.opsForHash();
        return ho.get(hkey, key);
    }

    @Override
    public Map<String, String> hgetAll(String hkey) {
        HashOperations<String, String, String> ho = redisTemplate.opsForHash();
        return ho.entries(hkey);
    }

    @Override
    public long expire(String key, int second) {
        throw new RuntimeException("客户端不支持 expire");
    }

    @Override
    public long ttl(String key) {
        throw new RuntimeException("客户端不支持 ttl");
    }

    @Override
    public void multi() {
        // 每次multi / exec都会新建连接而不是使用本来的连接，暂时RedisTemplate不支持多个命令共享一个Connection
        // 可以使用execute(RedisCallback)来实现：https://jimgreat.iteye.com/blog/1596058
        throw new RuntimeException("客户端不支持 multi");
    }

    @Override
    public List<Object> exec() {
        throw new RuntimeException("客户端不支持 exec");
    }

    @Override
    public String loadScript(String luaScript) {
        return redisTemplate.getConnectionFactory().getConnection()
                .scriptingCommands()
                .scriptLoad(luaScript.getBytes());
    }

    @Override
    public String executeLua(String sha, List<String> keys, List<String> params) {
        List<String> keysAndArgs = Lists.newArrayList();
        keysAndArgs.addAll(keys);
        keysAndArgs.addAll(params);
        try {

            byte[] resBytes = redisTemplate.getConnectionFactory().getConnection()
                    .scriptingCommands()
                    .evalSha(sha, ReturnType.VALUE, 1,
                            convertToBytes(params.toArray(new String[params.size()]), keys));

            if (null != resBytes) {
                return new String(resBytes);
            }
            return null;
        } catch (
                Exception e) {
            throw new RuntimeException("执行脚本失败", e);
        }

    }

    @Override
    public byte[] executeLuas(String sha, List<String> keys, List<String> params) {
        try {
            return redisTemplate.getConnectionFactory().getConnection()
                    .scriptingCommands()
                    .evalSha(sha, ReturnType.VALUE, 1,
                            convertToBytes(params.toArray(new String[0]), keys));
        } catch (Exception e) {
            throw new RuntimeException("执行脚本失败", e);
        }
    }


    private static byte[][] convertToBytes(String[] strings, List<String> keys) {
        byte[][] data = null;
        try {
            data = new byte[strings.length + 1][];
            data[0] = JsonUtil.write(keys).getBytes();
            for (int i = 0; i < strings.length; i++) {
                String string = strings[i];
                data[i + 1] = string.getBytes(Charset.defaultCharset()); // you can chose charset
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return data;
    }

}
