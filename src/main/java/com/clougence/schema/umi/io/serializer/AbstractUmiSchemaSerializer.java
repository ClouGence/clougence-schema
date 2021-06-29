package com.clougence.schema.umi.io.serializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.clougence.schema.umi.AbstractUmiSchema;
import com.clougence.schema.umi.UmiConstraint;
import com.clougence.schema.umi.io.SerializerRegistry;

public abstract class AbstractUmiSchemaSerializer<T extends AbstractUmiSchema> //
        extends AbstractUmiAttributesSerializer<T> {

    protected void writeToMap(T umiSchema, Map<String, Object> toMap) {
        super.writeToMap(umiSchema, toMap);

        toMap.put("name", umiSchema.getName());
        toMap.put("comment", umiSchema.getComment());
        if (umiSchema.getTypeDef() == null) {
            toMap.put("typeDef", null);
        } else {
            toMap.put("typeDef", umiSchema.getTypeDef().getFullTypeCodeKey());
        }
        //
        if (umiSchema.getParentPath() == null) {
            toMap.put("parentPath", null);
        } else if (umiSchema.getParentPath().length == 0) {
            toMap.put("parentPath", new ArrayList<>());
        } else {
            toMap.put("parentPath", Arrays.asList(umiSchema.getParentPath()));
        }
        //
        ArrayList<Object> constraintList = new ArrayList<>();
        List<UmiConstraint> constraints = umiSchema.getConstraints();
        for (UmiConstraint umiConstraint : constraints) {
            Function<Object, String> serializer = SerializerRegistry.getSerializer(umiConstraint.getClass());
            if (serializer == null) {
                throw new UnsupportedOperationException(umiConstraint.getClass().getName() + " type Unsupported.");
            } else {
                constraintList.add(serializer.apply(umiConstraint));
            }
        }
        toMap.put("constraints", constraintList);
    }

}
