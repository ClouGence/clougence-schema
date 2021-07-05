package com.clougence.schema.umi.io.deserializer.constraint;

import java.io.IOException;
import java.util.Map;

import com.clougence.schema.umi.constraint.NonNull;
import com.clougence.schema.umi.io.deserializer.AbstractUmiConstraintDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NonNullDeserializer extends AbstractUmiConstraintDeserializer<NonNull> {

    @Override
    public NonNull eApply(String jsonData) throws IOException {
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
