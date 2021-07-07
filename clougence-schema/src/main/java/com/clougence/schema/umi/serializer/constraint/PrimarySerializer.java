package com.clougence.schema.umi.serializer.constraint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.serializer.AbstractUmiConstraintSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrimarySerializer extends AbstractUmiConstraintSerializer<Primary> {

    protected void writeToMap(Primary primaryKey, Map<String, Object> dataMap) throws IOException {
        super.writeToMap(primaryKey, dataMap);
        dataMap.put("class", Primary.class.getName());
    }

    @Override
    public String serialize(Primary object) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(object, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);
    }

    @Override
    public Primary deserialize(String jsonData) throws IOException {
        try {
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });

            Primary primary = new Primary();
            this.readData(readValue, primary);
            return primary;
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }

}
