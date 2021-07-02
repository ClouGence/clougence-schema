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
package com.clougence.schema.umi.provider;
import java.sql.Connection;
import javax.sql.DataSource;

import com.clougence.schema.DsType;
import com.clougence.schema.metadata.MetadataServiceRegister;
import com.clougence.schema.umi.provider.rdb.MySqlUmiService;
import com.clougence.schema.umi.provider.rdb.OracleUmiService;
import com.clougence.schema.umi.provider.rdb.PostgreSqlUmiService;
import com.clougence.schema.umi.provider.rdb.RdbUmiService;

/**
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class UmiServiceRegister {

    public static RdbUmiService createRdbUmiService(DsType dsType, Connection connection) {
        switch (dsType) {
            case AdbForMySQL:
            case MySQL:
                return new MySqlUmiService(() -> MetadataServiceRegister.createMetaDataService(dsType, connection));
            case PostgreSQL:
                return new PostgreSqlUmiService(() -> MetadataServiceRegister.createMetaDataService(dsType, connection));
            case Oracle:
                return new OracleUmiService(() -> MetadataServiceRegister.createMetaDataService(dsType, connection));
            default:
                throw new UnsupportedOperationException("");
        }
    }

    public static RdbUmiService createRdbUmiService(DsType dsType, DataSource dataSource) {
        switch (dsType) {
            case AdbForMySQL:
            case MySQL:
                return new MySqlUmiService(() -> MetadataServiceRegister.createMetaDataService(dsType, dataSource));
            case PostgreSQL:
                return new PostgreSqlUmiService(() -> MetadataServiceRegister.createMetaDataService(dsType, dataSource));
            case Oracle:
                return new OracleUmiService(() -> MetadataServiceRegister.createMetaDataService(dsType, dataSource));
            default:
                throw new UnsupportedOperationException("");
        }
    }
}
