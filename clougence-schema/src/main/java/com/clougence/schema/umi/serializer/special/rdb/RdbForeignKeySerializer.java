package com.clougence.schema.umi.serializer.special.rdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clougence.utils.StringUtils;
import com.clougence.schema.umi.serializer.AbstractUmiConstraintSerializer;
import com.clougence.schema.umi.special.rdb.RdbForeignKey;
import com.clougence.schema.umi.special.rdb.RdbForeignKeyRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbForeignKeySerializer extends AbstractUmiConstraintSerializer<RdbForeignKey> {

    protected void readData(Map<String, Object> jsonMap, RdbForeignKey rdbForeignKey) throws ClassNotFoundException, IOException {
        super.readData(jsonMap, rdbForeignKey);

        String updateRuleStr = StringUtils.toString(jsonMap.get("updateRule"));
        String deleteRuleStr = StringUtils.toString(jsonMap.get("deleteRule"));
        RdbForeignKeyRule updateRule = RdbForeignKeyRule.valueOfCode(updateRuleStr);
        RdbForeignKeyRule deleteRule = RdbForeignKeyRule.valueOfCode(deleteRuleStr);
        rdbForeignKey.setUpdateRule(updateRule);
        rdbForeignKey.setDeleteRule(deleteRule);

        List<String> columnList = (List<String>) jsonMap.get("columnList");
        rdbForeignKey.setColumnList(new ArrayList<>(columnList));

        rdbForeignKey.setReferenceSchema(StringUtils.toString(jsonMap.get("referenceSchema")));
        rdbForeignKey.setReferenceTable(StringUtils.toString(jsonMap.get("referenceTable")));

        Map<String, Object> referenceMapping = (Map<String, Object>) jsonMap.get("referenceMapping");
        rdbForeignKey.setReferenceMapping(new HashMap<>());
        if (referenceMapping != null) {
            referenceMapping.forEach((key, value) -> {
                rdbForeignKey.getReferenceMapping().put(key, (value == null) ? null : value.toString());
            });
        }
    }

    protected void writeToMap(RdbForeignKey rdbForeignKey, Map<String, Object> toMap) throws IOException {
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
    public String serialize(RdbForeignKey object) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        this.writeToMap(object, dataMap);
        return new ObjectMapper().writeValueAsString(dataMap);
    }

    @Override
    public RdbForeignKey deserialize(String jsonData) throws IOException {
        try {
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });

            RdbForeignKey rdbForeignKey = new RdbForeignKey();
            this.readData(readValue, rdbForeignKey);
            return rdbForeignKey;
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }
}
