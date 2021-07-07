package com.clougence.schema.umi.serializer.special.rdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clougence.schema.umi.serializer.AbstractUmiConstraintSerializer;
import com.clougence.schema.umi.special.rdb.RdbUniqueKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbUniqueKeySerializer extends AbstractUmiConstraintSerializer<RdbUniqueKey> {

    protected void readData(Map<String, Object> jsonMap, RdbUniqueKey rdbUniqueKey) throws ClassNotFoundException, IOException {
        super.readData(jsonMap, rdbUniqueKey);

        List<String> columnList = (List<String>) jsonMap.get("columnList");
        rdbUniqueKey.setColumnList(new ArrayList<>(columnList));
    }

    protected void writeToMap(RdbUniqueKey uniqueKey, Map<String, Object> dataMap) throws IOException {
        super.writeToMap(uniqueKey, dataMap);
        dataMap.put("class", RdbUniqueKey.class.getName());

        dataMap.put("columnList", uniqueKey.getColumnList());
    }

    @Override
    public String serialize(RdbUniqueKey object) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(object, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);
    }

    @Override
    public RdbUniqueKey deserialize(String jsonData) throws IOException {
        try {
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });

            RdbUniqueKey rdbUniqueKey = new RdbUniqueKey();
            this.readData(readValue, rdbUniqueKey);
            return rdbUniqueKey;
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }
}
