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
package com.clougence.schema;
/**
 * The enum Db type.
 *
 * @author wanshao create time is 2019/12/12 3:36 下午
 */
public enum DataSourceType {
    MySQL("MySQL"), //
    AdbForMySQL("AdbForMySQL"), //
    PostgreSQL("PostgreSQL"), //
    Oracle("Oracle"), //
    DM("DM"), //
    ;
    private final String typeName;

    DataSourceType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static DataSourceType valueOfCode(String code) {
        for (DataSourceType constraintType : DataSourceType.values()) {
            if (constraintType.typeName != null && constraintType.typeName.equalsIgnoreCase(code)) {
                return constraintType;
            }
        }
        return null;
    }
}
