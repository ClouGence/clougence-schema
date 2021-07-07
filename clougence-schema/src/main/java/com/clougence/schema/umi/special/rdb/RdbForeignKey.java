package com.clougence.schema.umi.special.rdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clougence.schema.umi.AbstractUmiConstraint;
import com.clougence.schema.umi.UmiConstraintType;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonDeserializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonSerializer;
import com.clougence.schema.umi.serializer.special.rdb.RdbForeignKeySerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonSerialize(using = RdbForeignKey.JacksonSerializer.class)
@JsonDeserialize(using = RdbForeignKey.JacksonDeserializer.class)
public class RdbForeignKey extends AbstractUmiConstraint {

    public static class JacksonDeserializer extends AbstractJsonDeserializer<RdbForeignKey> {

        public JacksonDeserializer(){
            super(new RdbForeignKeySerializer());
        }
    }

    public static class JacksonSerializer extends AbstractJsonSerializer<RdbForeignKey> {

        public JacksonSerializer(){
            super(new RdbForeignKeySerializer());
        }
    }

    private List<String>        columnList       = new ArrayList<>();
    private String              referenceSchema;
    private String              referenceTable;
    private Map<String, String> referenceMapping = new HashMap<>();
    private RdbForeignKeyRule   updateRule       = RdbForeignKeyRule.NoAction;
    private RdbForeignKeyRule   deleteRule       = RdbForeignKeyRule.NoAction;

    @Override
    public final UmiConstraintType getType() { return () -> "FOREIGN KEY"; }
}
