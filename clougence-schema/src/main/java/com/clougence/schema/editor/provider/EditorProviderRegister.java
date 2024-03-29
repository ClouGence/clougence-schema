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
package com.clougence.schema.editor.provider;

import java.util.HashMap;
import java.util.Map;

import com.clougence.schema.DsType;
import com.clougence.schema.editor.provider.mysql.MySqlEditorProvider;
import com.clougence.schema.editor.provider.postgresql.PostgreSqlEditorProvider;

/**
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class EditorProviderRegister {

    private static final Map<DsType, BuilderProvider> dialectCache = new HashMap<>();

    static {
        dialectCache.put(DsType.MySQL, new MySqlEditorProvider());
        dialectCache.put(DsType.PostgreSQL, new PostgreSqlEditorProvider());
        dialectCache.put(DsType.Oracle, new PostgreSqlEditorProvider());
    }

    public static BuilderProvider findProvider(DsType dsType) {
        if (dsType == null) {
            throw new IllegalArgumentException("dataSourceType is null.");
        }
        BuilderProvider orDefault = dialectCache.getOrDefault(dsType, null);
        if (orDefault == null) {
            throw new UnsupportedOperationException("dataSource " + dsType.name() + " Unsupported.");
        }
        return orDefault;
    }
}
