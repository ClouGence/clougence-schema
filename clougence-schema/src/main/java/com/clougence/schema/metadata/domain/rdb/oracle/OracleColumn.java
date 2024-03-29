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
import java.sql.JDBCType;

import com.clougence.schema.metadata.domain.rdb.ColumnDef;
import lombok.Getter;
import lombok.Setter;

/**
 * Oracle 的列
 * @version : 2021-04-29
 * @author 赵永春 (zyc@hasor.net)
 */
@Getter
@Setter
public class OracleColumn implements ColumnDef {

    private String         name;
    private boolean        nullable;
    private String         columnType;
    private String         columnTypeOwner;
    private OracleSqlTypes sqlType;
    private JDBCType       jdbcType;
    private boolean        primaryKey;
    private boolean        uniqueKey;       //如若存在联合唯一索引需要借助getUniqueKey 来查询具体信息，这里只会表示该列存在至少一个唯一索引的引用。
    private String         comment;
    //
    private Long           dataBytesLength;
    private Long           dataCharLength;
    private Integer        dataPrecision;
    private Integer        dataScale;
    private String         defaultValue;
    private String         characterSetName;
    private boolean        hidden;
    private boolean        virtual;
    private boolean        identity;
    private boolean        sensitive;

    @Override
    public String getName() { return this.name; }

    @Override
    public String getColumnType() { return this.columnType; }

    @Override
    public JDBCType getJdbcType() { return this.jdbcType; }

    @Override
    public boolean isPrimaryKey() { return this.primaryKey; }
}
