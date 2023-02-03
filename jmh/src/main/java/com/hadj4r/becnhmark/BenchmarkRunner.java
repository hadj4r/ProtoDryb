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
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openjdk.jmh.annotations.Mode.Throughput;
import static org.openjdk.jmh.annotations.Scope.Thread;

@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 7, time = 1)
@Fork(1)
@BenchmarkMode(Throughput)
@OutputTimeUnit(MILLISECONDS)
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
    public byte[] _000_Custom_Serialize() {
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
}