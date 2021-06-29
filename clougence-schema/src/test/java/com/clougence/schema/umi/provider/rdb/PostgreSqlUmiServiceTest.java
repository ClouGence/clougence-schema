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
package com.clougence.schema.umi.provider.rdb;
import com.clougence.schema.AbstractMetadataServiceSupplierTest;
import com.clougence.schema.DsType;
import com.clougence.schema.DsUtils;
import com.clougence.schema.metadata.domain.rdb.postgres.PostgresTypes;
import com.clougence.schema.metadata.provider.rdb.PostgresMetadataProvider;
import com.clougence.schema.umi.UmiSchema;
import com.clougence.schema.umi.UmiService;
import com.clougence.schema.umi.ValueUmiSchema;
import com.clougence.schema.umi.constraint.GeneralConstraintType;
import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.constraint.Unique;
import com.clougence.schema.umi.provider.UmiServiceRegister;
import com.clougence.schema.umi.special.rdb.*;
import com.clougence.schema.umi.types.JavaTypes;
import com.clougence.schema.umi.types.UmiTypes;
import net.hasor.db.jdbc.core.JdbcTemplate;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/***
 *
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class PostgreSqlUmiServiceTest extends AbstractMetadataServiceSupplierTest<PostgresMetadataProvider> {
    @Override
    protected Connection initConnection() throws SQLException {
        return DsUtils.localPg();
    }

    @Override
    protected PostgresMetadataProvider initRepository(Connection con) {
        return new PostgresMetadataProvider(con);
    }

    @Override
    protected void beforeTest(JdbcTemplate jdbcTemplate, PostgresMetadataProvider repository) throws SQLException, IOException {
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
    public void getRootSchemasTest() throws SQLException {
        UmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        //
        List<UmiSchema> rootSchemas = umiService.getRootSchemas();
        List<String> collect = rootSchemas.stream().map(UmiSchema::getName).collect(Collectors.toList());
        assert collect.contains("tester_db");
    }

    @Test
    public void getSchemaTest_01() throws SQLException {
        UmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        //
        UmiSchema schema1 = umiService.getSchemaByPath("tester_db");
        assert schema1 != null;
        assert ((ValueUmiSchema) schema1).getTypeDef() == UmiTypes.Catalog;
        assert schema1.getDataType() == JavaTypes.String;
    }

    @Test
    public void getSchemaTest_02() throws SQLException {
        UmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        List<UmiSchema> schemaByPath1 = umiService.getChildSchemaByPath("tester_db");
        List<String> tablesOfSchema1 = schemaByPath1.stream().map(UmiSchema::getName).collect(Collectors.toList());
        assert schemaByPath1.isEmpty();
        assert tablesOfSchema1.isEmpty();
        //
        List<UmiSchema> schemaByPath2 = umiService.getChildSchemaByPath("postgres");
        List<String> tablesOfSchema2 = schemaByPath2.stream().map(UmiSchema::getName).collect(Collectors.toList());
        assert tablesOfSchema2.contains("tester");
        assert tablesOfSchema2.contains("information_schema");
        assert tablesOfSchema2.contains("public");
        assert tablesOfSchema2.contains("pg_catalog");
    }

    @Test
    public void getSchemaTest_03() throws SQLException {
        UmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        List<UmiSchema> schemaByPath1 = umiService.getChildSchemaByPath("postgres", "tester");
        List<String> tablesOfSchema1 = schemaByPath1.stream().map(UmiSchema::getName).collect(Collectors.toList());
        //
        assert tablesOfSchema1.contains("proc_table");
        assert tablesOfSchema1.contains("proc_table_ref");
        assert tablesOfSchema1.contains("tb_user");
        assert tablesOfSchema1.contains("tb_postgre_types");
    }

    @Test
    public void getSchemaTest_04() throws SQLException {
        UmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        List<UmiSchema> columnSchemas = umiService.getChildSchemaByPath("postgres", "tester", "proc_table");
        List<String> columnsOfTable = columnSchemas.stream().map(UmiSchema::getName).collect(Collectors.toList());
        //
        assert columnsOfTable.contains("c_id");
        assert columnsOfTable.contains("c_name");
    }

    @Test
    public void getSchemaTest_05() throws SQLException {
        UmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        UmiSchema columnSchema = umiService.getSchemaByPath("postgres", "tester", "proc_table", "c_id");
        //
        assert columnSchema.getName().equalsIgnoreCase("c_id");
        assert columnSchema.getDataType() == PostgresTypes.CHARACTER_VARYING;
        assert columnSchema instanceof RdbColumn;
        assert ((RdbColumn) columnSchema).getTypeDef() == UmiTypes.Column;
    }
    // ------------------------------------------------------------------------------------------------------

    @Test
    public void getCatalogsTest() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        List<ValueUmiSchema> catalogs = umiService.getCatalogs();
        List<String> collect = catalogs.stream().map(ValueUmiSchema::getName).collect(Collectors.toList());
        assert collect.size() >= 1;
        assert collect.contains("tester_db");
    }

    @Test
    public void getSchemasTest() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        List<ValueUmiSchema> schemas = umiService.getSchemas();
        List<String> collect = schemas.stream().map(ValueUmiSchema::getName).collect(Collectors.toList());
        assert collect.contains("tester");
        assert collect.contains("information_schema");
        assert collect.contains("public");
        assert collect.contains("pg_catalog");
    }

    @Test
    public void getSchemaTest() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        ValueUmiSchema schema1 = umiService.getSchema("postgres", "abc");
        ValueUmiSchema schema2 = umiService.getSchema("postgres", "tester");
        assert schema1 == null;
        assert schema2 != null;
    }

    @Test
    public void getTables() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        List<ValueUmiSchema> pgTables1 = umiService.getTables("postgres", "tester");
        List<ValueUmiSchema> pgTables2 = umiService.getTables("postgres", "public");
        //
        List<String> tableForSchema1 = pgTables1.stream().map(ValueUmiSchema::getName).collect(Collectors.toList());
        assert tableForSchema1.contains("proc_table");
        assert tableForSchema1.contains("proc_table_ref");
        assert tableForSchema1.contains("t1");
        assert tableForSchema1.contains("t3");
        List<String> tableForSchema2 = pgTables2.stream().map(ValueUmiSchema::getName).collect(Collectors.toList());
        assert tableForSchema2.isEmpty();
    }

    @Test
    public void findTables() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        List<ValueUmiSchema> tableList = umiService.getTables("postgres", "tester", new String[] { "proc_table_ref", "tb_user_view", "tb_user_view_m" });
        //
        List<String> tableNames = tableList.stream().map(ValueUmiSchema::getName).collect(Collectors.toList());
        assert tableNames.size() == 3;
        assert tableNames.contains("proc_table_ref");
        assert tableNames.contains("tb_user_view");
        assert tableNames.contains("tb_user_view_m");
    }

    @Test
    public void getTable() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        RdbTable tableObj1 = umiService.loadTable("postgres", "tester", "proc_table_ref");
        RdbTable tableObj2 = umiService.loadTable("postgres", "tester", "tb_user_view");
        RdbTable tableObj3 = umiService.loadTable("postgres", "tester", "abc");
        RdbTable tableObj4 = umiService.loadTable("postgres", "tester", "tb_user");
        assert tableObj1 != null;
        assert tableObj1.getTableType() == RdbTableType.Table;
        assert tableObj2 != null;
        assert tableObj2.getTableType() == RdbTableType.View;
        assert tableObj3 == null;
        assert tableObj4 != null;
        assert tableObj4.getTableType() == RdbTableType.Table;
    }

    @Test
    public void getColumns_1() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        List<RdbColumn> columnList = umiService.getColumns("postgres", "tester", "tb_postgre_types");
        //
        Map<String, RdbColumn> columnMap = columnList.stream().collect(Collectors.toMap(RdbColumn::getName, c -> c));
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
        assert columnMap.get("c_decimal").getDataType() == PostgresTypes.NUMERIC;
        assert columnMap.get("c_date").getDataType() == PostgresTypes.DATE;
        assert columnMap.get("c_timestamp").getDataType() == PostgresTypes.TIMESTAMP_WITHOUT_TIME_ZONE;
        assert columnMap.get("c_numeric_p").getDataType() == PostgresTypes.NUMERIC;
        assert columnMap.get("c_text").getDataType() == PostgresTypes.TEXT;
        assert columnMap.get("c_char_n").getDataType() == PostgresTypes.BPCHAR;
        assert columnMap.get("c_character_varying").getDataType() == PostgresTypes.CHARACTER_VARYING;
        assert columnMap.get("c_uuid").getDataType() == PostgresTypes.UUID;
        assert columnMap.get("c_int4range").getDataType() == PostgresTypes.INT4RANGE;
        assert columnMap.get("c_timestamp_n").getDataType() == PostgresTypes.TIMESTAMP_WITHOUT_TIME_ZONE;
    }

    @Test
    public void getColumns_2() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        List<RdbColumn> columnList = umiService.getColumns("postgres", "tester", "proc_table_ref");
        Map<String, RdbColumn> columnMap = columnList.stream().collect(Collectors.toMap(RdbColumn::getName, c -> c));
        assert columnMap.size() == 6;
        assert columnMap.get("r_int").hasConstraint(Primary.class);
        assert !columnMap.get("r_int").hasConstraint(Unique.class);
        assert !columnMap.get("r_k1").hasConstraint(Primary.class);
        assert !columnMap.get("r_k1").hasConstraint(Unique.class);
        assert !columnMap.get("r_k2").hasConstraint(Primary.class);
        assert !columnMap.get("r_k2").hasConstraint(Unique.class);
        assert !columnMap.get("r_name").hasConstraint(Primary.class);
        assert columnMap.get("r_name").hasConstraint(Unique.class);
        assert !columnMap.get("r_index").hasConstraint(Primary.class);
        assert !columnMap.get("r_index").hasConstraint(Unique.class);
        assert !columnMap.get("r_data").hasConstraint(Primary.class);
        assert !columnMap.get("r_data").hasConstraint(Unique.class);
    }

    @Test
    public void getConstraint1() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        RdbTable rdbTable = umiService.loadTable("postgres", "tester", "proc_table_ref");
        //
        assert rdbTable.hasConstraint(Primary.class);
        assert rdbTable.hasConstraint(Unique.class);
        assert rdbTable.hasConstraint(RdbPrimaryKey.class);
        assert rdbTable.hasConstraint(RdbUniqueKey.class);
        assert rdbTable.hasConstraint(RdbForeignKey.class);
    }

    @Test
    public void getConstraint2() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        RdbTable rdbTable = umiService.loadTable("postgres", "tester", "proc_table_ref");
        //
        List<Primary> constraints1 = rdbTable.getConstraint(Primary.class);
        assert !constraints1.isEmpty();
        assert constraints1.get(0).getName().equals("proc_table_ref_pkey");
        List<RdbPrimaryKey> constraints2 = rdbTable.getConstraint(RdbPrimaryKey.class);
        assert !constraints2.isEmpty();
        assert constraints2.get(0).getName().equals("proc_table_ref_pkey");
        assert constraints1.get(0) == constraints2.get(0);
        //
        //
        List<Unique> constraints3 = rdbTable.getConstraint(Unique.class);
        Map<String, Unique> columnMap = constraints3.stream().collect(Collectors.toMap(Unique::getName, c -> c));
        assert columnMap.size() == 2;
        assert columnMap.containsKey("proc_table_ref_pkey");
        assert columnMap.containsKey("proc_table_ref_uk");
        assert columnMap.get("proc_table_ref_pkey").getType() == GeneralConstraintType.Unique;
        assert columnMap.get("proc_table_ref_pkey").getName().equalsIgnoreCase("proc_table_ref_pkey");
        assert columnMap.get("proc_table_ref_uk").getType() == GeneralConstraintType.Unique;
        assert columnMap.get("proc_table_ref_uk").getName().equalsIgnoreCase("proc_table_ref_uk");
        //
        assert columnMap.get("proc_table_ref_pkey") instanceof RdbUniqueKey;
        assert columnMap.get("proc_table_ref_uk") instanceof RdbUniqueKey;
        //
        List<RdbForeignKey> constraints5 = rdbTable.getConstraint(RdbForeignKey.class);
        assert !constraints5.isEmpty();
        assert constraints5.get(0).getName().equals("ptr");
    }

    @Test
    public void getPrimaryKey1() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        RdbPrimaryKey primaryKey = umiService.getPrimaryKey("postgres", "tester", "proc_table_ref");
        assert primaryKey.getType() == GeneralConstraintType.Primary;
        assert primaryKey.getName().startsWith("proc_table_ref_pkey");
        assert primaryKey.getColumnList().size() == 1;
        assert primaryKey.getColumnList().contains("r_int");
    }

    @Test
    public void getPrimaryKey2() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        RdbPrimaryKey primaryKey = umiService.getPrimaryKey("postgres", "tester", "proc_table");
        assert primaryKey.getType() == GeneralConstraintType.Primary;
        assert primaryKey.getName().startsWith("proc_table_pkey");
        assert primaryKey.getColumnList().size() == 2;
        assert primaryKey.getColumnList().contains("c_id");
        assert primaryKey.getColumnList().contains("c_name");
    }

    @Test
    public void getPrimaryKey3() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        RdbTable table = umiService.loadTable("postgres", "tester", "t3");
        RdbPrimaryKey primaryKey = umiService.getPrimaryKey("postgres", "tester", "t3");
        assert table != null;
        assert primaryKey == null;
    }

    @Test
    public void getUniqueKey() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        List<RdbUniqueKey> uniqueKeyList = umiService.getUniqueKey("postgres", "tester", "tb_user");
        Map<String, RdbUniqueKey> uniqueKeyMap = uniqueKeyList.stream().collect(Collectors.toMap(RdbUniqueKey::getName, u -> u));
        assert uniqueKeyMap.size() == 2;
        //
        assert uniqueKeyMap.containsKey("tb_user_useruuid_uindex");
        assert uniqueKeyMap.get("tb_user_useruuid_uindex").getType() == GeneralConstraintType.Unique;
        assert uniqueKeyMap.get("tb_user_useruuid_uindex").getColumnList().size() == 1;
        assert uniqueKeyMap.get("tb_user_useruuid_uindex").getColumnList().contains("useruuid");
        //
        assert uniqueKeyMap.containsKey("tb_user_email_useruuid_uindex");
        assert uniqueKeyMap.get("tb_user_email_useruuid_uindex").getType() == GeneralConstraintType.Unique;
        assert uniqueKeyMap.get("tb_user_email_useruuid_uindex").getColumnList().size() == 2;
        assert uniqueKeyMap.get("tb_user_email_useruuid_uindex").getColumnList().contains("useruuid");
        assert uniqueKeyMap.get("tb_user_email_useruuid_uindex").getColumnList().contains("email");
    }

    @Test
    public void getForeignKey() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        List<RdbForeignKey> foreignKeyList1 = umiService.getForeignKey("postgres", "tester", "tb_user");
        assert foreignKeyList1.size() == 0;
        List<RdbForeignKey> foreignKeyList2 = umiService.getForeignKey("postgres", "tester", "proc_table_ref");
        assert foreignKeyList2.size() == 1;
        RdbForeignKey foreignKey = foreignKeyList2.get(0);
        assert foreignKey.getType().getCode().equalsIgnoreCase("FOREIGN KEY");
        assert foreignKey.getColumnList().size() == 2;
        assert foreignKey.getColumnList().get(0).equals("r_k1");
        assert foreignKey.getColumnList().get(1).equals("r_k2");
        assert foreignKey.getName().equals("ptr");
        assert foreignKey.getReferenceSchema().equals("tester");
        assert foreignKey.getReferenceTable().equals("proc_table");
        assert foreignKey.getReferenceMapping().get("r_k1").equals("c_name");
        assert foreignKey.getReferenceMapping().get("r_k2").equals("c_id");
    }

    @Test
    public void getIndexes1() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        List<RdbIndex> indexList = umiService.getIndexes("postgres", "tester", "tb_user");
        Map<String, RdbIndex> indexMap = indexList.stream().collect(Collectors.toMap(RdbIndex::getName, i -> i));
        assert indexMap.size() == 3;
        //
        assert indexMap.containsKey("normal_index_tb_user");
        assert indexMap.get("normal_index_tb_user").getColumnList().size() == 2;
        assert indexMap.get("normal_index_tb_user").getColumnList().get(0).equals("loginpassword");
        assert indexMap.get("normal_index_tb_user").getColumnList().get(1).equals("loginname");
        assert indexMap.get("normal_index_tb_user").getType().equals("Normal");
        //
        assert indexMap.containsKey("tb_user_email_useruuid_uindex");
        assert indexMap.get("tb_user_email_useruuid_uindex").getColumnList().size() == 2;
        assert indexMap.get("tb_user_email_useruuid_uindex").getColumnList().get(0).equals("email");
        assert indexMap.get("tb_user_email_useruuid_uindex").getColumnList().get(1).equals("useruuid");
        assert indexMap.get("tb_user_email_useruuid_uindex").getType().equals("Unique");
        //
        assert indexMap.containsKey("tb_user_useruuid_uindex");
        assert indexMap.get("tb_user_useruuid_uindex").getColumnList().size() == 1;
        assert indexMap.get("tb_user_useruuid_uindex").getColumnList().get(0).equals("useruuid");
        assert indexMap.get("tb_user_useruuid_uindex").getType().equals("Unique");
    }

    @Test
    public void getIndexes2() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.PostgreSQL, this.connection);
        List<RdbIndex> indexList = umiService.getIndexes("postgres", "tester", "proc_table_ref");
        Map<String, RdbIndex> indexMap = indexList.stream().collect(Collectors.toMap(RdbIndex::getName, i -> i));
        //
        assert indexMap.size() == 3;
        assert indexMap.containsKey("proc_table_ref_uk");
        assert indexMap.containsKey("proc_table_ref_pkey");
        assert indexMap.containsKey("proc_table_ref_index");
        //
        assert indexMap.get("proc_table_ref_uk").getColumnList().size() == 1;
        assert indexMap.get("proc_table_ref_uk").getColumnList().get(0).equals("r_name");
        assert indexMap.get("proc_table_ref_uk").getType().equals("Unique");
        //
        assert indexMap.get("proc_table_ref_pkey").getColumnList().size() == 1;
        assert indexMap.get("proc_table_ref_pkey").getColumnList().get(0).equals("r_int");
        assert indexMap.get("proc_table_ref_pkey").getType().equals("Unique");
        //
        assert indexMap.get("proc_table_ref_index").getColumnList().size() == 1;
        assert indexMap.get("proc_table_ref_index").getColumnList().get(0).equals("r_index");
        assert indexMap.get("proc_table_ref_index").getType().equals("Normal");
    }
}