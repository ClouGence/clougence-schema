package com.clougence.schema.metadata.typemapping;
import com.clougence.schema.metadata.FieldType;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;
import net.hasor.utils.function.ESupplier;
import net.hasor.utils.supplier.SingleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * SqlType Mapping Registry
 *
 * @author mode create time is 2020-05-07
 */
public final class TypeMapping {
    public static SingleProvider<TypeMapping> DEFAULT     = new SingleProvider<>((ESupplier<TypeMapping, Exception>) TypeMapping::new);
    private final List<MappingEnt>            mappingList = new ArrayList<>();

    public TypeMapping() throws IOException, ClassNotFoundException {
        InputStream resourceAsStream = ResourcesUtils.getResourceAsStream("/META-INF/clougence/sql-type-mapping.properties");
        Properties properties = new Properties();
        properties.load(resourceAsStream);
        //
        for (Object key : properties.keySet()) {
            String mappingKey = key.toString();
            String property = properties.getProperty(mappingKey);
            if (StringUtils.isBlank(mappingKey) || StringUtils.isBlank(property)) {
                continue;
            }
            InputStream mappingItem = ResourcesUtils.getResourceAsStream(property);
            if (mappingItem == null) {
                continue;
            }
            Properties mappingItems = new Properties();
            mappingItems.load(mappingItem);
            if (mappingItems.isEmpty()) {
                continue;
            }
            //
            this.initMapping(mappingKey, mappingItems);
        }
    }

    private void initMapping(String mappingKey, Properties mappingItems) throws ClassNotFoundException {
        String[] mappingSplit = mappingKey.split(",");
        if (mappingSplit.length < 2 || mappingItems.isEmpty()) {
            return;
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //
        Class<?> formType = classLoader.loadClass(mappingSplit[0]);
        Class<?> toType = classLoader.loadClass(mappingSplit[1]);
        if (!formType.isEnum() || !toType.isEnum()) {
            return;
        }
        //
        List<String> ketList = new ArrayList<>();
        for (Object src : mappingItems.keySet()) {
            ketList.add(src.toString());
        }
        ketList.sort(String::compareTo);
        for (String srcType : ketList) {
            String dstType = mappingItems.getProperty(srcType);
            FieldType sourceTypeDef = (FieldType) ConverterUtils.convert(srcType, formType);
            FieldType targetTypeDef = (FieldType) ConverterUtils.convert(dstType, toType);
            this.mappingList.add(new MappingEnt(sourceTypeDef, targetTypeDef));
        }
    }
    //    public FieldType getTypeDef(DataSourceType dataSourceType, String fieldType) {
    //        //
    //    }

    public Map<String, String> getMappingString(Class<? extends FieldType> src, Class<? extends FieldType> dst) {
        Map<String, String> findMappings = new LinkedHashMap<>();
        this.mappingList.forEach((dat) -> {
            if (src.isInstance(dat.getSource()) && dst.isInstance(dat.getTarget())) {
                findMappings.put(dat.getSource().getCodeKey(), dat.getTarget().getCodeKey());
            }
        });
        if (findMappings.isEmpty()) {
            throw new IllegalArgumentException("Not supported source (" + src.getSimpleName() + ") and target ds type (" + dst.getSimpleName() + ")");
        }
        return findMappings;
    }

    public Map<FieldType, FieldType> getMapping(Class<? extends FieldType> src, Class<? extends FieldType> dst) {
        Map<FieldType, FieldType> findMappings = new LinkedHashMap<>();
        this.mappingList.forEach((dat) -> {
            if (src.isInstance(dat.getSource()) && dst.isInstance(dat.getTarget())) {
                findMappings.put(dat.getSource(), dat.getTarget());
            }
        });
        if (findMappings.isEmpty()) {
            throw new IllegalArgumentException("Not supported source (" + src.getSimpleName() + ") and target ds type (" + dst.getSimpleName() + ")");
        }
        return findMappings;
    }
}
