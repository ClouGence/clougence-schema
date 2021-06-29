package com.clougence.schema.umi.provider.rdb;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.clougence.schema.DsType;
import com.clougence.schema.metadata.domain.rdb.oracle.*;
import com.clougence.schema.metadata.provider.rdb.OracleMetadataProvider;
import com.clougence.schema.umi.UmiSchema;
import com.clougence.schema.umi.ValueUmiSchema;
import com.clougence.schema.umi.constraint.NonNull;
import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.constraint.Unique;
import com.clougence.schema.umi.special.rdb.*;
import com.clougence.schema.umi.types.JavaTypes;
import com.clougence.schema.umi.types.UmiTypes;
import net.hasor.utils.StringUtils;
import net.hasor.utils.function.ESupplier;
import net.hasor.utils.json.JSON;

/**
 * @author mode 2021/1/8 19:56
 */
public class OracleUmiService extends AbstractRdbUmiService<OracleMetadataProvider> {

    public OracleUmiService(ESupplier<OracleMetadataProvider, SQLException> metadataSupplier){
        super(metadataSupplier);
    }

    @Override
    public DsType getDataSourceType() { return DsType.Oracle; }

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
        List<OracleSchema> oracle = this.metadataSupplier.eGet().getSchemas();
        if (oracle != null && !oracle.isEmpty()) {
            return oracle.stream().map(this::convertSchema).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<ValueUmiSchema> getSchemas(String catalog) throws SQLException {
        return getSchemas();
    }

    @Override
    public ValueUmiSchema getSchema(String catalog, String schema) throws SQLException {
        OracleSchema oracle = this.metadataSupplier.eGet().getSchema(schema);
        if (oracle != null) {
            return this.convertSchema(oracle);
        }
        return null;
    }

    @Override
    public List<ValueUmiSchema> getTables(String catalog, String schema) throws SQLException {
        List<OracleTable> oracle = this.metadataSupplier.eGet().getAllTables(schema);
        if (oracle != null && !oracle.isEmpty()) {
            return oracle.stream().map(this::convertTable).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public ValueUmiSchema getTable(String catalog, String schema, String table) throws SQLException {
        OracleTable oracle = this.metadataSupplier.eGet().getTable(schema, table);
        if (oracle != null) {
            return this.convertTable(oracle);
        }
        return null;
    }

    @Override
    public List<ValueUmiSchema> getTables(String catalog, String schema, String[] tables) throws SQLException {
        List<OracleTable> oracle = this.metadataSupplier.eGet().findTable(schema, tables);
        if (oracle != null && !oracle.isEmpty()) {
            return oracle.stream().map(this::convertTable).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<RdbColumn> getColumns(String catalog, String schema, String table) throws SQLException {
        List<OracleColumn> oracle = this.metadataSupplier.eGet().getColumns(schema, table);
        if (oracle != null && !oracle.isEmpty()) {
            return oracle.stream().map(c -> convertColumn(schema, table, c)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public RdbPrimaryKey getPrimaryKey(String catalog, String schema, String table) throws SQLException {
        OraclePrimaryKey oracle = this.metadataSupplier.eGet().getPrimaryKey(schema, table);
        if (oracle != null) {
            return this.convertPrimaryKey(oracle);
        }
        return null;
    }

    @Override
    public List<RdbUniqueKey> getUniqueKey(String catalog, String schema, String table) throws SQLException {
        List<OracleUniqueKey> oracle = this.metadataSupplier.eGet().getUniqueKey(schema, table);
        if (oracle != null && !oracle.isEmpty()) {
            return oracle.stream().map(this::convertUniqueKey).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<RdbForeignKey> getForeignKey(String catalog, String schema, String table) throws SQLException {
        List<OracleForeignKey> oracle = this.metadataSupplier.eGet().getForeignKey(schema, table);
        if (oracle != null && !oracle.isEmpty()) {
            return oracle.stream().map(this::convertForeignKey).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<RdbIndex> getIndexes(String catalog, String schema, String table) throws SQLException {
        List<OracleIndex> oracle = this.metadataSupplier.eGet().getIndexes(schema, table);
        if (oracle != null && !oracle.isEmpty()) {
            return oracle.stream().map(this::convertIndex).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    protected ValueUmiSchema convertSchema(OracleSchema oracleSchema) {
        ValueUmiSchema schema = new ValueUmiSchema();
        schema.setName(oracleSchema.getSchema());
        schema.setParentPath(new String[0]);
        schema.setTypeDef(UmiTypes.Schema);
        schema.setDataType(JavaTypes.String);
        //
        if (oracleSchema.getStatus() != null) {
            schema.getAttributes().setValue("status", oracleSchema.getStatus().name());
        }
        if (oracleSchema.getLockDate() != null) {
            schema.getAttributes().setValue("lockDate", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(oracleSchema.getLockDate()));
        }
        if (oracleSchema.getExpiryDate() != null) {
            schema.getAttributes().setValue("expiryDate", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(oracleSchema.getExpiryDate()));
        }
        schema.getAttributes().setValue("defaultTablespace", oracleSchema.getDefaultTablespace());
        schema.getAttributes().setValue("temporaryTablespace", oracleSchema.getTemporaryTablespace());
        if (oracleSchema.getCreated() != null) {
            schema.getAttributes().setValue("created", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(oracleSchema.getCreated()));
        }
        schema.getAttributes().setValue("profile", oracleSchema.getProfile());
        if (oracleSchema.getAuthenticationType() != null) {
            schema.getAttributes().setValue("authenticationType", oracleSchema.getAuthenticationType().name());
        }
        if (oracleSchema.getLastLogin() != null) {
            schema.getAttributes().setValue("lastLogin", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(oracleSchema.getLastLogin()));
        }
        return schema;
    }

    protected ValueUmiSchema convertTable(OracleTable oracleTable) {
        ValueUmiSchema schema = new ValueUmiSchema();
        schema.setName(oracleTable.getTable());
        schema.setParentPath(new String[] { oracleTable.getSchema() });
        if (oracleTable.getTableType() == OracleTableType.View) {
            schema.setTypeDef(UmiTypes.View);
        } else {
            schema.setTypeDef(UmiTypes.Table);
        }
        schema.setDataType(JavaTypes.String);
        schema.setComment(oracleTable.getComment());
        //
        schema.getAttributes().setValue("tableSpace", oracleTable.getTableSpace());
        schema.getAttributes().setValue("readOnly", String.valueOf(oracleTable.getReadOnly()));
        schema.getAttributes().setValue("tableType", oracleTable.getTableType().name());
        schema.getAttributes().setValue("materializedLog", oracleTable.getMaterializedLog());
        return schema;
    }

    protected RdbColumn convertColumn(String schemaName, String tableName, OracleColumn oracleColumn) {
        RdbColumn schema = new RdbColumn();
        schema.setName(oracleColumn.getName());
        schema.setParentPath(new String[] { schemaName, tableName });
        schema.setDefaultValue(oracleColumn.getDefaultValue());
        schema.setTypeDef(UmiTypes.Column);
        schema.setDataType(oracleColumn.getSqlType());
        schema.setComment(oracleColumn.getComment());
        //
        if (!oracleColumn.isNullable()) {
            schema.getConstraints().add(new NonNull());
        }
        if (oracleColumn.isPrimaryKey()) {
            schema.getConstraints().add(new Primary());
        }
        if (oracleColumn.isUniqueKey()) {
            schema.getConstraints().add(new Unique());
        }
        //
        schema.setCharLength(oracleColumn.getDataCharLength());
        schema.setByteLength(oracleColumn.getDataBytesLength());
        schema.setNumericPrecision(oracleColumn.getDataPrecision());
        schema.setNumericScale(oracleColumn.getDataScale());
        if (oracleColumn.getJdbcType() != null) {
            schema.getAttributes().setValue("jdbcType", oracleColumn.getJdbcType().name());
        }
        schema.getAttributes().setValue("columnType", oracleColumn.getColumnType());
        schema.getAttributes().setValue("columnTypeOwner", oracleColumn.getColumnTypeOwner());
        //
        schema.getAttributes().setValue("dataPrecision", String.valueOf(oracleColumn.getDataPrecision()));
        schema.getAttributes().setValue("dataScale", String.valueOf(oracleColumn.getDataScale()));
        //
        schema.getAttributes().setValue("characterSetName", oracleColumn.getCharacterSetName());
        schema.getAttributes().setValue("dataCharLength", String.valueOf(oracleColumn.getDataCharLength()));
        schema.getAttributes().setValue("dataBytesLength", String.valueOf(oracleColumn.getDataBytesLength()));
        //
        schema.getAttributes().setValue("hidden", String.valueOf(oracleColumn.isHidden()));
        schema.getAttributes().setValue("virtual", String.valueOf(oracleColumn.isVirtual()));
        schema.getAttributes().setValue("identity", String.valueOf(oracleColumn.isIdentity()));
        schema.getAttributes().setValue("sensitive", String.valueOf(oracleColumn.isSensitive()));
        return schema;
    }

    protected RdbPrimaryKey convertPrimaryKey(OraclePrimaryKey oraclePrimaryKey) {
        RdbPrimaryKey schema = new RdbPrimaryKey();
        schema.setName(oraclePrimaryKey.getName());
        schema.setColumnList(oraclePrimaryKey.getColumns());
        //
        schema.getAttributes().setValue("schema", oraclePrimaryKey.getSchema());
        if (oraclePrimaryKey.getConstraintType() != null) {
            schema.getAttributes().setValue("constraintType", oraclePrimaryKey.getConstraintType().name());
        }
        schema.getAttributes().setValue("enabled", String.valueOf(oraclePrimaryKey.isEnabled()));
        schema.getAttributes().setValue("validated", String.valueOf(oraclePrimaryKey.isValidated()));
        schema.getAttributes().setValue("generated", String.valueOf(oraclePrimaryKey.isGenerated()));
        return schema;
    }

    protected RdbUniqueKey convertUniqueKey(OracleUniqueKey oracleUniqueKey) {
        RdbUniqueKey schema = new RdbUniqueKey();
        schema.setName(oracleUniqueKey.getName());
        schema.setColumnList(oracleUniqueKey.getColumns());
        //
        schema.getAttributes().setValue("schema", oracleUniqueKey.getSchema());
        if (oracleUniqueKey.getConstraintType() != null) {
            schema.getAttributes().setValue("constraintType", oracleUniqueKey.getConstraintType().name());
        }
        schema.getAttributes().setValue("enabled", String.valueOf(oracleUniqueKey.isEnabled()));
        schema.getAttributes().setValue("validated", String.valueOf(oracleUniqueKey.isValidated()));
        schema.getAttributes().setValue("generated", String.valueOf(oracleUniqueKey.isGenerated()));
        return schema;
    }

    protected RdbForeignKey convertForeignKey(OracleForeignKey oracleForeignKey) {
        RdbForeignKey schema = new RdbForeignKey();
        schema.setName(oracleForeignKey.getName());
        schema.setColumnList(oracleForeignKey.getColumns());
        schema.setReferenceSchema(oracleForeignKey.getReferenceSchema());
        schema.setReferenceTable(oracleForeignKey.getReferenceTable());
        schema.setReferenceMapping(oracleForeignKey.getReferenceMapping());
        schema.setUpdateRule(null);
        schema.setDeleteRule(convertForeignKeyRule(oracleForeignKey.getDeleteRule()));
        //
        schema.getAttributes().setValue("schema", oracleForeignKey.getSchema());
        if (oracleForeignKey.getConstraintType() != null) {
            schema.getAttributes().setValue("constraintType", oracleForeignKey.getConstraintType().name());
        }
        schema.getAttributes().setValue("enabled", String.valueOf(oracleForeignKey.isEnabled()));
        schema.getAttributes().setValue("validated", String.valueOf(oracleForeignKey.isValidated()));
        schema.getAttributes().setValue("generated", String.valueOf(oracleForeignKey.isGenerated()));
        return schema;
    }

    protected RdbForeignKeyRule convertForeignKeyRule(OracleForeignKeyRule rule) {
        if (rule == null) {
            return null;
        }
        switch (rule) {
            case NoAction:
                return RdbForeignKeyRule.NoAction;
            case Cascade:
                return RdbForeignKeyRule.Cascade;
            case SetNull:
                return RdbForeignKeyRule.SetNull;
            default:
                return null;
        }
    }

    protected RdbIndex convertIndex(OracleIndex pgIndex) {
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
