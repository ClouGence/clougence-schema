package com.clougence.schema.umi.io.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.clougence.schema.umi.AbstractUmiAttributes;
import net.hasor.utils.StringUtils;
import net.hasor.utils.function.EFunction;

public abstract class AbstractUmiAttributesSerializer<T extends AbstractUmiAttributes> //
        implements EFunction<T, String, IOException> {

    protected void writeToMap(T umiAttr, Map<String, Object> toMap) {
        Map<String, String> attrMap = new HashMap<>();
        umiAttr.getAttributes().toMap().forEach((BiConsumer<Object, Object>) (key, value) -> {
            String keyStr = StringUtils.toString(key);
            String valueStr = StringUtils.toString(value);
            umiAttr.getAttributes().setValue(keyStr, valueStr);
        });
        toMap.put("attributes", attrMap);
    }
}
