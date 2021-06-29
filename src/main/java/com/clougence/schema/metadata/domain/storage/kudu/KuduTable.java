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

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Kudu
 * @version : 2021-06-24
 * @author 赵永春 (zyc@hasor.net)
 */
@Getter
@Setter
public class KuduTable {

    private String              tableId;
    private String              tableName;
    private int                 numReplicas;
    private String              comment;

    private Map<String, String> extraConfig;
    private String              owner;
    private long                onDiskSize;
    private long                liveRowCount;

    private List<String>        partitionColumnList;

    //    private final List<ColumnSchema>    columnsByIndex;
    //    private final List<ColumnSchema>    primaryKeyColumns;
    //    private final Map<String, Integer>  columnsByName;
    //    private final Map<Integer, Integer> columnsById;
    //    private final Map<String, Integer>  columnIdByName;
    //    private final int[]                 columnOffsets;
    //    private final int                   varLengthColumnCount;
    //    private final int                   rowSize;
    //    private final boolean               hasNullableColumns;
    //    private final int                   isDeletedIndex;
    //    private static final int            NO_IS_DELETED_INDEX = -1;

}
