package com.hadj4r.model;


import com.dslplatform.json.CompiledJson;
import static com.dslplatform.json.CompiledJson.Behavior.FAIL;
import static com.dslplatform.json.CompiledJson.Format.ARRAY;
import static com.dslplatform.json.CompiledJson.Format.OBJECT;

public class ImmutableTestClass {
    private final boolean booleanVar;
    // private final char charVar;
    // private final byte byteVar;
    private final short shortVar;
    private final int intVar;
    private final long longVar;
    private final float floatVar;
    private final double doubleVar;
    private final String stringVar;
    private final String stringVar2;

    // @CompiledJson(formats = {ARRAY, OBJECT}, onUnknown = FAIL)
    @CompiledJson(formats = {OBJECT}, onUnknown = FAIL)
    // public ImmutableTestClass(boolean booleanVar, char charVar, byte byteVar, short shortVar, int intVar, long longVar,
    //                           float floatVar, double doubleVar, String stringVar, final String stringVar2) {
    public ImmutableTestClass(boolean booleanVar, short shortVar, int intVar, long longVar,
                              float floatVar, double doubleVar, String stringVar, final String stringVar2) {
            this.booleanVar = booleanVar;
        // this.charVar = charVar;
        // this.byteVar = byteVar;
        this.shortVar = shortVar;
        this.intVar = intVar;
        this.longVar = longVar;
        this.floatVar = floatVar;
        this.doubleVar = doubleVar;
        this.stringVar = stringVar;
        this.stringVar2 = stringVar2;
    }

    public boolean isBooleanVar() {
        return booleanVar;
    }

    // public char getCharVar() {
    //     return charVar;
    // }
    //
    // public byte getByteVar() {
    //     return byteVar;
    // }

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
}
