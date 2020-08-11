//package com.tbex.idmpotent.test;
//
//
//import com.tbex.idmpotent.client.util.CompressUtil;
//import com.tbex.idmpotent.client.util.JsonUtil;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.util.Base64;
//
///**
// * @author hgc
// * @date 4/6/19
// */
//public class SerializeTest {
//
//  private String content = "OK";
//
//  private byte[] serialize(Object obj) throws IOException {
//    String ser = JsonUtil.write(obj);
//    byte[] com = CompressUtil.gzip(ser);
//    return Base64.getEncoder().encode(com);
//  }
//
//  private <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
//    byte[] decoded = Base64.getDecoder().decode(bytes);
//    String uncom = CompressUtil.unGZIP(decoded);
//    return JsonUtil.read(uncom, type);
//  }
//
//  @Test
//  public void testBase64() {
//    byte[] bytes = content.getBytes();
//    byte[] encoded = Base64.getEncoder().encode(bytes);
//    System.out.println();
//    assert content.equals(new String(Base64.getDecoder().decode(encoded)));
//  }
//
//  @Test
//  public void testGZIP() throws IOException {
//    byte[] compressed = CompressUtil.gzip(content);
//    String unCompressed = CompressUtil.unGZIP(compressed);
//    assert content.equals(unCompressed);
//  }
//
//  @Test
//  public void test() throws IOException {
//    String obj = content;
//    byte[] sered = serialize(obj);
//    assert obj.equals(deserialize(sered, String.class));
//  }
//
//}
