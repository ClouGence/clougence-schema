package com.clougence.schema.umi.io.deserializer.constraint;

import java.io.IOException;
import java.util.Map;

import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.io.deserializer.AbstractUmiConstraintDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrimaryDeserializer extends AbstractUmiConstraintDeserializer<Primary> {

    @Override
    public Primary eApply(String jsonData) throws IOException {
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
