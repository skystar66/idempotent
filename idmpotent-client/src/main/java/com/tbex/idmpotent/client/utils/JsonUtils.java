package com.tbex.idmpotent.client.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT;

/**
 * @author boyce
 * @date 2017/10/11
 */
@Slf4j
public class JsonUtils {

    /**
     * 不输出value为空的结点
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .setSerializationInclusion(Include.NON_NULL)
        .enable(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        .configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        .configure(Feature.ALLOW_SINGLE_QUOTES, true)
        // BigDecimal要去尾零和写成string
//        .registerModule(new SimpleModule().addSerializer(BigDecimal.class, new JsonSerializer<BigDecimal>() {
//            @Override
//            public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers)
//                throws IOException {
//                gen.writeString(value.stripTrailingZeros()
//                    .toPlainString());
//            }
//        }))
            ;

    public static String toJSONString(Object o) {
        try {
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (Throwable e) {
            log.error("convert json error ", e.getMessage());
        }
        return "";
    }

    public static <T> T parseString(String jsonString, Class<T> tClass) {
        try {
            return OBJECT_MAPPER.readValue(jsonString, tClass);
        } catch (Throwable e) {
            log.error("parse json error ", e.getMessage());
        }
        return null;
    }

    public static <T> List<T> parseStringToList(String listJson, Class<T> tClass) {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory()
            .constructParametricType(ArrayList.class, tClass);
        try {
            return OBJECT_MAPPER.readValue(listJson, javaType);
        } catch (Throwable e) {
            log.error(" parse list error ", e.getMessage());
        }
        return new ArrayList<>();
    }

    public static <T> List<T> parseStringToListObject(String json, Class<T> tClass, Class childClass) {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory()
            .constructParametricType(tClass, childClass);
        JavaType parentType = OBJECT_MAPPER.getTypeFactory()
            .constructParametricType(List.class, javaType);
        try {
            return OBJECT_MAPPER.readValue(json, parentType);
        } catch (Throwable e) {
            log.error(" parse list object error ", e.getMessage());
        }
        return new ArrayList<>();
    }
}
