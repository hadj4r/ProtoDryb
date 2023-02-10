package com.hadj4r.serializer.model;

public class Duplicate {
    private final int duplicateIntVar;
    private final int[] duplicateIntArrayVar;
    private final boolean duplicateBooleanVar;

    public Duplicate(final int duplicateIntVar, final int[] duplicateIntArrayVar, final boolean duplicateBooleanVar) {
        this.duplicateIntVar = duplicateIntVar;
        this.duplicateIntArrayVar = duplicateIntArrayVar;
        this.duplicateBooleanVar = duplicateBooleanVar;
    }

    public int getDuplicateIntVar() {
        return duplicateIntVar;
    }

    public int[] getDuplicateIntArrayVar() {
        return duplicateIntArrayVar;
    }

    public boolean isDuplicateBooleanVar() {
        return duplicateBooleanVar;
    }
}
