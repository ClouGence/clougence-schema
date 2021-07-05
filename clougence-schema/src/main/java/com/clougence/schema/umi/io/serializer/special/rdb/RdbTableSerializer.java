package com.clougence.schema.umi.io.serializer.special.rdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clougence.schema.umi.io.serializer.StrutsUmiSchemaSerializer;
import com.clougence.schema.umi.special.rdb.RdbIndex;
import com.clougence.schema.umi.special.rdb.RdbTable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbTableSerializer extends StrutsUmiSchemaSerializer<RdbTable> {

    @Override
    public String eApply(RdbTable rdbTable) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(rdbTable, dataMap);

        List<RdbIndex> indices = rdbTable.getIndices();
        List<Object> indicesJson = new ArrayList<>();
        for (RdbIndex rdbIndex : indices) {
            String indexJson = new RdbIndexSerializer().eApply(rdbIndex);
            indicesJson.add(new ObjectMapper().readValue(indexJson, new TypeReference<Map<String, Object>>() {
            }));
        }

        dataMap.put("indices", indicesJson);
        return new ObjectMapper().writeValueAsString(dataMap);
    }
}
