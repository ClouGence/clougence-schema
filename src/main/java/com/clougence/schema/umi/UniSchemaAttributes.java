package com.clougence.schema.umi;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import net.hasor.utils.CollectionUtils;

@Getter
@Setter
public class UniSchemaAttributes {

    private Map<String, String> attribute = new HashMap<>();

    @JsonIgnore
    public Enumeration<String> keys() {
        return CollectionUtils.asEnumeration(this.attribute.keySet().iterator());
    }

    @JsonIgnore
    public boolean containsKey(String attrName) {
        return this.attribute.containsKey(attrName);
    }

    @JsonIgnore
    public String getValue(String attrName) {
        return this.attribute.get(attrName);
    }

    @JsonIgnore
    public void setValue(String attrName, String value) {
        this.attribute.put(attrName, value);
    }

    @JsonIgnore
    public void setAttributes(UniSchemaAttributes attributes) {
        this.attribute.putAll(attributes.attribute);
    }

    @JsonIgnore
    public Map<String, String> toMap() {
        return this.attribute;
    }
}
