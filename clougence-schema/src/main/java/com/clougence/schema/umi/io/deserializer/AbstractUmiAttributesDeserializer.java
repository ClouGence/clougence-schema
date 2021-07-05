package com.clougence.schema.umi.io.deserializer;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;

import com.clougence.schema.umi.AbstractUmiAttributes;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.hasor.utils.StringUtils;
import net.hasor.utils.function.EFunction;

public abstract class AbstractUmiAttributesDeserializer<T extends AbstractUmiAttributes> //
        implements EFunction<String, T, IOException> {

    protected void readData(Map<String, Object> jsonMap, T target) throws ClassNotFoundException, JsonProcessingException {
        Object attributes = jsonMap.get("attributes");
        if (attributes instanceof Map) {
            ((Map<?, ?>) attributes).forEach((BiConsumer<Object, Object>) (key, value) -> {
                String keyStr = StringUtils.toString(key);
                String valueStr = StringUtils.toString(value);
                target.getAttributes().setValue(keyStr, valueStr);
            });
        }
    }

}
