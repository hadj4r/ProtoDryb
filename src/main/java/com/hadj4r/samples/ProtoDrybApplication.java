package com.hadj4r.samples;

import com.hadj4r.samples.converters.PointConverter;
import com.hadj4r.samples.models.Point;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ProtoDrybApplication {

	public static void main(String[] args) {
		final ConfigurableApplicationContext context = SpringApplication.run(ProtoDrybApplication.class, args);
		final PointConverter pointConverter = context.getBean(PointConverter.class);
		final byte[] encode = pointConverter.encode(new Point(1, 2));
		final Point decode = pointConverter.decode(encode);
		System.out.println("========================================");
		System.out.println(decode);
		System.out.println("========================================");
	}

}
