package com.clougence.schema.umi.serializer.constraint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.umi.constraint.Unique;
import com.clougence.schema.umi.serializer.AbstractUmiConstraintSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UniqueSerializer extends AbstractUmiConstraintSerializer<Unique> {

    protected void writeToMap(Unique uniqueKey, Map<String, Object> dataMap) throws IOException {
        super.writeToMap(uniqueKey, dataMap);
        dataMap.put("class", Unique.class.getName());
    }

    @Override
    public String serialize(Unique object) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(object, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);
    }

    @Override
    public Unique deserialize(String jsonData) throws IOException {
        try {
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });

            Unique unique = new Unique();
            this.readData(readValue, unique);
            return unique;
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }
}
