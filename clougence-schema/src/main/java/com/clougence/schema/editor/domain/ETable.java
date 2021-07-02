/*
 * Copyright 2002-2010 the original author or authors.
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
package com.clougence.schema.editor.domain;

import java.util.*;

import lombok.Getter;
import lombok.Setter;

/**
 * @author mode 2021/5/21 19:56
 */
@Getter
@Setter
public class ETable extends EChange<ETable> {

    private String              catalog;
    private String              schema;
    private String              name;
    private String              comment;
    private EPrimaryKey         primaryKey;
    private List<EColumn>       columnList  = new ArrayList<>();
    private List<EIndex>        indices     = new ArrayList<>();
    private List<EForeignKey>   foreignKeys = new ArrayList<>();
    private Map<String, String> attribute   = new HashMap<>();

    @Override
    public ETable clone() {
        ETable eTable = new ETable();
        //
        eTable.catalog = this.catalog;
        eTable.schema = this.schema;
        eTable.name = this.name;
        eTable.comment = this.comment;
        if (this.primaryKey != null) {
            eTable.primaryKey = this.primaryKey.clone();
        }
        this.columnList.forEach(o -> eTable.columnList.add(o.clone()));
        this.indices.forEach(o -> eTable.indices.add(o.clone()));
        this.foreignKeys.forEach(o -> eTable.foreignKeys.add(o.clone()));
        //
        eTable.attribute.putAll(this.attribute);
        return eTable;
    }

    @Override
    boolean testChanged(ETable initData) {
        if (initData == null) {
            return true;
        }
        if (!Objects.equals(this.catalog, initData.catalog)) {
            return true;
        }
        if (!Objects.equals(this.schema, initData.schema)) {
            return true;
        }
        if (!Objects.equals(this.name, initData.name)) {
            return true;
        }
        if (!Objects.equals(this.comment, initData.comment)) {
            return true;
        }
        if (this.primaryKey == null && initData.primaryKey == null) {
            // no Changed
        } else {
            EPrimaryKey key1 = this.primaryKey;
            EPrimaryKey key2 = initData.primaryKey;
            if (key1 == null) {
                key1 = initData.primaryKey;
                key2 = this.primaryKey;
            }
            if (key1.testChanged(key2)) {
                return true;
            }
        }
        if (EditorUtils.testList(this.columnList, initData.columnList)) {
            return true;
        }
        if (EditorUtils.testList(this.indices, initData.indices)) {
            return true;
        }
        if (EditorUtils.testList(this.foreignKeys, initData.foreignKeys)) {
            return true;
        }
        if (EditorUtils.testAttribute(this.attribute, initData.attribute)) {
            return true;
        }
        return false;
    }
}
