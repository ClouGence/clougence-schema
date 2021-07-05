package com.clougence.schema.umi.io.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.umi.ValueUmiSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ValueUmiSchemaSerializer<T extends ValueUmiSchema> //
        extends AbstractUmiSchemaSerializer<T> {

    protected void writeToMap(T umiSchema, Map<String, Object> toMap) throws JsonProcessingException {
        super.writeToMap(umiSchema, toMap);
        toMap.put("class", ValueUmiSchema.class.getName());

        String defaultValue = umiSchema.getDefaultValue();
        toMap.put("defaultValue", defaultValue);
        if (umiSchema.getDataType() == null) {
            toMap.put("dataType", null);
        } else {
            toMap.put("dataType", umiSchema.getDataType().getFullTypeCodeKey());
        }
    }

    @Override
    public String eApply(T valueUmiSchema) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(valueUmiSchema, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);
    }
}
