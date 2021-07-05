package com.clougence.schema;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.function.EFunction;
import net.hasor.utils.io.IOUtils;

public class SerializerRegistry {

    private static final Map<Type, EFunction<Object, String, IOException>> serializerMap   = new HashMap<>();
    private static final Map<Type, EFunction<String, Object, IOException>> deserializerMap = new HashMap<>();

    public static Set<Type> serializerKeys() {
        return serializerMap.keySet();
    }

    public static Set<Type> deserializerKeys() {
        return deserializerMap.keySet();
    }

    public static Function<Object, String> getSerializer(Type type) {
        return serializerMap.get(type);
    }

    public static Function<String, Object> getDeserializer(Type type) {
        return deserializerMap.get(type);
    }

    public static void registrySerializer(Type type, EFunction<Object, String, IOException> serializer) {
        if (serializer != null) {
            serializerMap.put(type, serializer);
        }
    }

    public static void registryDeserializer(Type type, EFunction<String, Object, IOException> deserializer) {
        if (deserializer != null) {
            deserializerMap.put(type, deserializer);
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
}
