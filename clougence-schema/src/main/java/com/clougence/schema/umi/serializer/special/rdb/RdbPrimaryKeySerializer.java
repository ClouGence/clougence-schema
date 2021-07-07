package com.clougence.schema.umi.serializer.special.rdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clougence.schema.umi.serializer.AbstractUmiConstraintSerializer;
import com.clougence.schema.umi.special.rdb.RdbPrimaryKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbPrimaryKeySerializer extends AbstractUmiConstraintSerializer<RdbPrimaryKey> {

    protected void readData(Map<String, Object> jsonMap, RdbPrimaryKey rdbPrimaryKey) throws ClassNotFoundException, IOException {
        super.readData(jsonMap, rdbPrimaryKey);

        List<String> columnList = (List<String>) jsonMap.get("columnList");
        rdbPrimaryKey.setColumnList(new ArrayList<>(columnList));
    }

    protected void writeToMap(RdbPrimaryKey primaryKey, Map<String, Object> dataMap) throws IOException {
        super.writeToMap(primaryKey, dataMap);
        dataMap.put("class", RdbPrimaryKey.class.getName());

        dataMap.put("columnList", primaryKey.getColumnList());
    }

    @Override
    public String serialize(RdbPrimaryKey object) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(object, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);
    }

    @Override
    public RdbPrimaryKey deserialize(String jsonData) throws IOException {
        try {
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });

            RdbPrimaryKey rdbPrimaryKey = new RdbPrimaryKey();
            this.readData(readValue, rdbPrimaryKey);
            return rdbPrimaryKey;
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }
}
