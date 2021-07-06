package com.clougence.schema;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.function.EFunction;
import net.hasor.utils.io.IOUtils;

public class SerializerRegistry {

    private static final Map<Class<?>, EFunction<Object, String, IOException>> serializerMap   = new HashMap<>();
    private static final Map<Class<?>, EFunction<String, Object, IOException>> deserializerMap = new HashMap<>();
    private static final SimpleModule                                          simpleModule    = new SimpleModule();

    public static ObjectMapper newObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

    public static Set<Class<?>> serializerKeys() {
        return serializerMap.keySet();
    }

    public static Set<Class<?>> deserializerKeys() {
        return deserializerMap.keySet();
    }

    public static Function<Object, String> getSerializer(Class<?> type) {
        return serializerMap.get(type);
    }

    public static Function<String, Object> getDeserializer(Class<?> type) {
        return deserializerMap.get(type);
    }

    public static <T> void registrySerializer(Class<T> type, EFunction<Object, String, IOException> serializer) {
        if (serializer != null) {
            serializerMap.put(type, serializer);
            simpleModule.addSerializer(type, new InnerJsonSerializer<>(serializer));
        }
    }

    public static <T> void registryDeserializer(Class<T> type, EFunction<String, Object, IOException> deserializer) {
        if (deserializer != null) {
            deserializerMap.put(type, deserializer);
            simpleModule.addDeserializer(type, new InnerJsonDeserializer<>(deserializer));
        }
    }

    private static void loadSerializer(InputStream inStream, ClassLoader classLoader) throws Exception {
        Properties properties = new Properties();
        properties.load(inStream);
        Enumeration<Object> keys = properties.keys();
        while (keys.hasMoreElements()) {
            Object keyStr = keys.nextElement();
            Class<?> typeClass = classLoader.loadClass(keyStr.toString());
            Class<?> serializerClass = classLoader.loadClass(properties.getProperty(keyStr.toString()));
            registrySerializer(typeClass, (EFunction<Object, String, IOException>) serializerClass.newInstance());
        }

    }

    private static void loadDeserializerMap(InputStream inStream, ClassLoader classLoader) throws Exception {
        Properties properties = new Properties();
        properties.load(inStream);
        Enumeration<Object> keys = properties.keys();
        while (keys.hasMoreElements()) {
            Object keyStr = keys.nextElement();
            Class<?> typeClass = classLoader.loadClass(keyStr.toString());
            Class<?> serializerClass = classLoader.loadClass(properties.getProperty(keyStr.toString()));
            registryDeserializer(typeClass, (EFunction<String, Object, IOException>) serializerClass.newInstance());
        }

    }

    static {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            List<InputStream> dList = ResourcesUtils.getResourceAsStreamList(classLoader, "/META-INF/clougence/deserializers");

            for (InputStream inputStream : dList) {
                try {
                    loadDeserializerMap(inputStream, classLoader);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                }
            }

            List<InputStream> sList = ResourcesUtils.getResourceAsStreamList(classLoader, "/META-INF/clougence/serializers");
            for (InputStream inputStream : sList) {
                try {
                    loadSerializer(inputStream, classLoader);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class InnerJsonSerializer<T> extends JsonSerializer<T> {

        private final EFunction<T, String, IOException> eFunction;

        public InnerJsonSerializer(EFunction<T, String, IOException> eFunction){
            this.eFunction = eFunction;
        }

        @Override
        public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeRaw(this.eFunction.eApply(value));
        }
    }

    private static class InnerJsonDeserializer<T> extends JsonDeserializer<T> {

        private final EFunction<String, Object, IOException> eFunction;

        public InnerJsonDeserializer(EFunction<String, Object, IOException> eFunction){
            this.eFunction = eFunction;
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String jsonData = p.readValueAsTree().toString();
            return (T) eFunction.apply(jsonData);
        }
    }
}
