package com.hadj4r.model;

public class Child {
    private final boolean childBooleanVar1;
    private final Grandchild grandchild;
    private final boolean childBooleanVar2;

    public Child(final boolean childBooleanVar1, Grandchild grandchild, final boolean childBooleanVar2) {
        this.childBooleanVar1 = childBooleanVar1;
        this.grandchild = grandchild;
        this.childBooleanVar2 = childBooleanVar2;
    }

    public boolean isChildBooleanVar1() {
        return childBooleanVar1;
    }

    public Grandchild getGrandchild() {
        return grandchild;
    }

    public boolean isChildBooleanVar2() {
        return childBooleanVar2;
    }
}
