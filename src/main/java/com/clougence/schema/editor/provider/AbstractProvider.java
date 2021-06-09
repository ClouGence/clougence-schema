package com.clougence.schema.editor.provider;
import com.clougence.schema.DataSourceType;
import com.clougence.schema.dialect.SqlDialect;
import com.clougence.schema.dialect.SqlDialectRegister;
import com.clougence.schema.metadata.CaseSensitivityType;

public abstract class AbstractProvider {
    protected abstract DataSourceType getDataSourceType();

    protected String fmtName(boolean useDelimited, CaseSensitivityType caseSensitivity, String stringData) {
        if (useDelimited) {
            return fmtCaseSensitivity(caseSensitivity, stringData);
        } else {
            SqlDialect dialect = SqlDialectRegister.findSqlDialect(getDataSourceType());
            String left = dialect.leftQualifier();
            String right = dialect.rightQualifier();
            if (left == null || right == null) {
                throw new UnsupportedOperationException("Unsupported ds type -> " + getDataSourceType().name());
            }
            return left + stringData + right;
        }
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
