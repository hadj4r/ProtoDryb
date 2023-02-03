package com.hadj4r.model;


import com.dslplatform.json.CompiledJson;
import static com.dslplatform.json.CompiledJson.Behavior.FAIL;
import static com.dslplatform.json.CompiledJson.Format.OBJECT;

public class ImmutableTestClass {
    private final boolean booleanVar;
    private final short shortVar;
    private final int intVar;
    private final long longVar;
    private final float floatVar;
    private final double doubleVar;
    private final String stringVar;
    private final String stringVar2;
    private final boolean[] booleanArrayVar;
    private final short[] shortArrayVar;
    private final int[] intArrayVar;
    private final long[] longArrayVar;
    private final float[] floatArrayVar;
    private final double[] doubleArrayVar;

    @CompiledJson(onUnknown = FAIL, formats = OBJECT)
    public ImmutableTestClass(final boolean booleanVar, final short shortVar,
                              final int intVar, final long longVar, final float floatVar, final double doubleVar,
                              final String stringVar, final String stringVar2, final boolean[] booleanArrayVar,
                              final short[] shortArrayVar, final int[] intArrayVar, final long[] longArrayVar,
                              final float[] floatArrayVar, final double[] doubleArrayVar) {
        this.booleanVar = booleanVar;
        this.shortVar = shortVar;
        this.intVar = intVar;
        this.longVar = longVar;
        this.floatVar = floatVar;
        this.doubleVar = doubleVar;
        this.stringVar = stringVar;
        this.stringVar2 = stringVar2;
        this.booleanArrayVar = booleanArrayVar;
        this.shortArrayVar = shortArrayVar;
        this.intArrayVar = intArrayVar;
        this.longArrayVar = longArrayVar;
        this.floatArrayVar = floatArrayVar;
        this.doubleArrayVar = doubleArrayVar;
    }

    public boolean isBooleanVar() {
        return booleanVar;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean booleanVar;
        private short shortVar;
        private int intVar;
        private long longVar;
        private float floatVar;
        private double doubleVar;
        private String stringVar;
        private String stringVar2;
        private boolean[] booleanArrayVar;
        private short[] shortArrayVar;
        private int[] intArrayVar;
        private long[] longArrayVar;
        private float[] floatArrayVar;
        private double[] doubleArrayVar;

        public Builder setBooleanVar(boolean booleanVar) {
            this.booleanVar = booleanVar;
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

        public ImmutableTestClass build() {
            return new ImmutableTestClass(booleanVar, shortVar, intVar, longVar, floatVar, doubleVar,
                    stringVar, stringVar2, booleanArrayVar, shortArrayVar, intArrayVar,
                    longArrayVar, floatArrayVar, doubleArrayVar);
        }
    }
}
