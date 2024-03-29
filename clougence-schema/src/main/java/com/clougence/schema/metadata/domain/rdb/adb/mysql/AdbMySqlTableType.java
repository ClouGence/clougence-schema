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
package com.clougence.schema.metadata.domain.rdb.adb.mysql;

import com.clougence.schema.metadata.domain.rdb.TableType;

/**
 * AdbMySql 表类型
 * @version : 2021-04-01
 * @author 赵永春 (zyc@hasor.net)
 */
public enum AdbMySqlTableType implements TableType {

    /** 物化视图(Adb For MySql 3.0 特有) */
    Materialized("BASE TABLE"),
    /** 普通表 */
    Table("BASE TABLE"),
    /** 普通视图 */
    View("VIEW"),
    /** 系统视图 */
    SystemView("SYSTEM VIEW");

    private final String typeName;

    AdbMySqlTableType(String typeName){
        this.typeName = typeName;
    }

    public String getTypeName() { return this.typeName; }

    public static AdbMySqlTableType valueOfCode(String code) {
        for (AdbMySqlTableType tableType : AdbMySqlTableType.values()) {
            if (tableType.typeName.equalsIgnoreCase(code)) {
                return tableType;
            }
        }
        return null;
    }
}
