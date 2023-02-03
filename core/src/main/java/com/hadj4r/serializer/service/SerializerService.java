package com.hadj4r.serializer.service;

public interface SerializerService<T> {
    byte[] serialize(final T object);
}
