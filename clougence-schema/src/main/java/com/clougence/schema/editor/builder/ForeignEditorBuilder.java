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
import com.clougence.schema.editor.TableEditor.ForeignKeyEditor;
import com.clougence.schema.editor.domain.EForeignKey;
import com.clougence.schema.editor.domain.ETable;
import com.clougence.schema.umi.special.rdb.RdbForeignKeyRule;
import net.hasor.utils.StringUtils;

/**
 * @author mode 2021/5/21 19:56
 */
class ForeignEditorBuilder extends AbstractBuilder implements ForeignKeyEditor {

    private final ETable      eTable;
    private final EForeignKey eForeignKey;

    public ForeignEditorBuilder(boolean beAffected, EditorContext context, ETable eTable, EForeignKey eForeignKey){
        super(beAffected, context);
        this.eTable = eTable;
        this.eForeignKey = eForeignKey;
    }

    @Override
    public EForeignKey getSource() { return this.eForeignKey; }

    @Override
    public void rename(String newName) {
        if (StringUtils.isBlank(newName)) {
            throw new NullPointerException("new name is null.");
        }
        ForeignKeyEditor foreignKeyEditor = new TableEditorBuilder(true, this.context, this.eTable).getForeignKeyEditor(newName);
        if (foreignKeyEditor != null) {
            throw new ConflictException("foreign Key '" + newName + " already exists.");
        }
        //
        if (context.getBuilderProvider().supportForeignRename()) {
            EForeignKey oldFk = this.eForeignKey.clone();
            this.eForeignKey.setName(newName);
            super.triggerForeignRename(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), oldFk, newName);
            return;
        }
        //
        EForeignKey oldFk = this.getSource().clone();
        this.delete();
        //
        Map<String, String> referenceMapping = new LinkedHashMap<>();
        for (String column : oldFk.getColumnList()) {
            String refToColumn = oldFk.getReferenceMapping().get(column);
            referenceMapping.put(column, refToColumn);
        }
        new TableEditorBuilder(this.beAffected, this.context, this.eTable)
            .addForeignKeyEditor(newName, oldFk.getReferenceSchema(), oldFk.getReferenceTable(), oldFk.getUpdateRule(), oldFk.getDeleteRule(), referenceMapping);
    }

    @Override
    public void addColumn(String columnName, String referenceColumn) {
        if (this.eForeignKey.getColumnList().contains(columnName)) {
            String refColumn = this.eForeignKey.getReferenceMapping().get(columnName);
            if (Objects.equals(refColumn, referenceColumn)) {
                return;
            } else {
                throw new ConflictException("foreign Key column '" + columnName + " have different references.");
            }
        }
        //
        this.eForeignKey.getColumnList().add(columnName);
        this.eForeignKey.getReferenceMapping().put(columnName, referenceColumn);
        reAdd();
    }

    @Override
    public void removeColumn(String[] columnName) {
        List<String> needRemove = new ArrayList<>();
        for (String column : columnName) {
            if (this.eForeignKey.getColumnList().contains(column)) {
                needRemove.add(column);
            }
        }
        //
        if (needRemove.isEmpty()) {
            return;
        }
        //
        this.eForeignKey.getColumnList().removeAll(needRemove);
        needRemove.forEach(key -> {
            this.eForeignKey.getReferenceMapping().remove(key);
        });
        if (this.eForeignKey.getColumnList().isEmpty()) {
            this.delete();
        } else {
            reAdd();
        }
    }

    @Override
    public void configUpdateRule(RdbForeignKeyRule foreignKeyRule) {
        foreignKeyRule = (foreignKeyRule == null) ? RdbForeignKeyRule.NoAction : foreignKeyRule;
        if (this.eForeignKey.getUpdateRule() == foreignKeyRule) {
            return;
        }
        this.eForeignKey.setUpdateRule(foreignKeyRule);
        this.reAdd();
    }

    @Override
    public void configDeleteRule(RdbForeignKeyRule foreignKeyRule) {
        foreignKeyRule = (foreignKeyRule == null) ? RdbForeignKeyRule.NoAction : foreignKeyRule;
        if (this.eForeignKey.getDeleteRule() == foreignKeyRule) {
            return;
        }
        this.eForeignKey.setDeleteRule(foreignKeyRule);
        this.reAdd();
    }

    private void reAdd() {
        Map<String, String> referenceMapping = new LinkedHashMap<>();
        for (String column : this.eForeignKey.getColumnList()) {
            String refToColumn = this.eForeignKey.getReferenceMapping().get(column);
            referenceMapping.put(column, refToColumn);
        }
        //
        triggerForeignKeyDrop(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), this.eForeignKey.clone());
        new TableEditorBuilder(this.beAffected, this.context, this.eTable).addForeignKeyEditor(this.eForeignKey.getName(), this.eForeignKey.getReferenceSchema(), this.eForeignKey
            .getReferenceTable(), this.eForeignKey.getUpdateRule(), this.eForeignKey.getDeleteRule(), referenceMapping);
    }

    public void delete() {
        EForeignKey oriForeignKey = this.eTable.getForeignKeys().stream().filter(foreignKey -> {
            return foreignKey.getName().equals(this.eForeignKey.getName());
        }).findFirst().orElse(null);
        //
        if (oriForeignKey != null) {
            this.eTable.getForeignKeys().remove(oriForeignKey);
            triggerForeignKeyDrop(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), oriForeignKey);
        }
    }
}
