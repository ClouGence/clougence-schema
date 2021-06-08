/*
 * Copyright 2008-2009 the original author or authors.
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
package com.clougence.schema.metadata.jdbc;
import com.clougence.schema.metadata.domain.jdbc.JdbcTable;
import com.clougence.schema.metadata.AbstractMetadataServiceSupplierTest;
import com.clougence.schema.metadata.DsUtils;
import com.clougence.schema.metadata.provider.JdbcMetadataProvider;
import net.hasor.db.jdbc.core.JdbcTemplate;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/***
 *
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class Jdbc4PostgresMetadataServiceSupplierTest extends AbstractMetadataServiceSupplierTest<JdbcMetadataProvider> {
    @Override
    protected Connection initConnection() throws SQLException {
        return DsUtils.localPg();
    }

    @Override
    protected JdbcMetadataProvider initRepository(Connection con) {
        return new JdbcMetadataProvider(con);
    }

    @Override
    protected void beforeTest(JdbcTemplate jdbcTemplate, JdbcMetadataProvider repository) throws SQLException, IOException {
        applySql("drop database if exists tester_db");
        applySql("create database tester_db");
        //
        applySql("drop schema if exists tester cascade");
        applySql("create schema tester");
        applySql("set search_path = \"tester\"");
        //
        applySql("drop materialized view tb_user_view_m");
        applySql("drop table tb_user");
        applySql("drop table proc_table_ref");
        applySql("drop table proc_table");
        applySql("drop table t3");
        applySql("drop table t1");
        applySql("drop table tb_postgre_types");
        //
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/metadata/postgre_script.sql");
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/all_types/tb_postgre_types.sql");
    }

    @Test
    public void getCatalogsTest() throws SQLException {
        List<String> catalogs = this.repository.getCatalogs();
        assert catalogs.size() == 1;
        assert catalogs.contains("postgres");
        assert !catalogs.contains("tester_db");// JDBC 实现中无法察觉其它数据库
    }

    @Test
    public void getSchemasTest() throws SQLException {
        List<com.clougence.schema.metadata.domain.jdbc.JdbcSchema> schemas = this.repository.getSchemas();
        List<String> collect = schemas.stream().map(com.clougence.schema.metadata.domain.jdbc.JdbcSchema::getSchema).collect(Collectors.toList());
        assert collect.contains("tester");
        assert collect.contains("information_schema");
        assert collect.contains("public");
        assert collect.contains("pg_catalog");
    }

    @Test
    public void getSchemaTest() throws SQLException {
        com.clougence.schema.metadata.domain.jdbc.JdbcSchema schema1 = this.repository.getSchemaByName(null, "abc");
        com.clougence.schema.metadata.domain.jdbc.JdbcSchema schema2 = this.repository.getSchemaByName(null, "tester");
        assert schema1 == null;
        assert schema2 != null;
    }

    @Test
    public void getTables() throws SQLException {
        List<com.clougence.schema.metadata.domain.jdbc.JdbcTable> tableList = this.repository.findTables(null, "tester", new String[] { "proc_table", "proc_table_ref", "t1" });
        assert tableList.size() == 3;
        List<String> tableNames = tableList.stream().map(com.clougence.schema.metadata.domain.jdbc.JdbcTable::getTable).collect(Collectors.toList());
        assert tableNames.contains("proc_table");
        assert tableNames.contains("proc_table_ref");
        assert tableNames.contains("t1");
    }

    @Test
    public void getTable() throws SQLException {
        com.clougence.schema.metadata.domain.jdbc.JdbcTable tableObj1 = this.repository.getTable(null, "tester", "proc_table");
        com.clougence.schema.metadata.domain.jdbc.JdbcTable tableObj2 = this.repository.getTable(null, "tester", "abc");
        com.clougence.schema.metadata.domain.jdbc.JdbcTable tableObj3 = this.repository.getTable(null, "tester", "tb_user_view");
        com.clougence.schema.metadata.domain.jdbc.JdbcTable tableObj4 = this.repository.getTable(null, "tester", "tb_user_view_m");
        assert tableObj1 != null && tableObj1.getTableType() == com.clougence.schema.metadata.domain.jdbc.JdbcTableType.Table;
        assert tableObj2 == null;
        assert tableObj3 != null && tableObj3.getTableType() == com.clougence.schema.metadata.domain.jdbc.JdbcTableType.View;
        assert tableObj4 != null && tableObj4.getTableType() == com.clougence.schema.metadata.domain.jdbc.JdbcTableType.Materialized;
    }

    @Test
    public void getColumns_1() throws SQLException {
        List<com.clougence.schema.metadata.domain.jdbc.JdbcColumn> columnList = this.repository.getColumns(null, "tester", "tb_postgre_types");
        //
        Map<String, com.clougence.schema.metadata.domain.jdbc.JdbcColumn> columnMap = columnList.stream().collect(Collectors.toMap(com.clougence.schema.metadata.domain.jdbc.JdbcColumn::getName, c -> c));
        assert columnMap.containsKey("c_decimal");
        assert columnMap.containsKey("c_date");
        assert columnMap.containsKey("c_timestamp");
        assert columnMap.containsKey("c_numeric_p");
        assert columnMap.containsKey("c_text");
        assert columnMap.containsKey("c_char_n");
        assert columnMap.containsKey("c_character_varying");
        assert columnMap.containsKey("c_uuid");
        assert columnMap.containsKey("c_int4range");
        assert columnMap.containsKey("c_timestamp_n");
        //
        assert columnMap.get("c_decimal").getJdbcType() == JDBCType.NUMERIC;
        assert columnMap.get("c_date").getJdbcType() == JDBCType.DATE;
        assert columnMap.get("c_timestamp").getJdbcType() == JDBCType.TIMESTAMP;
        assert columnMap.get("c_numeric_p").getJdbcType() == JDBCType.NUMERIC;
        assert columnMap.get("c_text").getJdbcType() == JDBCType.VARCHAR;
        assert columnMap.get("c_char_n").getJdbcType() == JDBCType.CHAR;
        assert columnMap.get("c_character_varying").getJdbcType() == JDBCType.VARCHAR;
        assert columnMap.get("c_uuid").getJdbcType() == JDBCType.OTHER;
        assert columnMap.get("c_int4range").getJdbcType() == JDBCType.OTHER;
        assert columnMap.get("c_timestamp_n").getJdbcType() == JDBCType.TIMESTAMP;
    }

    @Test
    public void getColumns_2() throws SQLException {
        List<com.clougence.schema.metadata.domain.jdbc.JdbcColumn> columnList = this.repository.getColumns(null, "tester", "proc_table_ref");
        Map<String, com.clougence.schema.metadata.domain.jdbc.JdbcColumn> columnMap = columnList.stream().collect(Collectors.toMap(com.clougence.schema.metadata.domain.jdbc.JdbcColumn::getName, c -> c));
        assert columnMap.size() == 6;
        assert columnMap.get("r_int").isPrimaryKey();
        assert columnMap.get("r_int").isUniqueKey();
        assert !columnMap.get("r_k1").isPrimaryKey();
        assert !columnMap.get("r_k1").isUniqueKey(); // JDBC 驱动无法识别 FK 的联合唯一特性
        assert !columnMap.get("r_k2").isPrimaryKey();
        assert !columnMap.get("r_k2").isUniqueKey(); // JDBC 驱动无法识别 FK 的联合唯一特性
        assert !columnMap.get("r_name").isPrimaryKey();
        assert columnMap.get("r_name").isUniqueKey();
        assert !columnMap.get("r_index").isPrimaryKey();
        assert !columnMap.get("r_index").isUniqueKey();
        assert !columnMap.get("r_data").isPrimaryKey();
        assert !columnMap.get("r_data").isUniqueKey();
    }

    @Test
    public void getColumns_3() throws SQLException {
        List<com.clougence.schema.metadata.domain.jdbc.JdbcColumn> columnList = this.repository.getColumns(null, "tester", "tb_user");
        Map<String, com.clougence.schema.metadata.domain.jdbc.JdbcColumn> columnMap = columnList.stream().collect(Collectors.toMap(com.clougence.schema.metadata.domain.jdbc.JdbcColumn::getName, c -> c));
        assert columnMap.size() == 7;
        assert columnMap.get("useruuid").isPrimaryKey();
        assert columnMap.get("useruuid").isUniqueKey();
        assert !columnMap.get("email").isPrimaryKey();
        assert columnMap.get("email").isUniqueKey();
    }

    @Test
    public void getConstraint1() throws SQLException {
        List<com.clougence.schema.metadata.domain.jdbc.JdbcConstraint> columnList = this.repository.getConstraint(null, "tester", "proc_table_ref");
        Map<String, com.clougence.schema.metadata.domain.jdbc.JdbcConstraintType> typeMap = columnList.stream().collect(Collectors.toMap(com.clougence.schema.metadata.domain.jdbc.JdbcConstraint::getName, com.clougence.schema.metadata.domain.jdbc.JdbcConstraint::getConstraintType));
        assert typeMap.size() == 2;
        assert typeMap.containsKey("proc_table_ref_pkey");
        assert typeMap.containsKey("ptr");
        assert typeMap.get("proc_table_ref_pkey") == com.clougence.schema.metadata.domain.jdbc.JdbcConstraintType.PrimaryKey;
        assert typeMap.get("ptr") == com.clougence.schema.metadata.domain.jdbc.JdbcConstraintType.ForeignKey;
    }

    @Test
    public void getConstraint2() throws SQLException {
        List<com.clougence.schema.metadata.domain.jdbc.JdbcConstraint> columnList = this.repository.getConstraint(null, "tester", "proc_table_ref", com.clougence.schema.metadata.domain.jdbc.JdbcConstraintType.ForeignKey);
        Map<String, com.clougence.schema.metadata.domain.jdbc.JdbcConstraintType> typeMap = columnList.stream().collect(Collectors.toMap(com.clougence.schema.metadata.domain.jdbc.JdbcConstraint::getName, com.clougence.schema.metadata.domain.jdbc.JdbcConstraint::getConstraintType));
        assert typeMap.size() == 1;
        assert typeMap.containsKey("ptr");
        assert typeMap.get("ptr") == com.clougence.schema.metadata.domain.jdbc.JdbcConstraintType.ForeignKey;
    }

    @Test
    public void getPrimaryKey1() throws SQLException {
        com.clougence.schema.metadata.domain.jdbc.JdbcPrimaryKey primaryKey = this.repository.getPrimaryKey(null, "tester", "proc_table_ref");
        assert primaryKey.getConstraintType() == com.clougence.schema.metadata.domain.jdbc.JdbcConstraintType.PrimaryKey;
        assert primaryKey.getName().equals("proc_table_ref_pkey");
        assert primaryKey.getColumns().size() == 1;
        assert primaryKey.getColumns().contains("r_int");
    }

    @Test
    public void getPrimaryKey2() throws SQLException {
        com.clougence.schema.metadata.domain.jdbc.JdbcPrimaryKey primaryKey = this.repository.getPrimaryKey(null, "tester", "proc_table");
        assert primaryKey.getConstraintType() == com.clougence.schema.metadata.domain.jdbc.JdbcConstraintType.PrimaryKey;
        assert primaryKey.getName().equals("proc_table_pkey");
        assert primaryKey.getColumns().size() == 2;
        assert primaryKey.getColumns().contains("c_id");
        assert primaryKey.getColumns().contains("c_name");
    }

    @Test
    public void getPrimaryKey3() throws SQLException {
        JdbcTable table = this.repository.getTable(null, "tester", "t3");
        com.clougence.schema.metadata.domain.jdbc.JdbcPrimaryKey primaryKey = this.repository.getPrimaryKey(null, "tester", "t3");
        assert table != null;
        assert primaryKey == null;
    }

    @Test
    public void getUniqueKey() throws SQLException {
        List<com.clougence.schema.metadata.domain.jdbc.JdbcIndex> uniqueKeyList = this.repository.getUniqueKey(null, "tester", "tb_user");
        Map<String, com.clougence.schema.metadata.domain.jdbc.JdbcIndex> uniqueKeyMap = uniqueKeyList.stream().collect(Collectors.toMap(com.clougence.schema.metadata.domain.jdbc.JdbcIndex::getName, u -> u));
        assert uniqueKeyMap.size() == 2;
        assert uniqueKeyMap.containsKey("tb_user_useruuid_uindex");
        assert uniqueKeyMap.get("tb_user_useruuid_uindex").getColumns().size() == 1;
        assert uniqueKeyMap.get("tb_user_useruuid_uindex").getColumns().contains("useruuid");
        //
        assert uniqueKeyMap.containsKey("tb_user_email_useruuid_uindex");
        assert uniqueKeyMap.get("tb_user_email_useruuid_uindex").getColumns().size() == 2;
        assert uniqueKeyMap.get("tb_user_email_useruuid_uindex").getColumns().contains("useruuid");
        assert uniqueKeyMap.get("tb_user_email_useruuid_uindex").getColumns().contains("email");
    }

    @Test
    public void getForeignKey() throws SQLException {
        List<com.clougence.schema.metadata.domain.jdbc.JdbcForeignKey> foreignKeyList1 = this.repository.getForeignKey(null, "tester", "tb_user");
        assert foreignKeyList1.size() == 0;
        List<com.clougence.schema.metadata.domain.jdbc.JdbcForeignKey> foreignKeyList2 = this.repository.getForeignKey(null, "tester", "proc_table_ref");
        assert foreignKeyList2.size() == 1;
        com.clougence.schema.metadata.domain.jdbc.JdbcForeignKey foreignKey = foreignKeyList2.get(0);
        assert foreignKey.getConstraintType() == com.clougence.schema.metadata.domain.jdbc.JdbcConstraintType.ForeignKey;
        assert foreignKey.getColumns().size() == 2;
        assert foreignKey.getColumns().get(0).equals("r_k1");
        assert foreignKey.getColumns().get(1).equals("r_k2");
        assert foreignKey.getName().equals("ptr");
        assert foreignKey.getReferenceSchema().equals("tester");
        assert foreignKey.getReferenceTable().equals("proc_table");
        assert foreignKey.getReferenceMapping().get("r_k1").equals("c_name");
        assert foreignKey.getReferenceMapping().get("r_k2").equals("c_id");
    }

    @Test
    public void getIndexes1() throws SQLException {
        List<com.clougence.schema.metadata.domain.jdbc.JdbcIndex> indexList = this.repository.getIndexes(null, "tester", "tb_user");
        Map<String, com.clougence.schema.metadata.domain.jdbc.JdbcIndex> indexMap = indexList.stream().collect(Collectors.toMap(com.clougence.schema.metadata.domain.jdbc.JdbcIndex::getName, i -> i));
        assert indexMap.size() == 3;
        //
        assert indexMap.containsKey("tb_user_useruuid_uindex");
        assert indexMap.get("tb_user_useruuid_uindex").getColumns().size() == 1;
        assert indexMap.get("tb_user_useruuid_uindex").getColumns().get(0).equals("useruuid");
        assert indexMap.get("tb_user_useruuid_uindex").isUnique();
        //
        assert indexMap.containsKey("normal_index_tb_user");
        assert indexMap.get("normal_index_tb_user").getColumns().size() == 2;
        assert indexMap.get("normal_index_tb_user").getColumns().get(0).equals("loginpassword");
        assert indexMap.get("normal_index_tb_user").getColumns().get(1).equals("loginname");
        assert !indexMap.get("normal_index_tb_user").isUnique();
        //
        assert indexMap.containsKey("tb_user_email_useruuid_uindex");
        assert indexMap.get("tb_user_email_useruuid_uindex").getColumns().size() == 2;
        assert indexMap.get("tb_user_email_useruuid_uindex").getColumns().get(0).equals("email");
        assert indexMap.get("tb_user_email_useruuid_uindex").getColumns().get(1).equals("useruuid");
        assert indexMap.get("tb_user_email_useruuid_uindex").isUnique();
    }

    @Test
    public void getIndexes2() throws SQLException {
        List<com.clougence.schema.metadata.domain.jdbc.JdbcIndex> indexList = this.repository.getIndexes(null, "tester", "proc_table_ref");
        Map<String, com.clougence.schema.metadata.domain.jdbc.JdbcIndex> indexMap = indexList.stream().collect(Collectors.toMap(com.clougence.schema.metadata.domain.jdbc.JdbcIndex::getName, i -> i));
        //
        assert indexMap.containsKey("proc_table_ref_pkey");
        assert indexMap.get("proc_table_ref_pkey").getColumns().size() == 1;
        assert indexMap.get("proc_table_ref_pkey").getColumns().get(0).equals("r_int");
        assert indexMap.get("proc_table_ref_pkey").isUnique();
        //
        assert indexMap.containsKey("proc_table_ref_uk");
        assert indexMap.get("proc_table_ref_uk").getColumns().size() == 1;
        assert indexMap.get("proc_table_ref_uk").getColumns().get(0).equals("r_name");
        assert indexMap.get("proc_table_ref_uk").isUnique();
        //
        assert indexMap.containsKey("proc_table_ref_index");
        assert indexMap.get("proc_table_ref_index").getColumns().size() == 1;
        assert indexMap.get("proc_table_ref_index").getColumns().get(0).equals("r_index");
        assert !indexMap.get("proc_table_ref_index").isUnique();
    }

    @Test
    public void getIndexes4() throws SQLException {
        com.clougence.schema.metadata.domain.jdbc.JdbcIndex index = this.repository.getIndexes(null, "tester", "proc_table_ref", "proc_table_ref_uk");
        assert index.getName().equals("proc_table_ref_uk");
        assert index.getColumns().size() == 1;
        assert index.getColumns().get(0).equals("r_name");
        assert index.isUnique();
    }
}