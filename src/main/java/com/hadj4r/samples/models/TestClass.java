package com.hadj4r.samples.models;

import com.hadj4r.protodryb.Convertable;
import com.hadj4r.protodryb.PrimitiveField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Convertable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TestClass {
    @PrimitiveField(order = 1)
    private boolean booleanValue;
    @PrimitiveField(order = 2)
    private char charValue;
    @PrimitiveField(order = 3)
    private byte byteValue;
    @PrimitiveField(order = 4)
    private short shortValue;
    @PrimitiveField(order = 5)
    private int intValue;
    @PrimitiveField(order = 6)
    private long longValue;
    @PrimitiveField(order = 7)
    private float floatValue;
    @PrimitiveField(order = 8)
    private double doubleValue;
}

