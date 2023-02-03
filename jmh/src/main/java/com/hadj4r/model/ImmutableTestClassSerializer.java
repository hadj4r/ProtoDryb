package com.hadj4r.model;

import com.hadj4r.serializer.Serializer;
import com.hadj4r.serializer.service.SerializerService;
import com.hadj4r.serializer.factory.Serializers;

@Serializer
public interface ImmutableTestClassSerializer extends SerializerService<ImmutableTestClass> {
    ImmutableTestClassSerializer INSTANCE = Serializers.getSerializer(ImmutableTestClassSerializer.class);
}
