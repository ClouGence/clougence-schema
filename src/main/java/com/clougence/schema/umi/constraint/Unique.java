package com.clougence.schema.umi.constraint;
import com.clougence.schema.umi.AbstractUmiConstraint;
import com.clougence.schema.umi.UmiConstraintType;

public class Unique extends AbstractUmiConstraint {
    @Override
    public final UmiConstraintType getType() {
        return GeneralConstraintType.Unique;
    }
}
