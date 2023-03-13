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
                .setBooleanVar(true)                                            // 1
                .setCharVar('a')                                                // 3
                .setByteVar((byte) 1)                                           // 4
                .setShortVar((short) 2)                                         // 6
                .setIntVar(3)                                                   // 10
                .setLongVar(4L)                                                 // 18
                .setFloatVar(5.0f)                                              // 22
                .setDoubleVar(6.0)                                              // 30
                .setStringVar("test")                                           // 36 (1 for optional + 1 for size)
                .setStringVar2("another test")                                  // 50
                .setBooleanArrayVar(new boolean[]{false, true, false})          // 55
                .setCharArrayVar(new char[]{'a', 'b', 'c'})                     // 63
                .setByteArrayVar(new byte[]{1, 2, 3})                           // 68
                .setShortArrayVar(new short[]{4, 5, 6})                         // 76
                .setIntArrayVar(new int[]{7, 8, 9})                             // 90
                .setLongArrayVar(new long[]{10L, 11L, 12L})                     // 118
                .setFloatArrayVar(new float[]{13.f, 14.f, 15.f})                // 132
                .setDoubleArrayVar(new double[]{16., 17., 18.})                 // 158
                .setChildVar(
                        new Child(                                              // 159
                                false,                                          // 160
                                new Grandchild(                                 // 161
                                        (byte) 19,                              // 162
                                        new Shared(20.)           // 172
                                ),
                                true                                            // 173
                        )
                )
                .setChild2Var(
                        new Child2(                                             // 174
                                new Duplicate(                                  // 175
                                        21,                                     // 179
                                        new int[]{22, 23, 24},                  // 193
                                        false                                   // 194
                                ),
                                new Grandchild2(                                // 195
                                        "grandchild2",                          // 208
                                        new Shared(25.)           // 218
                                ),
                                new Duplicate(                                  // 219
                                        26,                                     // 223
                                        new int[]{27, 28, 29},                  // 237
                                        true                                    // 238
                                ),
                                new Shared(30.)                   // 248
                        )
                )
                .build();

        final byte[] serialized = INSTANCE.serialize(immutableTestClass);

        assertThat(serialized)
                .isNotNull()
                .hasSize(248)
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
