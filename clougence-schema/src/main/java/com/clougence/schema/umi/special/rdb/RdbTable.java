/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clougence.schema.umi.special.rdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.clougence.schema.umi.StrutsUmiSchema;
import com.clougence.schema.umi.constraint.GeneralConstraintType;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonDeserializer;
import com.clougence.schema.umi.serializer.jackson.AbstractJsonSerializer;
import com.clougence.schema.umi.serializer.special.rdb.RdbTableSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

/**
 * 结构类型
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-05-21
 */
@Getter
@Setter
@JsonSerialize(using = RdbTable.JacksonSerializer.class)
@JsonDeserialize(using = RdbTable.JacksonDeserializer.class)
public class RdbTable extends StrutsUmiSchema {

    public static class JacksonDeserializer extends AbstractJsonDeserializer<RdbTable> {

        public JacksonDeserializer(){
            super(new RdbTableSerializer());
        }
    }

    public static class JacksonSerializer extends AbstractJsonSerializer<RdbTable> {

        public JacksonSerializer(){
            super(new RdbTableSerializer());
        }
    }

    private List<RdbIndex> indices = new ArrayList<>();

    public RdbPrimaryKey getPrimaryKey() {
        List<RdbPrimaryKey> primaryKeys = this.getConstraint(RdbPrimaryKey.class, GeneralConstraintType.Primary);
        return primaryKeys.isEmpty() ? null : primaryKeys.get(0);
    }

    public void setPrimaryKey(RdbPrimaryKey primaryKey) {
        this.overwriteConstraint(RdbPrimaryKey.class, GeneralConstraintType.Primary, Collections.singletonList(primaryKey));
    }

    public List<RdbUniqueKey> getUniqueKey() { return this.getConstraint(RdbUniqueKey.class, GeneralConstraintType.Unique); }

    public void setUniqueKey(List<RdbUniqueKey> uniqueKeys) {
        this.overwriteConstraint(RdbUniqueKey.class, GeneralConstraintType.Unique, uniqueKeys);
    }

    public List<RdbForeignKey> getForeignKey() { return this.getConstraint(RdbForeignKey.class); }

    public void setForeignKey(List<RdbForeignKey> foreignKeys) {
        this.overwriteConstraint(RdbForeignKey.class, foreignKeys);
    }

    public RdbTableType getTableType() {
        String value = getAttributes().getValue(RdbTableType.class.getName());
        return RdbTableType.valueOfCode(value);
    }

    public void setTableType(RdbTableType tableType) {
        getAttributes().setValue(RdbTableType.class.getName(), tableType.getTypeName());
    }
}
