package com.clougence.schema.editor.provider.postgresql;
import com.clougence.schema.DataSourceType;
import com.clougence.schema.editor.domain.*;
import com.clougence.schema.editor.provider.AbstractProvider;
import com.clougence.schema.editor.triggers.TriggerContext;
import com.clougence.schema.metadata.CaseSensitivityType;
import lombok.extern.slf4j.Slf4j;
import net.hasor.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mode 2021/1/8 19:56
 */
@Slf4j
public class PostgreSqlCreateUtils extends AbstractProvider {
    @Override
    protected DataSourceType getDataSourceType() {
        return DataSourceType.PostgreSQL;
    }

    public List<String> buildCreate(TriggerContext buildContext, String catalog, String schema, String table, ETable eTable) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        List<String> ddlScripts = new ArrayList<>();
        //
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("create table ");
        if (StringUtils.isBlank(eTable.getSchema())) {
            sqlBuild.append(fmtName(useDelimited, caseSensitivity, eTable.getName()));
        } else {
            sqlBuild.append(fmtName(useDelimited, caseSensitivity, eTable.getSchema()) + "." + fmtName(useDelimited, caseSensitivity, eTable.getName()));
        }
        sqlBuild.append("(\n");
        //
        // columns
        List<EColumn> columnList = eTable.getColumnList();
        for (int i = 0; i < columnList.size(); i++) {
            if (i != 0) {
                sqlBuild.append(",\n");
            }
            EColumn eColumn = columnList.get(i);
            buildColumn(sqlBuild, buildContext, eColumn);
        }
        // pk
        EPrimaryKey primaryKey = eTable.getPrimaryKey();
        if (primaryKey != null) {
            sqlBuild.append(",\n");
            buildPrimaryKey(sqlBuild, buildContext, primaryKey);
        }
        // idx Unique
        List<EIndex> uniqueIndices = eTable.getIndices();
        uniqueIndices = uniqueIndices.stream().filter(eIndex -> eIndex.getType() == EIndexType.Unique).collect(Collectors.toList());
        for (int i = 0; i < uniqueIndices.size(); i++) {
            sqlBuild.append(",\n");
            EIndex eIndex = uniqueIndices.get(i);
            buildUniqueIndex(sqlBuild, buildContext, eIndex);
        }
        //
        // eTable.getForeignKeys();
        // private List<EForeignKey> foreignKeys = new ArrayList<>();
        //
        sqlBuild.append(");");
        ddlScripts.add(sqlBuild.toString());
        //
        if (eTable.getComment() != null && !"".equals(eTable.getComment())) {
            StringBuilder commentSql = new StringBuilder();
            this.buildTableComment(commentSql, buildContext, eTable);
            ddlScripts.add(commentSql.toString());
        }
        // columns comment
        for (int i = 0; i < columnList.size(); i++) {
            EColumn eColumn = columnList.get(i);
            StringBuilder columnCommentSql = new StringBuilder();
            this.buildColumnComment(columnCommentSql, buildContext, eTable, eColumn);
            ddlScripts.add(columnCommentSql.toString());
        }
        //
        // idx
        List<EIndex> idxIndices = eTable.getIndices();
        idxIndices = idxIndices.stream().filter(eIndex -> eIndex.getType() != EIndexType.Unique).collect(Collectors.toList());
        for (int i = 0; i < idxIndices.size(); i++) {
            if (i != 0) {
                sqlBuild.append(",\n");
            }
            EIndex eIndex = idxIndices.get(i);
            StringBuilder idxSql = new StringBuilder();
            buildIdxIndex(idxSql, buildContext, eTable, eIndex);
            ddlScripts.add(idxSql.toString());
        }
        //
        return ddlScripts;
    }

    private void buildColumn(StringBuilder sqlBuild, TriggerContext buildContext, EColumn eColumn) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        //
        sqlBuild.append("  ");
        sqlBuild.append(fmtName(useDelimited, caseSensitivity, eColumn.getName()));
        String columnType = PostgreSqlProviderUtils.buildColumnType(eColumn);
        sqlBuild.append(columnType);
        if (StringUtils.isNotBlank(eColumn.getComment())) {
            sqlBuild.append(" comment '" + eColumn.getComment() + "'");
        }
    }

    private void buildPrimaryKey(StringBuilder sqlBuild, TriggerContext buildContext, EPrimaryKey primaryKey) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        //
        sqlBuild.append(" constraint ");
        sqlBuild.append(fmtName(useDelimited, caseSensitivity, primaryKey.getPrimaryKeyName()));
        sqlBuild.append(" primary key(");
        List<String> pkColumns = primaryKey.getColumnList();
        for (int i = 0; i < pkColumns.size(); i++) {
            String column = pkColumns.get(i);
            if (i > 0) {
                sqlBuild.append(", ");
            }
            sqlBuild.append(fmtName(useDelimited, caseSensitivity, column));
        }
        sqlBuild.append(")");
    }

    private void buildUniqueIndex(StringBuilder sqlBuild, TriggerContext buildContext, EIndex eIndex) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        sqlBuild.append("  constraint ");
        sqlBuild.append(fmtName(useDelimited, caseSensitivity, eIndex.getName()));
        sqlBuild.append(" unique ");
        //
        List<String> idxColumns = eIndex.getColumnList();
        sqlBuild.append("(");
        for (int i = 0; i < idxColumns.size(); i++) {
            String column = idxColumns.get(i);
            if (i > 0) {
                sqlBuild.append(", ");
            }
            sqlBuild.append(fmtName(useDelimited, caseSensitivity, column));
        }
        sqlBuild.append(")");
    }

    private void buildIdxIndex(StringBuilder sqlBuild, TriggerContext buildContext, ETable eTable, EIndex eIndex) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        String indexName = eIndex.getName();
        List<String> columnName = eIndex.getColumnList();
        //
        sqlBuild.append("create index " + fmtName(useDelimited, caseSensitivity, indexName));
        sqlBuild.append(" on ");
        if (StringUtils.isBlank(eTable.getSchema())) {
            sqlBuild.append(fmtName(useDelimited, caseSensitivity, eTable.getName()));
        } else {
            sqlBuild.append(fmtName(useDelimited, caseSensitivity, eTable.getSchema()) + "." + fmtName(useDelimited, caseSensitivity, eTable.getName()));
        }
        //
        sqlBuild.append("(");
        for (int i = 0; i < columnName.size(); i++) {
            String column = columnName.get(i);
            if (i > 0) {
                sqlBuild.append(", ");
            }
            sqlBuild.append(fmtName(useDelimited, caseSensitivity, column));
        }
        sqlBuild.append(");");
    }

    private void buildTableComment(StringBuilder sqlBuild, TriggerContext buildContext, ETable eTable) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        if (eTable.getComment() != null && !"".equals(eTable.getComment())) {
            sqlBuild.append("comment on table ");
            if (StringUtils.isBlank(eTable.getSchema())) {
                sqlBuild.append(fmtName(useDelimited, caseSensitivity, eTable.getName()));
            } else {
                sqlBuild.append(fmtName(useDelimited, caseSensitivity, eTable.getSchema()) + "." + fmtName(useDelimited, caseSensitivity, eTable.getName()));
            }
            sqlBuild.append(" is '" + eTable.getComment() + "';");
        }
    }

    private void buildColumnComment(StringBuilder sqlBuild, TriggerContext buildContext, ETable eTable, EColumn eColumn) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        sqlBuild.append("comment on column ");
        if (StringUtils.isBlank(eTable.getSchema())) {
            sqlBuild.append(fmtName(useDelimited, caseSensitivity, eTable.getName()));
            sqlBuild.append(".");
            sqlBuild.append(fmtName(useDelimited, caseSensitivity, eColumn.getName()));
        } else {
            sqlBuild.append(fmtName(useDelimited, caseSensitivity, eTable.getSchema()));
            sqlBuild.append(".");
            sqlBuild.append(fmtName(useDelimited, caseSensitivity, eTable.getName()));
            sqlBuild.append(".");
            sqlBuild.append(fmtName(useDelimited, caseSensitivity, eColumn.getName()));
        }
        sqlBuild.append(" is '" + eColumn.getComment() + "';");
    }
}
