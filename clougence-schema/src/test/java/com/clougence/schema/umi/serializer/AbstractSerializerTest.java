package com.clougence.schema.umi.serializer;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.clougence.schema.umi.*;
import com.clougence.schema.umi.constraint.NonNull;
import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.constraint.Unique;
import com.clougence.schema.umi.special.rdb.RdbColumn;

public class AbstractSerializerTest {

    private void diffBasicUmi(AbstractUmiSchema data1, AbstractUmiSchema data2) {

        assert Objects.equals(data1.getName(), data2.getName());
        assert Objects.equals(data1.getComment(), data2.getComment());
        assert data1.getDataType() == data2.getDataType();
        //
        String[] parentPath1 = data1.getParentPath();
        String[] parentPath2 = data2.getParentPath();
        if (parentPath1 != null && parentPath2 != null) {
            assert parentPath1.length == parentPath2.length;
            for (int i = 0; i < parentPath1.length; i++) {
                assert Objects.equals(parentPath1[i], parentPath2[i]);
            }
        } else {
            assert parentPath1 == parentPath2;//both null.
        }
        //
        assert data1.hasConstraint(NonNull.class) == data2.hasConstraint(NonNull.class);
        assert data1.hasConstraint(Primary.class) == data2.hasConstraint(Primary.class);
        assert data1.hasConstraint(Unique.class) == data2.hasConstraint(Unique.class);
        //
        Set<String> attrKeys1 = data1.getAttributes().getAttribute().keySet();
        Set<String> attrKeys2 = data2.getAttributes().getAttribute().keySet();
        assert attrKeys1.size() == attrKeys2.size();
        //
        Set<String> mergeTest = new HashSet<>();
        mergeTest.addAll(attrKeys1);
        mergeTest.addAll(attrKeys2);
        assert mergeTest.size() == attrKeys1.size() && mergeTest.size() == attrKeys2.size();
        //
        for (String key : mergeTest) {
            String v1 = data1.getAttributes().getValue(key);
            String v2 = data2.getAttributes().getValue(key);
            assert Objects.equals(v1, v2);
        }
    }

    public void diffAbstractUmi(AbstractUmiSchema data1, AbstractUmiSchema data2) {
        assert data1 != null || data1 == data2;
        assert data1.getClass() == data2.getClass();

        if (data1 instanceof ValueUmiSchema) {
            diffValueUmi((ValueUmiSchema) data1, (ValueUmiSchema) data2);
        }
        if (data1 instanceof StrutsUmiSchema) {
            diffStrutsUmi((StrutsUmiSchema) data1, (StrutsUmiSchema) data2);
        }
        if (data1 instanceof MapUmiSchema) {
            diffMapUmi((MapUmiSchema) data1, (MapUmiSchema) data2);
        }
        if (data1 instanceof ArrayUmiSchema) {
            diffArraysUmi((ArrayUmiSchema) data1, (ArrayUmiSchema) data2);
        }
        if (data1 instanceof RdbColumn) {
            diffRdbColumn((RdbColumn) data1, (RdbColumn) data2);
        }
    }

    private void diffValueUmi(ValueUmiSchema data1, ValueUmiSchema data2) {
        diffBasicUmi(data1, data2);
        assert data1.getDefaultValue().equals(data2.getDefaultValue());
    }

    private void diffStrutsUmi(StrutsUmiSchema data1, StrutsUmiSchema data2) {
        diffBasicUmi(data1, data2);
        assert data1.getDataType() == UmiStrutsTypes.Struts;
        assert data2.getDataType() == UmiStrutsTypes.Struts;
        //
        Map<String, AbstractUmiSchema> data1Properties = data1.getProperties();
        Map<String, AbstractUmiSchema> data2Properties = data2.getProperties();
        assert data1Properties.size() == data2Properties.size();
        //
        Set<String> attrKeys1 = data1Properties.keySet();
        Set<String> attrKeys2 = data2Properties.keySet();
        assert attrKeys1.size() == attrKeys2.size();
        //
        Set<String> mergeTest = new HashSet<>();
        mergeTest.addAll(attrKeys1);
        mergeTest.addAll(attrKeys2);
        assert mergeTest.size() == attrKeys1.size() && mergeTest.size() == attrKeys2.size();
        //
        for (String key : mergeTest) {
            AbstractUmiSchema v1 = data1Properties.get(key);
            AbstractUmiSchema v2 = data2Properties.get(key);
            diffAbstractUmi(v1, v2);
        }
    }

    private void diffMapUmi(MapUmiSchema data1, MapUmiSchema data2) {
        diffBasicUmi(data1, data2);
        assert data1.getDataType() == UmiStrutsTypes.Map;
        assert data2.getDataType() == UmiStrutsTypes.Map;
    }

    private void diffArraysUmi(ArrayUmiSchema data1, ArrayUmiSchema data2) {
        diffBasicUmi(data1, data2);
        assert data1.getDataType() == UmiStrutsTypes.Array;
        assert data2.getDataType() == UmiStrutsTypes.Array;
    }

    private void diffRdbColumn(RdbColumn data1, RdbColumn data2) {
        diffBasicUmi(data1, data2);
        assert Objects.equals(data1.getCharLength(), data2.getCharLength());
        assert Objects.equals(data1.getByteLength(), data2.getByteLength());
        assert Objects.equals(data1.getNumericPrecision(), data2.getNumericPrecision());
        assert Objects.equals(data1.getNumericScale(), data2.getNumericScale());
        assert Objects.equals(data1.getDatetimePrecision(), data2.getDatetimePrecision());
    }

}
