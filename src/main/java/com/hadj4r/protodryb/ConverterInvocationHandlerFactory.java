package com.hadj4r.protodryb;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ConverterInvocationHandlerFactory {
    private final Comparator<? super Field> comparatorByOrder = createComparatorByOrder();

    public ConverterInvocationHandler create(final Class<? extends ByteConverter> converterInterface) {
        final Class<?> modelClass = getModelClass(converterInterface);
        final Set<Field> fields = getFieldsSortedByOrder(modelClass);

        return new ConverterInvocationHandler(modelClass, fields);
    }

    private Class<?> getModelClass(final Class<? extends ByteConverter> converterInterface) {
        ParameterizedType genericInterface = (ParameterizedType) converterInterface.getGenericInterfaces()[0];
        return (Class<?>) genericInterface.getActualTypeArguments()[0];
    }

    private Set<Field> getFieldsSortedByOrder(final Class<?> modelClass) {
        final List<Field> annotatedFields = Arrays.stream(modelClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(PrimitiveField.class))       // TODO: temporary, add more generic annotation
                .collect(Collectors.toList());

        validateFields(annotatedFields);

        return Arrays.stream(modelClass.getDeclaredFields())
                .filter(f -> f.getAnnotation(PrimitiveField.class).required())  // TODO: temporary, move logic to comparator or extra set
                .filter(f -> !Collection.class.isAssignableFrom(f.getType()))   // TODO: temporary
                .collect(Collectors.toCollection(() -> new TreeSet<>(comparatorByOrder)));
    }

    private void validateFields(final List<Field> annotatedFields) {
        final int initialSize = annotatedFields.size();
        annotatedFields.sort(comparatorByOrder);
        final int finalSize = annotatedFields.size();

        if (initialSize != finalSize) {
            throw new IllegalArgumentException("Fields with same order value");
        }

        int lastOrder = 0;
        for (final Field field : annotatedFields) {
            final int order = field.getAnnotation(PrimitiveField.class).order();
            field.setAccessible(true);  // TODO: unsafe & think where to put it
            if (order != ++lastOrder) {
                throw new IllegalArgumentException("Field " + field.getName() + " has invalid order");
            }
        }
    }

    private Comparator<? super Field> createComparatorByOrder() {
        return (f1, f2) -> {
            final int f1Order = f1.getAnnotation(PrimitiveField.class).order();
            final int f2Order = f2.getAnnotation(PrimitiveField.class).order();
            return f1Order - f2Order;
        };
    }
}
