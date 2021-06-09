package com.clougence.schema.editor;
import com.clougence.schema.DataSourceType;
import com.clougence.schema.editor.builder.actions.Action;
import com.clougence.schema.editor.domain.*;
import com.clougence.schema.metadata.CaseSensitivityType;

import java.util.List;
import java.util.Map;

public interface TableEditor extends EditorSource<ETable> {
    public void rename(String newName);

    public void setComment(String comment);

    public PrimaryEditor getPrimaryEditor();

    public default void createPrimaryEditor(List<String> columns) {
        createPrimaryEditor(null, columns);
    }

    public void createPrimaryEditor(String primaryName, List<String> columns);

    public ColumnEditor addColumn(String columnName, String dbType, boolean nullable, Long length, Integer numericPrecision, Integer numericScale, Integer datetimePrecision, String defaultValue, boolean autoGenerate, String comment);

    public ColumnEditor getColumn(String columnName);

    public IndexEditor getIndexEditor(String indexName);

    public IndexEditor addIndexEditor(String indexName, EIndexType indexType, String[] columnName);

    public ForeignKeyEditor getForeignKeyEditor(String fkName);

    public ForeignKeyEditor addForeignKeyEditor(String fkName, String referenceSchema, String referenceTable, EForeignKeyRule updateRule, EForeignKeyRule deleteRule, Map<String, String> referenceMapping);

    public void buildCreate(DataSourceType dataSourceType);

    public interface PrimaryEditor extends EditorSource<EPrimaryKey> {
        public void addColumn(String[] columnName);

        public void removeColumn(String[] columnName);

        public void delete();
    }

    public interface ColumnEditor extends EditorSource<EColumn> {
        public void rename(String newName);

        public void change(String dbType, boolean nullable, Long length, Integer numericPrecision, Integer numericScale, Integer datetimePrecision, String defaultValue, boolean autoGenerate, String comment);

        public void changeType(String dbType);

        public void setNullable(boolean isNullable);

        public void setCharLength(long length);

        public void setNumberLength(int numericPrecision, int numericScale);

        public void setDateTimeLength(int datetimePrecision);

        public void setDefault(String defaultValue);

        public void setComment(String comment);

        public void setAutoGenerate(boolean autoGenerate);

        public void delete();
    }

    public interface IndexEditor extends EditorSource<EIndex> {
        public void rename(String newName);

        public void addColumn(String[] columnName);

        public void removeColumn(String[] columnName);

        public void delete();
    }

    public interface ForeignKeyEditor extends EditorSource<EForeignKey> {
        public void rename(String newName);

        public void addColumn(String columnName, String referenceColumn);

        public void removeColumn(String[] columnName);

        public void configUpdateRule(EForeignKeyRule foreignKeyRule);

        public void configDeleteRule(EForeignKeyRule foreignKeyRule);

        public void delete();
    }

    public void diffToActions(TableEditor tableEditor);

    public List<Action> getActions();

    public CaseSensitivityType plainCaseSensitivity();

    public CaseSensitivityType delimitedCaseSensitivity();

    public boolean useDelimited();

    public void configCaseSensitivity(boolean useDelimited, CaseSensitivityType caseSensitivityType);
}
