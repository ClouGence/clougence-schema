package com.clougence.schema.umi;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.clougence.commons.CollectionUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UmiSchemaAttributes {

    private Map<String, String> attribute = new HashMap<>();

    public UmiSchemaAttributes(){
    }

    public UmiSchemaAttributes(Map<String, String> attribute){
        if (attribute != null) {
            this.attribute.putAll(attribute);
        }
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

    public void setAttributes(UmiSchemaAttributes attributes) {
        this.attribute.putAll(attributes.attribute);
    }

    public Map<String, String> toMap() {
        return this.attribute;
    }

}
