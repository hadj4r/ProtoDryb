package com.hadj4r.serializer.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public final class Serializers {

    private Serializers() {
    }

    public static <T> T getSerializer(final Class<T> clazz) {
        final List<ClassLoader> classLoaders = collectClassLoaders(clazz.getClassLoader());

        try {
            return getSerializer(clazz, classLoaders);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<ClassLoader> collectClassLoaders(final ClassLoader classLoader) {
        List<ClassLoader> classLoaders = new ArrayList<>(3);
        classLoaders.add(classLoader);

        if (Thread.currentThread().getContextClassLoader() != null) {
            classLoaders.add(Thread.currentThread().getContextClassLoader());
        }

        classLoaders.add(Serializers.class.getClassLoader());

        return classLoaders;
    }

    private static <T> T doGetSerializerClass(final Class<T> clazz, final ClassLoader classLoader) throws NoSuchMethodException {
        try {
            @SuppressWarnings("unchecked")
            Class<T> implementation = (Class<T>) classLoader.loadClass(clazz.getName() + "Impl");
            Constructor<T> constructor = implementation.getDeclaredConstructor();
            constructor.setAccessible(true);

            return constructor.newInstance();
        } catch (ClassNotFoundException e) {
            return getMapperFromServiceLoader(clazz, classLoader);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T getMapperFromServiceLoader(final Class<T> clazz, final ClassLoader classLoader) {
        ServiceLoader<T> loader = ServiceLoader.load(clazz, classLoader);

        for (T mapper : loader) {
            if (mapper != null) {
                return mapper;
            }
        }

        return null;
    }

    private static <T> T getSerializer(final Class<T> clazz, final List<ClassLoader> classLoaders) throws ClassNotFoundException, NoSuchMethodException {
        for (ClassLoader classLoader : classLoaders) {
            T serializer = doGetSerializerClass(clazz, classLoader);
            if (serializer != null) {
                return serializer;
            }
        }

        throw new ClassNotFoundException("Cannot find implementation for " + clazz.getName());
    }
}
