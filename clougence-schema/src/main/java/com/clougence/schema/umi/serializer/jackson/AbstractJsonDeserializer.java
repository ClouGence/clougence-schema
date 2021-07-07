package com.clougence.schema.umi.serializer.jackson;

import java.io.IOException;

import com.clougence.schema.umi.serializer.Serializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class AbstractJsonDeserializer<T> extends JsonDeserializer<T> {

    private Serializer<T> serializer = null;

    public AbstractJsonDeserializer(Serializer<T> serializer){
        this.serializer = serializer;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String jsonData = p.readValueAsTree().toString();
        return (T) this.serializer.deserialize(jsonData);
    }
}
