package com.clougence.schema.umi.io;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.hasor.utils.function.EFunction;

public class SerializerRegistry {

    private static final Map<String, EFunction<Object, String, IOException>> serializerMap   = new HashMap<>();
    private static final Map<String, EFunction<String, Object, IOException>> deserializerMap = new HashMap<>();

    public static Function<Object, String> getSerializer(Type type) {
        return serializerMap.get(type.getTypeName());
    }

    public static Function<String, Object> getDeserializerMap(Type type) {
        return deserializerMap.get(type.getTypeName());
    }

    public static void registry(Type type, EFunction<Object, String, IOException> serializer, EFunction<String, Object, IOException> deserializer) {
        if (serializer != null) {
            serializerMap.put(type.getTypeName(), serializer);
        }
        if (deserializer != null) {
            deserializerMap.put(type.getTypeName(), deserializer);
        }
    }

    static {
        //        try {
        //            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //            List<InputStream> dList = ResourcesUtils.getResourceAsStreamList("/META-INF/clougence/deserializers");
        //            for (InputStream inStream : dList) {
        //                if (inStream == null) {
        //                    continue;
        //                }
        //
        //                Properties properties = new Properties();
        //                properties.load(inStream);
        //                Enumeration<Object> keys = properties.keys();
        //
        //                while (keys.hasMoreElements()) {
        //                    Object keyStr = keys.nextElement();
        //
        //                }
        //
        //                properties.forEach((type, deserializer) -> {
        //
        //                    classLoader.loadClass(type.toString());
        //                    classLoader.loadClass(type.toString());
        //
        //                });
        //            }
        //
        //        } catch (IOException e) {
        //            throw new RuntimeException(e);
        //        }
        //
        //        List<InputStream> sList = ResourcesUtils.getResourceAsStreamList("/META-INF/clougence/serializers");

    }
}
