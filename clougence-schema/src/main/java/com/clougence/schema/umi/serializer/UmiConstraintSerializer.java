package com.clougence.schema.umi.serializer;

import java.io.IOException;
import java.util.Map;

import com.clougence.schema.umi.UmiConstraint;
import com.clougence.schema.umi.constraint.NonNull;
import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.constraint.Unique;
import com.clougence.schema.umi.serializer.constraint.NonNullSerializer;
import com.clougence.schema.umi.serializer.constraint.PrimarySerializer;
import com.clougence.schema.umi.serializer.constraint.UniqueSerializer;
import com.clougence.schema.umi.serializer.special.rdb.RdbForeignKeySerializer;
import com.clougence.schema.umi.serializer.special.rdb.RdbPrimaryKeySerializer;
import com.clougence.schema.umi.serializer.special.rdb.RdbUniqueKeySerializer;
import com.clougence.schema.umi.special.rdb.RdbForeignKey;
import com.clougence.schema.umi.special.rdb.RdbPrimaryKey;
import com.clougence.schema.umi.special.rdb.RdbUniqueKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.hasor.utils.ClassUtils;
import net.hasor.utils.StringUtils;

public class UmiConstraintSerializer implements Serializer<UmiConstraint> {

    @Override
    public String serialize(UmiConstraint object) throws IOException {
        if (object == null) {
            return "null";
        }
        if (object instanceof RdbForeignKey) {
            return new RdbForeignKeySerializer().serialize((RdbForeignKey) object);
        } else if (object instanceof RdbPrimaryKey) {
            return new RdbPrimaryKeySerializer().serialize((RdbPrimaryKey) object);
        } else if (object instanceof RdbUniqueKey) {
            return new RdbUniqueKeySerializer().serialize((RdbUniqueKey) object);
        } else if (object instanceof Primary) {
            return new PrimarySerializer().serialize((Primary) object);
        } else if (object instanceof Unique) {
            return new UniqueSerializer().serialize((Unique) object);
        } else if (object instanceof NonNull) {
            return new NonNullSerializer().serialize((NonNull) object);
        } else {
            throw new UnsupportedOperationException(object.getClass().getName() + " type is Unsupported.");
        }
    }

    @Override
    public UmiConstraint deserialize(String jsonData) throws IOException {
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        try {
            Map<String, Object> readValue = new ObjectMapper().readValue(jsonData, new TypeReference<Map<String, Object>>() {
            });
            String typeClassStr = (String) readValue.get("class");
            Class<?> typeClass = ClassUtils.getClass(typeClassStr);
            if (typeClass == RdbForeignKey.class) {
                return new RdbForeignKeySerializer().deserialize(jsonData);
            } else if (typeClass == RdbPrimaryKey.class) {
                return new RdbPrimaryKeySerializer().deserialize(jsonData);
            } else if (typeClass == RdbUniqueKey.class) {
                return new RdbUniqueKeySerializer().deserialize(jsonData);
            } else if (typeClass == Primary.class) {
                return new PrimarySerializer().deserialize(jsonData);
            } else if (typeClass == Unique.class) {
                return new UniqueSerializer().deserialize(jsonData);
            } else if (typeClass == NonNull.class) {
                return new NonNullSerializer().deserialize(jsonData);
            } else {
                throw new UnsupportedOperationException(typeClass.getName() + " type is Unsupported.");
            }
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new IOException(e);
        }
    }
}
