package com.hadj4r.protodryb;

import java.beans.Introspector;
import java.lang.reflect.Proxy;
import java.util.Set;
import org.reflections.Reflections;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ConverterApplicationContextInitializer implements ApplicationContextInitializer {
    private final ConverterInvocationHandlerFactory converterInvocationHandlerFactory = new ConverterInvocationHandlerFactory();

    @Override
    public void initialize(final ConfigurableApplicationContext context) {
        final String packagesToScan = context.getEnvironment().getProperty("protodryb.packages-to-scan");
        final Reflections scanner = new Reflections(packagesToScan);
        final Set<Class<? extends ByteConverter>> byteConverterInterfaces = scanner.getSubTypesOf(ByteConverter.class);
        // fix above line to get the needed type

        // TODO: test other implementations https://levelup.gitconnected.com/comparing-different-ways-to-build-proxies-in-java-2d09ae9c233a
        byteConverterInterfaces.forEach(byteConverterInterface -> {
            final Object proxy = Proxy.newProxyInstance(
                    byteConverterInterface.getClassLoader(),
                    new Class[]{byteConverterInterface},
                    converterInvocationHandlerFactory.create(byteConverterInterface)
            );
            context.getBeanFactory().registerSingleton(
                    Introspector.decapitalize(byteConverterInterface.getSimpleName()),
                    proxy
            );
        });

    }
}
