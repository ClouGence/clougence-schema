package com.clougence.schema.umi.constraint;

import com.clougence.schema.umi.AbstractUmiConstraint;
import com.clougence.schema.umi.UmiConstraintType;
import com.clougence.schema.umi.serializer.constraint.PrimarySerializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonDeserializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = Primary.JacksonSerializer.class)
@JsonDeserialize(using = Primary.JacksonDeserializer.class)
public class Primary extends AbstractUmiConstraint {

    public static class JacksonDeserializer extends AbstractJsonDeserializer<Primary> {

        public JacksonDeserializer(){
            super(new PrimarySerializer());
        }
    }

    public static class JacksonSerializer extends AbstractJsonSerializer<Primary> {

        public JacksonSerializer(){
            super(new PrimarySerializer());
        }
    }

    @Override
    public final UmiConstraintType getType() { return GeneralConstraintType.Primary; }
}
