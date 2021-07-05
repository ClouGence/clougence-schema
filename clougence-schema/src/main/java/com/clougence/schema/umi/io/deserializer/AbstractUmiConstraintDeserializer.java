package com.clougence.schema.umi.io.deserializer;

import java.util.Map;

import com.clougence.schema.umi.AbstractUmiConstraint;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.hasor.utils.StringUtils;

public abstract class AbstractUmiConstraintDeserializer<T extends AbstractUmiConstraint> //
        extends AbstractUmiAttributesDeserializer<T> {

    protected void readData(Map<String, Object> jsonMap, T umiConstraint) throws ClassNotFoundException, JsonProcessingException {
        super.readData(jsonMap, umiConstraint);

        umiConstraint.setName(StringUtils.toString(jsonMap.get("name")));
        umiConstraint.setComment(StringUtils.toString(jsonMap.get("comment")));
    }

}
