package com.hadj4r.serializer.service;

import com.hadj4r.serializer.model.ImmutableTestClass;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SerializerServiceTest {
    private static final ImmutableTestClassSerializer INSTANCE = ImmutableTestClassSerializer.INSTANCE;

    @Test
    void shouldReturnValidSerializedObject() {
        final ImmutableTestClass immutableTestClass = ImmutableTestClass.builder()
                .setBooleanVar(true)
                .setCharVar('a')
                .setByteVar((byte) 1)
                .setShortVar((short) 2)
                .setIntVar(3)
                .setLongVar(4L)
                .setFloatVar(5.0f)
                .setDoubleVar(6.0)
                .setStringVar("test")
                .setStringVar2("another test")
                .setBooleanArrayVar(new boolean[]{false, true, false})
                .setCharArrayVar(new char[]{'a', 'b', 'c'})
                .setByteArrayVar(new byte[]{1, 2, 3})
                .setShortArrayVar(new short[]{4, 5, 6})
                .setIntArrayVar(new int[]{7, 8, 9})
                .setLongArrayVar(new long[]{10L, 11L, 12L})
                .setFloatArrayVar(new float[]{13.0f, 14.0f, 15.0f})
                .setDoubleArrayVar(new double[]{16.0, 17.0, 18.0})
                .build();

        final byte[] serialized = INSTANCE.serialize(immutableTestClass);

        assertThat(serialized)
                .isNotNull()
                .hasSize(
                        1 +
                        2 +
                        1 +
                        2 +
                        4 +
                        8 +
                        4 +
                        8 +

                        1 + 4 +
                        1 + 12 +

                        1 + 3 +
                        1 + 2 * 3 +
                        1 + 3 +
                        1 + 2 * 3 +
                        1 + 4 * 3 +
                        1 + 8 * 3 +
                        1 + 4 * 3 +
                        1 + 8 * 3
                )
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
                        12, 97, 110, 111, 116, 104, 101, 114, 32, 116, 101, 115, 116, // String
                        3, 0, 1, 0, // boolean[]
                        3, 0, 97, 0, 98, 0, 99, // char[]
                        3, 1, 2, 3, // byte[]
                        3, 0, 4, 0, 5, 0, 6, // short[]
                        3, 0, 0, 0, 7, 0, 0, 0, 8, 0, 0, 0, 9, // int[]
                        3, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 12, // long[]
                        3, 65, 80, 0, 0, 65, 96, 0, 0, 65, 112, 0, 0,
                        3, 64, 48, 0, 0, 0, 0, 0, 0, 64, 49, 0, 0, 0, 0, 0, 0, 64, 50, 0, 0, 0, 0, 0, 0 // double[]
                });
    }
}
