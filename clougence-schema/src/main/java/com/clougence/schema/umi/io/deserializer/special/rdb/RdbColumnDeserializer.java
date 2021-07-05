package com.clougence.schema.umi.io.deserializer.special.rdb;

import java.io.IOException;
import java.util.Map;

import com.clougence.schema.umi.io.deserializer.ValueUmiSchemaDeserializer;
import com.clougence.schema.umi.special.rdb.RdbColumn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.hasor.utils.NumberUtils;
import net.hasor.utils.StringUtils;

public class RdbColumnDeserializer extends ValueUmiSchemaDeserializer<RdbColumn> {

    protected void readData(Map<String, Object> jsonMap, RdbColumn valueUmiSchema) throws ClassNotFoundException, JsonProcessingException {
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

    @Override
    public RdbColumn eApply(String jsonData) throws IOException {
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
