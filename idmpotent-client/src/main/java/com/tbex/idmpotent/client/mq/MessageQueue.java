package com.tbex.idmpotent.client.mq;

import java.time.Duration;

public interface MessageQueue<T> {

    boolean push(T msg, Duration maxWait);

    T pop(Duration maxWait);

    int size();
}
