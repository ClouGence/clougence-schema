package com.clougence.schema.umi.serializer.constraint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.umi.constraint.NonNull;
import com.clougence.schema.umi.serializer.AbstractUmiConstraintSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NonNullSerializer extends AbstractUmiConstraintSerializer<NonNull> {

    protected void writeToMap(NonNull nonNull, Map<String, Object> dataMap) throws IOException {
        super.writeToMap(nonNull, dataMap);
        dataMap.put("class", NonNull.class.getName());
    }

    @Override
    public String serialize(NonNull object) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(object, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);
    }

    @Override
    public NonNull deserialize(String jsonData) throws IOException {
        try {
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });

            NonNull nonNull = new NonNull();
            this.readData(readValue, nonNull);
            return nonNull;
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }
}
