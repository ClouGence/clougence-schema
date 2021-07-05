package com.clougence.schema.umi.io.deserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.clougence.schema.SerializerRegistry;
import com.clougence.schema.umi.AbstractUmiSchema;
import com.clougence.schema.umi.StrutsUmiSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.hasor.utils.ClassUtils;

public class StrutsUmiSchemaDeserializer<T extends StrutsUmiSchema> //
        extends AbstractUmiSchemaDeserializer<T> {

    protected void readData(Map<String, Object> jsonMap, T strutsUmiSchema) throws ClassNotFoundException, JsonProcessingException {
        super.readData(jsonMap, strutsUmiSchema);

        Map<String, AbstractUmiSchema> propertiesMap = new HashMap<>();

        Map<String, Object> strutsTypeMap = (Map<String, Object>) jsonMap.get("properties");
        for (String property : strutsTypeMap.keySet()) {

            Map<String, Object> propertySchema = (Map<String, Object>) strutsTypeMap.get(property);
            String schemaType = (String) propertySchema.get("class");
            Class<?> schemaClass = ClassUtils.getClass(schemaType);
            Function<String, Object> deserializer = SerializerRegistry.getDeserializer(schemaClass);
            Object apply = deserializer.apply(new ObjectMapper().writeValueAsString(propertySchema));

            propertiesMap.put(property, (AbstractUmiSchema) apply);
        }

        strutsUmiSchema.setProperties(propertiesMap);
    }

    @Override
    public T eApply(String jsonData) throws IOException {
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
