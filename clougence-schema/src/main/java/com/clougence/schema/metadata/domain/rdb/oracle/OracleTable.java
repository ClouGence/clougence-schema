package com.clougence.schema.metadata.domain.rdb.oracle;
import com.clougence.schema.metadata.domain.rdb.TableDef;
import lombok.Getter;
import lombok.Setter;

/**
 * Oracle 的 Table see: ALL_TABLES
 * @version : 2021-04-29
 * @author 赵永春 (zyc@hasor.net)
 */
@Getter
@Setter
public class OracleTable implements TableDef {

    private String                schema;
    private String                table;
    private String                tableSpace;
    private Boolean               readOnly;
    private OracleTableType       tableType;
    private OracleMaterializedLog materializedLog;
    private String                comment;

    @Override
    public String getCatalog() { return null; }

    @Override
    public String getSchema() { return this.schema; }

    @Override
    public String getTable() { return this.table; }

    public OracleTableType getTableType() { return this.tableType; }
}
