package com.clougence.schema.umi.io;

import java.lang.reflect.Type;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import com.clougence.schema.SerializerRegistry;
import com.clougence.schema.metadata.domain.rdb.adb.mysql.AdbMySqlTypes;
import com.clougence.schema.umi.ArrayUmiSchema;
import com.clougence.schema.umi.MapUmiSchema;
import com.clougence.schema.umi.StrutsUmiSchema;
import com.clougence.schema.umi.ValueUmiSchema;
import com.clougence.schema.umi.constraint.NonNull;
import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.constraint.Unique;

public class BasicSerializerTest extends AbstractSerializerTest {

    @Before
    public void beforeTest() {
        Set<Type> keys = SerializerRegistry.deserializerKeys();
        for (Type key : keys) {
            assert SerializerRegistry.getDeserializer(key) != null;
        }
    }

    @Test
    public void valueUmiSchemaTest() {
        ValueUmiSchema schemaBefore = new ValueUmiSchema();
        schemaBefore.setName("abc");
        schemaBefore.setComment("sssss");
        schemaBefore.setDataType(AdbMySqlTypes.DATE);
        schemaBefore.setDefaultValue("aaa");
        schemaBefore.getConstraints().add(new Primary());
        schemaBefore.getAttributes().setValue("abc", "123");
        //
        String jsonData = SerializerRegistry.getSerializer(ValueUmiSchema.class).apply(schemaBefore);
        ValueUmiSchema schemaAfter = (ValueUmiSchema) SerializerRegistry.getDeserializer(ValueUmiSchema.class).apply(jsonData);
        //
        diffAbstractUmi(schemaBefore, schemaAfter);
    }

    @Test
    public void strutsUmiSchemaTest() {
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
        String jsonData = SerializerRegistry.getSerializer(StrutsUmiSchema.class).apply(schemaBefore);
        StrutsUmiSchema schemaAfter = (StrutsUmiSchema) SerializerRegistry.getDeserializer(StrutsUmiSchema.class).apply(jsonData);
        //
        diffAbstractUmi(schemaBefore, schemaAfter);
    }

    @Test
    public void mapUmiSchemaTest() {
        MapUmiSchema schemaBefore = new MapUmiSchema();
        schemaBefore.setName("abc");
        schemaBefore.setComment("sssss");
        schemaBefore.getConstraints().add(new Primary());
        schemaBefore.getAttributes().setValue("abc", "123");
        //
        String jsonData = SerializerRegistry.getSerializer(MapUmiSchema.class).apply(schemaBefore);
        MapUmiSchema schemaAfter = (MapUmiSchema) SerializerRegistry.getDeserializer(MapUmiSchema.class).apply(jsonData);
        //
        diffAbstractUmi(schemaBefore, schemaAfter);
    }

    @Test
    public void arrayUmiSchemaTest() {
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
        String jsonData = SerializerRegistry.getSerializer(ArrayUmiSchema.class).apply(schemaBefore);
        ArrayUmiSchema schemaAfter = (ArrayUmiSchema) SerializerRegistry.getDeserializer(ArrayUmiSchema.class).apply(jsonData);
        //
        diffAbstractUmi(schemaBefore, schemaAfter);
    }

    @Test
    public void constraintTest() {
        MapUmiSchema schemaBefore = new MapUmiSchema();
        schemaBefore.getConstraints().add(new NonNull());
        schemaBefore.getConstraints().add(new Primary());
        schemaBefore.getConstraints().add(new Unique());
        //
        String jsonData = SerializerRegistry.getSerializer(MapUmiSchema.class).apply(schemaBefore);
        MapUmiSchema schemaAfter = (MapUmiSchema) SerializerRegistry.getDeserializer(MapUmiSchema.class).apply(jsonData);
        //
        diffAbstractUmi(schemaBefore, schemaAfter);
        //
        assert schemaAfter.hasConstraint(NonNull.class);
        assert schemaAfter.hasConstraint(Primary.class);
        assert schemaAfter.hasConstraint(Unique.class);
    }
}
