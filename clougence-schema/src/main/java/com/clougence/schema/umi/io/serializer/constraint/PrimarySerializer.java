package com.clougence.schema.umi.io.serializer.constraint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.io.serializer.AbstractUmiConstraintSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrimarySerializer extends AbstractUmiConstraintSerializer<Primary> {

    protected void writeToMap(Primary primaryKey, Map<String, Object> dataMap) throws JsonProcessingException {
        super.writeToMap(primaryKey, dataMap);
        dataMap.put("class", Primary.class.getName());
    }

    @Override
    public String eApply(Primary primaryKey) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(primaryKey, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);

    }
}
