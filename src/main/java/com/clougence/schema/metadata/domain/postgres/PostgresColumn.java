/*
 * Copyright 2008-2009 the original author or authors.
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
package com.clougence.schema.metadata.domain.postgres;
import com.clougence.schema.metadata.ColumnDef;
import lombok.Getter;
import lombok.Setter;

import java.sql.JDBCType;

/**
 * Postgres 的列
 * @version : 2021-05-10
 * @author 赵永春 (zyc@hasor.net)
 */
@Getter
@Setter
public class PostgresColumn implements ColumnDef {
    private String        name;
    private boolean       nullable;
    private String        columnType;
    private PostgresTypes sqlType;
    private JDBCType      jdbcType;
    private boolean       primaryKey;
    private boolean       uniqueKey;//如若存在联合唯一索引需要借助getUniqueKey 来查询具体信息，这里只会表示该列存在至少一个唯一索引的引用。
    private String        comment;
    //
    private Long          typeOid;
    private String        dataType;
    private String        elementType;
    private Integer       characterMaximumLength;
    private Integer       characterOctetLength;
    private String        defaultValue;
    //
    private Integer       numericPrecision;
    private Integer       numericPrecisionRadix;
    private Integer       numericScale;
    private Integer       datetimePrecision;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getColumnType() {
        return this.columnType;
    }

    @Override
    public JDBCType getJdbcType() {
        return this.jdbcType;
    }

    @Override
    public boolean isPrimaryKey() {
        return this.primaryKey;
    }
}
