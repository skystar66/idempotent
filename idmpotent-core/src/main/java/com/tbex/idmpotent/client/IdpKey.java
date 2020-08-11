package com.tbex.idmpotent.client;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @ClassName: IdpKey
 * @Description: todo 幂等性服务的消息体
 * @Author: xuliang
 * @Date: 2020/4/17 下午4:49
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class IdpKey {


    /**
     * 唯一标识一条 key
     */
    private String id;

    /**
     * key 的状态
     */
    private KeyState keyState;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * Response 序列化后的内容
     */
    private byte[] content;

}
