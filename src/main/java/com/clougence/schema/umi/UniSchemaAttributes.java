package com.clougence.schema.umi;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import net.hasor.utils.CollectionUtils;

@Getter
@Setter
public class UniSchemaAttributes {

    private Map<String, String> attribute = new HashMap<>();

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

    public void setAttributes(UniSchemaAttributes attributes) {
        this.attribute.putAll(attributes.attribute);
    }

    public Map<String, String> toMap() {
        return this.attribute;
    }
}
