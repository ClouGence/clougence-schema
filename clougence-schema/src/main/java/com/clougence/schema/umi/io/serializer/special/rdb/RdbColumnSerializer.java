package com.clougence.schema.umi.io.serializer.special.rdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.umi.io.serializer.ValueUmiSchemaSerializer;
import com.clougence.schema.umi.special.rdb.RdbColumn;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbColumnSerializer //
        extends ValueUmiSchemaSerializer<RdbColumn> {

    protected void writeToMap(RdbColumn rdbColumn, Map<String, Object> toMap) {
        super.writeToMap(rdbColumn, toMap);
        toMap.put("class", RdbColumn.class.getName());

        toMap.put("charLength", rdbColumn.getCharLength());
        toMap.put("byteLength", rdbColumn.getByteLength());
        toMap.put("numericPrecision", rdbColumn.getNumericPrecision());
        toMap.put("numericScale", rdbColumn.getNumericScale());
        toMap.put("datetimePrecision", rdbColumn.getDatetimePrecision());
    }

    @Override
    public String eApply(RdbColumn strutsUmiSchema) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(strutsUmiSchema, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);
    }
}
