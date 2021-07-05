package com.clougence.schema.umi.io.deserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.clougence.schema.SerializerRegistry;
import com.clougence.schema.metadata.FieldType;
import com.clougence.schema.umi.AbstractUmiSchema;
import com.clougence.schema.umi.UmiConstraint;
import com.clougence.schema.utils.EnumUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.hasor.utils.ClassUtils;
import net.hasor.utils.StringUtils;

public abstract class AbstractUmiSchemaDeserializer<T extends AbstractUmiSchema> //
        extends AbstractUmiAttributesDeserializer<T> {

    protected void readData(Map<String, Object> jsonMap, T umiSchema) throws ClassNotFoundException, JsonProcessingException {
        super.readData(jsonMap, umiSchema);

        umiSchema.setName(StringUtils.toString(jsonMap.get("name")));
        umiSchema.setComment(StringUtils.toString(jsonMap.get("comment")));

        String typeDef = StringUtils.toString(jsonMap.get("typeDef"));
        if (StringUtils.isBlank(typeDef)) {
            umiSchema.setTypeDef(null);
        } else {
            String typeName = typeDef.substring(0, typeDef.indexOf(","));
            String enumName = typeDef.substring(typeName.length() + 1);
            Class<?> aClass = ClassUtils.getClass(typeName);
            Enum<?> anEnum = EnumUtils.readEnum(enumName, aClass);
            umiSchema.setTypeDef((FieldType) anEnum);
        }

        List<String> parentPath = (List<String>) jsonMap.get("parentPath");
        if (parentPath == null) {
            umiSchema.setParentPath(null);
        } else {
            umiSchema.setParentPath(parentPath.toArray(new String[0]));
        }

        List<Object> constraintDataList = (List<Object>) jsonMap.get("constraints");
        List<UmiConstraint> constraintList = new ArrayList<>();
        for (Object constraintData : constraintDataList) {
            Map<String, Object> constraintMap = (Map<String, Object>) constraintData;
            String constraintType = (String) constraintMap.get("class");
            Class<?> constraintClass = ClassUtils.getClass(constraintType);

            Function<String, Object> deserializer = SerializerRegistry.getDeserializer(constraintClass);
            if (deserializer == null) {
                throw new UnsupportedOperationException(umiSchema.getClass().getName() + " type Unsupported.");
            } else {
                constraintList.add((UmiConstraint) deserializer.apply(new ObjectMapper().writeValueAsString(constraintMap)));
            }
        }
        umiSchema.setConstraints(constraintList);
    }
}
