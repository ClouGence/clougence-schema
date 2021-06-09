package com.clougence.schema.umi;
import com.clougence.schema.metadata.FieldType;

import java.util.List;

public interface UmiSchema {
    public String getName();

    public String getComment();

    public FieldType getDataType();

    public String[] getPath();

    public List<UmiConstraint> getConstraints();

    public UniSchemaAttributes getAttributes();
}
