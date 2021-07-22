package com.clougence.schema.umi.serializer.special.rdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.commons.NumberUtils;
import com.clougence.commons.StringUtils;
import com.clougence.schema.umi.serializer.ValueUmiSchemaSerializer;
import com.clougence.schema.umi.special.rdb.RdbColumn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbColumnSerializer extends ValueUmiSchemaSerializer<RdbColumn> {

    protected void readData(Map<String, Object> jsonMap, RdbColumn valueUmiSchema) throws ClassNotFoundException, IOException {
        super.readData(jsonMap, valueUmiSchema);
        if (jsonMap.get("charLength") != null) {
            valueUmiSchema.setCharLength(NumberUtils.createLong(StringUtils.toString(jsonMap.get("charLength"))));
        }
        if (jsonMap.get("byteLength") != null) {
            valueUmiSchema.setByteLength(NumberUtils.createLong(StringUtils.toString(jsonMap.get("byteLength"))));
        }
        if (jsonMap.get("numericPrecision") != null) {
            valueUmiSchema.setNumericPrecision(NumberUtils.createInteger(StringUtils.toString(jsonMap.get("numericPrecision"))));
        }
        if (jsonMap.get("numericScale") != null) {
            valueUmiSchema.setNumericScale(NumberUtils.createInteger(StringUtils.toString(jsonMap.get("numericScale"))));
        }
        if (jsonMap.get("datetimePrecision") != null) {
            valueUmiSchema.setDatetimePrecision(NumberUtils.createInteger(StringUtils.toString(jsonMap.get("datetimePrecision"))));
        }
    }

    protected void writeToMap(RdbColumn rdbColumn, Map<String, Object> toMap) throws IOException {
        super.writeToMap(rdbColumn, toMap);
        toMap.put("class", RdbColumn.class.getName());

        toMap.put("charLength", rdbColumn.getCharLength());
        toMap.put("byteLength", rdbColumn.getByteLength());
        toMap.put("numericPrecision", rdbColumn.getNumericPrecision());
        toMap.put("numericScale", rdbColumn.getNumericScale());
        toMap.put("datetimePrecision", rdbColumn.getDatetimePrecision());
    }

    @Override
    public String serialize(RdbColumn object) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(object, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);
    }

    @Override
    public RdbColumn deserialize(String jsonData) throws IOException {
        try {
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });

            RdbColumn rdbColumn = new RdbColumn();
            this.readData(readValue, rdbColumn);
            return rdbColumn;
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }

}
