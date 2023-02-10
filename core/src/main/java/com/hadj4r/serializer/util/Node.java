package com.hadj4r.serializer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public class Node {
    private final String type;
    private String fieldName;
    private final String fieldNameAsGetter;
    private final List<Node> children = new ArrayList<>();

    public Node(final TypeMirror type, final Element fieldElement, final Node parent) {
        this.type = type.toString();
        this.fieldName = fieldElement.toString();
        if (parent != null) {
            final String getterName = switch (this.type) {
                case "boolean" -> ".is";
                default -> ".get";
            };
            this.fieldNameAsGetter = parent.fieldNameAsGetter + getterName + this.fieldName.substring(0, 1).toUpperCase() + this.fieldName.substring(1) + "()";
        } else {
            this.fieldNameAsGetter = "model";
        }
    }

    public String getType() {
        return type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldNameAsGetter() {
        return fieldNameAsGetter;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(final Node child) {
        children.add(child);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Node node = (Node) o;
        return fieldNameAsGetter.equals(node.fieldNameAsGetter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldNameAsGetter);
    }
}
