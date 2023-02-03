package helpful;

import com.dslplatform.json.DslJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hadj4r.protodryb.ConverterInvocationHandlerFactory;
import com.hadj4r.samples.converters.PointConverter;
import com.hadj4r.samples.models.Point;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Proxy;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.openjdk.jmh.annotations.Scope.Thread;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 20, time = 1)
@Fork(3)
@BenchmarkMode(AverageTime)
@OutputTimeUnit(NANOSECONDS)
@State(Thread)
public class SerDesPerformanceTest {
    private Point pointObject;
    private byte[] pointCustomBytes;
    private ByteArrayOutputStream pointBaos;
    private byte[] pointBytes;
    private final PointConverter pointByteConverter = generatePointByteConverter();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DslJson<Point> pointDslJson = new DslJson<>();

    @Setup
    public void setup() {
        pointObject = new Point(1, 2);
        final String pointJson = "{\"x\":1,\"y\":2}";
        pointBytes = pointJson.getBytes(UTF_8);
        pointCustomBytes = new byte[]{0b0, 0b0, 0b0, 0b1, 0b0, 0b0, 0b0, 0b10};
        pointBaos = new ByteArrayOutputStream();
    }

    @Benchmark
    public byte[] _000_Custom_Serialize () {
        return pointByteConverter.encode(pointObject);
    }

    @Benchmark
    public Point _001_Custom_Deserialize() {
        return pointByteConverter.decode(pointCustomBytes);
    }

    @Benchmark
    public byte[] _100_Jackson_Serialize() throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(pointObject);
    }

    @Benchmark
    public Point _101_Jackson_Deserialize() throws IOException {
        return objectMapper.readValue(pointBytes, Point.class);
    }

    @Benchmark
    public byte[] _200_DslJson_Serialize() throws IOException {
        final ByteArrayOutputStream baos = getBaos();
        pointDslJson.serialize(pointObject, baos);
        return baos.toByteArray();
    }

    @Benchmark
    public Point _201_DslJson_Deserialize() throws IOException {
        return pointDslJson.deserialize(Point.class, pointBytes, pointBytes.length);
    }

    private PointConverter generatePointByteConverter() {
        final ConverterInvocationHandlerFactory converterInvocationHandlerFactory = new ConverterInvocationHandlerFactory();
        final Class<PointConverter> clazz = PointConverter.class;
        return (PointConverter) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                converterInvocationHandlerFactory.create(clazz)
        );
    }

    private ByteArrayOutputStream getBaos() {
        pointBaos.reset();
        return pointBaos;
    }
}
