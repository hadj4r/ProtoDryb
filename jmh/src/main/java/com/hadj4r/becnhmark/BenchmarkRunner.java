package com.hadj4r.becnhmark;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Settings;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hadj4r.model.ImmutableTestClass;
import com.hadj4r.model.ImmutableTestClassSerializer;
import java.io.IOException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openjdk.jmh.annotations.Mode.Throughput;
import static org.openjdk.jmh.annotations.Scope.Thread;

@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 20, time = 1)
@Fork(1)
@BenchmarkMode(Throughput)
@OutputTimeUnit(MICROSECONDS)
@State(Thread)
public class BenchmarkRunner {

    private static final ImmutableTestClassSerializer IMMUTABLE_TEST_CLASS_SERIALIZER = ImmutableTestClassSerializer.INSTANCE;
    private static final DslJson<Object> DSL_JSON = new DslJson<>(Settings.withRuntime().allowArrayFormat(true).includeServiceLoader());
    private static final JsonWriter WRITER = DSL_JSON.newWriter();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ImmutableTestClass immutableTestClass;

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }

    @Setup
    public void setup() {
        immutableTestClass = new ImmutableTestClass(true, (short) 2, 3, 4L, 5.0f, 6.0d, "test", "another test");
    }

    @Benchmark
    public byte[] _000_ProtoDryb_Serialize() {
        return IMMUTABLE_TEST_CLASS_SERIALIZER.serialize(immutableTestClass);
    }

    @Benchmark
    public byte[] _100_Jackson_Serialize() throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(immutableTestClass);
    }

    @Benchmark
    public byte[] _200_DslJson_Serialize() throws IOException {
        WRITER.reset();
        DSL_JSON.serialize(WRITER, immutableTestClass);
        return WRITER.toByteArray();
    }

    // TODO remove this after adding actual bitpacking
    // @Benchmark
    // public byte[] _withBitPacking() {
    //     final byte[] bytes = new byte[5];
    //     final boolean b1 = true;
    //     final boolean b2 = false;
    //     final boolean b3 = true;
    //     final boolean b4 = false;
    //     final boolean b5 = true;
    //     final boolean b6 = false;
    //     // final int i = 0xa40f1dd3;
    //     final int i = 480131031;
    //     bytes[0] = (byte) (b1 ? 1 << 7: 0);
    //     bytes[0] |= (byte) (b2 ? 1 << 6: 0);
    //     bytes[0] |= (byte) (b3 ? 1 << 5: 0);
    //     bytes[0] |= (byte) (b4 ? 1 << 4: 0);
    //     bytes[0] |= (byte) (b5 ? 1 << 3: 0);
    //     bytes[0] |= (byte) (b6 ? 1 << 2: 0);
    //     bytes[0] |= i >> 30;        // 0..=1 bits
    //     bytes[1] = i >> 24 - 2;     // 2..=7 bits
    //     bytes[2] = (byte) (i >> 8 + 8 - 2); // 8..=15 bits
    //     bytes[3] = (byte) (i >> 8 - 2); // 16..=23 bits
    //     bytes[4] = (byte) (i << 8 - 2); // 24..=31 bits
    //
    //     System.out.println(Integer.toBinaryString(i));
    //     // print first octet of i
    //     System.out.println(Integer.toBinaryString(i >> 24));
    //     // print second octet of i
    //     System.out.println(Integer.toBinaryString(i >> 16 & 0xff));
    //     // print third octet of i
    //     System.out.println(Integer.toBinaryString(i >> 8 & 0xff));
    //     // print last octet of i
    //     System.out.println(Integer.toBinaryString(i & 0xff));
    //     // 00011100 10011110 00110111 11010111
    //     // 00011100 10011110 00110111 11010111
    //
    //     int int1 = bytes[0] & 0x3 << 6;
    //     int1 |= bytes[1] >> 6;
    //     int int2 = bytes[1] & 0xff << 2;
    //     int2 |= bytes[2] >> 6;
    //     int int3 = bytes[2] & 0xff << 2;
    //     int3 |= bytes[3] >> 6;
    //     int int4 = bytes[3] & 0xff << 2;
    //     int4 |= bytes[4] >> 6;
    //     System.out.println(Integer.toBinaryString(int4 & 0xff));
    //     System.out.println(Integer.toBinaryString(int3 & 0xff));
    //     System.out.println(Integer.toBinaryString(int2 & 0xff));
    //     System.out.println(Integer.toBinaryString(int1 & 0xff));
    //     // int intSum = int1 & 0xff | int2 & 0xff << 8 | int3 & 0xff << 16 | int4 & 0xff << 24;
    //     // System.out.println(i);
    //     // System.out.println(intSum);
    //
    //     return bytes;
    // }
    //
    // @Benchmark
    // public byte[] _withoutBitPacking() {
    //     final byte[] bytes = new byte[10];
    //     final boolean b1 = true;
    //     final boolean b2 = false;
    //     final boolean b3 = true;
    //     final boolean b4 = false;
    //     final boolean b5 = true;
    //     final boolean b6 = false;
    //     final int i = 0xa40f1dd3;
    //     bytes[0] = (byte) (b1 ? 1: 0);
    //     bytes[1] = (byte) (b2 ? 1: 0);
    //     bytes[2] = (byte) (b3 ? 1: 0);
    //     bytes[3] = (byte) (b4 ? 1: 0);
    //     bytes[4] = (byte) (b5 ? 1: 0);
    //     bytes[5] = (byte) (b6 ? 1: 0);
    //     bytes[6] = (byte) i;
    //     bytes[7] = (byte) (i >> 8);
    //     bytes[8] = (byte) (i >> 16);
    //     bytes[9] = (byte) (i >> 24);
    //     return bytes;
    // }
}