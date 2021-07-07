package com.clougence.schema.umi.constraint;

import com.clougence.schema.umi.AbstractUmiConstraint;
import com.clougence.schema.umi.UmiConstraintType;
import com.clougence.schema.umi.serializer.constraint.UniqueSerializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonDeserializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = Unique.JacksonSerializer.class)
@JsonDeserialize(using = Unique.JacksonDeserializer.class)
public class Unique extends AbstractUmiConstraint {

    public static class JacksonDeserializer extends AbstractJsonDeserializer<Unique> {

        public JacksonDeserializer(){
            super(new UniqueSerializer());
        }
    }

    public static class JacksonSerializer extends AbstractJsonSerializer<Unique> {

        public JacksonSerializer(){
            super(new UniqueSerializer());
        }
    }

    @Override
    public final UmiConstraintType getType() { return GeneralConstraintType.Unique; }
}
