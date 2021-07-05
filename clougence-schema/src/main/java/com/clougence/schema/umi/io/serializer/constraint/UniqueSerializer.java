package com.clougence.schema.umi.io.serializer.constraint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.umi.constraint.Unique;
import com.clougence.schema.umi.io.serializer.AbstractUmiConstraintSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UniqueSerializer extends AbstractUmiConstraintSerializer<Unique> {

    protected void writeToMap(Unique uniqueKey, Map<String, Object> dataMap) throws JsonProcessingException {
        super.writeToMap(uniqueKey, dataMap);
        dataMap.put("class", Unique.class.getName());
    }

    @Override
    public String eApply(Unique uniqueKey) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(uniqueKey, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);

    }
}
