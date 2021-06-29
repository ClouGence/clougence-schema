package com.clougence.schema.umi.io.serializer;

import java.util.Map;

import com.clougence.schema.umi.AbstractUmiConstraint;

public abstract class AbstractUmiConstraintSerializer<T extends AbstractUmiConstraint> //
        extends AbstractUmiAttributesSerializer<T> {

    protected void writeToMap(T umiConstraint, Map<String, Object> toMap) {
        super.writeToMap(umiConstraint, toMap);
        toMap.put("name", umiConstraint.getName());
        toMap.put("comment", umiConstraint.getComment());
    }

}
