package com.hadj4r.protodryb;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConverterInvocationHandler implements InvocationHandler {
    private final Class<?> modelClass;
    private final Set<Field> fields;
    @Override
    public Object invoke(final Object o, final Method method, final Object[] objects) throws Throwable {
        // if method is encode then return encode method
        if (method.getName().equals("encode")) {
            return encode(objects[0]);
        }

        // if method is decode then return decode method
        if (method.getName().equals("decode")) {
            return decode((byte[])objects[0]);
        }

        throw new IllegalArgumentException("Method " + method.getName() + " is not declared in ByteConverter");
    }

    private Object encode(final Object object) throws IllegalAccessException {
        final int byteSize = calculateByteSize();   // TODO: not a const field bcs will differ based on non required fields/array fields
        final byte[] bytes = new byte[byteSize];
        int offset = 0;
        for (final Field field : fields) {
            field.setAccessible(true);
            final int fieldByteSize = javaTypeToByteSize(field.getType());
            final byte[] fieldBytes = javaTypeToBytes(field.getType(), field.get(object));  // TODO: dont recreate fieldBytes array
            System.arraycopy(fieldBytes, 0, bytes, offset, fieldByteSize);
            offset += fieldByteSize;
        }

        return bytes;
    }

    private Object decode(final byte[] bytes) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final Object newInstance = modelClass.getConstructor().newInstance();

        int offset = 0;
        for (final Field field : fields) {
            final int fieldByteSize = javaTypeToByteSize(field.getType());
            final byte[] fieldBytes = new byte[fieldByteSize];
            System.arraycopy(bytes, offset, fieldBytes, 0, fieldByteSize);
            // set field value of newInstance to fieldBytes
            field.set(newInstance, bytesToJavaType(field.getType(), fieldBytes));
            offset += fieldByteSize;
        }
        return newInstance;
    }

    private byte[] javaTypeToBytes(final Class<?> type, final Object value) {
        if (type == int.class) {
            return intToBytes((int) value);
        }
        throw new IllegalArgumentException("Unsupported type " + type.getName());
    }

    private Object bytesToJavaType(final Class<?> type, final byte[] bytes) {
        if (type == int.class) {
            return bytesToInt(bytes);
        }
        throw new IllegalArgumentException("Unsupported type " + type.getName());
    }

    private byte[] intToBytes(final int value) {
        return new byte[] {
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }

    private int bytesToInt(final byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    private int calculateByteSize() {
        return fields.stream()
                .map(Field::getType)
                .mapToInt(this::javaTypeToByteSize)
                .sum();
    }

    private int javaTypeToByteSize(final Class<?> typeClass) {
        if (typeClass.equals(int.class)) {
            return 4;
        }
        throw new IllegalArgumentException("Unsupported type " + typeClass.getName());

    }
}
