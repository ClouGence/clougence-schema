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
package com.clougence.schema.metadata.provider.storage;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.ColumnSchema.ColumnSchemaBuilder;
import org.apache.kudu.ColumnSchema.CompressionAlgorithm;
import org.apache.kudu.ColumnSchema.Encoding;
import org.apache.kudu.ColumnTypeAttributes;
import org.apache.kudu.ColumnTypeAttributes.ColumnTypeAttributesBuilder;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.*;
import org.apache.kudu.client.ListTablesResponse.TableInfo;
import org.apache.kudu.client.PartitionSchema.HashBucketSchema;
import org.apache.kudu.client.PartitionSchema.RangeSchema;
import com.clougence.utils.StringUtils;
import com.clougence.utils.ThreadUtils;
import com.clougence.utils.convert.ConverterUtils;
import com.clougence.schema.metadata.MetaDataService;
import com.clougence.schema.metadata.domain.storage.kudu.*;
import com.clougence.schema.metadata.domain.storage.kudu.KuduTable;

/**
 * fetch KuDU metadata，Resources：
 *   <li>https://kudu.apache.org/docs/schema_design.html</li>
 * @version : 2020-07-20
 * @author 赵永春 (zyc@hasor.net)
 */
public class KuDuMetadataProvider implements MetaDataService {

    private final Supplier<KuduClient> clientSupplier;

    public KuDuMetadataProvider(KuduClient client){
        this(() -> client);
    }

    public KuDuMetadataProvider(Supplier<KuduClient> clientSupplier){
        this.clientSupplier = clientSupplier;
    }

    @Override
    public String getVersion() throws SQLException { return "unknown"; }

    public List<String> getAllTableNames() throws KuduException {
        KuduClient kuduClient = this.clientSupplier.get();

        ListTablesResponse tableList = kuduClient.getTablesList();
        List<TableInfo> tableInfoList = tableList.getTableInfosList();
        if (tableInfoList == null || tableInfoList.isEmpty()) {
            return Collections.emptyList();
        }
        return tableInfoList.stream().map(TableInfo::getTableName).collect(Collectors.toList());
    }

    public List<KuduTable> getAllTables() throws KuduException {
        KuduClient kuduClient = this.clientSupplier.get();

        ListTablesResponse tableList = kuduClient.getTablesList();
        List<TableInfo> tableInfoList = tableList.getTableInfosList();
        if (tableInfoList == null || tableInfoList.isEmpty()) {
            return Collections.emptyList();
        }

        List<KuduTable> tables = new ArrayList<>();
        for (TableInfo tableInfo : tableInfoList) {
            org.apache.kudu.client.KuduTable openTable = kuduClient.openTable(tableInfo.getTableName());
            if (openTable == null) {
                continue;
            }
            KuduTable kuduTable = convertTable(openTable);
            tables.add(kuduTable);
        }
        return tables;
    }

    public KuduTable getTable(String tableName) throws KuduException {
        KuduClient kuduClient = this.clientSupplier.get();
        if (!kuduClient.tableExists(tableName)) {
            return null;
        }

        org.apache.kudu.client.KuduTable kuduTable = kuduClient.openTable(tableName);
        return convertTable(kuduTable);
    }

    public List<KuduColumn> getColumns(String tableName) throws KuduException {
        KuduClient kuduClient = this.clientSupplier.get();
        org.apache.kudu.client.KuduTable kuduTable = kuduClient.openTable(tableName);
        if (kuduTable == null) {
            return Collections.emptyList();
        }
        Schema kuduTableSchema = kuduTable.getSchema();
        List<ColumnSchema> allColumns = kuduTableSchema.getColumns();
        List<ColumnSchema> keyColumns = kuduTableSchema.getPrimaryKeyColumns();

        List<String> keyColumnNames = keyColumns.stream().map(ColumnSchema::getName).collect(Collectors.toList());

        List<KuduColumn> columnList = new ArrayList<>();
        for (ColumnSchema columnSchema : allColumns) {
            KuduColumn kuduColumn = this.convertColumn(columnSchema);
            kuduColumn.setPrimaryKey(keyColumnNames.contains(kuduColumn.getName()));
            columnList.add(kuduColumn);
        }
        return columnList;
    }

    public KuduPrimaryKey getPrimaryKey(String tableName) throws KuduException {
        KuduClient kuduClient = this.clientSupplier.get();
        org.apache.kudu.client.KuduTable kuduTable = kuduClient.openTable(tableName);
        if (kuduTable == null) {
            return null;
        }
        Schema kuduTableSchema = kuduTable.getSchema();
        List<ColumnSchema> keyColumns = kuduTableSchema.getPrimaryKeyColumns();
        List<String> keyColumnNames = keyColumns.stream().map(ColumnSchema::getName).collect(Collectors.toList());

        KuduPrimaryKey primaryKey = new KuduPrimaryKey();
        primaryKey.setColumns(keyColumnNames);
        return primaryKey;
    }

    private ColumnTypeAttributes buildColumnTypeAttributes(KuduColumn kuduColumn) {
        boolean buildAttr = kuduColumn.getKuduTypes() == KuduTypes.DECIMAL || kuduColumn.getKuduTypes() == KuduTypes.VARCHAR;
        if (!buildAttr) {
            return null;
        }
        //
        boolean hasPrecision = kuduColumn.getPrecision() != null;
        boolean hasScale = kuduColumn.getScale() != null;
        boolean hasLength = kuduColumn.getLength() != null;
        ColumnTypeAttributesBuilder attributesBuilder = new ColumnTypeAttributes.ColumnTypeAttributesBuilder();
        if (hasPrecision) {
            attributesBuilder.precision(kuduColumn.getPrecision() == null ? 0 : kuduColumn.getPrecision());
        }
        if (hasScale) {
            attributesBuilder.scale(kuduColumn.getScale() == null ? 0 : kuduColumn.getScale());
        }
        if (hasLength) {
            attributesBuilder.length(kuduColumn.getLength() == null ? 0 : kuduColumn.getLength());
        }
        return attributesBuilder.build();
    }

    public boolean createTable(KuduTable tableInfo, List<KuduColumn> columns, long waitDuration, TimeUnit timeUnit) throws KuduException, IllegalStateException {
        String tableName = tableInfo.getTableName();
        KuduClient kuduClient = this.clientSupplier.get();
        if (kuduClient.tableExists(tableName)) {
            throw new IllegalStateException("table '" + tableName + "' already existed.");
        }

        List<ColumnSchema> convertColumns = new ArrayList<>();
        for (KuduColumn kuduColumn : columns) {
            try {
                ColumnSchemaBuilder columnBuilder = new ColumnSchemaBuilder(kuduColumn.getName(), kuduColumn.getKuduTypes().getKuduType());
                columnBuilder.comment(kuduColumn.getComment());
                columnBuilder.key(kuduColumn.isPrimaryKey());
                columnBuilder.nullable(kuduColumn.isNullable());
                columnBuilder.defaultValue(kuduColumn.getDefaultValue());

                ColumnTypeAttributes typeAttributes = buildColumnTypeAttributes(kuduColumn);
                columnBuilder.typeAttributes(typeAttributes);

                if (StringUtils.isNotBlank(kuduColumn.getCompressionAlgorithm())) {
                    CompressionAlgorithm convert = (CompressionAlgorithm) ConverterUtils.convert(kuduColumn.getCompressionAlgorithm(), CompressionAlgorithm.class);
                    columnBuilder.compressionAlgorithm(convert == null ? CompressionAlgorithm.DEFAULT_COMPRESSION : convert);
                }
                if (kuduColumn.getDesiredBlockSize() != null) {
                    columnBuilder.desiredBlockSize(kuduColumn.getDesiredBlockSize());
                }
                if (StringUtils.isNotBlank(kuduColumn.getEncoding())) {
                    Encoding convert = (Encoding) ConverterUtils.convert(kuduColumn.getEncoding(), Encoding.class);
                    columnBuilder.encoding(convert == null ? Encoding.AUTO_ENCODING : convert);
                }

                ColumnSchema columnSchema = columnBuilder.build();
                convertColumns.add(columnSchema);
            } catch (Exception e) {
                throw new IllegalArgumentException("column '" + kuduColumn.getName() + "' " + e.getMessage(), e);
            }
        }

        CreateTableOptions tableOptions = new CreateTableOptions();
        tableOptions.setOwner(tableInfo.getOwner());
        tableOptions.setComment(tableInfo.getComment());
        if (tableInfo.getNumReplicas() != null) {
            tableOptions.setNumReplicas(tableInfo.getNumReplicas());
        }

        List<KuduPartition> partitionList = tableInfo.getPartitionList();
        List<KuduPartition> kuduRangePartitions = partitionList.stream().filter(p -> p.getPartitionType() == KuduPartitionType.RangePartition).collect(Collectors.toList());
        List<KuduPartition> kuduHashPartitions = partitionList.stream().filter(p -> p.getPartitionType() == KuduPartitionType.HashPartition).collect(Collectors.toList());

        if (!kuduRangePartitions.isEmpty()) {
            throw new UnsupportedOperationException("kuduRangePartitions Unsupported.");
        }

        for (KuduPartition partition : kuduHashPartitions) {
            Integer partitionSeed = partition.getSeed();
            if (partitionSeed == null) {
                tableOptions.addHashPartitions(partition.getColumns(), partition.getNumBuckets());
            } else {
                tableOptions.addHashPartitions(partition.getColumns(), partition.getNumBuckets(), partition.getSeed());
            }
        }

        kuduClient.createTable(tableName, new Schema(convertColumns), tableOptions);
        if (kuduClient.tableExists(tableName)) {
            return true;
        }

        ThreadUtils.sleep(100, TimeUnit.MICROSECONDS);
        long millis = timeUnit.toMillis(waitDuration);
        long timeout = System.currentTimeMillis() + millis;
        while (!kuduClient.isCreateTableDone(tableName)) {
            if (System.currentTimeMillis() > timeout) {
                break;
            } else {
                ThreadUtils.sleep(100, TimeUnit.MICROSECONDS);
            }
        }
        if (System.currentTimeMillis() > timeout && !kuduClient.isCreateTableDone(tableName)) {
            throw new IllegalStateException("create table timeout.");
        }
        return kuduClient.tableExists(tableName);
    }

    public boolean dropTable(String tableName, long waitDuration, TimeUnit timeUnit) throws KuduException {
        KuduClient kuduClient = this.clientSupplier.get();
        if (!kuduClient.tableExists(tableName)) {
            return true;
        }

        kuduClient.deleteTable(tableName);
        if (!kuduClient.tableExists(tableName)) {
            return true;
        }

        ThreadUtils.sleep(100, TimeUnit.MICROSECONDS);
        if (!kuduClient.tableExists(tableName)) {
            return true;
        }

        long millis = timeUnit.toMillis(waitDuration);
        long timeout = System.currentTimeMillis() + millis;
        while (kuduClient.tableExists(tableName)) {
            if (System.currentTimeMillis() > timeout) {
                break;
            } else {
                ThreadUtils.sleep(100, TimeUnit.MICROSECONDS);
            }
        }
        if (System.currentTimeMillis() > timeout && kuduClient.tableExists(tableName)) {
            throw new IllegalStateException("drop table timeout.");
        }
        return !kuduClient.tableExists(tableName);
    }

    protected KuduTable convertTable(org.apache.kudu.client.KuduTable kuduTable) throws KuduException {
        Map<Integer, String> idMappingName = getColumnIdMappingName(kuduTable);

        KuduTable tableInfo = new KuduTable();
        tableInfo.setTableId(kuduTable.getTableId());
        tableInfo.setTableName(kuduTable.getName());

        KuduTableStatistics tableStatistics = kuduTable.getTableStatistics();
        if (tableStatistics != null) {
            tableInfo.setOnDiskSize(tableStatistics.getOnDiskSize());
            tableInfo.setLiveRowCount(tableStatistics.getLiveRowCount());
        }

        tableInfo.setNumReplicas(kuduTable.getNumReplicas());
        tableInfo.setOwner(kuduTable.getOwner());
        tableInfo.setComment(kuduTable.getComment());
        tableInfo.setPartitionList(new ArrayList<>());

        PartitionSchema partitionSchema = kuduTable.getPartitionSchema();

        RangeSchema rangeSchema = partitionSchema.getRangeSchema();
        if (rangeSchema != null) {

            List<Integer> columnIds = rangeSchema.getColumnIds();
            List<String> columnNames = columnIds.stream().map(idMappingName::get).collect(Collectors.toList());

            KuduPartition partition = new KuduPartition();
            partition.setPartitionType(KuduPartitionType.RangePartition);
            partition.setColumns(columnNames);

            tableInfo.getPartitionList().add(partition);
        }

        List<HashBucketSchema> hashBucketSchemas = partitionSchema.getHashBucketSchemas();
        if (hashBucketSchemas != null) {
            for (HashBucketSchema hashBucket : hashBucketSchemas) {

                List<Integer> columnIds = hashBucket.getColumnIds();
                List<String> columnNames = columnIds.stream().map(idMappingName::get).collect(Collectors.toList());

                KuduPartition partition = new KuduPartition();
                partition.setPartitionType(KuduPartitionType.HashPartition);
                partition.setColumns(columnNames);
                partition.setNumBuckets(hashBucket.getNumBuckets());
                partition.setSeed(hashBucket.getSeed());

                tableInfo.getPartitionList().add(partition);
            }
        }

        return tableInfo;
    }

    protected KuduColumn convertColumn(ColumnSchema columnInfo) {
        Type columnType = columnInfo.getType();

        KuduColumn column = new KuduColumn();
        column.setName(columnInfo.getName());
        column.setKuduTypes(KuduTypes.valueOfCode(columnType.getName()));
        column.setColumnType(columnType.getName());
        column.setComment(columnInfo.getComment());

        column.setDefaultValue(columnInfo.getDefaultValue());
        column.setNullable(columnInfo.isNullable());
        ColumnTypeAttributes typeAttributes = columnInfo.getTypeAttributes();
        column.setPrecision(typeAttributes.getPrecision());
        column.setScale(typeAttributes.getScale());
        column.setLength(typeAttributes.getLength());

        column.setPrimaryKey(columnInfo.isKey());
        column.setDesiredBlockSize(columnInfo.getDesiredBlockSize());
        column.setEncoding(columnInfo.getEncoding().name());
        column.setCompressionAlgorithm(columnInfo.getCompressionAlgorithm().name());
        column.setTypeSize(columnInfo.getTypeSize());
        return column;
    }

    private Map<String, Integer> getColumnNameMappingId(org.apache.kudu.client.KuduTable kuduTable) {
        Schema tableSchema = kuduTable.getSchema();
        List<ColumnSchema> columnSchemaList = tableSchema.getColumns();
        Map<String, Integer> columnMap = new HashMap<>();
        for (ColumnSchema columnSchema : columnSchemaList) {
            String columnName = columnSchema.getName();
            int columnId = tableSchema.getColumnId(columnName);
            columnMap.put(columnName, columnId);
        }
        return columnMap;
    }

    private Map<Integer, String> getColumnIdMappingName(org.apache.kudu.client.KuduTable kuduTable) {
        Schema tableSchema = kuduTable.getSchema();
        List<ColumnSchema> columnSchemaList = tableSchema.getColumns();
        Map<Integer, String> columnMap = new HashMap<>();
        for (ColumnSchema columnSchema : columnSchemaList) {
            String columnName = columnSchema.getName();
            int columnId = tableSchema.getColumnId(columnName);
            columnMap.put(columnId, columnName);
        }
        return columnMap;
    }
}
