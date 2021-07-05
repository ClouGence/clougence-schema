package com.clougence.schema.umi.io.deserializer.special.rdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.clougence.schema.umi.io.deserializer.AbstractUmiConstraintDeserializer;
import com.clougence.schema.umi.special.rdb.RdbPrimaryKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbPrimaryKeyDeserializer extends AbstractUmiConstraintDeserializer<RdbPrimaryKey> {

    protected void readData(Map<String, Object> jsonMap, RdbPrimaryKey rdbPrimaryKey) throws ClassNotFoundException, JsonProcessingException {
        super.readData(jsonMap, rdbPrimaryKey);

        List<String> columnList = (List<String>) jsonMap.get("columnList");
        rdbPrimaryKey.setColumnList(new ArrayList<>(columnList));
    }

    @Override
    public RdbPrimaryKey eApply(String jsonData) throws IOException {
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
