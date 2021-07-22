package com.clougence.schema.umi.serializer;

import java.io.IOException;
import java.util.Map;

import com.clougence.commons.ClassUtils;
import com.clougence.commons.StringUtils;
import com.clougence.schema.umi.*;
import com.clougence.schema.umi.serializer.special.rdb.RdbColumnSerializer;
import com.clougence.schema.umi.serializer.special.rdb.RdbTableSerializer;
import com.clougence.schema.umi.special.rdb.RdbColumn;
import com.clougence.schema.umi.special.rdb.RdbTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UmiSchemaSerializer implements Serializer<UmiSchema> {

    @Override
    public String serialize(UmiSchema object) throws IOException {
        if (object == null) {
            return "null";
        }
        if (object instanceof RdbColumn) {
            return new RdbColumnSerializer().serialize((RdbColumn) object);
        } else if (object instanceof RdbTable) {
            return new RdbTableSerializer().serialize((RdbTable) object);
        } else if (object instanceof StrutsUmiSchema) {
            return new StrutsUmiSchemaSerializer<>().serialize((StrutsUmiSchema) object);
        } else if (object instanceof ArrayUmiSchema) {
            return new ArrayUmiSchemaSerializer<>().serialize((ArrayUmiSchema) object);
        } else if (object instanceof MapUmiSchema) {
            return new MapUmiSchemaSerializer<>().serialize((MapUmiSchema) object);
        } else if (object instanceof ValueUmiSchema) {
            return new ValueUmiSchemaSerializer<>().serialize((ValueUmiSchema) object);
        } else {
            throw new UnsupportedOperationException(object.getClass().getName() + " type is Unsupported.");
        }
    }

    @Override
    public UmiSchema deserialize(String jsonData) throws IOException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        try {
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });
            String typeClassStr = (String) readValue.get("class");
            Class<?> typeClass = ClassUtils.getClass(typeClassStr);

            if (typeClass == RdbColumn.class) {
                return new RdbColumnSerializer().deserialize(jsonData);
            } else if (typeClass == RdbTable.class) {
                return new RdbTableSerializer().deserialize(jsonData);
            } else if (typeClass == StrutsUmiSchema.class) {
                return new StrutsUmiSchemaSerializer<>().deserialize(jsonData);
            } else if (typeClass == ArrayUmiSchema.class) {
                return new ArrayUmiSchemaSerializer<>().deserialize(jsonData);
            } else if (typeClass == MapUmiSchema.class) {
                return new MapUmiSchemaSerializer<>().deserialize(jsonData);
            } else if (typeClass == ValueUmiSchema.class) {
                return new ValueUmiSchemaSerializer<>().deserialize(jsonData);
            } else {
                throw new UnsupportedOperationException(typeClass.getName() + " type is Unsupported.");
            }

        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }
}
