package com.clougence.schema.metadata.typemapping;
import com.clougence.schema.metadata.FieldType;

class MappingEnt {
    private final FieldType source;
    private final FieldType target;

    public MappingEnt(FieldType sourceTypeDef, FieldType targetTypeDef) {
        this.source = sourceTypeDef;
        this.target = targetTypeDef;
    }

    public FieldType getSource() {
        return this.source;
    }

    public FieldType getTarget() {
        return this.target;
    }

    @Override
    public String toString() {
        return "Ent: from " + this.source + " to " + this.target;
    }
}
