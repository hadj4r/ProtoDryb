package com.hadj4r.serializer.model;

public class Grandchild2 {
    private final String grandChild2StringVar;
    private final Shared shared1;

    public Grandchild2(final String GrandChild2StringVar, final Shared shared1) {
        this.grandChild2StringVar = GrandChild2StringVar;
        this.shared1 = shared1;
    }

    public String getGrandChild2StringVar() {
        return grandChild2StringVar;
    }

    public Shared getShared1() {
        return shared1;
    }
}
