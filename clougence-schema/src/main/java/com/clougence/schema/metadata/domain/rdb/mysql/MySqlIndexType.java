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
package com.clougence.schema.metadata.domain.rdb.mysql;

/**
 * MySQL 索引类型
 * 
 * @version : 2021-03-30
 * @author 赵永春 (zyc@hasor.net)
 */
public enum MySqlIndexType {

    /** 普通索引 */
    Normal,
    /** 唯一索引 */
    Unique,
    /** 主键索引 */
    Primary,
    /** 外建索引 */
    Foreign,;

    public static MySqlIndexType valueOfCode(String code) {
        for (MySqlIndexType indexType : MySqlIndexType.values()) {
            if (indexType.name().equalsIgnoreCase(code)) {
                return indexType;
            }
        }
        return null;
    }
}
