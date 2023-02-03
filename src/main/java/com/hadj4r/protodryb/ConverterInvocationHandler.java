package com.hadj4r.protodryb;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.hadj4r.protodryb.utils.PrimitiveConverter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;
import static com.hadj4r.protodryb.utils.PrimitiveConverter.getByteSize;

public class ConverterInvocationHandler implements InvocationHandler {
    private final MethodAccess methodAccess;
    private final ConstructorAccess<?> constructorAccess;
    private final Class<?>[] fieldTypes;
    private final byte[] byteSizes;
    private final short byteSizesSum;
    private final short byteSizeMax;
    private final int[] getterMethodIndices;
    private final int[] setterMethodIndices;

    public ConverterInvocationHandler(final Class<?> modelClass, final Set<Field> fields) {
        // calculate byte sizes
        int i = 0;
        short sum = 0;
        this.fieldTypes = new Class<?>[fields.size()];
        short currentByteMax = 0;
        this.byteSizes = new byte[fields.size()];
        for (final Field field : fields) {
            fieldTypes[i] = field.getType();
            byteSizes[i] = getByteSize(field.getType());
            sum += byteSizes[i];
            if (byteSizes[i] > currentByteMax) {
                currentByteMax = byteSizes[i];
            }
            ++i;
        }
        this.byteSizesSum = sum;
        this.byteSizeMax = currentByteMax;

        this.constructorAccess = ConstructorAccess.get(modelClass);
        this.methodAccess = MethodAccess.get(modelClass);

        // fill method indices
        this.getterMethodIndices = new int[fields.size()];
        this.setterMethodIndices = new int[fields.size()];
        i = 0;
        for (Field field : fields) {
            final String name = field.getName();
            // puts the index of the getter method
            getterMethodIndices[i] = methodAccess.getIndex("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
            // puts the index of the setter method
            setterMethodIndices[i] = methodAccess.getIndex("set" + name.substring(0, 1).toUpperCase() + name.substring(1));
            ++i;
        }
    }

    @Override
    public Object invoke(final Object o, final Method method, final Object[] objects) {
        return switch (method.getName()) {
            case "encode" -> encode(objects[0]);
            case "decode" -> decode((byte[]) objects[0]);
            default -> throw new UnsupportedOperationException("Method " + method.getName() + " is not supported");
        };
    }

    private Object encode(final Object object) {
        final int totalByteSize = calculateByteSize();   // TODO: not a const field bcs will differ based on non required fields/array fields
        final byte[] bytes = new byte[totalByteSize];
        int offset = 0;
        final byte[] fieldBytes = new byte[byteSizeMax]; // for caching purpose
        for (int i = 0; i < getterMethodIndices.length; ++i) {
            final int fieldByteSize = byteSizes[i];
            final Class<?> fieldType = fieldTypes[i];
            final Object fieldValue = methodAccess.invoke(object, getterMethodIndices[i]);
            PrimitiveConverter.toBytes(fieldType, fieldValue, fieldBytes);
            System.arraycopy(fieldBytes, 0, bytes, offset, fieldByteSize);
            offset += fieldByteSize;
        }

        return bytes;
    }

    private Object decode(final byte[] bytes) {
        final Object newInstance = constructorAccess.newInstance();
        int offset = 0;
        Object fieldValue;
        for (int i = 0; i < setterMethodIndices.length; ++i) {
            final int fieldByteSize = byteSizes[i];
            final Class<?> fieldType = fieldTypes[i];
            fieldValue = PrimitiveConverter.fromBytes(fieldType, bytes, offset);
            methodAccess.invoke(newInstance, setterMethodIndices[i], fieldValue);
            offset += fieldByteSize;
        }

        return newInstance;
    }

    private int calculateByteSize() {
        return byteSizesSum; // TODO: this is temporal until all fields are required
    }
}
