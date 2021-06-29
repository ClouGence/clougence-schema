package com.clougence.schema.umi.io.deserializer;
import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;

import com.clougence.schema.umi.AbstractUmiConstraint;
import net.hasor.utils.StringUtils;
import net.hasor.utils.function.EFunction;

public abstract class AbstractUmiSchemaDeserializer<T extends AbstractUmiConstraint> //
        implements EFunction<String, T, IOException> {

    protected void fillData(T umiConstraint, Map<String, Object> jsonMap) {
        umiConstraint.setName(StringUtils.toString(jsonMap.get("name")));
        umiConstraint.setComment(StringUtils.toString(jsonMap.get("comment")));
        Object attributes = jsonMap.get("attributes");
        if (attributes instanceof Map) {
            ((Map<?, ?>) attributes).forEach((BiConsumer<Object, Object>) (key, value) -> {
                String keyStr = StringUtils.toString(key);
                String valueStr = StringUtils.toString(value);
                umiConstraint.getAttributes().setValue(keyStr, valueStr);
            });

        }
    }
}
