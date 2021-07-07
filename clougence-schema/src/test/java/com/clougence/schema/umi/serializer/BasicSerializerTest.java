package com.clougence.schema.umi.serializer;

import org.junit.Test;
import com.clougence.schema.metadata.domain.rdb.adb.mysql.AdbMySqlTypes;
import com.clougence.schema.umi.ArrayUmiSchema;
import com.clougence.schema.umi.MapUmiSchema;
import com.clougence.schema.umi.StrutsUmiSchema;
import com.clougence.schema.umi.ValueUmiSchema;
import com.clougence.schema.umi.constraint.NonNull;
import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.constraint.Unique;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BasicSerializerTest extends AbstractSerializerTest {

    @Test
    public void valueUmiSchemaTest() throws JsonProcessingException {
        ValueUmiSchema schemaBefore = new ValueUmiSchema();
        schemaBefore.setName("abc");
        schemaBefore.setComment("sssss");
        schemaBefore.setDataType(AdbMySqlTypes.DATE);
        schemaBefore.setDefaultValue("aaa");
        schemaBefore.getConstraints().add(new Primary());
        schemaBefore.getAttributes().setValue("abc", "123");
        //
        String jsonData = new ObjectMapper().writeValueAsString(schemaBefore);
        ValueUmiSchema schemaAfter = new ObjectMapper().readValue(jsonData, ValueUmiSchema.class);
        //
        diffAbstractUmi(schemaBefore, schemaAfter);
    }

    @Test
    public void strutsUmiSchemaTest() throws JsonProcessingException {
        ValueUmiSchema propertiesA = new ValueUmiSchema();
        propertiesA.setName("abc");
        propertiesA.setComment("sssss");
        propertiesA.setDataType(AdbMySqlTypes.DATE);
        propertiesA.setDefaultValue("aaa");
        propertiesA.getConstraints().add(new Primary());
        propertiesA.getAttributes().setValue("abc", "123");
        ValueUmiSchema propertiesB = new ValueUmiSchema();
        propertiesB.setName("abc");
        propertiesB.setComment("sssss");
        propertiesB.setDataType(AdbMySqlTypes.DATE);
        propertiesB.setDefaultValue("aaa");
        propertiesB.getConstraints().add(new Primary());
        propertiesB.getAttributes().setValue("abc", "123");
        //
        StrutsUmiSchema schemaBefore = new StrutsUmiSchema();
        schemaBefore.setName("abc");
        schemaBefore.setComment("sssss");
        schemaBefore.getProperties().put("prop_a", propertiesA);
        schemaBefore.getProperties().put("prop_b", propertiesB);
        schemaBefore.getConstraints().add(new Primary());
        schemaBefore.getAttributes().setValue("abc", "123");
        //
        String jsonData = new ObjectMapper().writeValueAsString(schemaBefore);
        StrutsUmiSchema schemaAfter = new ObjectMapper().readValue(jsonData, StrutsUmiSchema.class);
        //
        diffAbstractUmi(schemaBefore, schemaAfter);
    }

    @Test
    public void mapUmiSchemaTest() throws JsonProcessingException {
        MapUmiSchema schemaBefore = new MapUmiSchema();
        schemaBefore.setName("abc");
        schemaBefore.setComment("sssss");
        schemaBefore.getConstraints().add(new Primary());
        schemaBefore.getAttributes().setValue("abc", "123");
        //
        String jsonData = new ObjectMapper().writeValueAsString(schemaBefore);
        MapUmiSchema schemaAfter = new ObjectMapper().readValue(jsonData, MapUmiSchema.class);
        //
        diffAbstractUmi(schemaBefore, schemaAfter);
    }

    @Test
    public void arrayUmiSchemaTest() throws JsonProcessingException {
        ValueUmiSchema propertiesA = new ValueUmiSchema();
        propertiesA.setName("abc");
        propertiesA.setComment("sssss");
        propertiesA.setDataType(AdbMySqlTypes.DATE);
        propertiesA.setDefaultValue("aaa");
        propertiesA.getConstraints().add(new Primary());
        propertiesA.getAttributes().setValue("abc", "123");
        ValueUmiSchema propertiesB = new ValueUmiSchema();
        propertiesB.setName("abc");
        propertiesB.setComment("sssss");
        propertiesB.setDataType(AdbMySqlTypes.DATE);
        propertiesB.setDefaultValue("aaa");
        propertiesB.getConstraints().add(new Primary());
        propertiesB.getAttributes().setValue("abc", "123");
        //
        StrutsUmiSchema genericType = new StrutsUmiSchema();
        genericType.setName("abc");
        genericType.setComment("sssss");
        genericType.getProperties().put("prop_a", propertiesA);
        genericType.getProperties().put("prop_b", propertiesB);
        genericType.getConstraints().add(new Primary());
        genericType.getAttributes().setValue("abc", "123");
        //
        ArrayUmiSchema schemaBefore = new ArrayUmiSchema();
        schemaBefore.setName("abc");
        schemaBefore.setComment("sssss");
        schemaBefore.setGenericType(genericType);
        //
        String jsonData = new ObjectMapper().writeValueAsString(schemaBefore);
        ArrayUmiSchema schemaAfter = new ObjectMapper().readValue(jsonData, ArrayUmiSchema.class);
        //
        diffAbstractUmi(schemaBefore, schemaAfter);
    }

    @Test
    public void constraintTest() throws JsonProcessingException {
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
}
