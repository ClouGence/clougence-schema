package com.clougence.schema.umi.io.deserializer;

import java.io.IOException;
import java.util.Map;

import com.clougence.schema.umi.*;
import com.clougence.schema.umi.io.deserializer.special.rdb.RdbColumnDeserializer;
import com.clougence.schema.umi.io.deserializer.special.rdb.RdbTableDeserializer;
import com.clougence.schema.umi.special.rdb.RdbColumn;
import com.clougence.schema.umi.special.rdb.RdbTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.hasor.utils.ClassUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.function.EFunction;

public class UmiSchemaDeserializer implements EFunction<String, UmiSchema, IOException> {

    @Override
    public UmiSchema eApply(String jsonData) throws IOException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        try {
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });
            String typeClassStr = (String) readValue.get("class");
            Class<?> typeClass = ClassUtils.getClass(typeClassStr);

            if (typeClass == RdbColumn.class) {
                return new RdbColumnDeserializer().apply(jsonData);
            } else if (typeClass == RdbTable.class) {
                return new RdbTableDeserializer().apply(jsonData);
            } else if (typeClass == StrutsUmiSchema.class) {
                return new StrutsUmiSchemaDeserializer<>().apply(jsonData);
            } else if (typeClass == ArrayUmiSchema.class) {
                return new ArrayUmiSchemaDeserializer<>().apply(jsonData);
            } else if (typeClass == MapUmiSchema.class) {
                return new MapUmiSchemaDeserializer<>().apply(jsonData);
            } else if (typeClass == ValueUmiSchema.class) {
                return new ValueUmiSchemaDeserializer<>().apply(jsonData);
            } else {
                throw new UnsupportedOperationException(typeClass.getName() + " type is Unsupported.");
            }

        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }

}
