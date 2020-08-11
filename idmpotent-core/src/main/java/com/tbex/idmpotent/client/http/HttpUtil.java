package com.tbex.idmpotent.client.http;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @ClassName: HttpUtil
 * @Description: todo http util工具类
 * @Author: xuliang
 * @Date: 2020/4/20 上午10:40
 * @Version: 1.0
 */
public class HttpUtil {

    private static RestTemplate restTemplate = new RestTemplate();

    /**
     * 查询
     */
    public static <T> T get(String url, Class<T> responseType) {
        return restTemplate.getForObject(url, responseType);
    }

    /**
     * 构建请求体
     * 1. Json
     * 2. utf8
     */
    private static HttpEntity<String> buildJsonRequest(String requestJson) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.APPLICATION_JSON_UTF8;
        headers.setContentType(type);
        return new HttpEntity<>(requestJson, headers);
    }

    private static HttpEntity<String> buildJsonEntity(Map<String, String> headerParams, String requestJson) {
        HttpHeaders headers = new HttpHeaders();
        headerParams.forEach(headers::add);
        return new HttpEntity<>(requestJson, headers);
    }

    /**
     * 创建
     */
    public static <T> T post(String url, Object request, Class<T> responseType, Object... uriVariables) {
        return restTemplate.postForObject(url, request, responseType, uriVariables);
    }

    public static <T> T postJson(String url, String requestJson, Class<T> responseType, Object... uriVariables) {
        HttpEntity<String> entity = buildJsonRequest(requestJson);
        return restTemplate.postForObject(url, entity, responseType, uriVariables);
    }

    public static <T> T postJson(String url, String requestJson, Map<String, String> headerParams, Class<T> responseType) {
        HttpEntity<String> entity = buildJsonEntity(headerParams, requestJson);
        return restTemplate.postForObject(url, entity, responseType);
    }

    /**
     * 更新
     */
    public static void put(String url, Object request, Object... uriVariables) {
        restTemplate.put(url, request, uriVariables);
    }

    public static <T> T putJson(String url, String requestJson, Class<T> responseType, Object... uriVariables) {
        HttpEntity<String> entity = buildJsonRequest(requestJson);
        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, entity, responseType, uriVariables);
        return responseEntity.getBody();
    }

    /**
     * 删除
     */
    public static void delete(String url, Object... uriVariables) {
        restTemplate.delete(url, uriVariables);
    }

    public static <T> T deleteJson(String url, String requestJson, Class<T> responseType, Object... uriVariables) {
        HttpEntity<String> entity = buildJsonRequest(requestJson);
        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, entity, responseType, uriVariables);
        return responseEntity.getBody();
    }

}
