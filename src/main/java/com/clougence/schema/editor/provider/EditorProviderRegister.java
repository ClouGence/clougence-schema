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
package com.clougence.schema.editor.provider;
import com.clougence.schema.DataSourceType;
import com.clougence.schema.editor.provider.mysql.MySqlEditorProvider;
import com.clougence.schema.editor.provider.postgresql.PostgreSqlEditorProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class EditorProviderRegister {
    private static final Map<DataSourceType, BuilderProvider> dialectCache = new HashMap<>();

    static {
        dialectCache.put(DataSourceType.MySQL, new MySqlEditorProvider());
        dialectCache.put(DataSourceType.PostgreSQL, new PostgreSqlEditorProvider());
    }

    public static BuilderProvider findProvider(DataSourceType dataSourceType) {
        if (dataSourceType == null) {
            throw new IllegalArgumentException("dataSourceType is null.");
        }
        BuilderProvider orDefault = dialectCache.getOrDefault(dataSourceType, null);
        if (orDefault == null) {
            throw new UnsupportedOperationException("dataSource " + dataSourceType.name() + " Unsupported.");
        }
        return orDefault;
    }
}
