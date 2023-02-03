package com.hadj4r.protodryb;

import com.hadj4r.samples.converters.PointConverter;
import com.hadj4r.samples.models.Point;
import java.util.Comparator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {PointConverter.class})
class PointConverterTest {
    private final Comparator<Point> pointComparator = Comparator.comparing(Point::getX).thenComparing(Point::getY);
    @Autowired
    private PointConverter pointConverter;

    @Test
    void testEncode() {
        final Point point = new Point(0b1, 0b10);
        final byte[] expected = new byte[]{0b0, 0b0, 0b0, 0b1, 0b0, 0b0, 0b0, 0b10};
        final byte[] encoded = pointConverter.encode(point);

        assertThat(encoded)
                .isNotNull()
                .hasSize(8)
                .isEqualTo(expected);
    }

    @Test
    void testEncodeWithNegativeValues() {
        final Point point = new Point(0b11111111111111111111111111111111, 0b11111111111111111111111111111110);
        final byte[] expected = new byte[]{(byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111110};
        final byte[] encoded = pointConverter.encode(point);

        assertThat(encoded)
                .isNotNull()
                .hasSize(8)
                .isEqualTo(expected);
    }

    @Test
    void testDecode() {
        byte[] pointAsBytes = new byte[]{0b0, 0b0, 0b0, 0b1, 0b0, 0b0, 0b0, 0b10};
        final Point expected = new Point(0b1, 0b10);
        final Point decoded = pointConverter.decode(pointAsBytes);

        assertThat(decoded)
                .isNotNull()
                .usingComparatorForFields(pointComparator, "x", "y")
                .isEqualTo(expected);
    }

    @Test
    void testDecodeWithNegativeValues() {
        byte[] pointAsBytes = new byte[]{(byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111110};
        final Point expected = new Point(0b11111111111111111111111111111111, 0b11111111111111111111111111111110);
        final Point decoded = pointConverter.decode(pointAsBytes);

        assertThat(decoded)
                .isNotNull()
                .usingComparatorForFields(pointComparator, "x", "y")
                .isEqualTo(expected);
    }

    @Test
    void testPointConverter() {
        final Point originalPoint = Point.builder().x(1).y(2).build();

        final byte[] bytes = pointConverter.encode(originalPoint);
        final Point decodedPoint = pointConverter.decode(bytes);

        assertThat(decodedPoint).isEqualTo(originalPoint);
    }
}
