package com.clougence.schema.umi.io.deserializer;

import java.io.IOException;
import java.util.Map;

import com.clougence.schema.umi.MapUmiSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MapUmiSchemaDeserializer<T extends MapUmiSchema> //
        extends AbstractUmiSchemaDeserializer<T> {

    protected void readData(Map<String, Object> jsonMap, T mapUmiSchema) throws ClassNotFoundException, JsonProcessingException {
        super.readData(jsonMap, mapUmiSchema);
    }

    @Override
    public T eApply(String jsonData) throws IOException {
        try {
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });

            MapUmiSchema mapUmiSchema = new MapUmiSchema();
            this.readData(readValue, (T) mapUmiSchema);
            return (T) mapUmiSchema;
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }
}
