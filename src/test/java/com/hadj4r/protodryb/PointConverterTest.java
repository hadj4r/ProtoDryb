package com.hadj4r.protodryb;

import com.hadj4r.samples.converters.PointConverter;
import com.hadj4r.samples.models.Point;
import java.util.Comparator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { PointConverter.class })
public class PointConverterTest {
    @Autowired
    private PointConverter pointConverter;

    @Test
    void testPointConverter() {
        Comparator<Point> comparator = Comparator.comparing(Point::getX).thenComparing(Point::getY);

        final Point originalPoint = Point.builder()
                .x(1)
                .y(2)
                .build();

        final byte[] bytes = pointConverter.encode(originalPoint);
        final Point decodedPoint = pointConverter.decode(bytes);

        assert comparator.compare(originalPoint, decodedPoint) == 0;
    }
}
