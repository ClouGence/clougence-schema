package com.clougence.schema.umi.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.umi.AbstractUmiSchema;
import com.clougence.schema.umi.ArrayUmiSchema;
import com.clougence.schema.umi.UmiSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ArrayUmiSchemaSerializer<T extends ArrayUmiSchema> //
        extends AbstractUmiSchemaSerializer<T> {

    protected Serializer<UmiSchema> serializer = new UmiSchemaSerializer();

    protected void readData(Map<String, Object> jsonMap, T arrayUmiSchema) throws ClassNotFoundException, IOException {
        super.readData(jsonMap, arrayUmiSchema);

        Map<String, Object> genericType = (Map<String, Object>) jsonMap.get("genericType");
        Object apply = this.serializer.deserialize(new ObjectMapper().writeValueAsString(genericType));

        arrayUmiSchema.setGenericType((AbstractUmiSchema) apply);
    }

    protected void writeToMap(T umiSchema, Map<String, Object> toMap) throws IOException {
        super.writeToMap(umiSchema, toMap);
        toMap.put("class", ArrayUmiSchema.class.getName());
        //
        AbstractUmiSchema genericType = umiSchema.getGenericType();

        String jsonData = this.serializer.serialize(genericType);
        Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
        });
        toMap.put("genericType", readValue);
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

            ArrayUmiSchema arrayUmiSchema = new ArrayUmiSchema();
            this.readData(readValue, (T) arrayUmiSchema);
            return (T) arrayUmiSchema;
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }
}
