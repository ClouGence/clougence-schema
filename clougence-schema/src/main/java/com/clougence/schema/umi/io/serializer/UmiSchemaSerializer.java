package com.clougence.schema.umi.io.serializer;

import java.io.IOException;

import com.clougence.schema.umi.*;
import com.clougence.schema.umi.io.serializer.special.rdb.RdbColumnSerializer;
import com.clougence.schema.umi.io.serializer.special.rdb.RdbTableSerializer;
import com.clougence.schema.umi.special.rdb.RdbColumn;
import com.clougence.schema.umi.special.rdb.RdbTable;
import net.hasor.utils.function.EFunction;

public class UmiSchemaSerializer implements EFunction<UmiSchema, String, IOException> {

    @Override
    public String eApply(UmiSchema umiSchema) throws IOException {
        if (umiSchema == null) {
            return "null";
        }
        if (umiSchema instanceof RdbColumn) {
            return new RdbColumnSerializer().apply((RdbColumn) umiSchema);
        } else if (umiSchema instanceof RdbTable) {
            return new RdbTableSerializer().apply((RdbTable) umiSchema);
        } else if (umiSchema instanceof StrutsUmiSchema) {
            return new StrutsUmiSchemaSerializer<>().apply((StrutsUmiSchema) umiSchema);
        } else if (umiSchema instanceof ArrayUmiSchema) {
            return new ArrayUmiSchemaSerializer<>().apply((ArrayUmiSchema) umiSchema);
        } else if (umiSchema instanceof MapUmiSchema) {
            return new MapUmiSchemaSerializer<>().apply((MapUmiSchema) umiSchema);
        } else if (umiSchema instanceof ValueUmiSchema) {
            return new ValueUmiSchemaSerializer<>().apply((ValueUmiSchema) umiSchema);
        } else {
            throw new UnsupportedOperationException(umiSchema.getClass().getName() + " type is Unsupported.");
        }
    }
}
