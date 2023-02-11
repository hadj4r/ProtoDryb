package com.hadj4r.model;

public class Grandchild {
    private final int grandChildIntVar;
    private final Shared shared1;

    public Grandchild(int grandChildIntVar, final Shared shared1) {
        this.grandChildIntVar = grandChildIntVar;
        this.shared1 = shared1;
    }

    public int getGrandChildIntVar() {
        return grandChildIntVar;
    }

    public Shared getShared1() {
        return shared1;
    }
}
