package com.hadj4r.serializer.service;

import com.hadj4r.serializer.model.ImmutableTestClass;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SerializerServiceTest {
    private static final ImmutableTestClassSerializer INSTANCE = ImmutableTestClassSerializer.INSTANCE;

    @Test
    void shouldReturnValidSerializedObject() {
        final ImmutableTestClass immutableTestClass = new ImmutableTestClass(true, 'a', (byte) 1, (short) 2, 3, 4L, 5.0f, 6.0, "test", "another test");

        final byte[] serialized = INSTANCE.serialize(immutableTestClass);

        assertThat(serialized)
                .isNotNull()
                .hasSize(1 + 2 + 1 + 2 + 4 + 8 + 4 + 8 + 1 + 4 + 1 + 12)
                .isEqualTo(new byte[]{
                        1, // boolean
                        0, 97, // char
                        1, // byte
                        0, 2, // short
                        0, 0, 0, 3, // int
                        0, 0, 0, 0, 0, 0, 0, 4, // long
                        0x40, (byte) 0xa0, 0, 0, // float
                        0x40, (byte) 0x18, 0, 0, 0, 0, 0, 0, // double
                        4, 116, 101, 115, 116, // String
                        12, 97, 110, 111, 116, 104, 101, 114, 32, 116, 101, 115, 116 // String
                });
    }
}
