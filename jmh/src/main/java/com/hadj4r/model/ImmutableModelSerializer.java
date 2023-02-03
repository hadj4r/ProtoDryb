package com.hadj4r.model;

import com.hadj4r.serializer.Serializer;
import com.hadj4r.serializer.service.SerializerService;
import com.hadj4r.serializer.factory.Serializers;

@Serializer
public interface ImmutableModelSerializer extends SerializerService<ImmutableModel> {
    ImmutableModelSerializer INSTANCE = Serializers.getSerializer(ImmutableModelSerializer.class);
}
