package com.tbex.idmpotent.client.utils;

import java.io.UnsupportedEncodingException;

/**
 * 字符编码
 * @author wgx
 * @version 2017年10月13日 
 */
public enum Charsets {

    GBK("GBK"), UTF8("UTF-8");

    public final String encoding;

    private Charsets(String value) {
        this.encoding = value;
    }

    public String toString(byte[] bytes) {
        return toString(bytes, encoding);
    }

    public byte[] getBytes(String s) {
        return getBytes(s, encoding);
    }

    public static byte[] getBytes(String s, String encoding) {
        try {
            return s.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            return s.getBytes();
        }
    }

    public static String toString(byte[] bytes, String encoding) {
        try {
            return new String(bytes, encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(bytes);
        }
    }
}
