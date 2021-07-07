package com.clougence.schema.umi.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.umi.MapUmiSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MapUmiSchemaSerializer<T extends MapUmiSchema> //
        extends AbstractUmiSchemaSerializer<T> {

    protected void readData(Map<String, Object> jsonMap, T mapUmiSchema) throws ClassNotFoundException, IOException {
        super.readData(jsonMap, mapUmiSchema);
    }

    protected void writeToMap(T umiSchema, Map<String, Object> toMap) throws IOException {
        super.writeToMap(umiSchema, toMap);
        toMap.put("class", MapUmiSchema.class.getName());
    }

    @Override
    public String serialize(T object) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(object, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);
    }

    @Override
    public T deserialize(String jsonData) throws IOException {
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
