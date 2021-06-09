package com.clougence.schema.umi;
import net.hasor.utils.CollectionUtils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class UniSchemaAttributes {
    private final Map<String, String> attribute;

    public UniSchemaAttributes() {
        this.attribute = new HashMap<>();
    }

    public UniSchemaAttributes(Map<String, String> attribute) {
        this.attribute = new HashMap<>(attribute);
    }

    public Enumeration<String> keys() {
        return CollectionUtils.asEnumeration(this.attribute.keySet().iterator());
    }

    public boolean containsKey(String attrName) {
        return this.attribute.containsKey(attrName);
    }

    public String getValue(String attrName) {
        return this.attribute.get(attrName);
    }

    public void setValue(String attrName, String value) {
        this.attribute.put(attrName, value);
    }
}
