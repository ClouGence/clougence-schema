package com.clougence.schema.umi.serializer.special.rdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clougence.schema.umi.serializer.StrutsUmiSchemaSerializer;
import com.clougence.schema.umi.special.rdb.RdbIndex;
import com.clougence.schema.umi.special.rdb.RdbTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbTableSerializer extends StrutsUmiSchemaSerializer<RdbTable> {

    protected void readData(Map<String, Object> jsonMap, RdbTable rdbTable) throws ClassNotFoundException, IOException {
        super.readData(jsonMap, rdbTable);

        List<RdbIndex> indices = new ArrayList<>();
        List<Object> indicesData = (List<Object>) jsonMap.get("indices");

        RdbIndexSerializer indexDeserializer = new RdbIndexSerializer();
        for (Object rdbIndex : indicesData) {
            RdbIndex apply = indexDeserializer.deserialize(new ObjectMapper().writeValueAsString(rdbIndex));
            indices.add(apply);
        }

        rdbTable.setIndices(indices);
    }

    @Override
    public String serialize(RdbTable object) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(object, dataMap);

        List<RdbIndex> indices = object.getIndices();
        List<Object> indicesJson = new ArrayList<>();
        for (RdbIndex rdbIndex : indices) {
            String indexJson = new RdbIndexSerializer().serialize(rdbIndex);
            indicesJson.add(new ObjectMapper().readValue(indexJson, new TypeReference<Map<String, Object>>() {
            }));
        }

        dataMap.put("indices", indicesJson);
        return new ObjectMapper().writeValueAsString(dataMap);
    }

    @Override
    public RdbTable deserialize(String jsonData) throws IOException {
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
