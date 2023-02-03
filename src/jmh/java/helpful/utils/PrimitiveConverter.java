package helpful.utils;

public final class PrimitiveConverter {
    private PrimitiveConverter() {
    }

    public static Class<?> getWrapperClass(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == boolean.class) {
                return Boolean.class;
            } else if (clazz == char.class) {
                return Character.class;
            } else if (clazz == byte.class) {
                return Byte.class;
            } else if (clazz == short.class) {
                return Short.class;
            } else if (clazz == int.class) {
                return Integer.class;
            } else if (clazz == long.class) {
                return Long.class;
            } else if (clazz == float.class) {
                return Float.class;
            } else if (clazz == double.class) {
                return Double.class;
            }
        }
        return clazz;
    }
}
