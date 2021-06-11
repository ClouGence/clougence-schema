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
package com.clougence.schema.umi.provider;
import com.clougence.schema.DataSourceType;
import com.clougence.schema.metadata.MetadataServiceRegister;
import com.clougence.schema.umi.provider.rdb.MySqlUmiService;
import com.clougence.schema.umi.provider.rdb.PostgreSqlUmiService;
import com.clougence.schema.umi.provider.rdb.RdbUmiService;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class UmiServiceRegister {
    public static RdbUmiService createRdbUmiService(DataSourceType dataSourceType, Connection connection) {
        switch (dataSourceType) {
            case AdbForMySQL:
            case MySQL:
                return new MySqlUmiService(() -> MetadataServiceRegister.createMetaDataService(dataSourceType, connection));
            case PostgreSQL:
                return new PostgreSqlUmiService(() -> MetadataServiceRegister.createMetaDataService(dataSourceType, connection));
            case Oracle:
            default:
                throw new UnsupportedOperationException("");
        }
    }

    public static RdbUmiService createRdbUmiService(DataSourceType dataSourceType, DataSource dataSource) {
        switch (dataSourceType) {
            case AdbForMySQL:
            case MySQL:
                return new MySqlUmiService(() -> MetadataServiceRegister.createMetaDataService(dataSourceType, dataSource));
            case PostgreSQL:
                return new PostgreSqlUmiService(() -> MetadataServiceRegister.createMetaDataService(dataSourceType, dataSource));
            case Oracle:
            default:
                throw new UnsupportedOperationException("");
        }
    }
}
