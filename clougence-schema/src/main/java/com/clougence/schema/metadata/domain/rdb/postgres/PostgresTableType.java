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
package com.clougence.schema.metadata.domain.rdb.postgres;

import com.clougence.schema.metadata.domain.rdb.TableType;

/**
 * Postgres 的表类型
 * @version : 2021-05-10
 * @author 赵永春 (zyc@hasor.net)
 */
public enum PostgresTableType implements TableType {

    Table("BASE TABLE"),
    View("VIEW"),
    ForeignTable("FOREIGN"),
    LocalTemporary("LOCAL TEMPORARY"),
    Materialized("MATERIALIZED VIEW"),;

    private final String typeName;

    PostgresTableType(String typeName){
        this.typeName = typeName;
    }

    public String getTypeName() { return this.typeName; }

    public static PostgresTableType valueOfCode(String code) {
        for (PostgresTableType tableType : PostgresTableType.values()) {
            if (tableType.typeName.equalsIgnoreCase(code)) {
                return tableType;
            }
        }
        return null;
    }
}
