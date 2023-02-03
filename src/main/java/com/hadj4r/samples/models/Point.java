package com.hadj4r.samples.models;

import com.hadj4r.protodryb.Convertable;
import com.hadj4r.protodryb.PrimitiveField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Convertable
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class Point {
    @PrimitiveField(order = 1)
    private int x;
    @PrimitiveField(order = 2)
    private int y;
}
