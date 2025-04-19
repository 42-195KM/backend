package com._42195km.alertservice.infrastructure.messaging.in;

public interface AlertStrategy<T> {

    String makeMessage(T eventDto);

    void throwException(Exception e);

    String getMediaId(T eventDto);
}
