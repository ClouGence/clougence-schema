/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clougence.schema.editor.builder;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.clougence.commons.StringUtils;
import com.clougence.schema.DsType;
import com.clougence.schema.editor.ConflictException;
import com.clougence.schema.editor.EditorContext;
import com.clougence.schema.editor.NoColumnException;
import com.clougence.schema.editor.TableEditor;
import com.clougence.schema.editor.builder.actions.Action;
import com.clougence.schema.editor.domain.*;
import com.clougence.schema.editor.provider.BuilderProvider;
import com.clougence.schema.editor.provider.EditorProviderRegister;
import com.clougence.schema.metadata.CaseSensitivityType;
import com.clougence.schema.metadata.typemapping.TypeMapping;
import com.clougence.schema.umi.special.rdb.RdbForeignKeyRule;

/**
 * @author mode 2021/5/21 19:56
 */
public class TableEditorBuilder extends AbstractBuilder implements TableEditor {

    private final ETable eTable;

    public TableEditorBuilder(String schema, String name, EditorContext context){
        super(false, context);
        this.eTable = new ETable();
        this.eTable.setSchema(schema);
        this.eTable.setName(name);
    }

    public TableEditorBuilder(ETable eTable, EditorContext context){
        super(false, context);
        this.eTable = eTable;
    }

    TableEditorBuilder(boolean beAffected, EditorContext context, ETable eTable){
        super(beAffected, context);
        this.eTable = eTable;
    }

    @Override
    public ETable getSource() { return this.eTable; }

    @Override
    public void rename(String newName) {
        if (StringUtils.isBlank(newName)) {
            throw new NullPointerException("new name is null.");
        }
        if (!context.getBuilderProvider().supportTableRename()) {
            throw new UnsupportedOperationException("provider " + context.getBuilderProvider().getClass().getName() + " table rename Unsupported.");
        }
        if (Objects.equals(this.eTable.getName(), newName)) {
            return;
        }
        String oldName = this.eTable.getName();
        this.eTable.setName(newName);
        //
        super.triggerTableRename(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), oldName, newName);
    }

    @Override
    public void setComment(String comment) {
        if (Objects.equals(eTable.getComment(), comment)) {
            return;
        }
        this.eTable.setComment(comment);
        super.triggerTableComment(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), comment);
    }

    @Override
    public PrimaryEditor getPrimaryEditor() { return new PrimaryEditorBuilder(this.beAffected, this.context, this.eTable, this.eTable.getPrimaryKey()); }

    @Override
    public void createPrimaryEditor(String primaryName, List<String> columns) {
        EPrimaryKey primaryKey = this.eTable.getPrimaryKey();
        if (primaryKey != null) {
            throw new ConflictException("primaryKey already exists.");
        }
        //
        List<String> tableAllColumns = this.eTable.getColumnList().stream().map(EColumn::getName).collect(Collectors.toList());
        for (String column : columns) {
            if (!tableAllColumns.contains(column)) {
                throw new NoColumnException("column '" + column + "' not found.");
            }
        }
        //
        primaryKey = new EPrimaryKey();
        primaryKey.setPrimaryKeyName(primaryName);
        primaryKey.setColumnList(columns);
        this.eTable.setPrimaryKey(primaryKey);
        super.triggerPrimaryKeyCreate(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), primaryKey);
    }

    @Override
    public ColumnEditor addColumn(String columnName, String dbType, boolean nullable, Long length, Integer numericPrecision, Integer numericScale, Integer datetimePrecision,
                                  String defaultValue, boolean autoGenerate, String comment) {
        List<String> columnNames = this.eTable.getColumnList().stream().map(EColumn::getName).collect(Collectors.toList());
        if (columnNames.contains(columnName)) {
            throw new ConflictException("column '" + columnName + " already exists.");
        }
        EColumn eColumn = new EColumn();
        eColumn.setName(columnName);
        eColumn.setDbType(dbType);
        eColumn.setNullable(nullable);
        eColumn.setLength(length);
        eColumn.setNumericPrecision(numericPrecision);
        eColumn.setNumericScale(numericScale);
        eColumn.setDatetimePrecision(datetimePrecision);
        eColumn.setDefaultValue(defaultValue);
        eColumn.setAutoGenerate(autoGenerate);
        eColumn.setComment(comment);
        this.eTable.getColumnList().add(eColumn);
        //
        super.triggerColumnAdd(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), eColumn);
        return new ColumnEditorBuilder(this.beAffected, this.context, this.eTable, eColumn);
    }

    @Override
    public ColumnEditor getColumn(String columnName) {
        EColumn columnInfo = this.eTable.getColumnList().stream().filter(eColumn -> {
            return eColumn.getName().equals(columnName);
        }).findFirst().orElse(null);
        //
        if (columnInfo != null) {
            return new ColumnEditorBuilder(this.beAffected, this.context, this.eTable, columnInfo);
        }
        return null;
    }

    @Override
    public IndexEditor getIndexEditor(String indexName) {
        EIndex eIndex = this.eTable.getIndices().stream().filter(index -> {
            return index.getName().equals(indexName);
        }).findFirst().orElse(null);
        //
        if (eIndex != null) {
            return new IndexEditorBuilder(this.beAffected, this.context, this.eTable, eIndex);
        }
        return null;
    }

    @Override
    public IndexEditor addIndexEditor(String indexName, EIndexType indexType, String[] columnName) {
        List<String> indexNames = this.eTable.getIndices().stream().map(EIndex::getName).collect(Collectors.toList());
        if (indexNames.contains(indexName)) {
            throw new ConflictException("index '" + indexName + " already exists.");
        }
        EIndex eIndex = new EIndex();
        eIndex.setName(indexName);
        eIndex.setType(indexType);
        this.eTable.getIndices().add(eIndex);
        //
        IndexEditorBuilder editorBuilder = new IndexEditorBuilder(this.beAffected, this.context, this.eTable, eIndex);
        editorBuilder.addColumn(columnName);
        return editorBuilder;
    }

    @Override
    public ForeignKeyEditor getForeignKeyEditor(String fkName) {
        EForeignKey eForeignKey = this.eTable.getForeignKeys().stream().filter(foreignKey -> {
            return foreignKey.getName().equals(fkName);
        }).findFirst().orElse(null);
        //
        if (eForeignKey != null) {
            return new ForeignEditorBuilder(this.beAffected, this.context, this.eTable, eForeignKey);
        }
        return null;
    }

    @Override
    public ForeignKeyEditor addForeignKeyEditor(String fkName, String referenceSchema, String referenceTable, RdbForeignKeyRule updateRule, RdbForeignKeyRule deleteRule,
                                                Map<String, String> referenceMapping) {
        List<String> indexNames = this.eTable.getIndices().stream().map(EIndex::getName).collect(Collectors.toList());
        if (indexNames.contains(fkName)) {
            throw new ConflictException("foreignKey '" + fkName + " already exists.");
        }
        EForeignKey eForeignKey = new EForeignKey();
        eForeignKey.setName(fkName);
        eForeignKey.setReferenceSchema(referenceSchema);
        eForeignKey.setReferenceTable(referenceTable);
        eForeignKey.setUpdateRule(updateRule == null ? RdbForeignKeyRule.NoAction : updateRule);
        eForeignKey.setDeleteRule(deleteRule == null ? RdbForeignKeyRule.NoAction : deleteRule);
        referenceMapping = (referenceMapping == null) ? Collections.emptyMap() : referenceMapping;
        for (String columnName : referenceMapping.keySet()) {
            String referenceColumn = referenceMapping.get(columnName);
            eForeignKey.getColumnList().add(columnName);
            eForeignKey.getReferenceMapping().put(columnName, referenceColumn);
        }
        this.eTable.getForeignKeys().add(eForeignKey);
        //
        super.triggerForeignKeyCreate(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), eForeignKey);
        return new ForeignEditorBuilder(this.beAffected, this.context, this.eTable, eForeignKey);
    }

    @Override
    public void diffToActions(TableEditor tableEditor) {
    }

    @Override
    public List<Action> getActions() { return this.actions; }

    @Override
    public void configCaseSensitivity(boolean useDelimited, CaseSensitivityType caseSensitivityType) {
        if (useDelimited) {
            this.context.setDelimitedCaseSensitivity(caseSensitivityType);
        } else {
            this.context.setPlainCaseSensitivity(caseSensitivityType);
        }
    }

    @Override
    public List<Action> buildCreate(DsType targetDsType) {
        if (StringUtils.isBlank(this.eTable.getName())) {
            throw new NullPointerException("table name is null.");
        }
        if (this.eTable.getColumnList().isEmpty()) {
            throw new NullPointerException("table contains at least one column.");
        }
        if (this.context.getBuilderProvider() == null) {
            throw new NullPointerException("the builderProvider is not ready.");
        }
        //
        BuilderProvider provider = null;
        Function<EColumn, String> columnTypeMapping = null;
        DsType sourceDsType = this.context.getBuilderProvider().getDataSourceType();
        if (targetDsType != sourceDsType) {
            provider = EditorProviderRegister.findProvider(targetDsType);
            //
            TypeMapping typeMapping = TypeMapping.DEFAULT.get();
            final Map<String, String> mappingString = typeMapping.getMapping(sourceDsType, targetDsType);
            columnTypeMapping = c -> {
                String targetType = mappingString.get(c.getDbType());
                if (StringUtils.isBlank(targetType)) {
                    throw new IllegalArgumentException("source Type '" + c.getDbType() + "' has no mapping.");
                }
                return targetType;
            };
        } else {
            provider = this.context.getBuilderProvider();
        }
        //
        List<Action> cacheActions = new ArrayList<>(this.context.getActions());
        try {
            this.context.getActions().clear();
            super.triggerTableCreate(provider, this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), this.eTable, columnTypeMapping);
            return new ArrayList<>(this.context.getActions());
        } finally {
            this.context.getActions().clear();
            this.context.getActions().addAll(cacheActions);
        }
    }
}
