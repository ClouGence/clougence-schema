package com.clougence.schema.umi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractUmiAttributes implements UmiAttributes {

    private UmiSchemaAttributes attributes = new UmiSchemaAttributes();

    @Override
    public UmiSchemaAttributes getAttributes() { return this.attributes; }

}
