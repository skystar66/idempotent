package com.tbex.idmpotent.client.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

/**
 * @ClassName: JsonUtil
 * @Description: todo json 处理
 * @Author: xuliang
 * @Date: 2020/4/17 下午5:00
 * @Version: 1.0
 */
public class JsonUtil {

    public static final ObjectMapper OM = new ObjectMapper();

    static {
        OM.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        OM.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        OM.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        OM.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        OM.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
        OM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OM.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    public static String write(Object obj) throws JsonProcessingException {
        return OM.writeValueAsString(obj);
    }

    public static <T> T read(String json, Class<T> type) throws IOException {
        return OM.readValue(json, type);
    }

}
