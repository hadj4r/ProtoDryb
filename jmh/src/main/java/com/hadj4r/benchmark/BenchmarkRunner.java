package com.hadj4r.benchmark;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Settings;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.flatbuffers.FlatBufferBuilder;
import com.hadj4r.model.ImmutableModel;
import com.hadj4r.model.ImmutableModelSerializer;
import com.hadj4r.model.flatbuffers.FlatbuffersModel;
import com.hadj4r.model.proto.ProtoModelOuterClass.ProtoModel;
import java.io.IOException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openjdk.jmh.annotations.Mode.Throughput;
import static org.openjdk.jmh.annotations.Scope.Thread;

@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 20, time = 1)
@Fork(1)
@BenchmarkMode(Throughput)
@OutputTimeUnit(MILLISECONDS)
@State(Thread)
public class BenchmarkRunner {

    private static final ImmutableModelSerializer IMMUTABLE_MODEL_SERIALIZER = ImmutableModelSerializer.INSTANCE;
    private static final DslJson<Object> DSL_JSON = new DslJson<>(Settings.withRuntime().allowArrayFormat(true).includeServiceLoader());
    private static final JsonWriter WRITER = DSL_JSON.newWriter();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final FlatBufferBuilder flatBufferBuilder = new FlatBufferBuilder(0);
    private ImmutableModel immutableModel;
    private ProtoModel immutableModelProto;

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }

    @Setup
    public void setup() {
    }

    @Benchmark
    public byte[] _000_ProtoDryb_Serialize() {
        initPojo();
        return IMMUTABLE_MODEL_SERIALIZER.serialize(immutableModel);
    }

    @Benchmark
    public byte[] _100_Jackson_Serialize() throws JsonProcessingException {
        initPojo();
        return OBJECT_MAPPER.writeValueAsBytes(immutableModel);
    }

    @Benchmark
    public byte[] _200_DslJson_Serialize() throws IOException {
        initPojo();
        WRITER.reset();
        DSL_JSON.serialize(WRITER, immutableModel);
        return WRITER.toByteArray();
    }

    @Benchmark
    public byte[] _300_ProtoBuf_Serialize() {
        initProtoPojo();
        return immutableModelProto.toByteArray();
    }

    @Benchmark
    public byte[] _400_FlatBuffers_Serialize() {
        initFlatBuffersPojo();
        return flatBufferBuilder.sizedByteArray();
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
    private void initPojo() {
        immutableModel = ImmutableModel.builder()
                .setBooleanVar(true)
                .setShortVar((short) 2)
                .setIntVar(3)
                .setLongVar(4L)
                .setFloatVar(5.0f)
                .setDoubleVar(6.0)
                .setStringVar("test")
                .setStringVar2("another test")
                .setBooleanArrayVar(new boolean[]{false, true, false})
                .setShortArrayVar(new short[]{4, 5, 6})
                .setIntArrayVar(new int[]{7, 8, 9})
                .setLongArrayVar(new long[]{10L, 11L, 12L})
                .setFloatArrayVar(new float[]{13.0f, 14.0f, 15.0f})
                .setDoubleArrayVar(new double[]{16.0, 17.0, 18.0})
                .build();
    }

    private void initProtoPojo() {
        immutableModelProto = ProtoModel.newBuilder()
                .setBooleanVar(true)
                .setShortVar(2)
                .setIntVar(3)
                .setLongVar(4L)
                .setFloatVar(5.0f)
                .setDoubleVar(6.0)
                .setStringVar("test")
                .setStringVar2("another test")
                .addBooleanArrayVar(false)
                .addBooleanArrayVar(true)
                .addBooleanArrayVar(false)
                .addShortArrayVar(4)
                .addShortArrayVar(5)
                .addShortArrayVar(6)
                .addIntArrayVar(7)
                .addIntArrayVar(8)
                .addIntArrayVar(9)
                .addLongArrayVar(10L)
                .addLongArrayVar(11L)
                .addLongArrayVar(12L)
                .addFloatArrayVar(13.0f)
                .addFloatArrayVar(14.0f)
                .addFloatArrayVar(15.0f)
                .addDoubleArrayVar(16.0)
                .addDoubleArrayVar(17.0)
                .addDoubleArrayVar(18.0)
                .build();
    }

    private void initFlatBuffersPojo() {
        flatBufferBuilder.clear();
        final int stringVar = flatBufferBuilder.createString("test");
        final int stringVar2 = flatBufferBuilder.createString("another test");
        final int booleanArrVar = FlatbuffersModel.createBooleanArrayVarVector(flatBufferBuilder, new boolean[]{false, true, false});
        final int shortArrVar = FlatbuffersModel.createShortArrayVarVector(flatBufferBuilder, new short[]{4, 5, 6});
        final int intArrVar = FlatbuffersModel.createIntArrayVarVector(flatBufferBuilder, new int[]{7, 8, 9});
        final int longArrVar = FlatbuffersModel.createLongArrayVarVector(flatBufferBuilder, new long[]{10L, 11L, 12L});
        final int floatArrVar = FlatbuffersModel.createFloatArrayVarVector(flatBufferBuilder, new float[]{13.0f, 14.0f, 15.0f});
        final int doubleArrVar = FlatbuffersModel.createDoubleArrayVarVector(flatBufferBuilder, new double[]{16.0, 17.0, 18.0});
        FlatbuffersModel.startFlatbuffersModel(flatBufferBuilder);
        FlatbuffersModel.addBooleanVar(flatBufferBuilder, true);
        FlatbuffersModel.addShortVar(flatBufferBuilder, (short) 2);
        FlatbuffersModel.addIntVar(flatBufferBuilder, 3);
        FlatbuffersModel.addLongVar(flatBufferBuilder, 4L);
        FlatbuffersModel.addFloatVar(flatBufferBuilder, 5.0f);
        FlatbuffersModel.addDoubleVar(flatBufferBuilder, 6.0);
        FlatbuffersModel.addStringVar(flatBufferBuilder, stringVar);
        FlatbuffersModel.addStringVar2(flatBufferBuilder, stringVar2);
        FlatbuffersModel.addBooleanArrayVar(flatBufferBuilder, booleanArrVar);
        FlatbuffersModel.addShortArrayVar(flatBufferBuilder, shortArrVar);
        FlatbuffersModel.addIntArrayVar(flatBufferBuilder, intArrVar);
        FlatbuffersModel.addLongArrayVar(flatBufferBuilder, longArrVar);
        FlatbuffersModel.addFloatArrayVar(flatBufferBuilder, floatArrVar);
        FlatbuffersModel.addDoubleArrayVar(flatBufferBuilder, doubleArrVar);
        final int model = FlatbuffersModel.endFlatbuffersModel(flatBufferBuilder);
        flatBufferBuilder.finish(model);
    }
}