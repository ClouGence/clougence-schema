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
package com.clougence.schema.metadata.domain.rdb.adb.mysql;
import com.clougence.schema.metadata.domain.rdb.TableDef;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * AdbMySql 表
 * @version : 2021-04-01
 * @author 赵永春 (zyc@hasor.net)
 */
@Getter
@Setter
public class AdbMySqlTable implements TableDef {
    private String            catalog;
    private String            schema;
    private String            table;
    private AdbMySqlTableType tableType;
    private String            collation;
    private Date              createTime;
    private Date              updateTime;
    private String            comment;

    public String getCatalog() {
        return this.catalog;
    }

    public String getSchema() {
        return this.schema;
    }

    public String getTable() {
        return this.table;
    }

    public AdbMySqlTableType getTableType() {
        return this.tableType;
    }
}
