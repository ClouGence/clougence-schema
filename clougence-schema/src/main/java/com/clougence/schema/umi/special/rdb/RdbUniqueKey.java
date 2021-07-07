package com.clougence.schema.umi.special.rdb;

import java.util.ArrayList;
import java.util.List;

import com.clougence.schema.umi.constraint.Unique;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonDeserializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonSerializer;
import com.clougence.schema.umi.serializer.special.rdb.RdbUniqueKeySerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonSerialize(using = RdbUniqueKey.JacksonSerializer.class)
@JsonDeserialize(using = RdbUniqueKey.JacksonDeserializer.class)
public class RdbUniqueKey extends Unique {

    public static class JacksonDeserializer extends AbstractJsonDeserializer<RdbUniqueKey> {

        public JacksonDeserializer(){
            super(new RdbUniqueKeySerializer());
        }
    }

    public static class JacksonSerializer extends AbstractJsonSerializer<RdbUniqueKey> {

        public JacksonSerializer(){
            super(new RdbUniqueKeySerializer());
        }
    }

    private List<String> columnList = new ArrayList<>();
}
