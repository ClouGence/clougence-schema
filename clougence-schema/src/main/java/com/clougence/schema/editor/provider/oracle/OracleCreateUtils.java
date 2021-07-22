package com.clougence.schema.editor.provider.oracle;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.clougence.commons.StringUtils;
import com.clougence.schema.DsType;
import com.clougence.schema.editor.domain.*;
import com.clougence.schema.editor.provider.AbstractProvider;
import com.clougence.schema.editor.triggers.TriggerContext;
import com.clougence.schema.metadata.CaseSensitivityType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mode 2021/1/8 19:56
 */
@Slf4j
public class OracleCreateUtils extends AbstractProvider {

    @Override
    public DsType getDataSourceType() { return DsType.Oracle; }

    public List<String> buildCreate(TriggerContext buildContext, String catalog, String schema, String table, ETable eTable, Function<EColumn, String> columnTypeMapping) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        //
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("create table ");
        sqlBuild.append(fmtTable(useDelimited, caseSensitivity, eTable.getSchema(), eTable.getName()));
        sqlBuild.append("(\n");
        //
        // columns
        List<EColumn> columnList = eTable.getColumnList();
        for (int i = 0; i < columnList.size(); i++) {
            if (i != 0) {
                sqlBuild.append(",\n");
            }
            EColumn eColumn = columnList.get(i);
            buildColumn(sqlBuild, buildContext, eColumn, columnTypeMapping);
        }
        // pk
        EPrimaryKey primaryKey = eTable.getPrimaryKey();
        if (primaryKey != null && !primaryKey.getColumnList().isEmpty()) {
            sqlBuild.append(",\n");
            buildPrimaryKey(sqlBuild, buildContext, primaryKey);
        }
        // idx
        List<EIndex> indices = eTable.getIndices();
        if (indices != null && !indices.isEmpty()) {
            for (int i = 0; i < indices.size(); i++) {
                sqlBuild.append(",\n");
                EIndex eIndex = indices.get(i);
                buildIndex(sqlBuild, buildContext, eIndex);
            }
        }
        //
        // eTable.getForeignKeys();
        // private List<EForeignKey> foreignKeys = new ArrayList<>();
        //
        sqlBuild.append(")");
        if (eTable.getComment() != null && !"".equals(eTable.getComment())) {
            sqlBuild.append(" comment '" + eTable.getComment() + "'");
        }
        sqlBuild.append(";");
        return Collections.singletonList(sqlBuild.toString());
    }

    private void buildColumn(StringBuilder sqlBuild, TriggerContext buildContext, EColumn eColumn, Function<EColumn, String> columnTypeMapping) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        if (columnTypeMapping != null) {
            eColumn = eColumn.clone();
            eColumn.setDbType(columnTypeMapping.apply(eColumn));
        }
        //
        sqlBuild.append("  ");
        sqlBuild.append(fmtColumn(useDelimited, caseSensitivity, eColumn.getName()));
        String columnType = OracleProviderUtils.buildColumnType(eColumn);
        sqlBuild.append("  ");
        sqlBuild.append(columnType);
        if (StringUtils.isNotBlank(eColumn.getComment())) {
            sqlBuild.append(" comment '" + eColumn.getComment() + "'");
        }
    }

    private void buildPrimaryKey(StringBuilder sqlBuild, TriggerContext buildContext, EPrimaryKey primaryKey) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        //
        sqlBuild.append("primary key(");
        List<String> pkColumns = primaryKey.getColumnList();
        for (int i = 0; i < pkColumns.size(); i++) {
            String column = pkColumns.get(i);
            if (i > 0) {
                sqlBuild.append(", ");
            }
            sqlBuild.append(fmtColumn(useDelimited, caseSensitivity, column));
        }
        sqlBuild.append(")");
    }

    private void buildIndex(StringBuilder sqlBuild, TriggerContext buildContext, EIndex eIndex) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        //
        if (eIndex.getType() == EIndexType.Unique) {
            sqlBuild.append("  unique key ");
        } else {
            sqlBuild.append("  key ");
        }
        sqlBuild.append(fmtIndex(useDelimited, caseSensitivity, eIndex.getName()));
        List<String> pkColumns = eIndex.getColumnList();
        sqlBuild.append("(");
        for (int i = 0; i < pkColumns.size(); i++) {
            String column = pkColumns.get(i);
            if (i > 0) {
                sqlBuild.append(", ");
            }
            sqlBuild.append(fmtColumn(useDelimited, caseSensitivity, column));
        }
        sqlBuild.append(")");
    }
}
