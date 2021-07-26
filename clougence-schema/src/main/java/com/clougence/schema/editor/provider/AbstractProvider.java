package com.clougence.schema.editor.provider;

import com.clougence.utils.StringUtils;
import com.clougence.schema.DsType;
import com.clougence.schema.dialect.Dialect;
import com.clougence.schema.dialect.DialectRegister;
import com.clougence.schema.metadata.CaseSensitivityType;
import com.clougence.schema.umi.ValueUmiSchema;

public abstract class AbstractProvider {

    public abstract DsType getDataSourceType();

    protected String fmtTable(boolean useDelimited, CaseSensitivityType caseSensitivity, String schema, String table) {
        Dialect dialect = DialectRegister.findSqlDialect(getDataSourceType());
        schema = fmtCaseSensitivity(caseSensitivity, schema);
        table = fmtCaseSensitivity(caseSensitivity, table);
        //
        ValueUmiSchema tableSchema = new ValueUmiSchema();
        tableSchema.setName(table);
        if (StringUtils.isNotBlank(schema)) {
            tableSchema.setParentPath(new String[] { schema });
        }
        return dialect.tableName(useDelimited, tableSchema);
    }

    protected String fmtIndex(boolean useDelimited, CaseSensitivityType caseSensitivity, String index) {
        return fmtTable(useDelimited, caseSensitivity, null, index);
    }

    protected String fmtColumn(boolean useDelimited, CaseSensitivityType caseSensitivity, String column) {
        Dialect dialect = DialectRegister.findSqlDialect(getDataSourceType());
        column = fmtCaseSensitivity(caseSensitivity, column);
        //
        ValueUmiSchema columnSchema = new ValueUmiSchema();
        columnSchema.setName(column);
        return dialect.columnName(useDelimited, columnSchema);
    }

    protected String fmtCaseSensitivity(CaseSensitivityType caseSensitivity, String stringData) {
        switch (caseSensitivity) {
            case Lower:
                return safeToString(stringData).toLowerCase();
            case Upper:
                return safeToString(stringData).toUpperCase();
            default:
                return safeToString(stringData);
        }
    }

    protected static String safeToString(Object obj) {
        return (obj == null) ? "" : obj.toString();
    }
}
