package com.tbex.idmpotent.netty.msg.enums;

/**
 * @author zhulei
 * @date 2017/9/18 20:32
 *
 * 1 通用错误码 10000
 * 2 账户错误码 20000
 * 3 交易错误码 30000
 * 4 提现错误码 40000
 * 5 充值错误码 50000
 * 6 币种错误吗 60000
 * 7 产品错误码 70000
 */
public enum ResponseCode {
    /**
     * 错误码
     */
    INVALID_REQUEST("10000", "无效请求"),
    INVALID_MESSAGE("10001", "无效消息"),
    INVALID_EVENT("10002", "无效事件类型"),
    PARAM_NULL("10003", "消息为空"),
    INVALID_TOPIC("10004", "无效的主题"),
    NOT_LOGIN("10006", "未登录"),
    UPDATE_READ_FLAG_FAIL("10009", "标记已读失败"),
    DELETE_MESSAGE_FAIL("10010", "删除失败"),
    ID_DUPLICATE("10011", "幂等性拦截！重复请求"),
    ID_SUCCESS("10012", "幂等性拦截！处理成功"),
    ID_NOT_EXIST("10013", "幂等性拦截！id不存在"),

    ID_DUPLICATE_PERIOD("10014", "幂等性拦截！手速太快啦，休息一下，稍后再试！"),


    ;
    private final String code;
    private final String desc;

    ResponseCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return new StringBuffer("{\"code\":\"")
            .append(code)
            .append("\",\"desc\":\"")
            .append(desc)
            .append("\"}")
            .toString();
    }
}
