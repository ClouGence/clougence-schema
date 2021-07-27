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
package com.clougence.schema.metadata.domain.rdb.kudu;

import java.sql.JDBCType;

import com.clougence.schema.metadata.FieldType;

/**
 * <li>https://kudu.apache.org/docs/schema_design.html</li>
 * @version : 2021-06-24
 * @author 赵永春 (zyc@hasor.net)
 */
public enum KuduTypes implements FieldType {

    INT8("int8", JDBCType.TINYINT, "INT8"),
    INT16("int16", JDBCType.SMALLINT, "INT16"),
    INT32("int32", JDBCType.INTEGER, "INT32"),
    INT64("int64", JDBCType.BIGINT, "INT64"),
    BINARY("binary", JDBCType.BINARY, "BINARY"),
    STRING("string", JDBCType.VARCHAR, "STRING"),
    BOOL("bool", JDBCType.BOOLEAN, "BOOL"),
    FLOAT("float", JDBCType.FLOAT, "FLOAT"),
    DOUBLE("double", JDBCType.DOUBLE, "DOUBLE"),
    UNIXTIME_MICROS("unixtime_micros", JDBCType.TIMESTAMP, "UNIXTIME_MICROS"),
    DECIMAL("decimal", JDBCType.DECIMAL, "DECIMAL"),
    VARCHAR("varchar", JDBCType.VARCHAR, "VARCHAR"),
    DATE("date", JDBCType.TIMESTAMP, "UNIXTIME_MICROS"),;

    private final String   codeKey;
    private final JDBCType jdbcType;
    private final String   kuduType;

    KuduTypes(String codeKey, JDBCType jdbcType, String kdType){
        this.codeKey = codeKey;
        this.jdbcType = jdbcType;
        this.kuduType = kdType;
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

    public String getKuduType() { return this.kuduType; }
}
