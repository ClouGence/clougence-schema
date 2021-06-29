package com.clougence.schema.editor.provider.postgresql;
import com.clougence.schema.editor.domain.EColumn;
import com.clougence.schema.metadata.domain.rdb.postgres.PostgresTypes;

class PostgreSqlProviderUtils {
    public static String buildColumnType(EColumn columnInfo) {
        PostgresTypes sqlTypes = PostgresTypes.valueOfCode(columnInfo.getDbType());
        return buildColumnType(sqlTypes, columnInfo) + " " + buildNullable(columnInfo.isNullable()) + " " + buildDefault(sqlTypes, columnInfo.getDefaultValue());
    }

    private static String buildNullable(boolean nullable) {
        if (!nullable) {
            return "not null";
        } else {
            return "null";
        }
    }

    private static String buildDefault(PostgresTypes sqlTypes, String defaultValue) {
        if (sqlTypes == null) {
            throw new IllegalArgumentException("build column Type is unknown.");
        }
        StringBuilder sqlBuild = new StringBuilder();
        if (defaultValue != null && !"".equals(defaultValue)) {
            switch (sqlTypes) {
                case CHARACTER:
                case BPCHAR:
                case CHARACTER_VARYING:
                case TEXT:
                case NAME:
                case JSON:
                    defaultValue = "'" + defaultValue + "'";
                    break;
                default:
                    defaultValue = defaultValue;
            }
            sqlBuild.append(" default " + defaultValue);
        }
        return sqlBuild.toString();
    }

    private static String buildColumnType(PostgresTypes sqlTypes, EColumn columnInfo) {
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
            case SMALLSERIAL:
                return "smallserial";
            case SERIAL:
                return "serial";
            case BIGSERIAL:
                return "bigserial";
            case SMALLINT:
                return "smallint";
            case INTEGER:
                return "integer";
            case BIGINT:
                return "bigint";
            case OID:
                return "oid";
            case NUMERIC: {
                if (numericPrecision != null && numericScale != null) {
                    return "numeric(" + numericPrecision + ", " + numericScale + ")";
                } else if (numericPrecision != null) {
                    return "numeric(" + numericPrecision + ")";
                } else {
                    return "numeric";
                }
            }
            case REAL:
                return "real";
            case DOUBLE_PRECISION:
                return "double precision";
            case MONEY:
                return "money";
            case CHARACTER:
                return "char" + ((length == null) ? "" : "(" + length + ")");
            case BPCHAR:
                return "";// TODO ？？
            case CHARACTER_VARYING:
                return "varchar" + ((length == null) ? "" : "(" + length + ")");
            case TEXT:
                return "text";
            case NAME:
                return "name";
            case TIMESTAMP_WITHOUT_TIME_ZONE:
                return "timestamp" + ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")");
            case TIMESTAMP_WITH_TIME_ZONE: {
                String timeLength = ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")");
                return "timestamp" + timeLength + "with time zone";
            }
            case TIME_WITHOUT_TIME_ZONE: {
                return "time" + ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")");
            }
            case TIME_WITH_TIME_ZONE: {
                String timeLength = ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")");
                return "time" + timeLength + "with time zone";
            }
            case DATE:
                return "date";
            case INTERVAL:
                return "interval";
            case BIT:
                return "bit" + ((length == null) ? "" : "(" + length + ")");
            case BIT_VARYING:
                return "bit varying" + ((length == null) ? "" : "(" + length + ")");
            case BOOLEAN:
                return "boolean";
            case XML:
                return "xml";
            case BYTEA:
                return "bytea";
            case POINT:
                return "point";
            case LINE:
                return "line";
            case LSEG:
                return "lseg";
            case BOX:
                return "box";
            case PATH:
                return "path";
            case POLYGON:
                return "polygon";
            case CIRCLE:
                return "circle";
            case CIDR:
                return "cidr";
            case INET:
                return "inet";
            case MACADDR:
                return "macaddr";
            case MACADDR8:
                return "macaddr8";
            case TSVECTOR:
                return "tsvector";
            case TSQUERY:
                return "tsquery";
            case UUID:
                return "uuid";
            case JSON:
                return "json";
            case JSONB:
                return "jsonb";
            case INT4RANGE:
                return "int4range";
            case INT8RANGE:
                return "int8range";
            case NUMRANGE:
                return "numrange";
            case TSRANGE:
                return "tsrange";
            case TSTZRANGE:
                return "tstzrange";
            case DATERANGE:
                return "daterange";
            case PG_LSN:
                return "pg_lsn";
            case TXID_SNAPSHOT:
                return "txid_snapshot";
            //
            //
            case SMALLINT_ARRAY:
                return "smallint[]";
            case INTEGER_ARRAY:
                return "integer[]";
            case BIGINT_ARRAY:
                return "bigint[]";
            case OID_ARRAY:
                return "oid[]";
            case NUMERIC_ARRAY: {
                if (numericPrecision != null && numericScale != null) {
                    return "numeric(" + numericPrecision + ", " + numericScale + ")[]";
                } else if (numericPrecision != null) {
                    return "numeric(" + numericPrecision + ")[]";
                } else {
                    return "numeric[]";
                }
            }
            case REAL_ARRAY:
                return "real[]";
            case DOUBLE_PRECISION_ARRAY:
                return "double precision[]";
            case MONEY_ARRAY:
                return "money[]";
            case CHARACTER_ARRAY:
                return "char" + ((length == null) ? "" : "(" + length + ")") + "[]";
            case BPCHAR_ARRAY:
                return "";// TODO ？？
            case CHARACTER_VARYING_ARRAY:
                return "varchar" + ((length == null) ? "" : "(" + length + ")") + "[]";
            case TEXT_ARRAY:
                return "text[]";
            case NAME_ARRAY:
                return "name[]";
            case TIMESTAMP_WITHOUT_TIME_ZONE_ARRAY:
                return "timestamp" + ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")") + "[]";
            case TIMESTAMP_WITH_TIME_ZONE_ARRAY: {
                String timeLength = ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")") + "[]";
                return "timestamp" + timeLength + "with time zone";
            }
            case TIME_WITHOUT_TIME_ZONE_ARRAY: {
                return "time" + ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")") + "[]";
            }
            case TIME_WITH_TIME_ZONE_ARRAY: {
                String timeLength = ((datetimePrecision == null) ? "" : "(" + datetimePrecision + ")") + "[]";
                return "time" + timeLength + "with time zone";
            }
            case DATE_ARRAY:
                return "date[]";
            case INTERVAL_ARRAY:
                return "interval[]";
            case BIT_ARRAY:
                return "bit" + ((length == null) ? "" : "(" + length + ")") + "[]";
            case BIT_VARYING_ARRAY:
                return "bit varying" + ((length == null) ? "" : "(" + length + ")") + "[]";
            case BOOLEAN_ARRAY:
                return "boolean[]";
            case XML_ARRAY:
                return "xml[]";
            case BYTEA_ARRAY:
                return "bytea[]";
            case POINT_ARRAY:
                return "point[]";
            case LINE_ARRAY:
                return "line[]";
            case LSEG_ARRAY:
                return "lseg[]";
            case BOX_ARRAY:
                return "box[]";
            case PATH_ARRAY:
                return "path[]";
            case POLYGON_ARRAY:
                return "polygon[]";
            case CIRCLE_ARRAY:
                return "circle[]";
            case CIDR_ARRAY:
                return "cidr[]";
            case INET_ARRAY:
                return "inet[]";
            case MACADDR_ARRAY:
                return "macaddr[]";
            case MACADDR8_ARRAY:
                return "macaddr8[]";
            case TSVECTOR_ARRAY:
                return "tsvector[]";
            case TSQUERY_ARRAY:
                return "tsquery[]";
            case UUID_ARRAY:
                return "uuid[]";
            case JSON_ARRAY:
                return "json[]";
            case JSONB_ARRAY:
                return "jsonb[]";
            case INT4RANGE_ARRAY:
                return "int4range[]";
            case INT8RANGE_ARRAY:
                return "int8range[]";
            case NUMRANGE_ARRAY:
                return "numrange[]";
            case TSRANGE_ARRAY:
                return "tsrange[]";
            case TSTZRANGE_ARRAY:
                return "tstzrange[]";
            case DATERANGE_ARRAY:
                return "daterange[]";
            case PG_LSN_ARRAY:
                return "pg_lsn[]";
            case TXID_SNAPSHOT_ARRAY:
                return "txid_snapshot[]";
            default:
                return columnInfo.getDbType();
        }
    }
}
