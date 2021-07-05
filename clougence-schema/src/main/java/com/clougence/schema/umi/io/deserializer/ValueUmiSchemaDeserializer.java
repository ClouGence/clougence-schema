package com.clougence.schema.umi.io.deserializer;

import java.io.IOException;
import java.util.Map;

import com.clougence.schema.metadata.FieldType;
import com.clougence.schema.umi.ValueUmiSchema;
import com.clougence.schema.utils.EnumUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.hasor.utils.ClassUtils;
import net.hasor.utils.StringUtils;

public class ValueUmiSchemaDeserializer<T extends ValueUmiSchema> //
        extends AbstractUmiSchemaDeserializer<T> {

    protected void readData(Map<String, Object> jsonMap, T valueUmiSchema) throws ClassNotFoundException, JsonProcessingException {
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

    @Override
    public T eApply(String jsonData) throws IOException {
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
