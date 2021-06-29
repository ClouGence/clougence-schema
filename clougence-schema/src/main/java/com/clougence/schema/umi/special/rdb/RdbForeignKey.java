package com.clougence.schema.umi.special.rdb;
import com.clougence.schema.umi.AbstractUmiConstraint;
import com.clougence.schema.umi.UmiConstraintType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RdbForeignKey extends AbstractUmiConstraint {
    private List<String>        columnList       = new ArrayList<>();
    private String              referenceSchema;
    private String              referenceTable;
    private Map<String, String> referenceMapping = new HashMap<>();
    private RdbForeignKeyRule   updateRule       = RdbForeignKeyRule.NoAction;
    private RdbForeignKeyRule   deleteRule       = RdbForeignKeyRule.NoAction;

    @Override
    public final UmiConstraintType getType() {
        return () -> "FOREIGN KEY";
    }
}
