package com.clougence.schema.umi.io.deserializer;

import java.io.IOException;
import java.util.Map;

import com.clougence.schema.umi.UmiConstraint;
import com.clougence.schema.umi.constraint.NonNull;
import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.constraint.Unique;
import com.clougence.schema.umi.io.deserializer.constraint.NonNullDeserializer;
import com.clougence.schema.umi.io.deserializer.constraint.PrimaryDeserializer;
import com.clougence.schema.umi.io.deserializer.constraint.UniqueDeserializer;
import com.clougence.schema.umi.io.deserializer.special.rdb.RdbForeignKeyDeserializer;
import com.clougence.schema.umi.io.deserializer.special.rdb.RdbPrimaryKeyDeserializer;
import com.clougence.schema.umi.io.deserializer.special.rdb.RdbUniqueKeyDeserializer;
import com.clougence.schema.umi.special.rdb.RdbForeignKey;
import com.clougence.schema.umi.special.rdb.RdbPrimaryKey;
import com.clougence.schema.umi.special.rdb.RdbUniqueKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.hasor.utils.ClassUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.function.EFunction;

public class UmiConstraintDeserializer implements EFunction<String, UmiConstraint, IOException> {

    @Override
    public UmiConstraint eApply(String jsonData) throws IOException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        try {
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });
            String typeClassStr = (String) readValue.get("class");
            Class<?> typeClass = ClassUtils.getClass(typeClassStr);
            if (typeClass == RdbForeignKey.class) {
                return new RdbForeignKeyDeserializer().apply(jsonData);
            } else if (typeClass == RdbPrimaryKey.class) {
                return new RdbPrimaryKeyDeserializer().apply(jsonData);
            } else if (typeClass == RdbUniqueKey.class) {
                return new RdbUniqueKeyDeserializer().apply(jsonData);
            } else if (typeClass == Primary.class) {
                return new PrimaryDeserializer().apply(jsonData);
            } else if (typeClass == Unique.class) {
                return new UniqueDeserializer().apply(jsonData);
            } else if (typeClass == NonNull.class) {
                return new NonNullDeserializer().apply(jsonData);
            } else {
                throw new UnsupportedOperationException(typeClass.getName() + " type is Unsupported.");
            }
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }
}
