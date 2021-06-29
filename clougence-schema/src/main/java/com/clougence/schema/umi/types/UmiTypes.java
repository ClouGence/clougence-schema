/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clougence.schema.umi.types;
import com.clougence.schema.metadata.FieldType;

import java.sql.JDBCType;

/**
 * 类型
 * @version : 2021-05-10
 * @author 赵永春 (zyc@hasor.net)
 */
public enum UmiTypes implements FieldType {
    Catalog("CATALOG"),
    Schema("SCHEMA"),
    Table("TABLE"),
    View("VIEW"),
    Column("COLUMN"),
    ;
    //
    private final String typeName;

    UmiTypes(String typeName) {
        this.typeName = typeName;
    }

    public static UmiTypes valueOfCode(String typeName) {
        for (UmiTypes tableType : UmiTypes.values()) {
            if (tableType.typeName.equalsIgnoreCase(typeName)) {
                return tableType;
            }
        }
        return null;
    }

    @Override
    public java.lang.String getCodeKey() {
        return this.typeName;
    }

    @Override
    public java.lang.Integer getJdbcType() {
        return null;
    }

    @Override
    public JDBCType toJDBCType() {
        return null;
    }
}
