package com.clougence.schema.umi.provider.rdb;

import java.sql.SQLException;
import java.util.List;

import com.clougence.schema.metadata.CaseSensitivityType;
import com.clougence.schema.umi.UmiService;
import com.clougence.schema.umi.ValueUmiSchema;
import com.clougence.schema.umi.special.rdb.*;

/**
 * Unified Meta Information Service
 * 
 * @author mode 2020/12/8 15:21
 */
public interface RdbUmiService extends UmiService {

    public CaseSensitivityType getPlain() throws SQLException;

    public CaseSensitivityType getDelimited() throws SQLException;

    public RdbTable loadTable(String catalog, String schema, String table) throws SQLException;

    public List<RdbTable> loadTables(String catalog, String schema, String[] tables) throws SQLException;

    public List<RdbTable> loadTables(String catalog, String schema) throws SQLException;

    public List<ValueUmiSchema> getCatalogs() throws SQLException;

    public List<ValueUmiSchema> getSchemas() throws SQLException;

    public List<ValueUmiSchema> getSchemas(String catalog) throws SQLException;

    public ValueUmiSchema getSchema(String catalog, String schema) throws SQLException;

    public List<ValueUmiSchema> getTables(String catalog, String schema) throws SQLException;

    public ValueUmiSchema getTable(String catalog, String schema, String table) throws SQLException;

    public List<ValueUmiSchema> getTables(String catalog, String schema, String[] tables) throws SQLException;

    public List<RdbColumn> getColumns(String catalog, String schema, String table) throws SQLException;

    public RdbPrimaryKey getPrimaryKey(String catalog, String schema, String table) throws SQLException;

    public List<RdbUniqueKey> getUniqueKey(String catalog, String schema, String table) throws SQLException;

    public List<RdbForeignKey> getForeignKey(String catalog, String schema, String table) throws SQLException;

    public List<RdbIndex> getIndexes(String catalog, String schema, String table) throws SQLException;
}
