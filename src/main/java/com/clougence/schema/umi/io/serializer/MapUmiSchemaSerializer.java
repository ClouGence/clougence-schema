package com.clougence.schema.umi.io.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.umi.MapUmiSchema;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MapUmiSchemaSerializer<T extends MapUmiSchema> //
        extends AbstractUmiSchemaSerializer<T> {

    protected void writeToMap(T umiSchema, Map<String, Object> toMap) {
        super.writeToMap(umiSchema, toMap);
        toMap.put("class", MapUmiSchema.class.getName());
    }

    @Override
    public String eApply(T mapUmiSchema) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(mapUmiSchema, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);
    }
}
