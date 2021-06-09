package com.clougence.schema.umi;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractUmiConstraint implements UmiConstraint {
    private String              name;
    private String              comment;
    private UniSchemaAttributes attributes = new UniSchemaAttributes();

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public UniSchemaAttributes getAttributes() {
        return this.attributes;
    }
}
