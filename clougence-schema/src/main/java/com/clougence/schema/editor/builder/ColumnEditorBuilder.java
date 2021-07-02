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

import com.clougence.schema.editor.ConflictException;
import com.clougence.schema.editor.EditorContext;
import com.clougence.schema.editor.TableEditor.ColumnEditor;
import com.clougence.schema.editor.TableEditor.ForeignKeyEditor;
import com.clougence.schema.editor.TableEditor.IndexEditor;
import com.clougence.schema.editor.domain.EColumn;
import com.clougence.schema.editor.domain.EForeignKey;
import com.clougence.schema.editor.domain.EIndex;
import com.clougence.schema.editor.domain.ETable;
import net.hasor.utils.StringUtils;

/**
 * @author mode 2021/5/21 19:56
 */
class ColumnEditorBuilder extends AbstractBuilder implements ColumnEditor {

    private final ETable  eTable;
    private final EColumn eColumn;

    public ColumnEditorBuilder(boolean beAffected, EditorContext context, ETable eTable, EColumn eColumn){
        super(beAffected, context);
        this.eTable = eTable;
        this.eColumn = eColumn;
    }

    @Override
    public EColumn getSource() { return this.eColumn; }

    @Override
    public void rename(String newName) {
        if (StringUtils.isBlank(newName)) {
            throw new NullPointerException("new name is null.");
        }
        ColumnEditor columnEditor = new TableEditorBuilder(true, this.context, this.eTable).getColumn(newName);
        if (columnEditor != null) {
            throw new ConflictException("column '" + newName + " already exists.");
        }
        if (!context.getBuilderProvider().supportColumnRename()) {
            throw new UnsupportedOperationException("provider " + this.context.getBuilderProvider().getClass() + " column rename Unsupported.");
        }
        //
        String oldName = this.eColumn.getName();
        boolean reAddPk = false;
        List<String> reAddIndex = new ArrayList<>();
        Map<String, String> reAddFk = new LinkedHashMap<>();
        Map<String, EIndex> oldIdxInfo = new LinkedHashMap<>();
        Map<String, EForeignKey> oldFkInfo = new LinkedHashMap<>();
        //
        new ArrayList<>(this.eTable.getForeignKeys()).forEach(eForeignKey -> {
            if (eForeignKey.getColumnList().contains(oldName)) {
                String refMapping = eForeignKey.getReferenceMapping().get(oldName);
                reAddFk.put(eForeignKey.getName(), refMapping);
                oldFkInfo.put(eForeignKey.getName(), eForeignKey.clone());
                new ForeignEditorBuilder(true, this.context, this.eTable, eForeignKey).removeColumn(new String[] { oldName });
            }
        });
        new ArrayList<>(this.eTable.getIndices()).forEach(eIndex -> {
            if (eIndex.getColumnList().contains(oldName)) {
                oldIdxInfo.put(eIndex.getName(), eIndex);
                reAddIndex.add(eIndex.getName());
                new IndexEditorBuilder(true, this.context, this.eTable, eIndex).removeColumn(new String[] { oldName });
            }
        });
        if (this.eTable.getPrimaryKey().getColumnList().contains(oldName)) {
            new PrimaryEditorBuilder(true, this.context, this.eTable, this.eTable.getPrimaryKey()).removeColumn(new String[] { oldName });
            reAddPk = true;
        }
        //
        EColumn oldColumn = this.eColumn.clone();
        this.eColumn.setName(newName);
        super.triggerColumnRename(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), oldColumn, newName);
        //
        if (reAddPk) {
            new PrimaryEditorBuilder(true, this.context, this.eTable, this.eTable.getPrimaryKey()).addColumn(new String[] { newName });
        }
        for (String indexName : reAddIndex) {
            IndexEditor indicesEditor = new TableEditorBuilder(true, this.context, this.eTable).getIndexEditor(indexName);
            if (indicesEditor == null) {
                EIndex oldIndex = oldIdxInfo.get(indexName);
                new TableEditorBuilder(true, this.context, this.eTable).addIndexEditor(indexName, oldIndex.getType(), new String[] { newName });
            } else {
                indicesEditor.addColumn(new String[] { newName });
            }
        }
        reAddFk.forEach((fkName, refColumn) -> {
            ForeignKeyEditor foreignKeyEditor = new TableEditorBuilder(true, this.context, this.eTable).getForeignKeyEditor(fkName);
            if (foreignKeyEditor != null) {
                foreignKeyEditor.addColumn(newName, refColumn);
                return;
            }
            //
            EForeignKey oldFk = oldFkInfo.get(fkName);
            Map<String, String> referenceMapping = new LinkedHashMap<>();
            referenceMapping.put(oldName, refColumn);
            new TableEditorBuilder(true, this.context, this.eTable)
                .addForeignKeyEditor(fkName, oldFk.getReferenceSchema(), oldFk.getReferenceTable(), oldFk.getUpdateRule(), oldFk.getDeleteRule(), referenceMapping);
        });
    }

    @Override
    public void setComment(String comment) {
        if (Objects.equals(this.eColumn.getComment(), comment)) {
            return;
        }
        this.eColumn.setComment(comment);
        super.triggerColumnComment(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), this.eColumn, comment);
    }

    @Override
    public void delete() {
        EColumn columnInfo = this.eTable.getColumnList().stream().filter(eColumn -> {
            return eColumn.getName().equals(this.eColumn.getName());
        }).findFirst().orElse(null);
        //
        if (columnInfo != null) {
            String columnName = this.eColumn.getName();
            new ArrayList<>(this.eTable.getForeignKeys()).forEach(eForeignKey -> {
                if (eForeignKey.getColumnList().contains(columnName)) {
                    new ForeignEditorBuilder(true, this.context, this.eTable, eForeignKey).removeColumn(new String[] { columnName });
                }
            });
            new ArrayList<>(this.eTable.getIndices()).forEach(eIndex -> {
                if (eIndex.getColumnList().contains(columnName)) {
                    new IndexEditorBuilder(true, this.context, this.eTable, eIndex).removeColumn(new String[] { columnName });
                }
            });
            if (this.eTable.getPrimaryKey().getColumnList().contains(columnName)) {
                new PrimaryEditorBuilder(true, this.context, this.eTable, this.eTable.getPrimaryKey()).removeColumn(new String[] { columnName });
            }
            this.eTable.getColumnList().remove(columnInfo);
            //
            super.triggerColumnDrop(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), columnInfo);
        }
    }

    private void diffChangeAdd(List<String> diffChange, boolean test, String key) {
        if (test && !diffChange.contains(key)) {
            diffChange.add(key);
        }
    }

    @Override
    public void change(String dbType, boolean nullable, Long length, Integer numericPrecision, Integer numericScale, Integer datetimePrecision, String defaultValue,
                       boolean autoGenerate, String comment) {
        boolean compareTypeDef = Objects.equals(dbType, this.eColumn.getDbType());
        boolean compareNullable = this.eColumn.isNullable() == nullable;
        boolean compareLength = Objects.equals(length, this.eColumn.getLength());
        boolean compareNumericPrecision = Objects.equals(numericPrecision, this.eColumn.getNumericPrecision());
        boolean compareNumericScale = Objects.equals(numericScale, this.eColumn.getNumericScale());
        boolean compareDatetimePrecision = Objects.equals(datetimePrecision, this.eColumn.getDatetimePrecision());
        boolean compareDefaultValue = Objects.equals(defaultValue, this.eColumn.getDefaultValue());
        boolean compareAutoGenerate = this.eColumn.isAutoGenerate() == autoGenerate;
        boolean compareComment = Objects.equals(comment, this.eColumn.getComment());
        //
        List<String> diffChange = new ArrayList<>();
        diffChangeAdd(diffChange, compareTypeDef, "dbType");
        diffChangeAdd(diffChange, compareNullable, "nullable");
        diffChangeAdd(diffChange, compareLength, "length");
        diffChangeAdd(diffChange, compareNumericPrecision, "numericPrecision");
        diffChangeAdd(diffChange, compareNumericScale, "numericScale");
        diffChangeAdd(diffChange, compareDatetimePrecision, "datetimePrecision");
        diffChangeAdd(diffChange, compareDefaultValue, "defaultValue");
        diffChangeAdd(diffChange, compareAutoGenerate, "autoGenerate");
        diffChangeAdd(diffChange, compareComment, "comment");
        //
        if (!diffChange.isEmpty()) {
            EColumn oldColumn = this.eColumn.clone();
            this.eColumn.setDbType(dbType);
            this.eColumn.setNullable(nullable);
            this.eColumn.setLength(length);
            this.eColumn.setNumericPrecision(numericPrecision);
            this.eColumn.setNumericScale(numericScale);
            this.eColumn.setDatetimePrecision(datetimePrecision);
            this.eColumn.setDefaultValue(defaultValue);
            this.eColumn.setAutoGenerate(autoGenerate);
            this.eColumn.setComment(comment);
            super.triggerColumnChangeType(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), oldColumn, this.eColumn, diffChange);
        }
    }

    @Override
    public void changeType(String dbType) {
        if (Objects.equals(this.eColumn.getDbType(), dbType)) {
            return;
        }
        this.change(dbType, this.eColumn.isNullable(), this.eColumn.getLength(), this.eColumn.getNumericPrecision(), this.eColumn.getNumericScale(), this.eColumn
            .getDatetimePrecision(), this.eColumn.getDefaultValue(), this.eColumn.isAutoGenerate(), this.eColumn.getComment());
    }

    @Override
    public void setNullable(boolean isNullable) {
        if (this.eColumn.isNullable() == isNullable) {
            return;
        }
        this.change(this.eColumn.getDbType(), isNullable, this.eColumn.getLength(), this.eColumn.getNumericPrecision(), this.eColumn.getNumericScale(), this.eColumn
            .getDatetimePrecision(), this.eColumn.getDefaultValue(), this.eColumn.isAutoGenerate(), this.eColumn.getComment());
    }

    @Override
    public void setCharLength(long length) {
        if (Long.valueOf(length).equals(this.eColumn.getLength())) {
            return;
        }
        this.change(this.eColumn.getDbType(), this.eColumn.isNullable(), length, this.eColumn.getNumericPrecision(), this.eColumn.getNumericScale(), this.eColumn
            .getDatetimePrecision(), this.eColumn.getDefaultValue(), this.eColumn.isAutoGenerate(), this.eColumn.getComment());
    }

    @Override
    public void setNumberLength(int numericPrecision, int numericScale) {
        if (Integer.valueOf(numericPrecision).equals(this.eColumn.getNumericPrecision()) && Integer.valueOf(numericScale).equals(this.eColumn.getNumericScale())) {
            return;
        }
        this.change(this.eColumn.getDbType(), this.eColumn.isNullable(), this.eColumn.getLength(), numericPrecision, numericScale, this.eColumn.getDatetimePrecision(), this.eColumn
            .getDefaultValue(), this.eColumn.isAutoGenerate(), this.eColumn.getComment());
    }

    @Override
    public void setDateTimeLength(int datetimePrecision) {
        if (Integer.valueOf(datetimePrecision).equals(this.eColumn.getDatetimePrecision())) {
            return;
        }
        this.change(this.eColumn.getDbType(), this.eColumn.isNullable(), this.eColumn.getLength(), this.eColumn.getNumericPrecision(), this.eColumn
            .getNumericScale(), datetimePrecision, this.eColumn.getDefaultValue(), this.eColumn.isAutoGenerate(), this.eColumn.getComment());
    }

    @Override
    public void setDefault(String defaultValue) {
        if (Objects.equals(this.eColumn.getDefaultValue(), defaultValue)) {
            return;
        }
        this.change(this.eColumn.getDbType(), this.eColumn.isNullable(), this.eColumn.getLength(), this.eColumn.getNumericPrecision(), this.eColumn.getNumericScale(), this.eColumn
            .getDatetimePrecision(), defaultValue, this.eColumn.isAutoGenerate(), this.eColumn.getComment());
    }

    @Override
    public void setAutoGenerate(boolean autoGenerate) {
        if (this.eColumn.isAutoGenerate() == autoGenerate) {
            return;
        }
        this.change(this.eColumn.getDbType(), this.eColumn.isNullable(), this.eColumn.getLength(), this.eColumn.getNumericPrecision(), this.eColumn.getNumericScale(), this.eColumn
            .getDatetimePrecision(), this.eColumn.getDefaultValue(), autoGenerate, this.eColumn.getComment());
    }
}
