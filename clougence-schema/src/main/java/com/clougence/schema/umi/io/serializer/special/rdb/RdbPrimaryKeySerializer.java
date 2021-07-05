package com.clougence.schema.umi.io.serializer.special.rdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.umi.io.serializer.AbstractUmiConstraintSerializer;
import com.clougence.schema.umi.special.rdb.RdbPrimaryKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbPrimaryKeySerializer extends AbstractUmiConstraintSerializer<RdbPrimaryKey> {

    protected void writeToMap(RdbPrimaryKey primaryKey, Map<String, Object> dataMap) throws JsonProcessingException {
        super.writeToMap(primaryKey, dataMap);
        dataMap.put("class", RdbPrimaryKey.class.getName());

        dataMap.put("columnList", primaryKey.getColumnList());
    }

    @Override
    public String eApply(RdbPrimaryKey primaryKey) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(primaryKey, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);

    }
}
