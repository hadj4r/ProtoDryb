package helpful;

import com.hadj4r.samples.models.Point;
import helpful.beanaccessor.BeanAccessor;
import helpful.beanaccessor.javacompiler.JavaCompilerBeanAccessorReaderFactory;
import helpful.beanaccessor.lambdametafactory.LambdaMetafactoryBeanAccessor;
import helpful.beanaccessor.reflectasm.ReflectAsmIndexLookupBeanAccessor;
import helpful.beanaccessor.reflectasm.ReflectAsmNameLookupBeanAccessor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.openjdk.jmh.annotations.Scope.Thread;

// for reference read https://www.optaplanner.org/blog/2018/01/09/JavaReflectionButMuchFaster.html
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 20, time = 1)
@Fork(3)
@BenchmarkMode(AverageTime)
@OutputTimeUnit(NANOSECONDS)
@State(Thread)
public class ReflectionBeanAccessorPerformanceTest {
    // TODO: read about https://github.com/jOOQ/jOOR
    private final BeanAccessor javaCompilerBeanAccessorReader = JavaCompilerBeanAccessorReaderFactory.generate(Point.class, "x");
    private final BeanAccessor lambdaMetafactoryBeanAccessorReader = new LambdaMetafactoryBeanAccessor(Point.class, "x");
    private final BeanAccessor reflectAsmNameLookupBeanAccessorReader = new ReflectAsmNameLookupBeanAccessor(Point.class, "x");
    private final BeanAccessor reflectAsmIndexLookupBeanAccessorReader = new ReflectAsmIndexLookupBeanAccessor(Point.class, "x");
    private Point point;

    @Setup
    public void setup() {
        point = new Point(1, 2);
    }

    @Benchmark
    public int _000_DirectAccess() {
        return point.getX();
    }

    @Benchmark
    public void _001_DirectAccessSet() {
        point.setX(3);
    }

    @Benchmark
    public int _100_JavaCompiler() {
        return (int) javaCompilerBeanAccessorReader.executeGetter(point);
    }

    @Benchmark
    public void _101_JavaCompilerSet() {
        javaCompilerBeanAccessorReader.executeSetter(point, 3);
    }

    @Benchmark
    public int _200_LambdaMetafactory() {
        return (int) lambdaMetafactoryBeanAccessorReader.executeGetter(point);
    }

    @Benchmark
    public void _201_LambdaMetafactorySet() {
        lambdaMetafactoryBeanAccessorReader.executeSetter(point, 3);
    }

    @Benchmark
    public int _300_ReflectAsmNameLookup() {
        return (int) reflectAsmNameLookupBeanAccessorReader.executeGetter(point);
    }

    @Benchmark
    public void _301_ReflectAsmNameLookupSet() {
        reflectAsmNameLookupBeanAccessorReader.executeSetter(point, 3);
    }

    @Benchmark
    public int _400_ReflectAsmIndexLookup() {
        return (int) reflectAsmIndexLookupBeanAccessorReader.executeGetter(point);
    }

    @Benchmark
    public void _401_ReflectAsmIndexLookupSet() {
        reflectAsmIndexLookupBeanAccessorReader.executeSetter(point, 3);
    }

}
