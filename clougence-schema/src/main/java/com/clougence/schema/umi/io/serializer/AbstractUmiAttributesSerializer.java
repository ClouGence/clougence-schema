package com.clougence.schema.umi.io.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.clougence.schema.umi.AbstractUmiAttributes;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.hasor.utils.StringUtils;
import net.hasor.utils.function.EFunction;

public abstract class AbstractUmiAttributesSerializer<T extends AbstractUmiAttributes> //
        implements EFunction<T, String, IOException> {

    protected void writeToMap(T umiAttr, Map<String, Object> toMap) throws JsonProcessingException {
        Map<String, String> attrMap = new HashMap<>();
        umiAttr.getAttributes().toMap().forEach((BiConsumer<Object, Object>) (key, value) -> {
            String keyStr = StringUtils.toString(key);
            String valueStr = StringUtils.toString(value);
            attrMap.put(keyStr, valueStr);
        });
        toMap.put("attributes", attrMap);
    }
}
