package com.clougence.schema.umi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractUmiConstraint extends AbstractUmiAttributes implements UmiConstraint {

    private String name;
    private String comment;

    @Override
    public String getName() { return this.name; }

    @Override
    public String getComment() { return this.comment; }

}
