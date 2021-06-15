package com.clougence.schema.editor.provider.postgresql;
import com.clougence.schema.DataSourceType;
import com.clougence.schema.editor.domain.*;
import com.clougence.schema.editor.provider.AbstractProvider;
import com.clougence.schema.editor.provider.BuilderProvider;
import com.clougence.schema.editor.triggers.TriggerContext;
import com.clougence.schema.metadata.CaseSensitivityType;
import lombok.extern.slf4j.Slf4j;
import net.hasor.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author mode 2021/1/8 19:56
 */
@Slf4j
public class PostgreSqlEditorProvider extends AbstractProvider implements BuilderProvider {
    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.PostgreSQL;
    }

    protected StringBuilder buildAlterTable(TriggerContext buildContext, String catalog, String schema, String table) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        //
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("alter table ");
        sqlBuild.append(fmtTable(useDelimited, caseSensitivity, schema, table));
        return sqlBuild;
    }

    @Override
    public List<String> tableRename(TriggerContext buildContext, String catalog, String schema, String table, String newName) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        StringBuilder sqlBuild = buildAlterTable(buildContext, catalog, schema, table);
        //
        sqlBuild.append(" rename to ");
        sqlBuild.append(fmtTable(useDelimited, caseSensitivity, schema, newName));
        sqlBuild.append(";");
        return Collections.singletonList(sqlBuild.toString());
    }

    @Override
    public List<String> tableComment(TriggerContext buildContext, String catalog, String schema, String table, String comment) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        //
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("comment on table ");
        sqlBuild.append(fmtTable(useDelimited, caseSensitivity, schema, table));
        sqlBuild.append(" is '" + comment + "';");
        return Collections.singletonList(sqlBuild.toString());
    }

    @Override
    public List<String> tableCreate(TriggerContext buildContext, String catalog, String schema, String table, ETable eTable, Function<EColumn, String> columnTypeMapping) {
        return new PostgreSqlCreateUtils().buildCreate(buildContext, catalog, schema, table, eTable, columnTypeMapping);
    }

    @Override
    public List<String> addColumn(TriggerContext buildContext, String catalog, String schema, String table, EColumn columnInfo) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        StringBuilder sqlBuild = buildAlterTable(buildContext, catalog, schema, table);
        //
        sqlBuild.append(" add " + fmtColumn(useDelimited, caseSensitivity, columnInfo.getName()));
        sqlBuild.append(" " + PostgreSqlProviderUtils.buildColumnType(columnInfo));
        sqlBuild.append(";");
        //
        ArrayList<String> columnScripts = new ArrayList<>();
        columnScripts.add(sqlBuild.toString());
        //
        columnScripts.addAll(columnComment(buildContext, catalog, schema, table, columnInfo, columnInfo.getComment()));
        return columnScripts;
    }

    @Override
    public List<String> dropColumn(TriggerContext buildContext, String catalog, String schema, String table, EColumn columnInfo) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        StringBuilder sqlBuild = buildAlterTable(buildContext, catalog, schema, table);
        sqlBuild.append(" drop column " + fmtColumn(useDelimited, caseSensitivity, columnInfo.getName()));
        if (buildContext.isCascade()) {
            sqlBuild.append(" cascade");
        }
        sqlBuild.append(";");
        return Collections.singletonList(sqlBuild.toString());
    }

    @Override
    public List<String> columnRename(TriggerContext buildContext, String catalog, String schema, String table, EColumn columnInfo, String newColumnName) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        StringBuilder sqlBuild = buildAlterTable(buildContext, catalog, schema, table);
        sqlBuild.append(" rename column ");
        sqlBuild.append(fmtColumn(useDelimited, caseSensitivity, columnInfo.getName()));
        sqlBuild.append(" to " + fmtColumn(useDelimited, caseSensitivity, newColumnName));
        sqlBuild.append(";");
        return Collections.singletonList(sqlBuild.toString());
    }

    @Override
    public List<String> columnChange(TriggerContext buildContext, String catalog, String schema, String table, EColumn columnInfo, EColumn newInfo, List<String> diffChange) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        StringBuilder sqlBuild = buildAlterTable(buildContext, catalog, schema, table);
        //
        sqlBuild.append(" alter column " + fmtColumn(useDelimited, caseSensitivity, columnInfo.getName()));
        String columnType = PostgreSqlProviderUtils.buildColumnType(newInfo);
        sqlBuild.append(" type " + columnType);
        sqlBuild.append(" using " + fmtColumn(useDelimited, caseSensitivity, columnInfo.getName()) + "::" + columnType);
        sqlBuild.append(";");
        return Collections.singletonList(sqlBuild.toString());
    }

    @Override
    public List<String> columnComment(TriggerContext buildContext, String catalog, String schema, String table, EColumn columnInfo, String comment) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("comment on column ");
        sqlBuild.append(fmtTable(useDelimited, caseSensitivity, schema, table));
        sqlBuild.append(".");
        sqlBuild.append(fmtColumn(useDelimited, caseSensitivity, columnInfo.getName()));
        sqlBuild.append(" is '" + comment + "';");
        return Collections.singletonList(sqlBuild.toString());
    }

    @Override
    public List<String> createIndex(TriggerContext buildContext, String catalog, String schema, String table, EIndex indexInfo) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        //
        StringBuilder sqlBuild = new StringBuilder();
        if (indexInfo.getType() == EIndexType.Unique) {
            sqlBuild.append("create unique index " + fmtIndex(useDelimited, caseSensitivity, indexInfo.getName()));
        } else {
            sqlBuild.append("create index " + fmtIndex(useDelimited, caseSensitivity, indexInfo.getName()));
        }
        sqlBuild.append(" on ");
        sqlBuild.append(fmtTable(useDelimited, caseSensitivity, schema, table));
        //
        sqlBuild.append("(");
        List<String> columnList = indexInfo.getColumnList();
        for (int i = 0; i < columnList.size(); i++) {
            String column = columnList.get(i);
            if (i > 0) {
                sqlBuild.append(", ");
            }
            sqlBuild.append(fmtColumn(useDelimited, caseSensitivity, column));
        }
        sqlBuild.append(");");
        return Collections.singletonList(sqlBuild.toString());
    }

    @Override
    public List<String> dropIndex(TriggerContext buildContext, String catalog, String schema, String table, EIndex indexInfo) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        //
        StringBuilder sqlBuild = new StringBuilder("drop index ");
        sqlBuild.append(fmtIndex(useDelimited, caseSensitivity, indexInfo.getName()));
        sqlBuild.append(";");
        return Collections.singletonList(sqlBuild.toString());
    }

    @Override
    public List<String> indexRename(TriggerContext buildContext, String catalog, String schema, String table, EIndex indexInfo, String newIndexName) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("alter index ");
        sqlBuild.append(fmtIndex(useDelimited, caseSensitivity, indexInfo.getName()));
        sqlBuild.append(" to ");
        sqlBuild.append(fmtIndex(useDelimited, caseSensitivity, newIndexName));
        sqlBuild.append(";");
        return Collections.singletonList(sqlBuild.toString());
    }

    @Override
    public List<String> indexAddColumn(TriggerContext buildContext, String catalog, String schema, String table, EIndex indexInfo, List<String> needAddColumns) {
        EIndex copy = indexInfo.clone();
        copy.getColumnList().addAll(needAddColumns);
        //
        ArrayList<String> indexScripts = new ArrayList<>();
        indexScripts.addAll(this.dropIndex(buildContext, catalog, schema, table, indexInfo));
        indexScripts.addAll(this.createIndex(buildContext, catalog, schema, table, copy));
        return indexScripts;
    }

    @Override
    public List<String> indexDropColumn(TriggerContext buildContext, String catalog, String schema, String table, EIndex indexInfo, List<String> needRemoveColumns) {
        EIndex copy = indexInfo.clone();
        copy.getColumnList().removeAll(needRemoveColumns);
        //
        ArrayList<String> indexScripts = new ArrayList<>();
        indexScripts.addAll(this.dropIndex(buildContext, catalog, schema, table, indexInfo));
        indexScripts.addAll(this.createIndex(buildContext, catalog, schema, table, copy));
        return indexScripts;
    }

    @Override
    public List<String> createPrimaryKey(TriggerContext buildContext, String catalog, String schema, String table, EPrimaryKey primaryInfo) {
        boolean useDelimited = buildContext.isUseDelimited();
        CaseSensitivityType caseSensitivity = useDelimited ? buildContext.getDelimitedCaseSensitivity() : buildContext.getPlainCaseSensitivity();
        StringBuilder sqlBuild = buildAlterTable(buildContext, catalog, schema, table);
        //
        sqlBuild.append(" add constraint ");
        String pkName = "";
        if (StringUtils.isBlank(primaryInfo.getPrimaryKeyName())) {
            pkName = table + "_pkey";
        } else {
            pkName = primaryInfo.getPrimaryKeyName();
        }
        //
        sqlBuild.append(fmtIndex(useDelimited, caseSensitivity, pkName));
        sqlBuild.append(" primary key ");
        sqlBuild.append("(");
        List<String> columnList = primaryInfo.getColumnList();
        for (int i = 0; i < columnList.size(); i++) {
            String column = columnList.get(i);
            if (i > 0) {
                sqlBuild.append(", ");
            }
            sqlBuild.append(fmtColumn(useDelimited, caseSensitivity, column));
        }
        sqlBuild.append(");");
        return Collections.singletonList(sqlBuild.toString());
    }

    @Override
    public List<String> dropPrimaryKey(TriggerContext buildContext, String catalog, String schema, String table, EPrimaryKey primaryInfo) {
        StringBuilder sqlBuild = buildAlterTable(buildContext, catalog, schema, table);
        sqlBuild.append(" drop constraint ");
        sqlBuild.append(primaryInfo.getPrimaryKeyName());
        sqlBuild.append(";");
        return Collections.singletonList(sqlBuild.toString());
    }

    @Override
    public List<String> primaryKeyAddColumn(TriggerContext buildContext, String catalog, String schema, String table, EPrimaryKey primaryInfo, List<String> needAddColumns) {
        EPrimaryKey copy = primaryInfo.clone();
        copy.getColumnList().addAll(needAddColumns);
        //
        ArrayList<String> indexScripts = new ArrayList<>();
        indexScripts.addAll(this.dropPrimaryKey(buildContext, catalog, schema, table, primaryInfo));
        indexScripts.addAll(this.createPrimaryKey(buildContext, catalog, schema, table, copy));
        return indexScripts;
    }

    @Override
    public List<String> primaryKeyDropColumn(TriggerContext buildContext, String catalog, String schema, String table, EPrimaryKey primaryInfo, List<String> needRemoveColumns) {
        EPrimaryKey copy = primaryInfo.clone();
        copy.getColumnList().removeAll(needRemoveColumns);
        //
        ArrayList<String> indexScripts = new ArrayList<>();
        indexScripts.addAll(this.dropPrimaryKey(buildContext, catalog, schema, table, primaryInfo));
        indexScripts.addAll(this.createPrimaryKey(buildContext, catalog, schema, table, copy));
        return indexScripts;
    }

    @Override
    public List<String> createForeignKey(TriggerContext buildContext, String catalog, String schema, String table, EForeignKey foreignKeyInfo) {
        return Collections.singletonList("postgresql build ddl script createForeignKey.");// TODO
    }

    @Override
    public List<String> dropForeignKey(TriggerContext buildContext, String catalog, String schema, String table, EForeignKey foreignKeyInfo) {
        return Collections.singletonList("postgresql build ddl script dropForeignKey.");// TODO
    }

    @Override
    public List<String> foreignKeyRename(TriggerContext buildContext, String catalog, String schema, String table, EForeignKey foreignKeyInfo, String newForeignKeyName) {
        return Collections.singletonList("postgresql build ddl script foreignKeyRename.");// TODO
    }
}
