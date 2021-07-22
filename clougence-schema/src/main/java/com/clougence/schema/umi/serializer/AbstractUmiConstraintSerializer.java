package com.clougence.schema.umi.serializer;

import java.io.IOException;
import java.util.Map;

import com.clougence.commons.StringUtils;
import com.clougence.schema.umi.AbstractUmiConstraint;

public abstract class AbstractUmiConstraintSerializer<T extends AbstractUmiConstraint> //
        extends AbstractUmiAttributesSerializer<T> {

    protected void readData(Map<String, Object> jsonMap, T umiConstraint) throws ClassNotFoundException, IOException {
        super.readData(jsonMap, umiConstraint);

        umiConstraint.setName(StringUtils.toString(jsonMap.get("name")));
        umiConstraint.setComment(StringUtils.toString(jsonMap.get("comment")));
    }

    protected void writeToMap(T umiConstraint, Map<String, Object> toMap) throws IOException {
        super.writeToMap(umiConstraint, toMap);

        toMap.put("name", umiConstraint.getName());
        toMap.put("comment", umiConstraint.getComment());
    }

}
