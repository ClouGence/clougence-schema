package com.clougence.schema.umi.provider.rdb;
import com.clougence.schema.umi.UmiService;
import com.clougence.schema.umi.ValueUmiSchema;
import com.clougence.schema.umi.special.rdb.RdbForeignKey;
import com.clougence.schema.umi.special.rdb.RdbIndex;
import com.clougence.schema.umi.special.rdb.RdbPrimaryKey;
import com.clougence.schema.umi.special.rdb.RdbUniqueKey;

import java.sql.SQLException;
import java.util.List;

/**
 * Unified Meta Information Service
 * @author mode 2020/12/8 15:21
 */
public interface RdbUmiService extends UmiService {
    public List<ValueUmiSchema> getCatalogs() throws SQLException;

    public ValueUmiSchema getCatalog(String catalog) throws SQLException;

    public List<ValueUmiSchema> getSchemas() throws SQLException;

    public List<ValueUmiSchema> getSchemas(String catalog) throws SQLException;

    public ValueUmiSchema getSchema(String catalog, String schema) throws SQLException;

    public List<ValueUmiSchema> getTables(String catalog, String schema) throws SQLException;

    public ValueUmiSchema getTable(String catalog, String schema, String table) throws SQLException;

    public List<ValueUmiSchema> getTables(String catalog, String schema, String[] tables) throws SQLException;

    public List<ValueUmiSchema> getColumns(String catalog, String schema, String table) throws SQLException;

    public RdbPrimaryKey getPrimaryKey(String catalog, String schema, String table) throws SQLException;

    public List<RdbUniqueKey> getUniqueKey(String catalog, String schema, String table) throws SQLException;

    public List<RdbForeignKey> getForeignKey(String catalog, String schema, String table) throws SQLException;

    public List<RdbIndex> getIndexes(String catalog, String schema, String table) throws SQLException;
}
