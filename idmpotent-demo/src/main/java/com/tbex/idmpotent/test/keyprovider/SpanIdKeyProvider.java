package com.tbex.idmpotent.test.keyprovider;

import com.tbex.idmpotent.client.keyprovider.KeyProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 */
@Component
@Primary
public class SpanIdKeyProvider implements KeyProvider {

  private static ThreadLocal<String> spanIdPool = new ThreadLocal<>();

  public static void put(String spanId) {
    spanIdPool.set(spanId);
  }

  public static void remove() {
    spanIdPool.remove();
  }

  @Override
  public String get(){
    return spanIdPool.get();
  }
}
