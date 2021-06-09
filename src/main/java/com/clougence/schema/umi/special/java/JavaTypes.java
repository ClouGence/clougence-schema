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
package com.clougence.schema.umi.special.java;
import com.clougence.schema.metadata.FieldType;
import net.hasor.db.types.TypeHandlerRegistry;

import java.sql.JDBCType;

/**
 * Java 的 数据类型
 * @version : 2021-05-10
 * @author 赵永春 (zyc@hasor.net)
 */
public enum JavaTypes implements FieldType {
    Byte(Byte.class),
    Sort(Short.class),
    Integer(Integer.class),
    Long(Long.class),
    Float(Float.class),
    Double(Double.class),
    Character(Character.class),
    //
    Null(Void.class),
    String(String.class),
    Date(java.util.Date.class),
    Object(Object.class),
    ;
    //
    private final Class<?> javaType;

    JavaTypes(Class<?> javaType) {
        this.javaType = javaType;
    }

    public static JavaTypes valueOfCode(Class<?> code) {
        for (JavaTypes tableType : JavaTypes.values()) {
            if (tableType.javaType == code) {
                return tableType;
            }
        }
        return Object;
    }

    @Override
    public java.lang.String getCodeKey() {
        return javaType.getSimpleName();
    }

    @Override
    public java.lang.Integer getJdbcType() {
        JDBCType jdbcType = toJDBCType();
        return (jdbcType == null) ? null : jdbcType.getVendorTypeNumber();
    }

    @Override
    public JDBCType toJDBCType() {
        return TypeHandlerRegistry.toSqlType(this.javaType);
    }
}
