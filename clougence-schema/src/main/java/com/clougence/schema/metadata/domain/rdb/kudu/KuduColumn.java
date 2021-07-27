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

import com.clougence.schema.metadata.domain.rdb.ColumnDef;
import lombok.Getter;
import lombok.Setter;

/**
 * Kudu Column
 * @version : 2021-04-01
 * @author 赵永春 (zyc@hasor.net)
 */
@Getter
@Setter
public class KuduColumn implements ColumnDef {

    private String    name;
    private KuduTypes kuduTypes;
    private String    columnType;
    private String    comment = "";
    private boolean   primaryKey;
    private boolean   partition;

    private boolean   nullable;
    private Object    defaultValue;
    private Integer   precision;
    private Integer   scale;
    private Integer   length;              // 1.10 not suppout

    private Integer   desiredBlockSize;
    private String    encoding;
    private String    compressionAlgorithm;
    private Integer   typeSize;

    @Override
    public JDBCType getJdbcType() { return (kuduTypes == null) ? null : kuduTypes.toJDBCType(); }
}
