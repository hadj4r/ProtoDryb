package com.hadj4r.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ProtoDrybApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(ProtoDrybApplication.class, args);
    }

}
