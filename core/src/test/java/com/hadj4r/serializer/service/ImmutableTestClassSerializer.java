package com.hadj4r.serializer.service;

import com.hadj4r.serializer.Serializer;
import com.hadj4r.serializer.factory.Serializers;
import com.hadj4r.serializer.model.ImmutableTestClass;

@Serializer
public interface ImmutableTestClassSerializer extends SerializerService<ImmutableTestClass> {
    ImmutableTestClassSerializer INSTANCE = Serializers.getSerializer(ImmutableTestClassSerializer.class);
}
