package com.tbex.idmpotent.server.utils;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 */
@Slf4j
public class FileUtil {

  /**
   * 设置一个文件大小阈值，提示文件不能过大、防止占用过大的内存空间
   */
  private static final long MAX_FILE_LENGTH = 2 * 1024 * 1024;

  /**
   * 资源文件夹路径前缀
   */
  public static final String INTERNAL_RES_PATH_PREFIX = "src/main/resources/";

  public static String concatPath(String prefix, String filePath) {
    if (null == prefix || null == filePath) {
      return null;
    }
    boolean prefixEndsSlash = prefix.endsWith("/");
    boolean filePathStartsSlash = filePath.startsWith("/");
    if (prefixEndsSlash && filePathStartsSlash) {
      return prefix + filePath.substring(1);
    } else if (prefixEndsSlash || filePathStartsSlash) {
      return prefix + filePath;
    }
    return prefix + "/" + filePath;
  }

  /**
   * 外部jar包内的资源文件路径是以/开头的
   */
  public static String genExternalPath(String filePath) {
    if (Strings.isNullOrEmpty(filePath)) {
      return null;
    } else if (!filePath.startsWith("/")) {
      return "/" + filePath;
    }
    return filePath;
  }

  public static String readStream(InputStream input) {
    return readStream(input, "UTF-8");
  }

  public static String readStream(InputStream input, String encode) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, encode))) {
      StringBuilder buffer = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        buffer.append(line).append("\n");
      }
      return buffer.length() == 0 ? "" : buffer.substring(0, buffer.length() - 1);
    } catch (IOException e) {
      throw new IllegalArgumentException("读取失败", e);
    }
  }

  public static String readFile(File file) {
    if (file.length() > MAX_FILE_LENGTH) {
      throw new RuntimeException("超出文件大小限制{" + MAX_FILE_LENGTH + "Byte}");
    }
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
      StringBuilder buffer = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        buffer.append(line).append("\n");
      }
      return buffer.length() == 0 ? "" : buffer.substring(0, buffer.length() - 1);
    } catch (IOException e) {
      throw new IllegalArgumentException("文件读取失败，请检查文件路径是否合法", e);
    }
  }

  /**
   * 读取资源文件
   *
   * @param filePath 文件路径，resources文件夹的下相对路径，需要拼接上一个前缀
   * @return 文件内容
   */
  public static String readResFile(String filePath) {
    // 先看是不是当前项目中的资源文件
    String internalPath = concatPath(INTERNAL_RES_PATH_PREFIX, filePath);
    File file = new File(internalPath);
    return readFile(file);
  }

  /**
   * 读取外部jar包内的资源文件，注意路径不大一样
   *
   * @param filePath 外部资源文件路径，如/properties/test.properties
   * @return 文件内容
   */
  public static String readExternalResFile(String filePath) {
    String externalPath = genExternalPath(filePath);
    InputStream input = FileUtil.class.getResourceAsStream(externalPath);
    return readStream(input);
  }

}
