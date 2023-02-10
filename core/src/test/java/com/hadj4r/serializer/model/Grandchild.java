package com.hadj4r.serializer.model;

public class Grandchild {
    private final byte grandChildByteVar;
    private final Shared shared1;

    public Grandchild(byte grandChildByteVar, final Shared shared1) {
        this.grandChildByteVar = grandChildByteVar;
        this.shared1 = shared1;
    }

    public byte getGrandChildByteVar() {
        return grandChildByteVar;
    }

    public Shared getShared1() {
        return shared1;
    }
}
