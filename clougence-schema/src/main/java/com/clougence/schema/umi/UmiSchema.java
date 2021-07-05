package com.clougence.schema.umi;

import java.util.List;

import com.clougence.schema.metadata.FieldType;

public interface UmiSchema extends UmiAttributes {

    public String getName();

    public String getComment();

    public FieldType getDataType();

    public List<String> getPath();

    public List<UmiConstraint> getConstraints();

}
