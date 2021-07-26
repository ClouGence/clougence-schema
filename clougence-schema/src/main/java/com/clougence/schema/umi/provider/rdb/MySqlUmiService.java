package com.clougence.schema.umi.provider.rdb;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.clougence.utils.StringUtils;
import com.clougence.utils.function.ESupplier;
import com.clougence.schema.DsType;
import com.clougence.schema.metadata.domain.rdb.mysql.*;
import com.clougence.schema.metadata.provider.rdb.MySqlMetadataProvider;
import com.clougence.schema.umi.UmiSchema;
import com.clougence.schema.umi.ValueUmiSchema;
import com.clougence.schema.umi.constraint.NonNull;
import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.constraint.Unique;
import com.clougence.schema.umi.special.rdb.*;
import com.clougence.schema.umi.types.JavaTypes;
import com.clougence.schema.umi.types.UmiTypes;
import net.hasor.utils.json.JSON;

/**
 * mysql DsSchemaRService
 * 
 * @author mode 2021/1/8 19:56
 */
public class MySqlUmiService extends AbstractRdbUmiService<MySqlMetadataProvider> {

    public MySqlUmiService(ESupplier<MySqlMetadataProvider, SQLException> metadataSupplier){
        super(metadataSupplier);
    }

    @Override
    public DsType getDataSourceType() { return DsType.MySQL; }

    @Override
    public List<UmiSchema> getRootSchemas() throws SQLException { return new ArrayList<>(getSchemas()); }

    @Override
    public UmiSchema getSchemaByPath(String... parentPath) throws SQLException {
        if (parentPath.length == 0) {
            // same as root
            throw new IndexOutOfBoundsException("path need 1 element.");
        } else if (parentPath.length == 1) {
            // load schema
            return getSchema(null, parentPath[0]);
        } else if (parentPath.length == 2) {
            // load table
            return getTable(null, parentPath[0], parentPath[1]);
        } else if (parentPath.length == 3) {
            // load column
            List<RdbColumn> columns = getColumns(null, parentPath[0], parentPath[1]);
            return columns.stream().filter(valueUmiSchema -> {
                return StringUtils.equals(valueUmiSchema.getName(), parentPath[2]);
            }).findFirst().orElse(null);
        } else {
            throw new IndexOutOfBoundsException("path is the deepest 3 levels.");
        }
    }

    @Override
    public List<UmiSchema> getChildSchemaByPath(String... parentPath) throws SQLException {
        if (parentPath.length == 0) {
            // load schemas
            return new ArrayList<>(getSchemas());
        } else if (parentPath.length == 1) {
            // load tables
            return new ArrayList<>(getTables(null, parentPath[0]));
        } else if (parentPath.length == 2) {
            // load columns
            return new ArrayList<>(getColumns(null, parentPath[0], parentPath[1]));
        } else {
            throw new IndexOutOfBoundsException("path is the deepest 2 levels.");
        }
    }

    @Override
    public List<ValueUmiSchema> getCatalogs() { return Collections.emptyList(); }

    @Override
    public List<ValueUmiSchema> getSchemas() throws SQLException {
        return this.metadataSupplier.eGet().getSchemas().stream().map(this::convertSchema).collect(Collectors.toList());
    }

    @Override
    public List<ValueUmiSchema> getSchemas(String catalog) throws SQLException {
        return getSchemas();
    }

    @Override
    public ValueUmiSchema getSchema(String catalog, String schema) throws SQLException {
        MySqlSchema mysql = this.metadataSupplier.eGet().getSchema(schema);
        if (mysql != null) {
            return this.convertSchema(mysql);
        }
        return null;
    }

    @Override
    public List<ValueUmiSchema> getTables(String catalog, String schema) throws SQLException {
        List<MySqlTable> mysql = this.metadataSupplier.eGet().getAllTables(schema);
        if (mysql != null && !mysql.isEmpty()) {
            return mysql.stream().map(this::convertTable).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public ValueUmiSchema getTable(String catalog, String schema, String table) throws SQLException {
        MySqlTable mysql = this.metadataSupplier.eGet().getTable(schema, table);
        if (mysql != null) {
            return this.convertTable(mysql);
        }
        return null;
    }

    @Override
    public List<ValueUmiSchema> getTables(String catalog, String schema, String[] tables) throws SQLException {
        List<MySqlTable> mysql = this.metadataSupplier.eGet().findTable(schema, tables);
        if (mysql != null && !mysql.isEmpty()) {
            return mysql.stream().map(this::convertTable).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<RdbColumn> getColumns(String catalog, String schema, String table) throws SQLException {
        List<MySqlColumn> mysql = this.metadataSupplier.eGet().getColumns(schema, table);
        if (mysql != null && !mysql.isEmpty()) {
            return mysql.stream().map(c -> convertColumn(schema, table, c)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public RdbPrimaryKey getPrimaryKey(String catalog, String schema, String table) throws SQLException {
        MySqlPrimaryKey mysql = this.metadataSupplier.eGet().getPrimaryKey(schema, table);
        if (mysql != null) {
            return this.convertPrimaryKey(mysql);
        }
        return null;
    }

    @Override
    public List<RdbUniqueKey> getUniqueKey(String catalog, String schema, String table) throws SQLException {
        List<MySqlUniqueKey> mysql = this.metadataSupplier.eGet().getUniqueKey(schema, table);
        if (mysql != null && !mysql.isEmpty()) {
            return mysql.stream().map(this::convertUniqueKey).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<RdbForeignKey> getForeignKey(String catalog, String schema, String table) throws SQLException {
        List<MySqlForeignKey> mysql = this.metadataSupplier.eGet().getForeignKey(schema, table);
        if (mysql != null && !mysql.isEmpty()) {
            return mysql.stream().map(this::convertForeignKey).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<RdbIndex> getIndexes(String catalog, String schema, String table) throws SQLException {
        List<MySqlIndex> mysql = this.metadataSupplier.eGet().getIndexes(schema, table);
        if (mysql != null && !mysql.isEmpty()) {
            return mysql.stream().map(this::convertIndex).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    protected ValueUmiSchema convertSchema(MySqlSchema mysqlSchema) {
        ValueUmiSchema schema = new ValueUmiSchema();
        schema.setName(mysqlSchema.getName());
        schema.setParentPath(new String[0]);
        schema.setTypeDef(UmiTypes.Schema);
        schema.setDataType(JavaTypes.String);
        //
        schema.getAttributes().setValue("defaultCollationName", mysqlSchema.getDefaultCollationName());
        schema.getAttributes().setValue("defaultCharacterSetName", mysqlSchema.getDefaultCollationName());
        return schema;
    }

    protected ValueUmiSchema convertTable(MySqlTable mysqlTable) {
        ValueUmiSchema schema = new ValueUmiSchema();
        schema.setName(mysqlTable.getTable());
        schema.setParentPath(new String[] { mysqlTable.getSchema() });
        if (mysqlTable.getTableType() == MySqlTableType.View || mysqlTable.getTableType() == MySqlTableType.SystemView) {
            schema.setTypeDef(UmiTypes.View);
        } else {
            schema.setTypeDef(UmiTypes.Table);
        }
        schema.setDataType(JavaTypes.String);
        schema.setComment(mysqlTable.getComment());
        //
        schema.getAttributes().setValue("catalog", mysqlTable.getCatalog());
        schema.getAttributes().setValue("schema", mysqlTable.getSchema());
        schema.getAttributes().setValue("tableType", mysqlTable.getTableType().name());
        schema.getAttributes().setValue("collation", mysqlTable.getCollation());
        if (mysqlTable.getCreateTime() != null) {
            schema.getAttributes().setValue("createTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(mysqlTable.getCreateTime()));
        }
        if (mysqlTable.getUpdateTime() != null) {
            schema.getAttributes().setValue("updateTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(mysqlTable.getUpdateTime()));
        }
        return schema;
    }

    protected RdbColumn convertColumn(String schemaName, String tableName, MySqlColumn mysqlColumn) {
        RdbColumn schema = new RdbColumn();
        schema.setName(mysqlColumn.getName());
        schema.setParentPath(new String[] { schemaName, tableName });
        schema.setDefaultValue(mysqlColumn.getDefaultValue());
        schema.setTypeDef(UmiTypes.Column);
        schema.setDataType(mysqlColumn.getSqlType());
        schema.setComment(mysqlColumn.getComment());
        //
        if (!mysqlColumn.isNullable()) {
            schema.getConstraints().add(new NonNull());
        }
        if (mysqlColumn.isPrimaryKey()) {
            schema.getConstraints().add(new Primary());
        }
        if (mysqlColumn.isUniqueKey()) {
            schema.getConstraints().add(new Unique());
        }
        //
        schema.getAttributes().setValue("dataType", mysqlColumn.getDataType());
        if (mysqlColumn.getJdbcType() != null) {
            schema.getAttributes().setValue("jdbcType", mysqlColumn.getJdbcType().name());
        }
        //
        schema.setCharLength(mysqlColumn.getCharactersMaxLength());
        schema.setByteLength(mysqlColumn.getBytesMaxLength());
        schema.setNumericPrecision(mysqlColumn.getNumericPrecision());
        schema.setNumericScale(mysqlColumn.getNumericScale());
        schema.setDatetimePrecision(mysqlColumn.getDatetimePrecision());
        schema.getAttributes().setValue("columnType", mysqlColumn.getColumnType());
        schema.getAttributes().setValue("datetimePrecision", String.valueOf(mysqlColumn.getDatetimePrecision()));
        schema.getAttributes().setValue("numericPrecision", String.valueOf(mysqlColumn.getNumericPrecision()));
        schema.getAttributes().setValue("numericScale", String.valueOf(mysqlColumn.getNumericScale()));
        schema.getAttributes().setValue("defaultCollationName", String.valueOf(mysqlColumn.getDefaultCollationName()));
        schema.getAttributes().setValue("defaultCharacterSetName", String.valueOf(mysqlColumn.getDefaultCharacterSetName()));
        schema.getAttributes().setValue("charactersMaxLength", String.valueOf(mysqlColumn.getCharactersMaxLength()));
        schema.getAttributes().setValue("bytesMaxLength", String.valueOf(mysqlColumn.getBytesMaxLength()));
        return schema;
    }

    protected RdbPrimaryKey convertPrimaryKey(MySqlPrimaryKey mysqlPrimaryKey) {
        RdbPrimaryKey schema = new RdbPrimaryKey();
        schema.setName(mysqlPrimaryKey.getName());
        schema.setColumnList(mysqlPrimaryKey.getColumns());
        //
        schema.getAttributes().setValue("schema", mysqlPrimaryKey.getSchema());
        if (mysqlPrimaryKey.getConstraintType() != null) {
            schema.getAttributes().setValue("constraintType", mysqlPrimaryKey.getConstraintType().name());
        }
        schema.getAttributes().setValue("storageType", JSON.toString(mysqlPrimaryKey.getStorageType()));
        return schema;
    }

    protected RdbUniqueKey convertUniqueKey(MySqlUniqueKey mysqlUniqueKey) {
        RdbUniqueKey schema = new RdbUniqueKey();
        schema.setName(mysqlUniqueKey.getName());
        schema.setColumnList(mysqlUniqueKey.getColumns());
        //
        schema.getAttributes().setValue("schema", mysqlUniqueKey.getSchema());
        if (mysqlUniqueKey.getConstraintType() != null) {
            schema.getAttributes().setValue("constraintType", mysqlUniqueKey.getConstraintType().name());
        }
        schema.getAttributes().setValue("storageType", JSON.toString(mysqlUniqueKey.getStorageType()));
        return schema;
    }

    protected RdbForeignKey convertForeignKey(MySqlForeignKey mysqlForeignKey) {
        RdbForeignKey schema = new RdbForeignKey();
        schema.setName(mysqlForeignKey.getName());
        schema.setColumnList(mysqlForeignKey.getColumns());
        schema.setReferenceSchema(mysqlForeignKey.getReferenceSchema());
        schema.setReferenceTable(mysqlForeignKey.getReferenceTable());
        schema.setReferenceMapping(mysqlForeignKey.getReferenceMapping());
        schema.setUpdateRule(convertForeignKeyRule(mysqlForeignKey.getUpdateRule()));
        schema.setDeleteRule(convertForeignKeyRule(mysqlForeignKey.getDeleteRule()));
        //
        schema.getAttributes().setValue("schema", mysqlForeignKey.getSchema());
        if (mysqlForeignKey.getConstraintType() != null) {
            schema.getAttributes().setValue("constraintType", mysqlForeignKey.getConstraintType().name());
        }
        schema.getAttributes().setValue("storageType", JSON.toString(mysqlForeignKey.getStorageType()));
        return schema;
    }

    protected RdbForeignKeyRule convertForeignKeyRule(MySqlForeignKeyRule rule) {
        if (rule == null) {
            return null;
        }
        switch (rule) {
            case Cascade:
                return RdbForeignKeyRule.Cascade;
            case SetNull:
                return RdbForeignKeyRule.SetNull;
            case NoAction:
                return RdbForeignKeyRule.NoAction;
            case Restrict:
                return RdbForeignKeyRule.Restrict;
            case SetDefault:
                return RdbForeignKeyRule.SetDefault;
            default:
                return null;
        }
    }

    protected RdbIndex convertIndex(MySqlIndex mysqlIndex) {
        RdbIndex schema = new RdbIndex();
        schema.setName(mysqlIndex.getName());
        schema.setColumnList(mysqlIndex.getColumns());
        schema.setType(mysqlIndex.getIndexType().name());
        //
        schema.getAttributes().setValue("storageType", JSON.toString(mysqlIndex.getStorageType()));
        return schema;
    }
}
