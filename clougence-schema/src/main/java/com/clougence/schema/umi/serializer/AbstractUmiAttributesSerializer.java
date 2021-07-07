package com.clougence.schema.umi.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.clougence.schema.umi.AbstractUmiAttributes;
import net.hasor.utils.StringUtils;

public abstract class AbstractUmiAttributesSerializer<T extends AbstractUmiAttributes> implements Serializer<T> {

    protected void readData(Map<String, Object> jsonMap, T target) throws ClassNotFoundException, IOException {
        Object attributes = jsonMap.get("attributes");
        if (attributes instanceof Map) {
            ((Map<?, ?>) attributes).forEach((BiConsumer<Object, Object>) (key, value) -> {
                String keyStr = StringUtils.toString(key);
                String valueStr = StringUtils.toString(value);
                target.getAttributes().setValue(keyStr, valueStr);
            });
        }
    }

    protected void writeToMap(T umiAttr, Map<String, Object> toMap) throws IOException {
        Map<String, String> attrMap = new HashMap<>();
        umiAttr.getAttributes().toMap().forEach((BiConsumer<Object, Object>) (key, value) -> {
            String keyStr = StringUtils.toString(key);
            String valueStr = StringUtils.toString(value);
            attrMap.put(keyStr, valueStr);
        });
        toMap.put("attributes", attrMap);
    }
}
