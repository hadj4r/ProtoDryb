package com.hadj4r.serializer.model;


public class ImmutableTestClass {
    private final boolean booleanVar;
    private final char charVar;
    private final byte byteVar;
    private final short shortVar;
    private final int intVar;
    private final long longVar;
    private final float floatVar;
    private final double doubleVar;
    private final String stringVar;
    private final String stringVar2;
    private final boolean[] booleanArrayVar;
    private final char[] charArrayVar;
    private final byte[] byteArrayVar;
    private final short[] shortArrayVar;
    private final int[] intArrayVar;
    private final long[] longArrayVar;
    private final float[] floatArrayVar;
    private final double[] doubleArrayVar;
    private final Child childVar;
    private final Child2 child2Var;

    public ImmutableTestClass(final boolean booleanVar, final char charVar, final byte byteVar, final short shortVar,
                              final int intVar, final long longVar, final float floatVar, final double doubleVar,
                              final String stringVar, final String stringVar2, final boolean[] booleanArrayVar,
                              final char[] charArrayVar, final byte[] byteArrayVar, final short[] shortArrayVar,
                              final int[] intArrayVar, final long[] longArrayVar, final float[] floatArrayVar,
                              final double[] doubleArrayVar, final Child childVar, final Child2 child2Var) {
        this.booleanVar = booleanVar;
        this.charVar = charVar;
        this.byteVar = byteVar;
        this.shortVar = shortVar;
        this.intVar = intVar;
        this.longVar = longVar;
        this.floatVar = floatVar;
        this.doubleVar = doubleVar;
        this.stringVar = stringVar;
        this.stringVar2 = stringVar2;
        this.booleanArrayVar = booleanArrayVar;
        this.charArrayVar = charArrayVar;
        this.byteArrayVar = byteArrayVar;
        this.shortArrayVar = shortArrayVar;
        this.intArrayVar = intArrayVar;
        this.longArrayVar = longArrayVar;
        this.floatArrayVar = floatArrayVar;
        this.doubleArrayVar = doubleArrayVar;
        this.childVar = childVar;
        this.child2Var = child2Var;
    }

    public boolean isBooleanVar() {
        return booleanVar;
    }

    public char getCharVar() {
        return charVar;
    }

    public byte getByteVar() {
        return byteVar;
    }

    public short getShortVar() {
        return shortVar;
    }

    public int getIntVar() {
        return intVar;
    }

    public long getLongVar() {
        return longVar;
    }

    public float getFloatVar() {
        return floatVar;
    }

    public double getDoubleVar() {
        return doubleVar;
    }

    public String getStringVar() {
        return stringVar;
    }

    public String getStringVar2() {
        return stringVar2;
    }

    public boolean[] getBooleanArrayVar() {
        return booleanArrayVar;
    }

    public char[] getCharArrayVar() {
        return charArrayVar;
    }

    public byte[] getByteArrayVar() {
        return byteArrayVar;
    }

    public short[] getShortArrayVar() {
        return shortArrayVar;
    }

    public int[] getIntArrayVar() {
        return intArrayVar;
    }

    public long[] getLongArrayVar() {
        return longArrayVar;
    }

    public float[] getFloatArrayVar() {
        return floatArrayVar;
    }

    public double[] getDoubleArrayVar() {
        return doubleArrayVar;
    }

    public Child getChildVar() {
        return childVar;
    }

    public Child2 getChild2Var() {
        return child2Var;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean booleanVar;
        private char charVar;
        private byte byteVar;
        private short shortVar;
        private int intVar;
        private long longVar;
        private float floatVar;
        private double doubleVar;
        private String stringVar;
        private String stringVar2;
        private boolean[] booleanArrayVar;
        private char[] charArrayVar;
        private byte[] byteArrayVar;
        private short[] shortArrayVar;
        private int[] intArrayVar;
        private long[] longArrayVar;
        private float[] floatArrayVar;
        private double[] doubleArrayVar;
        private Child childVar;
        private Child2 child2Var;

        public Builder() {
        }

        public Builder setBooleanVar(boolean booleanVar) {
            this.booleanVar = booleanVar;
            return this;
        }

        public Builder setCharVar(char charVar) {
            this.charVar = charVar;
            return this;
        }

        public Builder setByteVar(byte byteVar) {
            this.byteVar = byteVar;
            return this;
        }

        public Builder setShortVar(short shortVar) {
            this.shortVar = shortVar;
            return this;
        }

        public Builder setIntVar(int intVar) {
            this.intVar = intVar;
            return this;
        }

        public Builder setLongVar(long longVar) {
            this.longVar = longVar;
            return this;
        }

        public Builder setFloatVar(float floatVar) {
            this.floatVar = floatVar;
            return this;
        }

        public Builder setDoubleVar(double doubleVar) {
            this.doubleVar = doubleVar;
            return this;
        }

        public Builder setStringVar(String stringVar) {
            this.stringVar = stringVar;
            return this;
        }

        public Builder setStringVar2(String stringVar2) {
            this.stringVar2 = stringVar2;
            return this;
        }

        public Builder setBooleanArrayVar(boolean[] booleanArrayVar) {
            this.booleanArrayVar = booleanArrayVar;
            return this;
        }

        public Builder setCharArrayVar(char[] charArrayVar) {
            this.charArrayVar = charArrayVar;
            return this;
        }

        public Builder setByteArrayVar(byte[] byteArrayVar) {
            this.byteArrayVar = byteArrayVar;
            return this;
        }

        public Builder setShortArrayVar(short[] shortArrayVar) {
            this.shortArrayVar = shortArrayVar;
            return this;
        }

        public Builder setIntArrayVar(int[] intArrayVar) {
            this.intArrayVar = intArrayVar;
            return this;
        }

        public Builder setLongArrayVar(long[] longArrayVar) {
            this.longArrayVar = longArrayVar;
            return this;
        }

        public Builder setFloatArrayVar(float[] floatArrayVar) {
            this.floatArrayVar = floatArrayVar;
            return this;
        }

        public Builder setDoubleArrayVar(double[] doubleArrayVar) {
            this.doubleArrayVar = doubleArrayVar;
            return this;
        }

        public Builder setChildVar(Child childVar) {
            this.childVar = childVar;
            return this;
        }

        public Builder setChild2Var(Child2 child2Var) {
            this.child2Var = child2Var;
            return this;
        }

        public ImmutableTestClass build() {
            return new ImmutableTestClass(booleanVar, charVar, byteVar, shortVar, intVar, longVar, floatVar, doubleVar,
                    stringVar, stringVar2, booleanArrayVar, charArrayVar, byteArrayVar, shortArrayVar, intArrayVar,
                    longArrayVar, floatArrayVar, doubleArrayVar, childVar, child2Var);
        }
    }
}
