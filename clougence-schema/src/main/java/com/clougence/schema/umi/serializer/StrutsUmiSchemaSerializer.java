package com.clougence.schema.umi.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.clougence.schema.umi.AbstractUmiSchema;
import com.clougence.schema.umi.StrutsUmiSchema;
import com.clougence.schema.umi.UmiSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StrutsUmiSchemaSerializer<T extends StrutsUmiSchema> //
        extends AbstractUmiSchemaSerializer<T> {

    protected Serializer<UmiSchema> serializer = new UmiSchemaSerializer();

    protected void readData(Map<String, Object> jsonMap, T strutsUmiSchema) throws ClassNotFoundException, IOException {
        super.readData(jsonMap, strutsUmiSchema);

        Map<String, AbstractUmiSchema> propertiesMap = new HashMap<>();

        Map<String, Object> strutsTypeMap = (Map<String, Object>) jsonMap.get("properties");
        for (String property : strutsTypeMap.keySet()) {

            Map<String, Object> propertySchema = (Map<String, Object>) strutsTypeMap.get(property);
            Object apply = this.serializer.deserialize(new ObjectMapper().writeValueAsString(propertySchema));

            propertiesMap.put(property, (AbstractUmiSchema) apply);
        }

        strutsUmiSchema.setProperties(propertiesMap);
    }

    protected void writeToMap(T umiSchema, Map<String, Object> toMap) throws IOException {
        super.writeToMap(umiSchema, toMap);
        toMap.put("class", StrutsUmiSchema.class.getName());

        Map<String, AbstractUmiSchema> properties = umiSchema.getProperties();
        Map<String, Object> strutsMap = new LinkedHashMap<>();
        for (String propertyKey : properties.keySet()) {
            AbstractUmiSchema propertySchema = properties.get(propertyKey);
            String jsonData = this.serializer.serialize(propertySchema);
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });
            strutsMap.put(propertyKey, readValue);
        }

        toMap.put("properties", strutsMap);
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

            StrutsUmiSchema strutsUmiSchema = new StrutsUmiSchema();
            this.readData(readValue, (T) strutsUmiSchema);
            return (T) strutsUmiSchema;
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }
}
