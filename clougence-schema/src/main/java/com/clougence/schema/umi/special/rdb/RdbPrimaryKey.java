package com.clougence.schema.umi.special.rdb;

import java.util.ArrayList;
import java.util.List;

import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonDeserializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonSerializer;
import com.clougence.schema.umi.serializer.special.rdb.RdbPrimaryKeySerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonSerialize(using = RdbPrimaryKey.JacksonSerializer.class)
@JsonDeserialize(using = RdbPrimaryKey.JacksonDeserializer.class)
public class RdbPrimaryKey extends Primary {

    public static class JacksonDeserializer extends AbstractJsonDeserializer<RdbPrimaryKey> {

        public JacksonDeserializer(){
            super(new RdbPrimaryKeySerializer());
        }
    }

    public static class JacksonSerializer extends AbstractJsonSerializer<RdbPrimaryKey> {

        public JacksonSerializer(){
            super(new RdbPrimaryKeySerializer());
        }
    }

    private List<String> columnList = new ArrayList<>();
}
