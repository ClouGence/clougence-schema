package com.clougence.schema.umi.special.rdb;

import java.util.ArrayList;
import java.util.List;

import com.clougence.schema.umi.UmiSchemaAttributes;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonDeserializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonSerializer;
import com.clougence.schema.umi.serializer.special.rdb.RdbIndexSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonSerialize(using = RdbIndex.JacksonSerializer.class)
@JsonDeserialize(using = RdbIndex.JacksonDeserializer.class)
public class RdbIndex {

    public static class JacksonDeserializer extends AbstractJsonDeserializer<RdbIndex> {

        public JacksonDeserializer(){
            super(new RdbIndexSerializer());
        }
    }

    public static class JacksonSerializer extends AbstractJsonSerializer<RdbIndex> {

        public JacksonSerializer(){
            super(new RdbIndexSerializer());
        }
    }

    private String              name;
    private String              type;
    private List<String>        columnList = new ArrayList<>();
    private UmiSchemaAttributes attributes = new UmiSchemaAttributes();
}
