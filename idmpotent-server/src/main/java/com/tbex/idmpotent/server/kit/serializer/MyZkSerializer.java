package com.tbex.idmpotent.server.kit.serializer;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.nio.charset.Charset;

public class MyZkSerializer implements ZkSerializer {
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        return new String(bytes, Charset.forName("UTF-8"));
    }

    public byte[] serialize(Object obj) throws ZkMarshallingError {
        return String.valueOf(obj).getBytes(Charset.forName("UTF-8"));
    }
}