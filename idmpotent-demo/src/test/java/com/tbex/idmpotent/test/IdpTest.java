package com.tbex.idmpotent.test;

import com.tbex.idmpotent.client.http.HttpUtil;
import com.tbex.idmpotent.client.util.Constants;
import com.tbex.idmpotent.test.keyprovider.SpanIdKeyProvider;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 需要先启动对应存储中间件和TestIdpApplication
 */
public class IdpTest {

  private RestTemplate restTemplate = new RestTemplate();

  private static String httpurl = "http://localhost:6080/test/";


  private void sendSingleRequest(String url, Map<String, String> headerParams) {
    String res = HttpUtil.postJson(url, "", headerParams, String.class);
    System.out.println(res);
  }

  private void sendRetryRequest(String url, Map<String, String> headerParams)
      throws InterruptedException {
    ExecutorService tp = Executors.newFixedThreadPool(3);
    CountDownLatch latch = new CountDownLatch(3);
    for (int i = 0; i < 3; i++) {
      tp.submit(() -> {
        try {
          String res = HttpUtil.postJson(url, "", headerParams, String.class);
          System.out.println(res);
        } finally {
          latch.countDown();
        }
      });
    }
    latch.await();
  }

  private String genSpanId() {
    return Long.toString(System.currentTimeMillis());
  }



  /**
   * 该用例需要配置DefaultKeyProvider
   */
  @Test
  public void testHello() {
    String spanId = genSpanId();
    SpanIdKeyProvider.put(spanId);
    String url = httpurl+"/idp/hello?traceId="+spanId;
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(Constants.HEADER_KEY_TRACE_ID, spanId);
    sendSingleRequest(url, headerParams);
  }

  /**
   * 该用例需要配置SpanIdKeyProvider
   */
  @Test
  public void testSpanId() throws InterruptedException {
    String spanId = genSpanId();
    SpanIdKeyProvider.put(spanId);
    String url = httpurl+"/idp/spanId?traceId="+spanId;
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(Constants.HEADER_KEY_TRACE_ID, spanId);
    sendRetryRequest(url, headerParams);
  }

  /**
   * 该用例需要配置SpanIdKeyProvider 会重试几次
   */
  @Test
  public void testThrowEx() throws InterruptedException {
    String spanId = genSpanId();
    SpanIdKeyProvider.put(spanId);
    String url = httpurl+"/idp/throwEx?traceId="+spanId;
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(Constants.HEADER_KEY_TRACE_ID, spanId);
    sendRetryRequest(url, headerParams);
  }

  @Test
  public void testThrowRuntimeEx() throws InterruptedException {
    String spanId = genSpanId();
    String url = httpurl+"/idp/throwRuntimeEx?traceId="+spanId;
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put(Constants.HEADER_KEY_TRACE_ID, spanId);
    sendRetryRequest(url, headerParams);
  }


}
