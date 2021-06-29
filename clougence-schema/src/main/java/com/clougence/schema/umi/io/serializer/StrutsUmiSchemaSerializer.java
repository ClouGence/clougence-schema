package com.clougence.schema.umi.io.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import com.clougence.schema.umi.AbstractUmiSchema;
import com.clougence.schema.umi.StrutsUmiSchema;
import com.clougence.schema.umi.io.SerializerRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StrutsUmiSchemaSerializer<T extends StrutsUmiSchema> //
        extends AbstractUmiSchemaSerializer<T> {

    protected void writeToMap(T umiSchema, Map<String, Object> toMap) {
        super.writeToMap(umiSchema, toMap);
        toMap.put("class", StrutsUmiSchema.class.getName());

        Map<String, AbstractUmiSchema> properties = umiSchema.getProperties();
        Map<String, Object> strutsMap = new LinkedHashMap<>();
        for (String propertyKey : properties.keySet()) {
            AbstractUmiSchema propertySchema = properties.get(propertyKey);
            Function<Object, String> serializer = SerializerRegistry.getSerializer(propertySchema.getClass());
            toMap.put(propertyKey, serializer.apply(propertySchema));
        }

        toMap.put("properties", strutsMap);
    }

    @Override
    public String eApply(T strutsUmiSchema) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(strutsUmiSchema, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);
    }
}
