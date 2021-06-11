package com.clougence.schema.metadata.typemapping;
import com.clougence.schema.DataSourceType;
import lombok.Getter;

@Getter
class MappingDef {
    private final DataSourceType source;
    private final DataSourceType target;

    public MappingDef(DataSourceType sourceTypeDef, DataSourceType targetTypeDef) {
        this.source = sourceTypeDef;
        this.target = targetTypeDef;
    }
}
