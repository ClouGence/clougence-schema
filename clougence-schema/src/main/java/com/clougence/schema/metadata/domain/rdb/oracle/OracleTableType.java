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
package com.clougence.schema.metadata.domain.rdb.oracle;

import com.clougence.schema.metadata.domain.rdb.TableType;

/**
 * Oracle 表类型
 * @version : 2021-04-29
 * @author 赵永春 (zyc@hasor.net)
 */
public enum OracleTableType implements TableType {

    Table("TABLE"),
    View("VIEW");

    private final String typeName;

    OracleTableType(String typeName){
        this.typeName = typeName;
    }

    public String getTypeName() { return this.typeName; }

    public static OracleTableType valueOfCode(String code) {
        for (OracleTableType tableType : OracleTableType.values()) {
            if (tableType.typeName.equalsIgnoreCase(code)) {
                return tableType;
            }
        }
        return null;
    }
}
