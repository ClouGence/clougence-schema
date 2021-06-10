/*
 * Copyright 2002-2010 the original author or authors.
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
package com.clougence.schema.dialect;
import com.clougence.schema.DataSourceType;
import com.clougence.schema.dialect.provider.DmDialect;
import com.clougence.schema.dialect.provider.MyDialect;
import com.clougence.schema.dialect.provider.OracleDialect;
import com.clougence.schema.dialect.provider.PostgreDialect;

import java.util.HashMap;
import java.util.Map;

/**
 * 方言管理器
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class DialectRegister {
    private static final Map<DataSourceType, Dialect> dialectCache = new HashMap<>();

    static {
        dialectCache.put(DataSourceType.MySQL, new MyDialect());
        dialectCache.put(DataSourceType.AdbForMySQL, new MyDialect());
        dialectCache.put(DataSourceType.PostgreSQL, new PostgreDialect());
        dialectCache.put(DataSourceType.Oracle, new OracleDialect());
        dialectCache.put(DataSourceType.DM, new DmDialect());
    }

    public static Dialect findSqlDialect(DataSourceType dataSourceType) {
        if (dataSourceType == null) {
            return DefaultDialect.DEFAULT;
        }
        return dialectCache.getOrDefault(dataSourceType, DefaultDialect.DEFAULT);
    }
}


