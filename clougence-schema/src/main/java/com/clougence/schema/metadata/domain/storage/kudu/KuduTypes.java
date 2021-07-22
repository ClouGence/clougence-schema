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
package com.clougence.schema.metadata.domain.storage.kudu;

import java.sql.JDBCType;

import org.apache.kudu.Type;
import com.clougence.schema.metadata.FieldType;

/**
 * <li>https://kudu.apache.org/docs/schema_design.html</li>
 * @version : 2021-06-24
 * @author 赵永春 (zyc@hasor.net)
 */
public enum KuduTypes implements FieldType {

    INT8("int8", JDBCType.TINYINT, Type.INT8),
    INT16("int16", JDBCType.SMALLINT, Type.INT16),
    INT32("int32", JDBCType.INTEGER, Type.INT32),
    INT64("int64", JDBCType.BIGINT, Type.INT64),
    BINARY("binary", JDBCType.BINARY, Type.BINARY),
    STRING("string", JDBCType.VARCHAR, Type.STRING),
    BOOL("bool", JDBCType.BOOLEAN, Type.BOOL),
    FLOAT("float", JDBCType.FLOAT, Type.FLOAT),
    DOUBLE("double", JDBCType.DOUBLE, Type.DOUBLE),
    UNIXTIME_MICROS("unixtime_micros", JDBCType.TIMESTAMP, Type.UNIXTIME_MICROS),
    DECIMAL("decimal", JDBCType.DECIMAL, Type.DECIMAL),
    VARCHAR("varchar", JDBCType.VARCHAR, Type.VARCHAR),
    DATE("date", JDBCType.TIMESTAMP, Type.UNIXTIME_MICROS),;

    private final String   codeKey;
    private final JDBCType jdbcType;
    private final Type     kuduType;

    KuduTypes(String codeKey, JDBCType jdbcType, Type kuduType){
        this.codeKey = codeKey;
        this.jdbcType = jdbcType;
        this.kuduType = kuduType;
    }

    public static KuduTypes valueOfCode(String code) {
        for (KuduTypes tableType : KuduTypes.values()) {
            if (tableType.codeKey.equalsIgnoreCase(code)) {
                return tableType;
            }
        }
        return null;
    }

    @Override
    public String getCodeKey() { return this.codeKey; }

    @Override
    public Integer getJdbcType() { return this.jdbcType.getVendorTypeNumber(); }

    @Override
    public JDBCType toJDBCType() {
        return this.jdbcType;
    }

    public Type getKuduType() { return this.kuduType; }
}
