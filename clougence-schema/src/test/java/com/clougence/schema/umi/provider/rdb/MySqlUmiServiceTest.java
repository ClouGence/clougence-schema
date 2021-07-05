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
package com.clougence.schema.umi.provider.rdb;

import static com.clougence.schema.DsUtils.MYSQL_SCHEMA_NAME;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import com.clougence.schema.AbstractMetadataServiceSupplierTest;
import com.clougence.schema.DsType;
import com.clougence.schema.DsUtils;
import com.clougence.schema.metadata.domain.rdb.mysql.MySqlTypes;
import com.clougence.schema.metadata.provider.rdb.MySqlMetadataProvider;
import com.clougence.schema.umi.UmiSchema;
import com.clougence.schema.umi.UmiService;
import com.clougence.schema.umi.ValueUmiSchema;
import com.clougence.schema.umi.constraint.GeneralConstraintType;
import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.constraint.Unique;
import com.clougence.schema.umi.provider.UmiServiceRegister;
import com.clougence.schema.umi.special.rdb.*;
import com.clougence.schema.umi.types.UmiTypes;
import net.hasor.db.jdbc.core.JdbcTemplate;

/***
 *
 * @version : 2021-3-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class MySqlUmiServiceTest extends AbstractMetadataServiceSupplierTest<MySqlMetadataProvider> {

    @Override
    protected Connection initConnection() throws SQLException {
        return DsUtils.localMySQL();
    }

    @Override
    protected MySqlMetadataProvider initRepository(Connection con) {
        return new MySqlMetadataProvider(con);
    }

    @Override
    protected void beforeTest(JdbcTemplate jdbcTemplate, MySqlMetadataProvider repository) throws SQLException, IOException {
        applySql("drop table tb_user");
        applySql("drop table proc_table_ref");
        applySql("drop table proc_table");
        applySql("drop table t3");
        applySql("drop table t1");
        //
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/metadata/mysql_script.sql");
    }

    @Test
    public void getRootSchemasTest() throws SQLException {
        UmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        //
        List<UmiSchema> rootSchemas = umiService.getRootSchemas();
        List<String> collect = rootSchemas.stream().map(UmiSchema::getName).collect(Collectors.toList());
        assert collect.contains("information_schema");
        assert collect.contains("mysql");
        assert collect.contains(MYSQL_SCHEMA_NAME);
    }

    @Test
    public void getSchemaTest_01() throws SQLException {
        UmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        //
        UmiSchema schema1 = umiService.getSchemaByPath("abc");
        UmiSchema schema2 = umiService.getSchemaByPath(MYSQL_SCHEMA_NAME);
        assert schema1 == null;
        assert schema2 != null && schema2.getName().equalsIgnoreCase(MYSQL_SCHEMA_NAME);
    }

    @Test
    public void getSchemaTest_02() throws SQLException {
        UmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        List<UmiSchema> schemaByPath1 = umiService.getChildSchemaByPath("information_schema");
        List<UmiSchema> schemaByPath2 = umiService.getChildSchemaByPath("mysql");
        List<String> tablesOfSchema1 = schemaByPath1.stream().map(UmiSchema::getName).collect(Collectors.toList());
        List<String> tablesOfSchema2 = schemaByPath2.stream().map(UmiSchema::getName).collect(Collectors.toList());
        assert tablesOfSchema1.contains("COLUMNS");
        assert tablesOfSchema1.contains("TABLES");
        assert tablesOfSchema1.contains("SCHEMATA");
        assert tablesOfSchema2.size() > 3;
    }

    @Test
    public void getSchemaTest_03() throws SQLException {
        UmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        List<UmiSchema> columnSchemas = umiService.getChildSchemaByPath("information_schema", "COLUMNS");
        List<String> columnsOfTable = columnSchemas.stream().map(UmiSchema::getName).collect(Collectors.toList());
        //
        assert columnsOfTable.contains("TABLE_CATALOG");
        assert columnsOfTable.contains("TABLE_SCHEMA");
        assert columnsOfTable.contains("TABLE_NAME");
        assert columnsOfTable.contains("COLUMN_NAME");
    }

    @Test
    public void getSchemaTest_04() throws SQLException {
        UmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        UmiSchema columnSchemas = umiService.getSchemaByPath("information_schema", "COLUMNS", "COLUMN_NAME");
        //
        assert columnSchemas.getName().equalsIgnoreCase("COLUMN_NAME");
        assert columnSchemas.getDataType() == MySqlTypes.VARCHAR;
    }
    // ------------------------------------------------------------------------------------------------------

    @Test
    public void getSchemasTest() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        List<ValueUmiSchema> schemaList = umiService.getSchemas();
        List<String> collect = schemaList.stream().map(ValueUmiSchema::getName).collect(Collectors.toList());
        assert collect.contains("information_schema");
        assert collect.contains("mysql");
        assert collect.contains(MYSQL_SCHEMA_NAME);
    }

    @Test
    public void getSchemaTest() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        ValueUmiSchema schema1 = umiService.getSchema(null, "abc");
        ValueUmiSchema schema2 = umiService.getSchema(null, MYSQL_SCHEMA_NAME);
        assert schema1 == null;
        assert schema2 != null;
    }

    @Test
    public void getTables() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        List<ValueUmiSchema> mysqlTables1 = umiService.getTables(null, "mysql");
        List<ValueUmiSchema> mysqlTables2 = umiService.getTables(null, "information_schema");
        //
        List<String> tableForSchema1 = mysqlTables1.stream().map(ValueUmiSchema::getName).collect(Collectors.toList());
        assert tableForSchema1.contains("db");
        assert tableForSchema1.contains("event");
        assert tableForSchema1.contains("func");
        List<String> tableForSchema2 = mysqlTables2.stream().map(ValueUmiSchema::getName).collect(Collectors.toList());
        assert tableForSchema2.contains("COLUMNS");
        assert tableForSchema2.contains("TABLES");
        assert tableForSchema2.contains("SCHEMATA");
    }

    @Test
    public void findTables() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        List<ValueUmiSchema> tableList = umiService.getTables(null, "information_schema", new String[] { "COLUMNS", "TABLES", "SCHEMATA", "ABC" });
        List<String> tableNames = tableList.stream().map(ValueUmiSchema::getName).collect(Collectors.toList());
        assert tableNames.size() == 3;
        assert tableNames.contains("COLUMNS");
        assert tableNames.contains("TABLES");
        assert tableNames.contains("SCHEMATA");
    }

    @Test
    public void getTable() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        RdbTable tableObj1 = umiService.loadTable(null, "information_schema", "COLUMNS");
        RdbTable tableObj2 = umiService.loadTable(null, "information_schema", "ABC");
        RdbTable tableObj3 = umiService.loadTable(null, MYSQL_SCHEMA_NAME, "t3");
        assert tableObj1 != null;
        assert tableObj1.getTableType() == RdbTableType.View;
        assert tableObj2 == null;
        assert tableObj3 != null;
        assert tableObj3.getTableType() == RdbTableType.Table;
    }

    @Test
    public void getColumns_1() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        List<RdbColumn> columnList = umiService.getColumns(null, "information_schema", "COLUMNS");
        Map<String, RdbColumn> columnMap = columnList.stream().collect(Collectors.toMap(RdbColumn::getName, c -> c));
        assert columnMap.size() > 11;
        assert columnMap.containsKey("TABLE_NAME");
        assert columnMap.containsKey("TABLE_SCHEMA");
        assert columnMap.containsKey("TABLE_CATALOG");
        assert columnMap.containsKey("DATA_TYPE");
        assert columnMap.containsKey("NUMERIC_PRECISION");
        assert columnMap.containsKey("IS_NULLABLE");
        assert columnMap.containsKey("NUMERIC_SCALE");
        assert columnMap.containsKey("DATETIME_PRECISION");
        assert columnMap.containsKey("CHARACTER_MAXIMUM_LENGTH");
        assert columnMap.containsKey("CHARACTER_OCTET_LENGTH");
        assert columnMap.containsKey("COLUMN_TYPE");
        assert columnMap.get("TABLE_NAME").getTypeDef() == UmiTypes.Column;
        assert columnMap.get("TABLE_NAME").getDataType() == MySqlTypes.VARCHAR;
    }

    @Test
    public void getColumns_2() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        List<RdbColumn> columnList = umiService.getColumns(null, MYSQL_SCHEMA_NAME, "proc_table_ref");
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
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        RdbTable rdbTable = umiService.loadTable(null, MYSQL_SCHEMA_NAME, "proc_table_ref");
        assert rdbTable.hasConstraint(Primary.class);
        assert rdbTable.hasConstraint(Unique.class);
        assert rdbTable.hasConstraint(RdbPrimaryKey.class);
        assert rdbTable.hasConstraint(RdbUniqueKey.class);
        assert rdbTable.hasConstraint(RdbForeignKey.class);
    }

    @Test
    public void getConstraint2() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        RdbTable rdbTable = umiService.loadTable(null, MYSQL_SCHEMA_NAME, "proc_table_ref");
        //
        List<Primary> constraints1 = rdbTable.getConstraint(Primary.class);
        assert !constraints1.isEmpty();
        assert constraints1.get(0).getName().equals("PRIMARY");
        List<RdbPrimaryKey> constraints2 = rdbTable.getConstraint(RdbPrimaryKey.class);
        assert !constraints2.isEmpty();
        assert constraints2.get(0).getName().equals("PRIMARY");
        assert constraints1.get(0) == constraints2.get(0);
        //
        //
        List<Unique> constraints3 = rdbTable.getConstraint(Unique.class);
        assert !constraints3.isEmpty();
        assert constraints3.get(0).getName().equals("proc_table_ref_uk");
        List<RdbUniqueKey> constraints4 = rdbTable.getConstraint(RdbUniqueKey.class);
        assert !constraints4.isEmpty();
        assert constraints4.get(0).getName().equals("proc_table_ref_uk");
        assert constraints3.get(0) == constraints4.get(0);
        //
        //
        List<RdbForeignKey> constraints5 = rdbTable.getConstraint(RdbForeignKey.class);
        assert !constraints5.isEmpty();
        assert constraints5.get(0).getName().equals("ptr");
    }

    @Test
    public void getPrimaryKey1() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        RdbTable rdbTable = umiService.loadTable(null, MYSQL_SCHEMA_NAME, "proc_table_ref");
        //
        RdbPrimaryKey primaryKey = rdbTable.getPrimaryKey();
        assert primaryKey.getType() == GeneralConstraintType.Primary;
        assert primaryKey.getName().equals("PRIMARY");
        assert primaryKey.getColumnList().size() == 1;
        assert primaryKey.getColumnList().contains("r_int");
    }

    @Test
    public void getPrimaryKey2() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        RdbTable rdbTable = umiService.loadTable(null, MYSQL_SCHEMA_NAME, "proc_table");
        //
        RdbPrimaryKey primaryKey = rdbTable.getPrimaryKey();
        assert primaryKey.getType() == GeneralConstraintType.Primary;
        assert primaryKey.getName().equals("PRIMARY");
        assert primaryKey.getColumnList().size() == 2;
        assert primaryKey.getColumnList().contains("c_id");
        assert primaryKey.getColumnList().contains("c_name");
    }

    @Test
    public void getPrimaryKey3() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        ValueUmiSchema rdbTable = umiService.getTable(null, MYSQL_SCHEMA_NAME, "t3");
        RdbPrimaryKey primaryKey = umiService.getPrimaryKey(null, MYSQL_SCHEMA_NAME, "t3");
        //
        assert rdbTable != null;
        assert primaryKey == null;
    }

    @Test
    public void getUniqueKey() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        List<RdbUniqueKey> uniqueKeyList = umiService.getUniqueKey(null, MYSQL_SCHEMA_NAME, "tb_user");
        Map<String, RdbUniqueKey> uniqueKeyMap = uniqueKeyList.stream().collect(Collectors.toMap(RdbUniqueKey::getName, u -> u));
        assert uniqueKeyMap.size() == 2;
        assert uniqueKeyMap.containsKey("tb_user_userUUID_uindex");
        assert uniqueKeyMap.containsKey("tb_user_email_userUUID_uindex");
        assert uniqueKeyMap.get("tb_user_userUUID_uindex").getColumnList().size() == 1;
        assert uniqueKeyMap.get("tb_user_userUUID_uindex").getType() == GeneralConstraintType.Unique;
        assert uniqueKeyMap.get("tb_user_userUUID_uindex").getColumnList().contains("userUUID");
        assert uniqueKeyMap.get("tb_user_email_userUUID_uindex").getColumnList().size() == 2;
        assert uniqueKeyMap.get("tb_user_email_userUUID_uindex").getType() == GeneralConstraintType.Unique;
        assert uniqueKeyMap.get("tb_user_email_userUUID_uindex").getColumnList().contains("userUUID");
        assert uniqueKeyMap.get("tb_user_email_userUUID_uindex").getColumnList().contains("email");
    }

    @Test
    public void getForeignKey() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        List<RdbForeignKey> foreignKeyList1 = umiService.getForeignKey(null, MYSQL_SCHEMA_NAME, "tb_user");
        assert foreignKeyList1.size() == 0;
        List<RdbForeignKey> foreignKeyList2 = umiService.getForeignKey(null, MYSQL_SCHEMA_NAME, "proc_table_ref");
        assert foreignKeyList2.size() == 1;
        RdbForeignKey foreignKey = foreignKeyList2.get(0);
        assert foreignKey.getType().getCode().equalsIgnoreCase("FOREIGN KEY");
        assert foreignKey.getColumnList().size() == 2;
        assert foreignKey.getColumnList().get(0).equals("r_k1");
        assert foreignKey.getColumnList().get(1).equals("r_k2");
        assert foreignKey.getName().equals("ptr");
        assert foreignKey.getReferenceSchema().equals(MYSQL_SCHEMA_NAME);
        assert foreignKey.getReferenceTable().equals("proc_table");
        assert foreignKey.getReferenceMapping().get("r_k1").equals("c_id");
        assert foreignKey.getReferenceMapping().get("r_k2").equals("c_name");
    }

    @Test
    public void getIndexes1() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        List<RdbIndex> indexList = umiService.getIndexes(null, MYSQL_SCHEMA_NAME, "tb_user");
        Map<String, RdbIndex> indexMap = indexList.stream().collect(Collectors.toMap(RdbIndex::getName, i -> i));
        assert indexMap.size() == 4;
        assert indexMap.containsKey("PRIMARY");
        assert indexMap.containsKey("tb_user_userUUID_uindex");
        assert indexMap.containsKey("tb_user_email_userUUID_uindex");
        assert indexMap.containsKey("normal_index_tb_user");
        assert indexMap.get("PRIMARY").getColumnList().size() == 1;
        assert indexMap.get("PRIMARY").getColumnList().get(0).equals("userUUID");
        assert indexMap.get("PRIMARY").getType().equals("Primary");
        assert indexMap.get("tb_user_userUUID_uindex").getColumnList().size() == 1;
        assert indexMap.get("tb_user_userUUID_uindex").getColumnList().get(0).equals("userUUID");
        assert indexMap.get("tb_user_userUUID_uindex").getType().equals("Unique");
        assert indexMap.get("tb_user_email_userUUID_uindex").getColumnList().size() == 2;
        assert indexMap.get("tb_user_email_userUUID_uindex").getColumnList().get(0).equals("email");
        assert indexMap.get("tb_user_email_userUUID_uindex").getColumnList().get(1).equals("userUUID");
        assert indexMap.get("tb_user_email_userUUID_uindex").getType().equals("Unique");
        assert indexMap.get("normal_index_tb_user").getColumnList().size() == 2;
        assert indexMap.get("normal_index_tb_user").getColumnList().get(0).equals("loginPassword");
        assert indexMap.get("normal_index_tb_user").getColumnList().get(1).equals("loginName");
        assert indexMap.get("normal_index_tb_user").getType().equals("Normal");
    }

    @Test
    public void getIndexes2() throws SQLException {
        RdbUmiService umiService = UmiServiceRegister.createRdbUmiService(DsType.MySQL, this.connection);
        List<RdbIndex> indexList = umiService.getIndexes(null, MYSQL_SCHEMA_NAME, "proc_table_ref");
        Map<String, RdbIndex> indexMap = indexList.stream().collect(Collectors.toMap(RdbIndex::getName, i -> i));
        assert indexMap.size() == 4;
        assert indexMap.containsKey("PRIMARY");
        assert indexMap.containsKey("proc_table_ref_uk");
        assert indexMap.containsKey("proc_table_ref_index");
        assert indexMap.containsKey("ptr");
        assert indexMap.get("PRIMARY").getColumnList().size() == 1;
        assert indexMap.get("PRIMARY").getColumnList().get(0).equals("r_int");
        assert indexMap.get("PRIMARY").getType().equals("Primary");
        assert indexMap.get("proc_table_ref_uk").getColumnList().size() == 1;
        assert indexMap.get("proc_table_ref_uk").getColumnList().get(0).equals("r_name");
        assert indexMap.get("proc_table_ref_uk").getType().equals("Unique");
        assert indexMap.get("proc_table_ref_index").getColumnList().size() == 1;
        assert indexMap.get("proc_table_ref_index").getColumnList().get(0).equals("r_index");
        assert indexMap.get("proc_table_ref_index").getType().equals("Normal");
        assert indexMap.get("ptr").getColumnList().size() == 2;
        assert indexMap.get("ptr").getColumnList().get(0).equals("r_k1");
        assert indexMap.get("ptr").getColumnList().get(1).equals("r_k2");
        assert indexMap.get("ptr").getType().equals("Foreign");
    }
}
