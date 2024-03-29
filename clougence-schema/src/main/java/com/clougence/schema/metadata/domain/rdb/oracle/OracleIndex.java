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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Oracle 索引
 * @version : 2021-04-29
 * @author 赵永春 (zyc@hasor.net)
 */
@Getter
@Setter
public class OracleIndex {

    private String              schema;
    private String              name;
    private OracleIndexType     indexType;
    private List<String>        columns     = new ArrayList<>();
    private Map<String, String> storageType = new HashMap<>();
    //
    private boolean             primaryKey;
    private boolean             unique;
    private boolean             partitioned;                    // YES / NO
    /** Indicates whether the index is on a temporary table (Y) or not (N) */
    private boolean             temporary;                      //
    /** Indicates whether the name of the index is system-generated */
    private boolean             generated;                      //system-generated (Y) or not (N)
}
