package com.clougence.schema.editor.provider.mysql;

import com.clougence.schema.editor.domain.EColumn;
import com.clougence.schema.metadata.domain.rdb.mysql.MySqlTypes;

class MySqlProviderUtils {

    public static String buildColumnType(EColumn columnInfo) {
        MySqlTypes sqlTypes = MySqlTypes.valueOfCode(columnInfo.getDbType());
        return buildColumnType(sqlTypes, columnInfo) + " " + buildNullable(columnInfo.isNullable()) + " " + buildDefault(sqlTypes, columnInfo.getDefaultValue());
    }

    private static String buildNullable(boolean nullable) {
        if (!nullable) {
            return "not null";
        } else {
            return "null";
        }
    }

    private static String buildDefault(MySqlTypes sqlTypes, String defaultValue) {
        StringBuilder sqlBuild = new StringBuilder();
        if (defaultValue != null && !"".equals(defaultValue)) {
            switch (sqlTypes) {
                case CHAR:
                case VARCHAR:
                case TINYTEXT:
                case TEXT:
                case MEDIUMTEXT:
                case LONGTEXT:
                case JSON: {
                    defaultValue = "'" + defaultValue + "'";
                    break;
                }
                default:
                    defaultValue = defaultValue;
            }
            sqlBuild.append("default " + defaultValue);
        }
        return sqlBuild.toString();
    }

    private static String buildColumnType(MySqlTypes sqlTypes, EColumn columnInfo) {
        if (sqlTypes == null) {
            return columnInfo.getDbType();
        }
        Long length = columnInfo.getLength();
        Integer numericPrecision = columnInfo.getNumericPrecision();
        Integer numericScale = columnInfo.getNumericScale();
        Integer datetimePrecision = columnInfo.getDatetimePrecision();
        length = (length != null && length == 0) ? null : length;
        numericPrecision = (numericPrecision != null && numericPrecision == 0) ? null : numericPrecision;
        numericScale = (numericScale != null && numericScale == 0) ? null : numericScale;
        datetimePrecision = (datetimePrecision != null && datetimePrecision == 0) ? null : datetimePrecision;
        //
        switch (sqlTypes) {
            case BIT: {
                return "bit" + ((length == null) ? "" : "(" + length + ")");
            }
            case TINYINT: {
                return "tinyint" + ((numericPrecision == null) ? "" : "(" + numericPrecision + ")");
            }
            case SMALLINT: {
                return "smallint" + ((numericPrecision == null) ? "" : "(" + numericPrecision + ")");
            }
            case MEDIUMINT: {
                return "mediumint" + ((numericPrecision == null) ? "" : "(" + numericPrecision + ")");
            }
            case INT: {
                return "int" + ((numericPrecision == null) ? "" : "(" + numericPrecision + ")");
            }
            case BIGINT: {
                return "bigint" + ((numericPrecision == null) ? "" : "(" + numericPrecision + ")");
            }
            case DECIMAL: {
                if (numericPrecision != null && numericScale != null) {
                    return "decimal(" + numericPrecision + ", " + numericScale + ")";
                } else if (numericPrecision != null) {
                    return "decimal(" + numericPrecision + ")";
                } else {
                    return "decimal";
                }
            }
            case FLOAT: {
                return "float" + ((numericPrecision == null) ? "" : "(" + numericPrecision + ")");
            }
            case DOUBLE: {
                if (numericPrecision != null && numericScale != null) {
                    return "double(" + numericPrecision + ", " + numericScale + ")";
                } else if (numericPrecision != null) {
                    return "double(" + numericPrecision + ")";
                } else {
                    return "double";
                }
            }
            case DATE: {
                return "date";
            }
            case DATETIME: {
                return "datetime" + ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")");
            }
            case TIMESTAMP: {
                return "timestamp" + ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")");
            }
            case TIME: {
                return "time" + ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")");
            }
            case YEAR: {
                return "year" + ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")");
            }
            case CHAR: {
                return "char" + ((length == null) ? "" : "(" + length + ")");
            }
            case VARCHAR: {
                return "varchar" + ((length == null) ? "" : "(" + length + ")");
            }
            case BINARY: {
                return "binary" + ((length == null) ? "" : "(" + length + ")");
            }
            case VARBINARY: {
                return "varbinary" + ((length == null) ? "" : "(" + length + ")");
            }
            case TINYBLOB: {
                return "tinyblob";
            }
            case BLOB: {
                return "blob" + ((length == null) ? "" : "(" + length + ")");
            }
            case MEDIUMBLOB: {
                return "mediumblob";
            }
            case LONGBLOB: {
                return "longblob";
            }
            case TINYTEXT: {
                return "tinytext";
            }
            case TEXT: {
                return "text" + ((length == null) ? "" : "(" + length + ")");
            }
            case MEDIUMTEXT: {
                return "mediumtext";
            }
            case LONGTEXT: {
                return "longtext";
            }
            case JSON: {
                return "json";
            }
            case GEOMETRY: {
                return "geometry";
            }
            case POINT: {
                return "point";
            }
            case LINESTRING: {
                return "linestring";
            }
            case POLYGON: {
                return "polygon";
            }
            case GEOMETRY_COLLECTION: {
                return "geometrycollection";
            }
            // c_multipoint multipoint,
            // c_multilinestring multilinestring,
            // c_multipolygon multipolygon,
            case ENUM:
            case SET:
            default:
                return columnInfo.getDbType();
        }
    }
}
