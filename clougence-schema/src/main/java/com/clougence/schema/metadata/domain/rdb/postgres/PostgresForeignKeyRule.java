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
package com.clougence.schema.metadata.domain.rdb.postgres;

/**
 * Postgres 外建约束的及联更新策略
 * @version : 2021-05-17
 * @author 赵永春 (zyc@hasor.net)
 */
public enum PostgresForeignKeyRule {

    NoAction("NO ACTION"),
    Restrict("RESTRICT"),
    SetDefault("SET DEFAULT"),
    Cascade("CASCADE"),
    SetNull("SET NULL");

    private final String typeName;

    PostgresForeignKeyRule(String typeName){
        this.typeName = typeName;
    }

    public String getTypeName() { return this.typeName; }

    public static PostgresForeignKeyRule valueOfCode(String code) {
        for (PostgresForeignKeyRule foreignKeyRule : PostgresForeignKeyRule.values()) {
            if (foreignKeyRule.typeName.equalsIgnoreCase(code)) {
                return foreignKeyRule;
            }
        }
        return null;
    }
}
