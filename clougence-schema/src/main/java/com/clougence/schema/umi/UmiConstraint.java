package com.clougence.schema.umi;

public interface UmiConstraint extends UmiAttributes {

    public String getName();

    public String getComment();

    public UmiConstraintType getType();

}
