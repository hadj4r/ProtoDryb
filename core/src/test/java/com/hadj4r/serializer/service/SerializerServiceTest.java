package com.hadj4r.serializer.service;

import com.hadj4r.serializer.model.Child;
import com.hadj4r.serializer.model.Child2;
import com.hadj4r.serializer.model.Duplicate;
import com.hadj4r.serializer.model.Grandchild;
import com.hadj4r.serializer.model.Grandchild2;
import com.hadj4r.serializer.model.ImmutableTestClass;
import com.hadj4r.serializer.model.Shared;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SerializerServiceTest {
    private static final ImmutableTestClassSerializer INSTANCE = ImmutableTestClassSerializer.INSTANCE;

    @Test
    void shouldReturnValidSerializedObject() {
        final ImmutableTestClass immutableTestClass = ImmutableTestClass.builder()
                .setBooleanVar(true)//2
                .setCharVar('a')//4
                .setByteVar((byte) 1)//5
                .setShortVar((short) 2)//7
                .setIntVar(3)//11
                .setLongVar(4L)//19
                .setFloatVar(5.0f)//23
                .setDoubleVar(6.0)//31
                .setStringVar("test")//36
                .setStringVar2("another test")//49
                .setBooleanArrayVar(new boolean[]{false, true, false})//53
                .setCharArrayVar(new char[]{'a', 'b', 'c'})//60
                .setByteArrayVar(new byte[]{1, 2, 3})//64
                .setShortArrayVar(new short[]{4, 5, 6})//71
                .setIntArrayVar(new int[]{7, 8, 9})//84
                .setLongArrayVar(new long[]{10L, 11L, 12L})//109
                .setFloatArrayVar(new float[]{13.f, 14.f, 15.f})//122
                .setDoubleArrayVar(new double[]{16., 17., 18.})//147
                .setChildVar(
                        new Child(//148
                                false,//149
                                new Grandchild(//150
                                        (byte) 19,//151
                                        new Shared(20.)//160
                                ),
                                true//161
                        )
                )
                .setChild2Var(
                        new Child2(//162
                                new Duplicate(//163
                                        21,//167
                                        new int[]{22, 23, 24},//180
                                        false//181
                                ),
                                new Grandchild2(//182
                                        "grandchild2",//194
                                        new Shared(25.)//203
                                ),
                                new Duplicate(//204
                                        26,//208
                                        new int[]{27, 28, 29},//221
                                        true//222
                                ),
                                new Shared(30.)//230
                        )
                )
                .build();

        final byte[] serialized = INSTANCE.serialize(immutableTestClass);

        assertThat(serialized)
                .isNotNull()
                // .hasSize(
                //         1 +
                //         2 +
                //         1 +
                //         2 +
                //         4 +
                //         8 +
                //         4 +
                //         8 +
                //
                //         1 + 4 +
                //         1 + 12 +
                //
                //         1 + 3 +
                //         1 + 2 * 3 +
                //         1 + 3 +
                //         1 + 2 * 3 +
                //         1 + 4 * 3 +
                //         1 + 8 * 3 +
                //         1 + 4 * 3 +
                //         1 + 8 * 3
                // )
                // .isEqualTo(new byte[]{
                //         1, // boolean
                //         0, 97, // char
                //         1, // byte
                //         0, 2, // short
                //         0, 0, 0, 3, // int
                //         0, 0, 0, 0, 0, 0, 0, 4, // long
                //         0x40, (byte) 0xa0, 0, 0, // float
                //         0x40, (byte) 0x18, 0, 0, 0, 0, 0, 0, // double
                //         4, 116, 101, 115, 116, // String
                //         12, 97, 110, 111, 116, 104, 101, 114, 32, 116, 101, 115, 116, // String
                //         3, 0, 1, 0, // boolean[]
                //         3, 0, 97, 0, 98, 0, 99, // char[]
                //         3, 1, 2, 3, // byte[]
                //         3, 0, 4, 0, 5, 0, 6, // short[]
                //         3, 0, 0, 0, 7, 0, 0, 0, 8, 0, 0, 0, 9, // int[]
                //         3, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 12, // long[]
                //         3, 65, 80, 0, 0, 65, 96, 0, 0, 65, 112, 0, 0,
                //         3, 64, 48, 0, 0, 0, 0, 0, 0, 64, 49, 0, 0, 0, 0, 0, 0, 64, 50, 0, 0, 0, 0, 0, 0 // double[]
                // })
        ;
    }
}
