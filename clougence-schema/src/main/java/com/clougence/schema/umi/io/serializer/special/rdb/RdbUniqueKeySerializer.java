package com.clougence.schema.umi.io.serializer.special.rdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.umi.io.serializer.AbstractUmiConstraintSerializer;
import com.clougence.schema.umi.special.rdb.RdbUniqueKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbUniqueKeySerializer extends AbstractUmiConstraintSerializer<RdbUniqueKey> {

    protected void writeToMap(RdbUniqueKey uniqueKey, Map<String, Object> dataMap) throws JsonProcessingException {
        super.writeToMap(uniqueKey, dataMap);
        dataMap.put("class", RdbUniqueKey.class.getName());

        dataMap.put("columnList", uniqueKey.getColumnList());
    }

    @Override
    public String eApply(RdbUniqueKey uniqueKey) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(uniqueKey, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);

    }
}
