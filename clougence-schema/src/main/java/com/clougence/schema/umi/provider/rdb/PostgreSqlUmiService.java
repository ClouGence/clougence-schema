package com.clougence.schema.umi.provider.rdb;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import com.clougence.schema.DsType;
import com.clougence.schema.metadata.domain.rdb.postgres.*;
import com.clougence.schema.metadata.provider.rdb.PostgresMetadataProvider;
import com.clougence.schema.umi.UmiSchema;
import com.clougence.schema.umi.ValueUmiSchema;
import com.clougence.schema.umi.constraint.NonNull;
import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.constraint.Unique;
import com.clougence.schema.umi.special.rdb.*;
import com.clougence.schema.umi.types.JavaTypes;
import com.clougence.schema.umi.types.UmiTypes;
import com.clougence.utils.StringUtils;
import com.clougence.utils.function.ESupplier;
import net.hasor.utils.json.JSON;

/**
 * @author mode 2021/1/8 19:56
 */
public class PostgreSqlUmiService extends AbstractRdbUmiService<PostgresMetadataProvider> {

    public PostgreSqlUmiService(DataSource dataSource){
        this(new PostgresMetadataProvider(dataSource));
    }

    public PostgreSqlUmiService(Connection connection){
        this(new PostgresMetadataProvider(connection));
    }

    public PostgreSqlUmiService(PostgresMetadataProvider metadataProvider){
        super(() -> metadataProvider);
    }

    public PostgreSqlUmiService(ESupplier<PostgresMetadataProvider, SQLException> metadataSupplier){
        super(metadataSupplier);
    }

    @Override
    public DsType getDataSourceType() { return DsType.PostgreSQL; }

    @Override
    public List<UmiSchema> getRootSchemas() throws SQLException { return new ArrayList<>(getCatalogs()); }

    @Override
    public UmiSchema getSchemaByPath(String... parentPath) throws SQLException {
        if (parentPath.length == 0) {
            // same as root
            throw new IndexOutOfBoundsException("path need 1 element.");
        } else if (parentPath.length == 1) {
            // load catalog
            return getCatalog(parentPath[0]);
        } else if (parentPath.length == 2) {
            // load schema
            return getSchema(parentPath[0], parentPath[1]);
        } else if (parentPath.length == 3) {
            // load table
            return getTable(parentPath[0], parentPath[1], parentPath[2]);
        } else if (parentPath.length == 4) {
            // load column
            List<RdbColumn> columns = getColumns(parentPath[0], parentPath[1], parentPath[2]);
            return columns.stream().filter(valueUmiSchema -> {
                return StringUtils.equals(valueUmiSchema.getName(), parentPath[3]);
            }).findFirst().orElse(null);
        } else {
            throw new IndexOutOfBoundsException("path more than 4 layers.");
        }
    }

    @Override
    public List<UmiSchema> getChildSchemaByPath(String... parentPath) throws SQLException {
        if (parentPath.length == 0) {
            // load catalogs
            return new ArrayList<>(getCatalogs());
        } else if (parentPath.length == 1) {
            // load schemas
            return new ArrayList<>(getSchemas(parentPath[0]));
        } else if (parentPath.length == 2) {
            // load tables
            return new ArrayList<>(getTables(parentPath[0], parentPath[1]));
        } else if (parentPath.length == 3) {
            // load columns
            return new ArrayList<>(getColumns(parentPath[0], parentPath[1], parentPath[2]));
        } else {
            throw new IndexOutOfBoundsException("path more than 3 layers.");
        }
    }

    @Override
    public List<ValueUmiSchema> getCatalogs() throws SQLException {
        return this.metadataSupplier.eGet().getCatalogs().stream().map(this::convertCatalog).collect(Collectors.toList());
    }

    public ValueUmiSchema getCatalog(String catalog) throws SQLException {
        List<String> pg = this.metadataSupplier.eGet().getCatalogs();
        if (pg.contains(catalog)) {
            return this.convertCatalog(catalog);
        }
        return null;
    }

    @Override
    public List<ValueUmiSchema> getSchemas() throws SQLException {
        String catalog = this.metadataSupplier.eGet().getCurrentCatalog();
        List<PostgresSchema> pg = this.metadataSupplier.eGet().getSchemas();
        if (pg != null && !pg.isEmpty()) {
            return pg.stream().map(s -> convertSchema(catalog, s)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<ValueUmiSchema> getSchemas(String catalog) throws SQLException {
        String currentCatalog = this.metadataSupplier.eGet().getCurrentCatalog();
        if (!StringUtils.equals(currentCatalog, catalog)) {
            return Collections.emptyList();
        }
        return getSchemas();
    }

    @Override
    public ValueUmiSchema getSchema(String catalog, String schema) throws SQLException {
        String currentCatalog = this.metadataSupplier.eGet().getCurrentCatalog();
        if (!StringUtils.equals(currentCatalog, catalog)) {
            return null;
        }
        PostgresSchema pg = this.metadataSupplier.eGet().getSchema(schema);
        if (pg != null) {
            return this.convertSchema(catalog, pg);
        }
        return null;
    }

    @Override
    public List<ValueUmiSchema> getTables(String catalog, String schema) throws SQLException {
        String currentCatalog = this.metadataSupplier.eGet().getCurrentCatalog();
        if (!StringUtils.equals(currentCatalog, catalog)) {
            return Collections.emptyList();
        }
        List<PostgresTable> pg = this.metadataSupplier.eGet().getAllTables(schema);
        if (pg != null && !pg.isEmpty()) {
            return pg.stream().map(this::convertTable).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public ValueUmiSchema getTable(String catalog, String schema, String table) throws SQLException {
        String currentCatalog = this.metadataSupplier.eGet().getCurrentCatalog();
        if (!StringUtils.equals(currentCatalog, catalog)) {
            return null;
        }
        PostgresTable pg = this.metadataSupplier.eGet().getTable(schema, table);
        if (pg != null) {
            return this.convertTable(pg);
        }
        return null;
    }

    @Override
    public List<ValueUmiSchema> getTables(String catalog, String schema, String[] tables) throws SQLException {
        String currentCatalog = this.metadataSupplier.eGet().getCurrentCatalog();
        if (!StringUtils.equals(currentCatalog, catalog)) {
            return Collections.emptyList();
        }
        List<PostgresTable> pg = this.metadataSupplier.eGet().findTable(schema, tables);
        if (pg != null && !pg.isEmpty()) {
            return pg.stream().map(this::convertTable).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<RdbColumn> getColumns(String catalog, String schema, String table) throws SQLException {
        String currentCatalog = this.metadataSupplier.eGet().getCurrentCatalog();
        if (!StringUtils.equals(currentCatalog, catalog)) {
            return Collections.emptyList();
        }
        List<PostgresColumn> pg = this.metadataSupplier.eGet().getColumns(schema, table);
        if (pg != null && !pg.isEmpty()) {
            return pg.stream().map(c -> convertColumn(catalog, schema, table, c)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public RdbPrimaryKey getPrimaryKey(String catalog, String schema, String table) throws SQLException {
        String currentCatalog = this.metadataSupplier.eGet().getCurrentCatalog();
        if (!StringUtils.equals(currentCatalog, catalog)) {
            return null;
        }
        PostgresPrimaryKey pg = this.metadataSupplier.eGet().getPrimaryKey(schema, table);
        if (pg != null) {
            return this.convertPrimaryKey(pg);
        }
        return null;
    }

    @Override
    public List<RdbUniqueKey> getUniqueKey(String catalog, String schema, String table) throws SQLException {
        String currentCatalog = this.metadataSupplier.eGet().getCurrentCatalog();
        if (!StringUtils.equals(currentCatalog, catalog)) {
            return Collections.emptyList();
        }
        List<PostgresUniqueKey> pg = this.metadataSupplier.eGet().getUniqueKey(schema, table);
        if (pg != null && !pg.isEmpty()) {
            return pg.stream().map(this::convertUniqueKey).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<RdbForeignKey> getForeignKey(String catalog, String schema, String table) throws SQLException {
        String currentCatalog = this.metadataSupplier.eGet().getCurrentCatalog();
        if (!StringUtils.equals(currentCatalog, catalog)) {
            return Collections.emptyList();
        }
        List<PostgresForeignKey> pg = this.metadataSupplier.eGet().getForeignKey(schema, table);
        if (pg != null && !pg.isEmpty()) {
            return pg.stream().map(this::convertForeignKey).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<RdbIndex> getIndexes(String catalog, String schema, String table) throws SQLException {
        String currentCatalog = this.metadataSupplier.eGet().getCurrentCatalog();
        if (!StringUtils.equals(currentCatalog, catalog)) {
            return Collections.emptyList();
        }
        List<PostgresIndex> pg = this.metadataSupplier.eGet().getIndexes(schema, table);
        if (pg != null && !pg.isEmpty()) {
            return pg.stream().map(this::convertIndex).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    protected ValueUmiSchema convertCatalog(String pgCatalog) {
        ValueUmiSchema schema = new ValueUmiSchema();
        schema.setName(pgCatalog);
        schema.setParentPath(new String[0]);
        schema.setTypeDef(UmiTypes.Catalog);
        schema.setDataType(JavaTypes.String);
        //
        return schema;
    }

    protected ValueUmiSchema convertSchema(String catalogName, PostgresSchema pgSchema) {
        ValueUmiSchema schema = new ValueUmiSchema();
        schema.setName(pgSchema.getSchema());
        schema.setParentPath(new String[] { catalogName });
        schema.setTypeDef(UmiTypes.Schema);
        schema.setDataType(JavaTypes.String);
        //
        schema.getAttributes().setValue("owner", pgSchema.getOwner());
        return schema;
    }

    protected ValueUmiSchema convertTable(PostgresTable pgTable) {
        ValueUmiSchema schema = new ValueUmiSchema();
        schema.setName(pgTable.getTable());
        schema.setParentPath(new String[] { pgTable.getCatalog(), pgTable.getSchema() });
        if (pgTable.getTableType() == PostgresTableType.View || pgTable.getTableType() == PostgresTableType.Materialized) {
            schema.setTypeDef(UmiTypes.View);
        } else {
            schema.setTypeDef(UmiTypes.Table);
        }
        schema.setDataType(JavaTypes.String);
        schema.setComment(pgTable.getComment());
        //
        schema.getAttributes().setValue("catalog", pgTable.getCatalog());
        schema.getAttributes().setValue("schema", pgTable.getSchema());
        schema.getAttributes().setValue("tableType", pgTable.getTableType().name());
        schema.getAttributes().setValue("typed", String.valueOf(pgTable.isTyped()));
        return schema;
    }

    protected RdbColumn convertColumn(String catalogName, String schemaName, String tableName, PostgresColumn pgColumn) {
        RdbColumn schema = new RdbColumn();
        schema.setName(pgColumn.getName());
        schema.setParentPath(new String[] { catalogName, schemaName, tableName });
        schema.setDefaultValue(pgColumn.getDefaultValue());
        schema.setTypeDef(UmiTypes.Column);
        schema.setDataType(pgColumn.getSqlType());
        schema.setComment(pgColumn.getComment());
        //
        if (!pgColumn.isNullable()) {
            schema.getConstraints().add(new NonNull());
        }
        if (pgColumn.isPrimaryKey()) {
            schema.getConstraints().add(new Primary());
        }
        if (pgColumn.isUniqueKey()) {
            schema.getConstraints().add(new Unique());
        }
        //
        schema.setCharLength(pgColumn.getCharacterMaximumLength());
        schema.setByteLength(pgColumn.getCharacterOctetLength());
        schema.setNumericPrecision(pgColumn.getNumericPrecision());
        schema.setNumericScale(pgColumn.getNumericScale());
        schema.setDatetimePrecision(pgColumn.getDatetimePrecision());
        schema.getAttributes().setValue("dataType", pgColumn.getDataType());
        if (pgColumn.getJdbcType() != null) {
            schema.getAttributes().setValue("jdbcType", pgColumn.getJdbcType().name());
        }
        schema.getAttributes().setValue("columnType", pgColumn.getColumnType());
        schema.getAttributes().setValue("datetimePrecision", String.valueOf(pgColumn.getDatetimePrecision()));
        schema.getAttributes().setValue("numericPrecision", String.valueOf(pgColumn.getNumericPrecision()));
        schema.getAttributes().setValue("numericPrecisionRadix", String.valueOf(pgColumn.getNumericPrecisionRadix()));
        schema.getAttributes().setValue("numericScale", String.valueOf(pgColumn.getNumericScale()));
        //
        schema.getAttributes().setValue("typeOid", String.valueOf(pgColumn.getTypeOid()));
        schema.getAttributes().setValue("elementType", pgColumn.getElementType());
        schema.getAttributes().setValue("characterMaximumLength", String.valueOf(pgColumn.getCharacterMaximumLength()));
        schema.getAttributes().setValue("characterOctetLength", String.valueOf(pgColumn.getCharacterOctetLength()));
        return schema;
    }

    protected RdbPrimaryKey convertPrimaryKey(PostgresPrimaryKey pgPrimaryKey) {
        RdbPrimaryKey schema = new RdbPrimaryKey();
        schema.setName(pgPrimaryKey.getName());
        schema.setColumnList(pgPrimaryKey.getColumns());
        //
        schema.getAttributes().setValue("schema", pgPrimaryKey.getSchema());
        if (pgPrimaryKey.getConstraintType() != null) {
            schema.getAttributes().setValue("constraintType", pgPrimaryKey.getConstraintType().name());
        }
        return schema;
    }

    protected RdbUniqueKey convertUniqueKey(PostgresUniqueKey pgUniqueKey) {
        RdbUniqueKey schema = new RdbUniqueKey();
        schema.setName(pgUniqueKey.getName());
        schema.setColumnList(pgUniqueKey.getColumns());
        //
        schema.getAttributes().setValue("schema", pgUniqueKey.getSchema());
        if (pgUniqueKey.getConstraintType() != null) {
            schema.getAttributes().setValue("constraintType", pgUniqueKey.getConstraintType().name());
        }
        schema.getAttributes().setValue("storageType", JSON.toString(pgUniqueKey.getStorageType()));
        return schema;
    }

    protected RdbForeignKey convertForeignKey(PostgresForeignKey pgForeignKey) {
        RdbForeignKey schema = new RdbForeignKey();
        schema.setName(pgForeignKey.getName());
        schema.setColumnList(pgForeignKey.getColumns());
        schema.setReferenceSchema(pgForeignKey.getReferenceSchema());
        schema.setReferenceTable(pgForeignKey.getReferenceTable());
        schema.setReferenceMapping(pgForeignKey.getReferenceMapping());
        schema.setUpdateRule(convertForeignKeyRule(pgForeignKey.getUpdateRule()));
        schema.setDeleteRule(convertForeignKeyRule(pgForeignKey.getDeleteRule()));
        //
        schema.getAttributes().setValue("schema", pgForeignKey.getSchema());
        if (pgForeignKey.getConstraintType() != null) {
            schema.getAttributes().setValue("constraintType", pgForeignKey.getConstraintType().name());
        }
        if (pgForeignKey.getMatchOption() != null) {
            schema.getAttributes().setValue("matchOption", pgForeignKey.getMatchOption().name());
        }
        return schema;
    }

    protected RdbForeignKeyRule convertForeignKeyRule(PostgresForeignKeyRule rule) {
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

    protected RdbIndex convertIndex(PostgresIndex pgIndex) {
        RdbIndex schema = new RdbIndex();
        schema.setName(pgIndex.getName());
        schema.setColumnList(pgIndex.getColumns());
        schema.setType(pgIndex.getIndexType().name());
        //
        schema.getAttributes().setValue("schema", pgIndex.getSchema());
        schema.getAttributes().setValue("indexType", pgIndex.getIndexType().name());
        schema.getAttributes().setValue("storageType", JSON.toString(pgIndex.getStorageType()));
        return schema;
    }
}
