package com.hadj4r.protodryb.utils;

import java.util.Arrays;

public final class PrimitiveConverter {
    // for caching purposes
    private static final byte[] $1_BYTE_ARRAY = new byte[1];
    private static final byte[] $2_BYTE_ARRAY = new byte[2];
    private static final byte[] $4_BYTE_ARRAY = new byte[4];
    private static final byte[] $8_BYTE_ARRAY = new byte[8];

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

    public static byte getByteSize(final Class<?> clazz) {
        if (clazz == boolean.class) {
            return 1;
        } else if (clazz == char.class) {
            return 2;
        } else if (clazz == byte.class) {
            return 1;
        } else if (clazz == short.class) {
            return 2;
        } else if (clazz == int.class) {
            return 4;
        } else if (clazz == long.class) {
            return 8;
        } else if (clazz == float.class) {
            return 4;
        } else if (clazz == double.class) {
            return 8;
        }
        throw new IllegalArgumentException("Unknown primitive type: " + clazz); // TODO: add this later
    }

    public static void toBytes(final Class<?> clazz, final Object fieldValue, final byte[] bytes, final int offset) {
        if (clazz == boolean.class) {
            booleanToBytes((boolean) fieldValue, bytes, offset);
        } else if (clazz == char.class) {
            charToBytes((char) fieldValue, bytes, offset);
        } else if (clazz == byte.class) {
            byteToBytes((byte) fieldValue, bytes, offset);
        } else if (clazz == short.class) {
            shortToBytes((short) fieldValue, bytes, offset);
        } else if (clazz == int.class) {
            intToBytes((int) fieldValue, bytes, offset);
        } else if (clazz == long.class) {
            longToBytes((long) fieldValue, bytes, offset);
        } else if (clazz == float.class) {
            floatToBytes((float) fieldValue, bytes, offset);
        } else if (clazz == double.class) {
            doubleToBytes((double) fieldValue, bytes, offset);
        } else {
            throw new IllegalArgumentException("Unknown primitive type: " + clazz); // TODO: add this later
        }
    }

    private static void booleanToBytes(final boolean value, final byte[] fieldBytes, final int offset) {
        fieldBytes[offset] = (byte) (value ? 1 : 0);
    }

    private static void charToBytes(final char value, final byte[] bytes, final int offset) {
        bytes[offset] = (byte) value;
        bytes[offset + 1] = (byte) (value >> 8);
    }

    private static void byteToBytes(final byte value, final byte[] bytes, final int offset) {
        bytes[offset] = value;
    }

    private static void shortToBytes(final short value, final byte[] bytes, final int offset) {
        bytes[offset] = (byte) value;
        bytes[offset + 1] = (byte) (value >> 8);
    }

    private static void intToBytes(final int value, final byte[] bytes, final int offset) {
        bytes[offset] = (byte) value;
        bytes[offset + 1] = (byte) (value >> 8);
        bytes[offset + 2] = (byte) (value >> 16);
        bytes[offset + 3] = (byte) (value >> 24);
    }

    private static void longToBytes(long value, final byte[] bytes, final int offset) {
        bytes[offset] = (byte) value;
        bytes[offset + 1] = (byte) (value >> 8);
        bytes[offset + 2] = (byte) (value >> 16);
        bytes[offset + 3] = (byte) (value >> 24);
        bytes[offset + 4] = (byte) (value >> 32);
        bytes[offset + 5] = (byte) (value >> 40);
        bytes[offset + 6] = (byte) (value >> 48);
        bytes[offset + 7] = (byte) (value >> 56);
    }

    private static void floatToBytes(final float value, final byte[] bytes, final int offset) {
        intToBytes(Float.floatToIntBits(value), bytes, offset);
    }

    private static void doubleToBytes(final double value, final byte[] bytes, final int offset) {
        longToBytes(Double.doubleToLongBits(value), bytes, offset);
    }

    public static Object fromBytes(final Class<?> clazz, final byte[] allBytes, final int offset) {
        if (clazz == boolean.class) {
            return bytesToBoolean(allBytes, offset);
        } else if (clazz == char.class) {
            return bytesToChar(allBytes, offset);
        } else if (clazz == byte.class) {
            return bytesToByte(allBytes, offset);
        } else if (clazz == short.class) {
            return bytesToShort(allBytes, offset);
        } else if (clazz == int.class) {
            return bytesToInt(allBytes, offset);
        } else if (clazz == long.class) {
            return bytesToLong(allBytes, offset);
        } else if (clazz == float.class) {
            return bytesToFloat(allBytes, offset);
        } else if (clazz == double.class) {
            return bytesToDouble(allBytes, offset);
        }
        throw new IllegalArgumentException("Unknown primitive type: " + clazz); // TODO: add this later
    }

    private static boolean bytesToBoolean(final byte[] allBytes, final int offset) {
        return allBytes[offset] == 1;
    }

    private static char bytesToChar(final byte[] allBytes, final int offset) {
        return (char) ((allBytes[offset + 1] << 8) | (allBytes[offset] & 0xff));
    }

    private static byte bytesToByte(final byte[] allBytes, final int offset) {
        return allBytes[offset];
    }

    private static short bytesToShort(final byte[] allBytes, final int offset) {
        return (short) ((allBytes[offset + 1] << 8) | (allBytes[offset] & 0xff));
    }

    private static int bytesToInt(final byte[] allBytes, final int offset) {
        return fourBytesToInt(allBytes, offset);
    }

    private static long bytesToLong(final byte[] allBytes, final int offset) {
        return fourBytesToLong(allBytes, offset);
    }

    private static float bytesToFloat(final byte[] allBytes, final int offset) {
        return Float.intBitsToFloat(fourBytesToInt(allBytes, offset));
    }

    private static double bytesToDouble(final byte[] allBytes, final int offset) {
        return Double.longBitsToDouble(fourBytesToLong(allBytes, offset));
    }

    private static int fourBytesToInt(final byte[] allBytes, final int offset) {
        return (allBytes[offset + 3] << 24) | ((allBytes[offset + 2] & 0xff) << 16) | ((allBytes[offset + 1] & 0xff) << 8) | (allBytes[offset] & 0xff);
    }

    private static long fourBytesToLong(final byte[] allBytes, final int offset) {
        return ((long) allBytes[offset + 7] << 56) | ((long) (allBytes[offset + 6] & 0xff) << 48) | ((long) (allBytes[offset + 5] & 0xff) << 40) | ((long) (allBytes[offset + 4] & 0xff) << 32) | ((long) (allBytes[offset + 3] & 0xff) << 24) | ((allBytes[offset + 2] & 0xff) << 16) | ((allBytes[offset + 1] & 0xff) << 8) | (allBytes[offset] & 0xff);
    }
}
