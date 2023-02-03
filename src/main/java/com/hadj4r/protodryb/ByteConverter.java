package com.hadj4r.protodryb;

public interface ByteConverter<T> {
    byte[] encode(T object);
    T decode(byte[] bytes);
}
