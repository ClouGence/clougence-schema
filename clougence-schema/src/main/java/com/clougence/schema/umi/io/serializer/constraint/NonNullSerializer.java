package com.clougence.schema.umi.io.serializer.constraint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.umi.constraint.NonNull;
import com.clougence.schema.umi.io.serializer.AbstractUmiConstraintSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NonNullSerializer extends AbstractUmiConstraintSerializer<NonNull> {

    protected void writeToMap(NonNull nonNull, Map<String, Object> dataMap) throws JsonProcessingException {
        super.writeToMap(nonNull, dataMap);
        dataMap.put("class", NonNull.class.getName());
    }

    @Override
    public String eApply(NonNull nonNull) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(nonNull, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);

    }
}
