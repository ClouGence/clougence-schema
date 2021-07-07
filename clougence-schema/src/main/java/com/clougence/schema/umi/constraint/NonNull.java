package com.clougence.schema.umi.constraint;

import com.clougence.schema.umi.AbstractUmiConstraint;
import com.clougence.schema.umi.UmiConstraintType;
import com.clougence.schema.umi.serializer.constraint.NonNullSerializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonDeserializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = NonNull.JacksonSerializer.class)
@JsonDeserialize(using = NonNull.JacksonDeserializer.class)
public class NonNull extends AbstractUmiConstraint {

    public static class JacksonDeserializer extends AbstractJsonDeserializer<NonNull> {

        public JacksonDeserializer(){
            super(new NonNullSerializer());
        }
    }

    public static class JacksonSerializer extends AbstractJsonSerializer<NonNull> {

        public JacksonSerializer(){
            super(new NonNullSerializer());
        }
    }

    @Override
    public final UmiConstraintType getType() { return GeneralConstraintType.NonNull; }
}
