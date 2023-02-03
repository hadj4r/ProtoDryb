package com.hadj4r.protodryb;

import com.hadj4r.protodryb.utils.PrimitiveConverter;
import com.hadj4r.samples.converters.TestClassConverter;
import com.hadj4r.samples.models.TestClass;
import java.util.Arrays;
import java.util.Comparator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {TestClass.class})
class TestClassConverterTest {
    private final Comparator<TestClass> comparator = Comparator
            .comparing(TestClass::isBooleanValue)
            .thenComparing(TestClass::getCharValue)
            .thenComparing(TestClass::getByteValue)
            .thenComparing(TestClass::getShortValue)
            .thenComparingInt(TestClass::getIntValue)
            .thenComparingLong(TestClass::getLongValue)
            .thenComparing(TestClass::getFloatValue)
            .thenComparingDouble(TestClass::getDoubleValue);
    @Autowired
    private TestClassConverter testClassConverter;

    @Test
    void testEncode() {
        final boolean booleanValue = true;
        final char charValue = 'a';
        final byte byteValue = 1;
        final short shortValue = 2;
        final int intValue = 3;
        final long longValue = 4;
        final float floatValue = 5.0f;
        final double doubleValue = 6.0;
        final TestClass testClass = TestClass.builder()
                .booleanValue(booleanValue)
                .charValue(charValue)
                .byteValue(byteValue)
                .shortValue(shortValue)
                .intValue(intValue)
                .longValue(longValue)
                .floatValue(floatValue)
                .doubleValue(doubleValue)
                .build();

        final byte[] expected = new byte[] {
                0x01, // booleanValue
                0x61, 0x00, // charValue
                0x01, // byteValue
                0x02, 0x00, // shortValue
                0x03, 0x00, 0x00, 0x00, // intValue
                0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // longValue
                0x0, 0x0, (byte) 0xa0, 0x40, // floatValue
                0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x18, 0x40 // doubleValue
        };

        final byte[] encoded = testClassConverter.encode(testClass);

        assertThat(encoded)
                .isNotNull()
                .hasSize(30)    // 1 byte for boolean, 2 bytes for char, 1 byte for byte, 2 bytes for short, 4 bytes for int, 8 bytes for long, 4 bytes for float, 8 bytes for double
                .isEqualTo(expected);
    }

    // @Test
    // void testEncodeWithNegativeValues() {
    //     final Point point = new Point(0b11111111111111111111111111111111, 0b11111111111111111111111111111110);
    //     final byte[] expected = new byte[]{(byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111110};
    //     final byte[] encoded = pointConverter.encode(point);
    //
    //     assertThat(encoded)
    //             .isNotNull()
    //             .hasSize(8)
    //             .isEqualTo(expected);
    // }
    //
    // @Test
    // void testDecode() {
    //     byte[] pointAsBytes = new byte[]{0b0, 0b0, 0b0, 0b1, 0b0, 0b0, 0b0, 0b10};
    //     final Point expected = new Point(0b1, 0b10);
    //     final Point decoded = pointConverter.decode(pointAsBytes);
    //
    //     assertThat(decoded)
    //             .isNotNull()
    //             .usingComparatorForFields(pointComparator, "x", "y")
    //             .isEqualTo(expected);
    // }
    //
    // @Test
    // void testDecodeWithNegativeValues() {
    //     byte[] pointAsBytes = new byte[]{(byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111110};
    //     final Point expected = new Point(0b11111111111111111111111111111111, 0b11111111111111111111111111111110);
    //     final Point decoded = pointConverter.decode(pointAsBytes);
    //
    //     assertThat(decoded)
    //             .isNotNull()
    //             .usingComparatorForFields(pointComparator, "x", "y")
    //             .isEqualTo(expected);
    // }
    //
    @Test
    void testPointConverter() {
        final TestClass testClass = TestClass.builder()
                .booleanValue(true)
                .charValue('a')
                .byteValue((byte) 1)
                .shortValue((short) 2)
                .intValue(3)
                .longValue(4)
                .floatValue(5.0f)
                .doubleValue(6)
                .build();

        final byte[] encoded = testClassConverter.encode(testClass);
        final TestClass decoded = testClassConverter.decode(encoded);

        assertThat(decoded)
                .isNotNull()
                .usingComparator(comparator)
                .isEqualTo(testClass);
    }

}
