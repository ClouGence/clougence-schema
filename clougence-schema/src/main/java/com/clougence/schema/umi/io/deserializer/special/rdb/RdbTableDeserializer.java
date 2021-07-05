package com.clougence.schema.umi.io.deserializer.special.rdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.clougence.schema.umi.io.deserializer.StrutsUmiSchemaDeserializer;
import com.clougence.schema.umi.special.rdb.RdbIndex;
import com.clougence.schema.umi.special.rdb.RdbTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbTableDeserializer extends StrutsUmiSchemaDeserializer<RdbTable> {

    protected void readData(Map<String, Object> jsonMap, RdbTable rdbTable) throws ClassNotFoundException, JsonProcessingException {
        super.readData(jsonMap, rdbTable);

        List<RdbIndex> indices = new ArrayList<>();
        List<Object> indicesData = (List<Object>) jsonMap.get("indices");

        RdbIndexDeserializer indexDeserializer = new RdbIndexDeserializer();
        for (Object rdbIndex : indicesData) {
            RdbIndex apply = indexDeserializer.apply(new ObjectMapper().writeValueAsString(rdbIndex));
            indices.add(apply);
        }

        rdbTable.setIndices(indices);
    }

    @Override
    public RdbTable eApply(String jsonData) throws IOException {
        try {
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });

            RdbTable rdbTable = new RdbTable();
            this.readData(readValue, rdbTable);
            return rdbTable;
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }

}
