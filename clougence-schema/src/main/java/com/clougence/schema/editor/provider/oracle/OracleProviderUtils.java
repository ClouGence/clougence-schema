package com.clougence.schema.editor.provider.oracle;

import com.clougence.schema.editor.domain.EColumn;
import com.clougence.schema.metadata.domain.rdb.oracle.OracleSqlTypes;

class OracleProviderUtils {

    public static String buildColumnType(EColumn columnInfo) {
        OracleSqlTypes sqlTypes = OracleSqlTypes.valueOfCode(columnInfo.getDbType());
        return buildColumnType(sqlTypes, columnInfo) + " " + buildNullable(columnInfo.isNullable()) + " " + buildDefault(sqlTypes, columnInfo.getDefaultValue());
    }

    private static String buildNullable(boolean nullable) {
        if (!nullable) {
            return "not null";
        } else {
            return "null";
        }
    }

    private static String buildDefault(OracleSqlTypes sqlTypes, String defaultValue) {
        StringBuilder sqlBuild = new StringBuilder();
        if (defaultValue != null && !"".equals(defaultValue)) {
            switch (sqlTypes) {
                case CHAR:
                case NCHAR:
                case VARCHAR2:
                case NVARCHAR:
                case NVARCHAR2: {
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

    private static String buildColumnType(OracleSqlTypes sqlTypes, EColumn columnInfo) {
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
            case CHAR: {
                return "char" + ((length == null) ? "" : "(" + length + ")");
            }
            case NCHAR: {
                return "nchar" + ((numericPrecision == null) ? "" : "(" + numericPrecision + ")");
            }
            case VARCHAR2: {
                return "varchar2" + ((numericPrecision == null) ? "" : "(" + numericPrecision + ")");
            }
            case NVARCHAR: {
                return "nvarchar" + ((numericPrecision == null) ? "" : "(" + numericPrecision + ")");
            }
            case NVARCHAR2: {
                return "nvarchar2" + ((numericPrecision == null) ? "" : "(" + numericPrecision + ")");
            }
            case CLOB: {
                return "clob";
            }
            case NCLOB: {
                return "nclob";
            }
            case NUMBER: {
                if (numericPrecision != null && numericScale != null) {
                    return "number(" + numericPrecision + ", " + numericScale + ")";
                } else if (numericPrecision == null && numericScale != null) {
                    return "number(*, " + numericScale + ")";
                } else if (numericPrecision != null) {
                    return "number(" + numericPrecision + ")";
                } else {
                    return "number";
                }
            }
            case FLOAT: {
                return "float" + ((numericPrecision == null) ? "" : "(" + numericPrecision + ")");
            }
            case BINARY_FLOAT: {
                return "binary_float";
            }
            case BINARY_DOUBLE: {
                return "binary_double";
            }
            //
            case DATE: {
                return "date";
            }
            case TIMESTAMP: {
                return "timestamp" + ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")");
            }
            case TIMESTAMP_WITH_TIME_ZONE: {
                return "timestamp" + ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")") + " with time zone";
            }
            case TIMESTAMP_WITH_LOCAL_TIME_ZONE: {
                return "timestamp" + ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")") + " with local time zone";
            }
            case INTERVAL_YEAR_TO_MONTH: {
                return "interval year to month";//interval year(9) to month
            }
            case INTERVAL_DAY_TO_SECOND: {
                return "interval day to second";//interval day(9) to second(9)
            }
            //
            case RAW: {
                return "raw" + ((length == null) ? "" : "(" + length + ")");
            }
            case LONG_RAW: {
                return "long raw";
            }
            case LONG: {
                return "long";
            }

            case BLOB: {
                return "blob";
            }
            case BFILE: {
                return "bfile";
            }
            case ROWID: {
                return "rowid";
            }
            case UROWID: {
                return "urowid";
            }
            case XMLTYPE: {
                return "xmltype";
            }
            //
            case OBJECT:
            case REF:
            case VARRAY:
            case NESTED_TABLE:
            case PLSQL_BOOLEAN:
            case ANYTYPE:
            case ANYDATA:
            case ANYDATASET:
            case HTTPURITYPE:
            case XDBURITYPE:
            case DBURITYPE:
            case SDO_GEOMETRY:
            case SDO_TOPO_GEOMETRY:
            case SDO_GEORASTER:
            case ORDAUDIO:
            case ORDDICOM:
            case ORDDOC:
            case ORDIMAGE:
            case ORDVIDEO:
            case SI_AVERAGE_COLOR:
            case SI_COLOR:
            case SI_COLOR_HISTOGRAM:
            case SI_FEATURE_LIST:
            case SI_POSITIONAL_COLOR:
            case SI_STILL_IMAGE:
            case SI_TEXTURE:
            default:
                return columnInfo.getDbType();
        }
    }
}
