package com.clougence.schema.metadata.domain.oracle.driver;
import java.sql.SQLType;

/**
 * 参考 ojdbc8-19.8.0.0.jar
 */
public enum OracleType implements SQLType {
    VARCHAR2("VARCHAR2", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.VARCHAR),
    NVARCHAR("NVARCHAR", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.NVARCHAR, true),
    NUMBER("NUMBER", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.NUMERIC),
    FLOAT("FLOAT", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.FLOAT),
    LONG("LONG", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.LONGVARCHAR),
    DATE("DATE", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.DATE),
    BINARY_FLOAT("BINARY FLOAT", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.BINARY_FLOAT),
    BINARY_DOUBLE("BINARY DOUBLE", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.BINARY_DOUBLE),
    TIMESTAMP("TIMESTAMP", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.TIMESTAMP),
    TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.TIMESTAMPTZ),
    TIMESTAMP_WITH_LOCAL_TIME_ZONE("TIMESTAMP WITH LOCAL TIME ZONE", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.TIMESTAMPLTZ),
    INTERVAL_YEAR_TO_MONTH("INTERVAL YEAR TO MONTH", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.INTERVALYM),
    INTERVAL_DAY_TO_SECOND("INTERVAL DAY TO SECOND", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.INTERVALDS),
    PLSQL_BOOLEAN("PLSQL_BOOLEAN", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.PLSQL_BOOLEAN),
    RAW("RAW", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.BINARY),
    LONG_RAW("LONG RAW", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.LONGVARBINARY),
    ROWID("ROWID", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.ROWID),
    UROWID("UROWID"),
    CHAR("CHAR", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.CHAR),
    NCHAR("NCHAR", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.NCHAR, true),
    CLOB("CLOB", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.CLOB),
    NCLOB("NCLOB", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.NCLOB, true),
    BLOB("BLOB", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.BLOB),
    BFILE("BFILE", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.BFILE),
    OBJECT("OBJECT", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.STRUCT),
    REF("REF", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.REF),
    VARRAY("VARRAY", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.ARRAY),
    NESTED_TABLE("NESTED_TABLE", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.ARRAY),
    ANYTYPE("ANYTYPE", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.OPAQUE),
    ANYDATA("ANYDATA", com.clougence.schema.metadata.domain.oracle.driver.OracleTypes.OPAQUE),
    ANYDATASET("ANYDATASET"),
    XMLTYPE("XMLTYPE", OracleTypes.SQLXML),
    HTTPURITYPE("HTTPURITYPE"),
    XDBURITYPE("XDBURITYPE"),
    DBURITYPE("DBURITYPE"),
    SDO_GEOMETRY("SDO_GEOMETRY"),
    SDO_TOPO_GEOMETRY("SDO_TOPO_GEOMETRY"),
    SDO_GEORASTER("SDO_GEORASTER"),
    ORDAUDIO("ORDAUDIO"),
    ORDDICOM("ORDDICOM"),
    ORDDOC("ORDDOC"),
    ORDIMAGE("ORDIMAGE"),
    ORDVIDEO("ORDVIDEO"),
    SI_AVERAGE_COLOR("SI_AVERAGE_COLOR"),
    SI_COLOR("SI_COLOR"),
    SI_COLOR_HISTOGRAM("SI_COLOR_HISTOGRAM"),
    SI_FEATURE_LIST("SI_FEATURE_LIST"),
    SI_POSITIONAL_COLOR("SI_POSITIONAL_COLOR"),
    SI_STILL_IMAGE("SI_STILL_IMAGE"),
    SI_TEXTURE("SI_TEXTURE"),
    ;
    private final boolean isSupported;
    private final String  typeName;
    private final int     code;
    private final boolean isNationalCharacterSet;

    public static OracleType toOracleType(SQLType sqlType) {
        return sqlType instanceof OracleType ? (OracleType) sqlType : null;
    }

    public static OracleType toOracleType(int oracleType) {
        for (OracleType type : values()) {
            if (type.getVendorTypeNumber() == oracleType) {
                return type;
            }
        }
        return null;
    }

    OracleType(String oracleType) {
        this.isSupported = false;
        this.typeName = oracleType;
        this.code = -2147483648;
        this.isNationalCharacterSet = false;
    }

    OracleType(String oracleType, int jdbcType) {
        this.isSupported = true;
        this.typeName = oracleType;
        this.code = jdbcType;
        this.isNationalCharacterSet = false;
    }

    OracleType(String oracleType, int jdbcType, boolean var5) {
        this.isSupported = true;
        this.typeName = oracleType;
        this.code = jdbcType;
        this.isNationalCharacterSet = var5;
    }

    public String getName() {
        return this.typeName;
    }

    public String getVendor() {
        return "Oracle Database";
    }

    public Integer getVendorTypeNumber() {
        return this.code;
    }

    public boolean isNationalCharacterSet() {
        return this.isNationalCharacterSet;
    }

    public boolean isSupported() {
        return this.isSupported;
    }
}
