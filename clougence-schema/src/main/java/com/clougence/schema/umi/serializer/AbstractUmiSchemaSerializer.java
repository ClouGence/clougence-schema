package com.clougence.schema.umi.serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.clougence.utils.ClassUtils;
import com.clougence.utils.EnumUtils;
import com.clougence.utils.StringUtils;
import com.clougence.schema.metadata.FieldType;
import com.clougence.schema.umi.AbstractUmiSchema;
import com.clougence.schema.umi.UmiConstraint;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractUmiSchemaSerializer<T extends AbstractUmiSchema> //
        extends AbstractUmiAttributesSerializer<T> {

    protected Serializer<UmiConstraint> serializer = new UmiConstraintSerializer();

    protected void readData(Map<String, Object> jsonMap, T umiSchema) throws ClassNotFoundException, IOException {
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
            constraintList.add(this.serializer.deserialize(new ObjectMapper().writeValueAsString(constraintMap)));
        }
        umiSchema.setConstraints(constraintList);
    }

    protected void writeToMap(T umiSchema, Map<String, Object> toMap) throws IOException {
        super.writeToMap(umiSchema, toMap);

        toMap.put("name", umiSchema.getName());
        toMap.put("comment", umiSchema.getComment());
        if (umiSchema.getTypeDef() == null) {
            toMap.put("typeDef", null);
        } else {
            toMap.put("typeDef", umiSchema.getTypeDef().getFullTypeCodeKey());
        }
        //
        if (umiSchema.getParentPath() == null) {
            toMap.put("parentPath", null);
        } else if (umiSchema.getParentPath().length == 0) {
            toMap.put("parentPath", new ArrayList<>());
        } else {
            toMap.put("parentPath", Arrays.asList(umiSchema.getParentPath()));
        }
        //
        ArrayList<Object> constraintList = new ArrayList<>();
        List<UmiConstraint> constraints = umiSchema.getConstraints();
        for (UmiConstraint umiConstraint : constraints) {
            String applyJson = this.serializer.serialize(umiConstraint);
            Map<String, Object> readValue = new ObjectMapper().readValue(applyJson, new TypeReference<Map<String, Object>>() {
            });
            constraintList.add(readValue);

        }
        toMap.put("constraints", constraintList);
    }
}
