package com.clougence.schema.umi.io.deserializer.special.rdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clougence.schema.umi.io.deserializer.AbstractUmiConstraintDeserializer;
import com.clougence.schema.umi.special.rdb.RdbForeignKey;
import com.clougence.schema.umi.special.rdb.RdbForeignKeyRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.hasor.utils.StringUtils;

public class RdbForeignKeyDeserializer extends AbstractUmiConstraintDeserializer<RdbForeignKey> {

    protected void readData(Map<String, Object> jsonMap, RdbForeignKey rdbForeignKey) throws ClassNotFoundException, JsonProcessingException {
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

    @Override
    public RdbForeignKey eApply(String jsonData) throws IOException {
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
