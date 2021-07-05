package com.clougence.schema.umi.io.serializer;

import java.io.IOException;

import com.clougence.schema.umi.UmiConstraint;
import com.clougence.schema.umi.constraint.NonNull;
import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.constraint.Unique;
import com.clougence.schema.umi.io.serializer.constraint.NonNullSerializer;
import com.clougence.schema.umi.io.serializer.constraint.PrimarySerializer;
import com.clougence.schema.umi.io.serializer.constraint.UniqueSerializer;
import com.clougence.schema.umi.io.serializer.special.rdb.RdbForeignKeySerializer;
import com.clougence.schema.umi.io.serializer.special.rdb.RdbPrimaryKeySerializer;
import com.clougence.schema.umi.io.serializer.special.rdb.RdbUniqueKeySerializer;
import com.clougence.schema.umi.special.rdb.RdbForeignKey;
import com.clougence.schema.umi.special.rdb.RdbPrimaryKey;
import com.clougence.schema.umi.special.rdb.RdbUniqueKey;
import net.hasor.utils.function.EFunction;

public class UmiConstraintSerializer implements EFunction<UmiConstraint, String, IOException> {

    @Override
    public String eApply(UmiConstraint constraint) throws IOException {
        if (constraint == null) {
            return "null";
        }
        if (constraint instanceof RdbForeignKey) {
            return new RdbForeignKeySerializer().apply((RdbForeignKey) constraint);
        } else if (constraint instanceof RdbPrimaryKey) {
            return new RdbPrimaryKeySerializer().apply((RdbPrimaryKey) constraint);
        } else if (constraint instanceof RdbUniqueKey) {
            return new RdbUniqueKeySerializer().apply((RdbUniqueKey) constraint);
        } else if (constraint instanceof Primary) {
            return new PrimarySerializer().apply((Primary) constraint);
        } else if (constraint instanceof Unique) {
            return new UniqueSerializer().apply((Unique) constraint);
        } else if (constraint instanceof NonNull) {
            return new NonNullSerializer().apply((NonNull) constraint);
        } else {
            throw new UnsupportedOperationException(constraint.getClass().getName() + " type is Unsupported.");
        }
    }
}
