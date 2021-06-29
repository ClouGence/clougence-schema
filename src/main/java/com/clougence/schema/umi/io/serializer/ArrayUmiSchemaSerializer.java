package com.clougence.schema.umi.io.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.clougence.schema.umi.AbstractUmiSchema;
import com.clougence.schema.umi.ArrayUmiSchema;
import com.clougence.schema.umi.io.SerializerRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ArrayUmiSchemaSerializer<T extends ArrayUmiSchema> //
        extends AbstractUmiSchemaSerializer<T> {

    protected void writeToMap(T umiSchema, Map<String, Object> toMap) {
        super.writeToMap(umiSchema, toMap);
        toMap.put("class", ArrayUmiSchema.class.getName());
        //
        AbstractUmiSchema genericType = umiSchema.getGenericType();
        Function<Object, String> serializer = SerializerRegistry.getSerializer(genericType.getClass());
        toMap.put("genericType", serializer.apply(genericType));
    }

    @Override
    public String eApply(T arrayUmiSchema) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(arrayUmiSchema, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);
    }
}
