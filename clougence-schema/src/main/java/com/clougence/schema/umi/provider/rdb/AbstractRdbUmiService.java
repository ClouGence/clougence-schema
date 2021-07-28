package com.clougence.schema.umi.provider.rdb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.clougence.schema.metadata.CaseSensitivityType;
import com.clougence.schema.metadata.provider.rdb.RdbMetaDataService;
import com.clougence.schema.umi.ValueUmiSchema;
import com.clougence.schema.umi.provider.AbstractUmiService;
import com.clougence.schema.umi.special.rdb.*;
import com.clougence.utils.StringUtils;
import com.clougence.utils.function.ESupplier;

/**
 * mysql DsSchemaRService
 * @author mode 2021/1/8 19:56
 */
public abstract class AbstractRdbUmiService<T extends RdbMetaDataService> extends AbstractUmiService<T, SQLException> implements RdbUmiService {

    public AbstractRdbUmiService(ESupplier<T, SQLException> metadataSupplier){
        super(metadataSupplier);
    }

    @Override
    public String getVersion() throws SQLException { return this.metadataSupplier.get().getVersion(); }

    public CaseSensitivityType getPlain() throws SQLException { return this.metadataSupplier.get().getPlain(); }

    public CaseSensitivityType getDelimited() throws SQLException { return this.metadataSupplier.get().getDelimited(); }

    public String getCurrentSchema() throws SQLException { return this.metadataSupplier.get().getCurrentSchema(); }

    public String getCurrentCatalog() throws SQLException { return this.metadataSupplier.get().getCurrentCatalog(); }

    public RdbTable loadTable(String catalog, String schema, String table) throws SQLException {
        RdbTable rdbTable = new RdbTable();
        ValueUmiSchema umiTable = getTable(catalog, schema, table);
        if (umiTable == null) {
            return null;
        }
        //
        rdbTable.setName(umiTable.getName());
        rdbTable.setComment(umiTable.getComment());
        rdbTable.getAttributes().setValue(RdbTableType.class.getName(), umiTable.getDataType().getCodeKey());
        //
        RdbPrimaryKey primaryKey = getPrimaryKey(catalog, schema, table);
        if (primaryKey != null) {
            rdbTable.setPrimaryKey(primaryKey);
        }
        List<RdbUniqueKey> uniqueKeyList = getUniqueKey(catalog, schema, table);
        if (uniqueKeyList != null) {
            rdbTable.setUniqueKey(uniqueKeyList);
        }
        List<RdbForeignKey> foreignKeyList = getForeignKey(catalog, schema, table);
        if (foreignKeyList != null) {
            rdbTable.setForeignKey(foreignKeyList);
        }
        List<RdbIndex> indexesList = getIndexes(catalog, schema, table);
        if (indexesList != null) {
            rdbTable.getIndices().addAll(indexesList);
        }
        //
        List<RdbColumn> columns = getColumns(catalog, schema, table);
        for (RdbColumn umiColumn : columns) {
            rdbTable.getProperties().put(umiColumn.getName(), umiColumn);
        }
        //
        rdbTable.getAttributes().setAttributes(umiTable.getAttributes());
        return rdbTable;
    }

    @Override
    public List<RdbTable> loadTables(String catalog, String schema, String[] tables) throws SQLException {
        if (tables == null || tables.length == 0) {
            return Collections.emptyList();
        }
        List<RdbTable> resultList = new ArrayList<>();
        for (String tableName : tables) {
            if (StringUtils.isBlank(tableName)) {
                continue;
            }
            RdbTable rdbTable = loadTable(catalog, schema, tableName);
            resultList.add(rdbTable);
        }
        return resultList;
    }

    @Override
    public List<RdbTable> loadTables(String catalog, String schema) throws SQLException {
        List<ValueUmiSchema> tables = getTables(catalog, schema);
        List<RdbTable> resultList = new ArrayList<>();
        for (ValueUmiSchema tableName : tables) {
            RdbTable rdbTable = loadTable(catalog, schema, tableName.getName());
            resultList.add(rdbTable);
        }
        return resultList;
    }
}
