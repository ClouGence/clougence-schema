package com.clougence.schema.umi.io.deserializer.special.rdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.clougence.schema.umi.io.deserializer.AbstractUmiConstraintDeserializer;
import com.clougence.schema.umi.special.rdb.RdbUniqueKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbUniqueKeyDeserializer extends AbstractUmiConstraintDeserializer<RdbUniqueKey> {

    protected void readData(Map<String, Object> jsonMap, RdbUniqueKey rdbUniqueKey) throws ClassNotFoundException, JsonProcessingException {
        super.readData(jsonMap, rdbUniqueKey);

        List<String> columnList = (List<String>) jsonMap.get("columnList");
        rdbUniqueKey.setColumnList(new ArrayList<>(columnList));
    }

    @Override
    public RdbUniqueKey eApply(String jsonData) throws IOException {
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
