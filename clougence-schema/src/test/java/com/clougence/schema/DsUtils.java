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
package com.clougence.schema;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.pool.DruidDataSource;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.extractor.ColumnMapResultSetExtractor;

/***
 * 创建JDBC环境
 * @version : 2014-1-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class DsUtils {

    public static String MYSQL_SCHEMA_NAME    = "devtester";
    public static String ADBMYSQL_SCHEMA_NAME = "adb_mysql_4387qyy";

    public static DruidDataSource createDs(String dbID) throws Throwable {
        DruidDataSource druid = new DruidDataSource();
        druid.setUrl("jdbc:h2:mem:test_" + dbID);
        druid.setDriverClassName("org.h2.Driver");
        druid.setUsername("sa");
        druid.setPassword("");
        druid.setMaxActive(5);
        druid.setMaxWait(3 * 1000);
        druid.setInitialSize(1);
        druid.setConnectionErrorRetryAttempts(1);
        druid.setBreakAfterAcquireFailure(true);
        druid.setTestOnBorrow(true);
        druid.setTestWhileIdle(true);
        druid.setFailFast(true);
        druid.init();
        return druid;
    }

    public static void initDB(JdbcTemplate jdbcTemplate) throws SQLException, IOException {
        // init table
        jdbcTemplate.execute((ConnectionCallback<Object>) con -> {
            ResultSet resultSet = null;
            List<Map<String, Object>> mapList;
            //
            resultSet = con.getMetaData().getTables(null, null, "tb_user", null);
            mapList = new ColumnMapResultSetExtractor().extractData(resultSet);
            if (!mapList.isEmpty()) {
                jdbcTemplate.executeUpdate("drop table tb_user");
            }
            resultSet = con.getMetaData().getTables(null, null, "tb_h2types", null);
            mapList = new ColumnMapResultSetExtractor().extractData(resultSet);
            if (!mapList.isEmpty()) {
                jdbcTemplate.executeUpdate("drop table tb_h2types");
            }
            return null;
        });
        //
        jdbcTemplate.loadSQL("net_hasor_db/tb_user_for_h2.sql");
        jdbcTemplate.loadSQL("net_hasor_db/all_types/tb_h2_types.sql");
    }

    public static Connection localMySQL() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://192.168.0.152:50203/devtester?allowMultiQueries=true", "root", "123456");
    }

    public static Connection aliyunAdbMySQL() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://am-wz99xu17yks5p9e3f90650o.ads.aliyuncs.com:3306/adb_mysql_4387qyy", "lab_1930494464", "3c8cb997a455_#@Aa");
    }
    //

    public static Connection localPg() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://192.168.0.125:5432/postgres", "postgres", "123456");
    }

    public static Connection localOracle() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.125:1521:orcl", "SYSTEM", "oracle");
        connection.createStatement().execute("alter session set current_schema = TESTER");
        return connection;
    }
}
