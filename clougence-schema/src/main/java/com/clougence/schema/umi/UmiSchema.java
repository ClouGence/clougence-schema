package com.clougence.schema.umi;

import java.util.List;

import com.clougence.schema.metadata.FieldType;
import com.clougence.schema.umi.serializer.UmiSchemaSerializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonDeserializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = UmiSchema.JacksonSerializer.class)
@JsonDeserialize(using = UmiSchema.JacksonDeserializer.class)
public interface UmiSchema extends UmiAttributes {

    public static class JacksonDeserializer extends AbstractJsonDeserializer<UmiSchema> {

        public JacksonDeserializer(){
            super(new UmiSchemaSerializer());
        }
    }

    public static class JacksonSerializer extends AbstractJsonSerializer<UmiSchema> {

        public JacksonSerializer(){
            super(new UmiSchemaSerializer());
        }
    }

    public String getName();

    public String getComment();

    public FieldType getDataType();

    public List<String> getPath();

    public List<UmiConstraint> getConstraints();

}
