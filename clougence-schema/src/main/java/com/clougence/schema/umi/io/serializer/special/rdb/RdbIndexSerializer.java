package com.clougence.schema.umi.io.serializer.special.rdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.clougence.schema.umi.special.rdb.RdbIndex;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.hasor.utils.StringUtils;
import net.hasor.utils.function.EFunction;

public class RdbIndexSerializer implements EFunction<RdbIndex, String, IOException> {

    @Override
    public String eApply(RdbIndex rdbIndex) throws IOException {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("class", RdbIndex.class.getName());

        dataMap.put("name", rdbIndex.getName());
        dataMap.put("type", rdbIndex.getType());
        dataMap.put("columnList", rdbIndex.getColumnList());

        Map<String, String> attrMap = new HashMap<>();
        rdbIndex.getAttributes().toMap().forEach((BiConsumer<Object, Object>) (key, value) -> {
            String keyStr = StringUtils.toString(key);
            String valueStr = StringUtils.toString(value);
            rdbIndex.getAttributes().setValue(keyStr, valueStr);
        });
        dataMap.put("attributes", attrMap);

        return new ObjectMapper().writeValueAsString(dataMap);
    }
}
