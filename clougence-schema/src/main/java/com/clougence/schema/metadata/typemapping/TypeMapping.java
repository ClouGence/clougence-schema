package com.clougence.schema.metadata.typemapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.clougence.utils.function.ESupplier;
import com.clougence.utils.supplier.SingleProvider;
import com.clougence.config.InputStreamSettings;
import com.clougence.config.SettingNode;
import com.clougence.config.Settings;
import com.clougence.config.provider.StreamType;
import com.clougence.schema.DsType;
import com.clougence.schema.metadata.FieldType;

/**
 * SqlType Mapping Registry
 *
 * @author mode create time is 2020-05-07
 */
public final class TypeMapping {

    public static SingleProvider<TypeMapping> DEFAULT     = new SingleProvider<>((ESupplier<TypeMapping, Exception>) TypeMapping::new);
    private final List<MappingEnt>            mappingList = new ArrayList<>();

    private static Settings loadSetting(String resource) throws IOException {
        InputStreamSettings settings = new InputStreamSettings();
        settings.addResource(resource, StreamType.Yaml);
        settings.loadSettings();
        return settings;
    }

    public TypeMapping() throws Exception{
        SettingNode[] settingNodes = loadSetting("/META-INF/clougence/type-mapping.yml").getNodeArray("config.mappingConfig");
        for (SettingNode mappingNode : settingNodes) {
            String mapping = mappingNode.getSubValue("mapping");
            String source = mappingNode.getSubValue("source");
            String target = mappingNode.getSubValue("target");
            //
            DsType sourceDataSource = DsType.valueOfCode(source);
            DsType targetDataSource = DsType.valueOfCode(target);
            MappingDef mappingDef = new MappingDef(sourceDataSource, targetDataSource);
            this.initMapping(mappingDef, mapping);
        }
    }

    private void initMapping(MappingDef mappingDef, String mappingConfig) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Settings mappingSettings = loadSetting(mappingConfig);
        String sourceType = mappingSettings.getString("mappings.source");
        String targetType = mappingSettings.getString("mappings.target");
        //
        Class<?> formType = classLoader.loadClass(sourceType);
        Class<?> toType = classLoader.loadClass(targetType);
        if (!formType.isEnum() || !toType.isEnum()) {
            return;
        }
        //
        SettingNode mapNode = mappingSettings.getNode("mappings.map");
        if (mapNode == null) {
            return;
        }
        SettingNode[] mappingKeys = mapNode.getSubNodes();
        for (SettingNode mappingNode : mappingKeys) {
            String sourceDbType = mappingNode.getName();
            String targetDbType = mappingNode.getValue();
            FieldType sourceTypeDef = (FieldType) formType.getMethod("valueOfCode", String.class).invoke(null, sourceDbType);
            FieldType targetTypeDef = (FieldType) toType.getMethod("valueOfCode", String.class).invoke(null, targetDbType);
            if (sourceTypeDef == null || targetTypeDef == null) {
                continue;
            }
            this.mappingList.add(new MappingEnt(mappingDef, sourceTypeDef, targetTypeDef));
        }
    }

    public Map<String, String> getMapping(DsType sourceDsType, DsType targetDsType) {
        return mappingList.stream().filter(mappingEnt -> {
            MappingDef mappingDef = mappingEnt.getMappingDef();
            return mappingDef.getSource() == sourceDsType && mappingDef.getTarget() == targetDsType;
        }).collect(Collectors.toMap(ent -> ent.getSource().getCodeKey(), ent -> ent.getTarget().getCodeKey()));
    }

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
