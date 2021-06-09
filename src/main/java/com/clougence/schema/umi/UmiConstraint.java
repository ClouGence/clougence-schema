package com.clougence.schema.umi;
public interface UmiConstraint {
    public String getName();

    public String getComment();

    public UmiConstraintType getType();

    public UniSchemaAttributes getAttributes();
}
