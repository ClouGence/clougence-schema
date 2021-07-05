package com.clougence.schema.umi.io.serializer.special.rdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.umi.io.serializer.AbstractUmiConstraintSerializer;
import com.clougence.schema.umi.special.rdb.RdbForeignKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbForeignKeySerializer extends AbstractUmiConstraintSerializer<RdbForeignKey> {

    protected void writeToMap(RdbForeignKey rdbForeignKey, Map<String, Object> toMap) throws JsonProcessingException {
        super.writeToMap(rdbForeignKey, toMap);
        toMap.put("class", RdbForeignKey.class.getName());

        toMap.put("columnList", new ArrayList<>(rdbForeignKey.getColumnList()));
        toMap.put("referenceSchema", rdbForeignKey.getReferenceSchema());
        toMap.put("referenceTable", rdbForeignKey.getReferenceTable());
        toMap.put("referenceMapping", new HashMap<>(rdbForeignKey.getReferenceMapping()));
        if (rdbForeignKey.getUpdateRule() == null) {
            toMap.put("updateRule", null);
        } else {
            toMap.put("updateRule", rdbForeignKey.getUpdateRule().getTypeName());
        }
        if (rdbForeignKey.getDeleteRule() == null) {
            toMap.put("deleteRule", null);
        } else {
            toMap.put("deleteRule", rdbForeignKey.getDeleteRule().getTypeName());
        }

    }

    @Override
    public String eApply(RdbForeignKey strutsUmiSchema) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(strutsUmiSchema, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);
    }
}
