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
package com.clougence.schema.metadata.domain.rdb.jdbc;
import java.sql.JDBCType;

import com.clougence.schema.metadata.domain.rdb.ColumnDef;
import lombok.Getter;
import lombok.Setter;

/**
 * Jdbc Column
 * @version : 2020-04-25
 * @author 赵永春 (zyc@hasor.net)
 */
@Getter
@Setter
public class JdbcColumn implements ColumnDef {

    private String           tableCatalog;
    private String           tableSchema;
    private String           tableName;
    private String           columnName;
    private String           columnType;
    private JdbcSqlTypes     sqlType;
    private JDBCType         jdbcType;
    private Integer          jdbcNumber;
    private JdbcNullableType nullableType;
    private Boolean          nullable;
    private Boolean          autoincrement;
    private Boolean          generatedColumn;
    private Integer          columnSize;
    private boolean          primaryKey;
    private boolean          uniqueKey;      //如若存在联合唯一索引需要借助getUniqueKey 来查询具体信息，这里只会表示该列存在至少一个唯一索引的引用。
    private String           comment;
    //
    private String           scopeCatalog;
    private String           scopeSchema;
    private String           scopeTable;
    private Integer          decimalDigits;
    private Integer          numberPrecRadix;
    private String           defaultValue;
    private Integer          charOctetLength;
    private Integer          ordinalPosition;
    private Integer          sourceDataType;

    @Override
    public String getName() { return this.getColumnName(); }

    @Override
    public String getColumnType() { return this.columnType; }

    @Override
    public JDBCType getJdbcType() { return this.jdbcType; }

    @Override
    public boolean isPrimaryKey() { return this.primaryKey; }
}
