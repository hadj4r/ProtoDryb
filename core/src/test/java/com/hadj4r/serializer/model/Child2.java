package com.hadj4r.serializer.model;

public class Child2 {
    private final Duplicate duplicate1;
    private final Grandchild2 grandchild2;
    private final Duplicate duplicate2;
    private final Shared shared2;

    public Child2(final Duplicate duplicate1, final Grandchild2 grandchild2, final Duplicate duplicate2, final Shared shared2) {
        this.duplicate1 = duplicate1;
        this.grandchild2 = grandchild2;
        this.duplicate2 = duplicate2;
        this.shared2 = shared2;
    }

    public Duplicate getDuplicate1() {
        return duplicate1;
    }

    public Grandchild2 getGrandchild2() {
        return grandchild2;
    }

    public Duplicate getDuplicate2() {
        return duplicate2;
    }

    public Shared getShared2() {
        return shared2;
    }
}
