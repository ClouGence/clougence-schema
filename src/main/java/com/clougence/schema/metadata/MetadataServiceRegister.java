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
package com.clougence.schema.metadata;
import com.clougence.schema.DataSourceType;
import com.clougence.schema.metadata.provider.rdb.*;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class MetadataServiceRegister {
    public static <T extends RdbMetaDataService> T createMetaDataService(DataSourceType dataSourceType, Connection connection) {
        switch (dataSourceType) {
            case AdbForMySQL:
                return (T) new AdbMySqlMetadataProvider(connection);
            case MySQL:
                return (T) new MySqlMetadataProvider(connection);
            case PostgreSQL:
                return (T) new PostgresMetadataProvider(connection);
            case Oracle:
                return (T) new OracleMetadataProvider(connection);
            default:
                return (T) new JdbcMetadataProvider(connection);
        }
    }

    public static <T extends RdbMetaDataService> T createMetaDataService(DataSourceType dataSourceType, DataSource dataSource) {
        switch (dataSourceType) {
            case AdbForMySQL:
                return (T) new AdbMySqlMetadataProvider(dataSource);
            case MySQL:
                return (T) new MySqlMetadataProvider(dataSource);
            case PostgreSQL:
                return (T) new PostgresMetadataProvider(dataSource);
            case Oracle:
                return (T) new OracleMetadataProvider(dataSource);
            default:
                return (T) new JdbcMetadataProvider(dataSource);
        }
    }
}
