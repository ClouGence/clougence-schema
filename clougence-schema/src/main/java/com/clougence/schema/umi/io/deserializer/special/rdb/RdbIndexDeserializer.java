package com.clougence.schema.umi.io.deserializer.special.rdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.clougence.schema.umi.special.rdb.RdbIndex;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.hasor.utils.StringUtils;
import net.hasor.utils.function.EFunction;

public class RdbIndexDeserializer implements EFunction<String, RdbIndex, IOException> {

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
    public RdbIndex eApply(String jsonData) throws IOException {
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
