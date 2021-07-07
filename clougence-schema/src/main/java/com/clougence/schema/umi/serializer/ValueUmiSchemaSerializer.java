package com.clougence.schema.umi.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.metadata.FieldType;
import com.clougence.schema.umi.ValueUmiSchema;
import com.clougence.schema.utils.EnumUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.hasor.utils.ClassUtils;
import net.hasor.utils.StringUtils;

public class ValueUmiSchemaSerializer<T extends ValueUmiSchema> extends AbstractUmiSchemaSerializer<T> {

    protected void readData(Map<String, Object> jsonMap, T valueUmiSchema) throws ClassNotFoundException, IOException {
        super.readData(jsonMap, valueUmiSchema);

        valueUmiSchema.setDefaultValue(StringUtils.toString(jsonMap.get("defaultValue")));

        String typeDef = StringUtils.toString(jsonMap.get("dataType"));
        if (StringUtils.isBlank(typeDef)) {
            valueUmiSchema.setDataType(null);
        } else {
            String typeName = typeDef.substring(0, typeDef.indexOf(","));
            String enumName = typeDef.substring(typeName.length() + 1);
            Class<?> aClass = ClassUtils.getClass(typeName);
            Enum<?> anEnum = EnumUtils.readEnum(enumName, aClass);
            valueUmiSchema.setDataType((FieldType) anEnum);
        }
    }

    protected void writeToMap(T umiSchema, Map<String, Object> toMap) throws IOException {
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

            ValueUmiSchema valueUmiSchema = new ValueUmiSchema();
            this.readData(readValue, (T) valueUmiSchema);
            return (T) valueUmiSchema;
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }
}
