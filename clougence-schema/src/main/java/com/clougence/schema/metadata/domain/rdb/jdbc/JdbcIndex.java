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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Jdbc 索引
 * @version : 2020-04-29
 * @author 赵永春 (zyc@hasor.net)
 */
@Getter
@Setter
public class JdbcIndex {

    private String              tableCatalog;
    private String              tableSchema;
    private String              tableName;
    //
    private boolean             unique;
    private String              name;
    private List<String>        columns     = new ArrayList<>();
    //
    private JdbcIndexType       indexType;
    private Map<String, String> storageType = new HashMap<>();
    //
    private String              indexQualifier;
    private long                cardinality;
    private long                pages;
    private String              filterCondition;
}
