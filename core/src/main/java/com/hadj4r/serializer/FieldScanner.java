package com.hadj4r.serializer;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementScanner8;

public class FieldScanner extends ElementScanner8<Void, Void> {
    @Override
    public Void visitVariable(VariableElement e, Void p) {
        System.out.println("Field: " + e.asType() + " " + e.getSimpleName());
        return super.visitVariable(e, p);
    }

    @Override
    public Void visitType(TypeElement e, Void p) {
        scan(e.getEnclosedElements(), p);
        return null;
    }
}
