package com._42195km.alertservice.application.service;

public interface AlertStrategy<T> {

    String makeMessage(T eventDto);

    void throwException(Exception e);

    String getMediaId(T eventDto);
}
