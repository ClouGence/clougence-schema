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
package com.clougence.schema.dialect;

import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.DsType;
import com.clougence.schema.dialect.provider.DmDialect;
import com.clougence.schema.dialect.provider.MySqlDialect;
import com.clougence.schema.dialect.provider.OracleDialect;
import com.clougence.schema.dialect.provider.PostgreDialect;

/**
 * 方言管理器
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class DialectRegister {

    private static final Map<DsType, Dialect> dialectCache = new HashMap<>();

    static {
        dialectCache.put(DsType.MySQL, new MySqlDialect());
        dialectCache.put(DsType.AdbForMySQL, new MySqlDialect());
        dialectCache.put(DsType.PostgreSQL, new PostgreDialect());
        dialectCache.put(DsType.Oracle, new OracleDialect());
        dialectCache.put(DsType.DM, new DmDialect());
    }

    public static Dialect findSqlDialect(DsType dsType) {
        if (dsType == null) {
            return DefaultDialect.DEFAULT;
        }
        return dialectCache.getOrDefault(dsType, DefaultDialect.DEFAULT);
    }
}
