package com.clougence.schema.umi.io.deserializer;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import com.clougence.schema.umi.AbstractUmiSchema;
import com.clougence.schema.umi.ArrayUmiSchema;
import com.clougence.schema.SerializerRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.hasor.utils.ClassUtils;

public class ArrayUmiSchemaDeserializer<T extends ArrayUmiSchema> //
        extends AbstractUmiSchemaDeserializer<T> {

    protected void readData(Map<String, Object> jsonMap, T arrayUmiSchema) throws ClassNotFoundException, JsonProcessingException {
        super.readData(jsonMap, arrayUmiSchema);

        Map<String, Object> genericType = (Map<String, Object>) jsonMap.get("genericType");
        String genericUmiType = (String) genericType.get("class");
        Class<?> genericUmiClass = ClassUtils.getClass(genericUmiType);
        Function<String, Object> deserializer = SerializerRegistry.getDeserializer(genericUmiClass);
        Object apply = deserializer.apply(new ObjectMapper().writeValueAsString(genericType));

        arrayUmiSchema.setGenericType((AbstractUmiSchema) apply);
    }

    @Override
    public T eApply(String jsonData) throws IOException {
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
