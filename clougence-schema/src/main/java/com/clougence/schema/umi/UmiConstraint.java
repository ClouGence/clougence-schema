package com.clougence.schema.umi;

import com.clougence.schema.umi.serializer.UmiConstraintSerializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonDeserializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = UmiConstraint.JacksonSerializer.class)
@JsonDeserialize(using = UmiConstraint.JacksonDeserializer.class)
public interface UmiConstraint extends UmiAttributes {

    public static class JacksonDeserializer extends AbstractJsonDeserializer<UmiConstraint> {

        public JacksonDeserializer(){
            super(new UmiConstraintSerializer());
        }
    }

    public static class JacksonSerializer extends AbstractJsonSerializer<UmiConstraint> {

        public JacksonSerializer(){
            super(new UmiConstraintSerializer());
        }
    }

    public String getName();

    public String getComment();

    public UmiConstraintType getType();

}
