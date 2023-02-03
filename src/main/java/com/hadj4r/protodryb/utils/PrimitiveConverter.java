package com.hadj4r.protodryb.utils;

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

    public static void toBytes(final Class<?> clazz, final Object fieldValue, final byte[] fieldBytes) {
        final byte[] bytes;
        if (clazz == boolean.class) {
            bytes = booleanToBytes((boolean) fieldValue);
        } else if (clazz == char.class) {
            bytes = charToBytes((char) fieldValue);
        } else if (clazz == byte.class) {
            bytes = byteToBytes((byte) fieldValue);
        } else if (clazz == short.class) {
            bytes = shortToBytes((short) fieldValue);
        } else if (clazz == int.class) {
            bytes = intToBytes((int) fieldValue);
        } else if (clazz == long.class) {
            bytes = longToBytes((long) fieldValue);
        } else if (clazz == float.class) {
            bytes = floatToBytes((float) fieldValue);
        } else if (clazz == double.class) {
            bytes = doubleToBytes((double) fieldValue);
        } else {
            throw new IllegalArgumentException("Unknown primitive type: " + clazz); // TODO: add this later
        }
        System.arraycopy(bytes, 0, fieldBytes, 0, bytes.length);
    }

    private static byte[] booleanToBytes(final boolean value) {
        $1_BYTE_ARRAY[0] = (byte) (value ? 1 : 0);
        return $1_BYTE_ARRAY;
    }

    private static byte[] charToBytes(final char value) {
        $2_BYTE_ARRAY[0] = (byte) (value >> 8);
        $2_BYTE_ARRAY[1] = (byte) value;
        return $2_BYTE_ARRAY;
    }

    private static byte[] byteToBytes(final byte value) {
        $1_BYTE_ARRAY[0] = value;
        return $1_BYTE_ARRAY;
    }

    private static byte[] shortToBytes(final short value) {
        $2_BYTE_ARRAY[0] = (byte) (value >> 8);
        $2_BYTE_ARRAY[1] = (byte) value;
        return $2_BYTE_ARRAY;
    }

    private static byte[] intToBytes(final int value) {
        $4_BYTE_ARRAY[0] = (byte) (value >> 24);
        $4_BYTE_ARRAY[1] = (byte) (value >> 16);
        $4_BYTE_ARRAY[2] = (byte) (value >> 8);
        $4_BYTE_ARRAY[3] = (byte) value;
        return $4_BYTE_ARRAY;
    }

    private static byte[] longToBytes(long value) {
        return set8BytesArray(value);
    }

    private static byte[] floatToBytes(final float value) {
        final int data = Float.floatToIntBits(value);
        return set4BytesArray(data);
    }

    private static byte[] doubleToBytes(final double value) {
        final long data = Double.doubleToLongBits(value);
        return set8BytesArray(data);
    }

    private static byte[] set4BytesArray(final int data) {
        $4_BYTE_ARRAY[0] = (byte) (data >> 24);
        $4_BYTE_ARRAY[1] = (byte) (data >> 16);
        $4_BYTE_ARRAY[2] = (byte) (data >> 8);
        $4_BYTE_ARRAY[3] = (byte) data;
        return $4_BYTE_ARRAY;
    }

    private static byte[] set8BytesArray(final long data) {
        $8_BYTE_ARRAY[0] = (byte) (data >> 56);
        $8_BYTE_ARRAY[1] = (byte) (data >> 48);
        $8_BYTE_ARRAY[2] = (byte) (data >> 40);
        $8_BYTE_ARRAY[3] = (byte) (data >> 32);
        $8_BYTE_ARRAY[4] = (byte) (data >> 24);
        $8_BYTE_ARRAY[5] = (byte) (data >> 16);
        $8_BYTE_ARRAY[6] = (byte) (data >> 8);
        $8_BYTE_ARRAY[7] = (byte) data;
        return $8_BYTE_ARRAY;
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

    private static Object bytesToBoolean(final byte[] allBytes, final int offset) {
        return allBytes[offset] == 1;
    }

    private static Object bytesToChar(final byte[] allBytes, final int offset) {
        return (char) ((allBytes[offset] << 8) + (allBytes[offset + 1] & 0xff));
    }

    private static Object bytesToByte(final byte[] allBytes, final int offset) {
        return allBytes[offset];
    }

    private static Object bytesToShort(final byte[] allBytes, final int offset) {
        return (short) ((allBytes[offset] << 8) + (allBytes[offset + 1] & 0xff));
    }

    private static Object bytesToInt(final byte[] allBytes, final int offset) {
        return fourBytesToInt(allBytes, offset);
    }

    private static Object bytesToLong(final byte[] allBytes, final int offset) {
        return eightBytesToLong(allBytes, offset);
    }

    private static Object bytesToFloat(final byte[] allBytes, final int offset) {
        return Float.intBitsToFloat(fourBytesToInt(allBytes, offset));
    }

    private static Object bytesToDouble(final byte[] allBytes, final int offset) {
        return Double.longBitsToDouble(eightBytesToLong(allBytes, offset));
    }

    private static int fourBytesToInt(final byte[] allBytes, final int offset) {
        return (allBytes[offset] << 24) + ((allBytes[offset + 1] & 0xff) << 16) + ((allBytes[offset + 2] & 0xff) << 8) + (allBytes[offset + 3] & 0xff);
    }

    private static long eightBytesToLong(final byte[] allBytes, final int offset) {
        return ((long) allBytes[offset] << 56) + ((long) (allBytes[offset + 1] & 0xff) << 48) + ((long) (allBytes[offset + 2] & 0xff) << 40) + ((long) (allBytes[offset + 3] & 0xff) << 32) + ((long) (allBytes[offset + 4] & 0xff) << 24) + ((allBytes[offset + 5] & 0xff) << 16) + ((allBytes[offset + 6] & 0xff) << 8) + (allBytes[offset + 7] & 0xff);
    }
}
