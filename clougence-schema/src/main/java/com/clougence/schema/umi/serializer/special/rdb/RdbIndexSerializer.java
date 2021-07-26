package com.clougence.schema.umi.serializer.special.rdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.clougence.utils.StringUtils;
import com.clougence.schema.umi.serializer.Serializer;
import com.clougence.schema.umi.special.rdb.RdbIndex;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbIndexSerializer implements Serializer<RdbIndex> {

    protected void readData(Map<String, Object> jsonMap, RdbIndex rdbIndex) throws ClassNotFoundException, JsonProcessingException {
        rdbIndex.setName(StringUtils.toString(jsonMap.get("name")));
        rdbIndex.setType(StringUtils.toString(jsonMap.get("type")));

        List<String> columnList = (List<String>) jsonMap.get("columnList");
        rdbIndex.setColumnList(new ArrayList<>(columnList));

        Object attributes = jsonMap.get("attributes");
        if (attributes instanceof Map) {
            ((Map<?, ?>) attributes).forEach((BiConsumer<Object, Object>) (key, value) -> {
                String keyStr = StringUtils.toString(key);
                String valueStr = StringUtils.toString(value);
                rdbIndex.getAttributes().setValue(keyStr, valueStr);
            });
        }
    }

    @Override
    public String serialize(RdbIndex object) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("class", RdbIndex.class.getName());

        dataMap.put("name", object.getName());
        dataMap.put("type", object.getType());
        dataMap.put("columnList", object.getColumnList());

        Map<String, String> attrMap = new HashMap<>();
        object.getAttributes().toMap().forEach((BiConsumer<Object, Object>) (key, value) -> {
            String keyStr = StringUtils.toString(key);
            String valueStr = StringUtils.toString(value);
            object.getAttributes().setValue(keyStr, valueStr);
        });
        dataMap.put("attributes", attrMap);

        return new ObjectMapper().writeValueAsString(dataMap);
    }

    @Override
    public RdbIndex deserialize(String jsonData) throws IOException {
        try {
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });

            RdbIndex rdbIndex = new RdbIndex();
            this.readData(readValue, rdbIndex);
            return rdbIndex;
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }
}
