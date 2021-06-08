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
package com.clougence.schema.metadata.domain.jdbc;
import com.clougence.schema.metadata.TableDef;
import lombok.Getter;
import lombok.Setter;

/**
 * Jdbc Table
 * @version : 2020-04-25
 * @author 赵永春 (zyc@hasor.net)
 */
@Getter
@Setter
public class JdbcTable implements TableDef {
    private String        catalog;
    private String        schema;
    private String        table;
    private JdbcTableType tableType;
    private String        tableTypeString;
    private String        comment;
    //
    private String        typeCatalog;
    private String        typeSchema;
    private String        typeName;
    private String        selfReferencingColName;
    private String        refGeneration;

    @Override
    public String getCatalog() {
        return this.catalog;
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    @Override
    public String getTable() {
        return this.table;
    }

    @Override
    public JdbcTableType getTableType() {
        return this.tableType;
    }
}