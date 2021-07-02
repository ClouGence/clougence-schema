package com.clougence.schema.metadata.typemapping;
import com.clougence.schema.metadata.FieldType;
import lombok.Getter;

@Getter
class MappingEnt {

    private final MappingDef mappingDef;
    private final FieldType  source;
    private final FieldType  target;

    public MappingEnt(MappingDef mappingDef, FieldType sourceTypeDef, FieldType targetTypeDef){
        this.mappingDef = mappingDef;
        this.source = sourceTypeDef;
        this.target = targetTypeDef;
    }

    @Override
    public String toString() {
        return "Ent: from " + this.source + " to " + this.target;
    }
}
