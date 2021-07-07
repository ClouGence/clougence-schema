package com.clougence.schema.umi.serializer;

import java.io.IOException;

public interface Serializer<T> {

    public String serialize(T object) throws IOException;

    public T deserialize(String jsonData) throws IOException;
}
