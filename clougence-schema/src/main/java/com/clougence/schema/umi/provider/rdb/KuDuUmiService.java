package com.clougence.schema.umi.provider.rdb;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduException;
import com.clougence.schema.DsType;
import com.clougence.schema.metadata.domain.rdb.kudu.KuduColumn;
import com.clougence.schema.metadata.domain.rdb.kudu.KuduPrimaryKey;
import com.clougence.schema.metadata.domain.rdb.kudu.KuduTable;
import com.clougence.schema.metadata.provider.rdb.KuDuMetadataProvider;
import com.clougence.schema.umi.UmiSchema;
import com.clougence.schema.umi.ValueUmiSchema;
import com.clougence.schema.umi.constraint.NonNull;
import com.clougence.schema.umi.constraint.Primary;
import com.clougence.schema.umi.special.rdb.*;
import com.clougence.schema.umi.types.JavaTypes;
import com.clougence.schema.umi.types.UmiTypes;
import com.clougence.utils.StringUtils;
import com.clougence.utils.function.ESupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * mysql DsSchemaRService
 *
 * @author mode 2021/1/8 19:56
 */
public class KuDuUmiService extends AbstractRdbUmiService<KuDuMetadataProvider> {

    public KuDuUmiService(KuduClient client){
        this(new KuDuMetadataProvider(client));
    }

    public KuDuUmiService(Supplier<KuduClient> clientSupplier){
        this(new KuDuMetadataProvider(clientSupplier));
    }

    public KuDuUmiService(KuDuMetadataProvider metadataProvider){
        super(() -> metadataProvider);
    }

    public KuDuUmiService(ESupplier<KuDuMetadataProvider, SQLException> metadataSupplier){
        super(metadataSupplier);
    }

    @Override
    public DsType getDataSourceType() { return DsType.MySQL; }

    @Override
    public List<UmiSchema> getRootSchemas() throws SQLException { return new ArrayList<>(getTables(null, null)); }

    @Override
    public UmiSchema getSchemaByPath(String... parentPath) throws SQLException {
        if (parentPath.length == 0) {
            // same as root
            throw new IndexOutOfBoundsException("path need 1 element.");
        } else if (parentPath.length == 1) {
            // load table
            return getTable(null, null, parentPath[0]);
        } else if (parentPath.length == 2) {
            // load column
            List<RdbColumn> columns = getColumns(null, null, parentPath[0]);
            return columns.stream().filter(valueUmiSchema -> {
                return StringUtils.equals(valueUmiSchema.getName(), parentPath[1]);
            }).findFirst().orElse(null);
        } else {
            throw new IndexOutOfBoundsException("path more than 2 layers.");
        }
    }

    @Override
    public List<UmiSchema> getChildSchemaByPath(String... parentPath) throws SQLException {
        if (parentPath.length == 0) {
            // load schemas
            return new ArrayList<>(getTables(null, null));
        } else if (parentPath.length == 1) {
            // load columns
            return new ArrayList<>(getColumns(null, null, parentPath[0]));
        } else {
            throw new IndexOutOfBoundsException("path more than 1 layers.");
        }
    }

    @Override
    public List<ValueUmiSchema> getCatalogs() { return Collections.emptyList(); }

    @Override
    public List<ValueUmiSchema> getSchemas() { return Collections.emptyList(); }

    @Override
    public List<ValueUmiSchema> getSchemas(String catalog) throws SQLException {
        return Collections.emptyList();
    }

    @Override
    public ValueUmiSchema getSchema(String catalog, String schema) throws SQLException {
        return null;
    }

    @Override
    public List<ValueUmiSchema> getTables(String catalog, String schema) throws SQLException {
        try {
            List<KuduTable> kudu = this.metadataSupplier.eGet().getAllTables();
            if (kudu != null && !kudu.isEmpty()) {
                return kudu.stream().map(this::convertTable).collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (KuduException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public ValueUmiSchema getTable(String catalog, String schema, String table) throws SQLException {
        try {
            KuduTable kudu = this.metadataSupplier.eGet().getTable(table);
            if (kudu != null) {
                return convertTable(kudu);
            }
            return null;
        } catch (KuduException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<ValueUmiSchema> getTables(String catalog, String schema, String[] tables) throws SQLException {
        try {
            List<KuduTable> kudu = this.metadataSupplier.eGet().findTable(tables);
            if (kudu != null && !kudu.isEmpty()) {
                return kudu.stream().map(this::convertTable).collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (KuduException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<RdbColumn> getColumns(String catalog, String schema, String table) throws SQLException {
        try {
            List<KuduColumn> kudu = this.metadataSupplier.eGet().getColumns(table);
            if (kudu != null && !kudu.isEmpty()) {
                return kudu.stream().map(c -> convertColumn(table, c)).collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (KuduException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public RdbPrimaryKey getPrimaryKey(String catalog, String schema, String table) throws SQLException {
        try {
            KuduPrimaryKey primaryKey = this.metadataSupplier.eGet().getPrimaryKey(table);
            if (primaryKey != null) {
                return this.convertPrimaryKey(primaryKey);
            }
            return null;
        } catch (KuduException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<RdbUniqueKey> getUniqueKey(String catalog, String schema, String table) throws SQLException {
        return Collections.emptyList();
    }

    @Override
    public List<RdbForeignKey> getForeignKey(String catalog, String schema, String table) throws SQLException {
        return Collections.emptyList();
    }

    @Override
    public List<RdbIndex> getIndexes(String catalog, String schema, String table) throws SQLException {
        return Collections.emptyList();
    }

    @SneakyThrows
    protected ValueUmiSchema convertTable(KuduTable kuduTable) {
        ValueUmiSchema schema = new ValueUmiSchema();
        schema.setName(kuduTable.getTable());
        schema.setParentPath(new String[0]);
        schema.setTypeDef(UmiTypes.Table);
        schema.setDataType(JavaTypes.String);
        schema.setComment(kuduTable.getComment());
        //
        schema.getAttributes().setValue("tableId", kuduTable.getTableId());
        schema.getAttributes().setValue("onDiskSize", String.valueOf(kuduTable.getOnDiskSize()));
        schema.getAttributes().setValue("liveRowCount", String.valueOf(kuduTable.getLiveRowCount()));
        schema.getAttributes().setValue("numReplicas", String.valueOf(kuduTable.getNumReplicas()));
        schema.getAttributes().setValue("owner", kuduTable.getOwner());
        //
        List<Map<String, Object>> partitionList = new ArrayList<>();
        if (kuduTable.getPartitionList() != null) {
            partitionList = kuduTable.getPartitionList().stream().map(kuduPartition -> {
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("columns", kuduPartition.getColumns());
                info.put("numBuckets", kuduPartition.getNumBuckets());
                info.put("seed", kuduPartition.getSeed());
                info.put("partitionType", kuduPartition.getPartitionType().name());
                return info;
            }).collect(Collectors.toList());
        }
        schema.getAttributes().setValue("partitionList", new ObjectMapper().writeValueAsString(partitionList));
        return schema;
    }

    protected RdbColumn convertColumn(String tableName, KuduColumn kuduColumn) {
        RdbColumn schema = new RdbColumn();
        schema.setName(kuduColumn.getName());
        schema.setParentPath(new String[] { tableName });
        if (kuduColumn.getDefaultValue() != null) {
            schema.setDefaultValue(String.valueOf(kuduColumn.getDefaultValue()));
        }
        schema.setTypeDef(UmiTypes.Column);
        schema.setDataType(kuduColumn.getKuduTypes());
        schema.setComment(kuduColumn.getComment());
        //
        if (!kuduColumn.isNullable()) {
            schema.getConstraints().add(new NonNull());
        }
        if (kuduColumn.isPrimaryKey()) {
            schema.getConstraints().add(new Primary());
        }
        //
        schema.getAttributes().setValue("dataType", kuduColumn.getColumnType());
        if (kuduColumn.getJdbcType() != null) {
            schema.getAttributes().setValue("jdbcType", kuduColumn.getJdbcType().name());
        }
        //
        schema.setCharLength(Long.valueOf(kuduColumn.getLength())); // TODO 1.10 not suppout
        schema.setByteLength(null);// TODO ref plain, prefix, dictionary
        schema.setNumericPrecision(kuduColumn.getPrecision());
        schema.setNumericScale(kuduColumn.getScale());
        schema.setDatetimePrecision(null);

        schema.getAttributes().setValue("partition", String.valueOf(kuduColumn.isPartition()));
        schema.getAttributes().setValue("encoding", kuduColumn.getEncoding());
        if (kuduColumn.getDesiredBlockSize() != null) {
            schema.getAttributes().setValue("desiredBlockSize", String.valueOf(kuduColumn.getDesiredBlockSize()));
        }
        if (kuduColumn.getCompressionAlgorithm() != null) {
            schema.getAttributes().setValue("compressionAlgorithm", kuduColumn.getCompressionAlgorithm());
        }
        if (kuduColumn.getTypeSize() != null) {
            schema.getAttributes().setValue("typeSize", String.valueOf(kuduColumn.getTypeSize()));
        }
        return schema;
    }

    protected RdbPrimaryKey convertPrimaryKey(KuduPrimaryKey primaryKey) {
        RdbPrimaryKey schema = new RdbPrimaryKey();
        schema.setName("PRIMARY");
        schema.setColumnList(primaryKey.getColumns());
        return schema;
    }
}
