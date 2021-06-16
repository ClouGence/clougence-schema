package com.clougence.schema.metadata.typemapping;
import com.clougence.schema.DsType;
import lombok.Getter;

@Getter
class MappingDef {
    private final DsType source;
    private final DsType target;

    public MappingDef(DsType sourceTypeDef, DsType targetTypeDef) {
        this.source = sourceTypeDef;
        this.target = targetTypeDef;
    }
}
