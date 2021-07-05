/*
 * Copyright 2002-2010 the original author or authors.
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
package com.clougence.schema.umi.special.rdb;

import net.hasor.utils.StringUtils;

/**
 * @author mode 2021/5/21 19:56
 */
public enum RdbForeignKeyRule {

    NoAction("NO ACTION"),
    Restrict("RESTRICT"),
    SetDefault("SET DEFAULT"),
    Cascade("CASCADE"),
    SetNull("SET NULL");

    private final String typeName;

    RdbForeignKeyRule(String typeName){
        this.typeName = typeName;
    }

    public String getTypeName() { return this.typeName; }

    public static RdbForeignKeyRule valueOfCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (RdbForeignKeyRule foreignKeyRule : RdbForeignKeyRule.values()) {
            if (foreignKeyRule.typeName.equals(code)) {
                return foreignKeyRule;
            }
        }
        return null;
    }
}
