package com.hadj4r.protodryb;

import com.hadj4r.samples.converters.PointConverter;
import com.hadj4r.samples.models.Point;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {PointConverter.class})
class PointConverterTest {
    @Autowired
    private PointConverter pointConverter;

    @Test
    void testPointConverter() {
        final Point originalPoint = Point.builder().x(1).y(2).build();

        final byte[] bytes = pointConverter.encode(originalPoint);
        final Point decodedPoint = pointConverter.decode(bytes);

        assertThat(decodedPoint).isEqualTo(originalPoint);
    }
}
