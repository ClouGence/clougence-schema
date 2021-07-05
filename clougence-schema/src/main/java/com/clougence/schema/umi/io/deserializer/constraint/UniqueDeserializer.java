package com.clougence.schema.umi.io.deserializer.constraint;

import java.io.IOException;
import java.util.Map;

import com.clougence.schema.umi.constraint.Unique;
import com.clougence.schema.umi.io.deserializer.AbstractUmiConstraintDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UniqueDeserializer extends AbstractUmiConstraintDeserializer<Unique> {

    @Override
    public Unique eApply(String jsonData) throws IOException {
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
