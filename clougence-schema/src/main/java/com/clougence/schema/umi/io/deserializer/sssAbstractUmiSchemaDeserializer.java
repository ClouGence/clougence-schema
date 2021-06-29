package com.clougence.schema.umi.io.deserializer;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;

import com.clougence.schema.umi.AbstractUmiConstraint;
import net.hasor.utils.StringUtils;
import net.hasor.utils.function.EFunction;

public abstract class sssAbstractUmiSchemaDeserializer<T extends AbstractUmiConstraint> //
        implements EFunction<String, T, IOException> {

    protected void fillAttributes(T umiAttr, Map<String, Object> jsonMap) {
        Object attributes = jsonMap.get("attributes");
        if (attributes instanceof Map) {
            ((Map<?, ?>) attributes).forEach((BiConsumer<Object, Object>) (key, value) -> {
                String keyStr = StringUtils.toString(key);
                String valueStr = StringUtils.toString(value);
                umiAttr.getAttributes().setValue(keyStr, valueStr);
            });
        }
    }
}
