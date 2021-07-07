package com.clougence.schema.umi.serializer;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import com.clougence.schema.metadata.domain.rdb.adb.mysql.AdbMySqlTypes;
import com.clougence.schema.umi.MapUmiSchema;
import com.clougence.schema.umi.constraint.NonNull;
import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.constraint.Unique;
import com.clougence.schema.umi.special.rdb.RdbColumn;
import com.clougence.schema.umi.special.rdb.RdbIndex;
import com.clougence.schema.umi.special.rdb.RdbTable;
import com.clougence.schema.umi.special.rdb.RdbTableType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RdbSerializerTest extends AbstractSerializerTest {

    @Test
    public void rdbColumnSchemaTest() throws JsonProcessingException {
        RdbColumn schemaBefore = new RdbColumn();
        schemaBefore.setName("abc");
        schemaBefore.setComment("sssss");
        schemaBefore.setDataType(AdbMySqlTypes.DATE);
        schemaBefore.setDefaultValue("aaa");
        schemaBefore.getConstraints().add(new Primary());
        schemaBefore.getConstraints().add(new Unique());
        schemaBefore.getAttributes().setValue("abc", "123");
        schemaBefore.setCharLength(128L);
        schemaBefore.setByteLength(256L);
        schemaBefore.setNumericScale(null);
        schemaBefore.setNumericPrecision(12);
        schemaBefore.setDatetimePrecision(9);
        //
        String jsonData = new ObjectMapper().writeValueAsString(schemaBefore);
        RdbColumn schemaAfter = new ObjectMapper().readValue(jsonData, RdbColumn.class);
        //
        diffAbstractUmi(schemaBefore, schemaAfter);
        //
        assert schemaAfter.getCharLength().intValue() == 128;
        assert schemaAfter.getByteLength().intValue() == 256;
        assert schemaAfter.getNumericScale() == null;
        assert schemaAfter.getNumericPrecision() == 12;
        assert schemaAfter.getDatetimePrecision() == 9;
    }

    @Test
    public void rdbTableSchemaTest_1() throws JsonProcessingException {
        RdbTable schemaBefore = new RdbTable();
        schemaBefore.setName("abc");
        schemaBefore.setComment("sssss");
        schemaBefore.getConstraints().add(new Primary());
        schemaBefore.getConstraints().add(new Unique());
        schemaBefore.getAttributes().setValue("abc", "123");
        schemaBefore.setTableType(RdbTableType.Table);
        //
        RdbIndex rdbIndex = new RdbIndex();
        rdbIndex.setName("idx_aa");
        rdbIndex.setType("uk");
        rdbIndex.setColumnList(Arrays.asList("a", "b"));
        schemaBefore.setIndices(Collections.singletonList(rdbIndex));

        //
        String jsonData = new ObjectMapper().writeValueAsString(schemaBefore);
        RdbTable schemaAfter = new ObjectMapper().readValue(jsonData, RdbTable.class);
        //
        diffAbstractUmi(schemaBefore, schemaAfter);
        //
        assert schemaAfter.getTableType() == RdbTableType.Table;
        assert schemaAfter.getIndices().size() == 1;
        assert schemaAfter.getIndices().get(0).getName().equals("idx_aa");
        assert schemaAfter.getIndices().get(0).getType().equals("uk");
        assert schemaAfter.getIndices().get(0).getColumnList().get(0).equals("a");
        assert schemaAfter.getIndices().get(0).getColumnList().get(1).equals("b");
        assert schemaAfter.hasConstraint(Primary.class);
        assert schemaAfter.hasConstraint(Unique.class);
    }

    @Test
    public void rdbTableSchemaTest_2() throws JsonProcessingException {
        MapUmiSchema schemaBefore = new MapUmiSchema();
        schemaBefore.getConstraints().add(new NonNull());
        schemaBefore.getConstraints().add(new Primary());
        schemaBefore.getConstraints().add(new Unique());
        //
        String jsonData = new ObjectMapper().writeValueAsString(schemaBefore);
        MapUmiSchema schemaAfter = new ObjectMapper().readValue(jsonData, MapUmiSchema.class);
        //
        diffAbstractUmi(schemaBefore, schemaAfter);
        //
        assert schemaAfter.hasConstraint(NonNull.class);
        assert schemaAfter.hasConstraint(Primary.class);
        assert schemaAfter.hasConstraint(Unique.class);
    }
    //RdbForeignKeySerializer
    //RdbIndexSerializer
    //RdbPrimaryKeySerializer
    //RdbUniqueKeySerializer

}
