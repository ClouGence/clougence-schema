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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.clougence.utils.StringUtils;
import com.clougence.schema.editor.ConflictException;
import com.clougence.schema.editor.EditorContext;
import com.clougence.schema.editor.NoColumnException;
import com.clougence.schema.editor.TableEditor.IndexEditor;
import com.clougence.schema.editor.domain.EColumn;
import com.clougence.schema.editor.domain.EIndex;
import com.clougence.schema.editor.domain.ETable;

/**
 * @author mode 2021/5/21 19:56
 */
class IndexEditorBuilder extends AbstractBuilder implements IndexEditor {

    private final ETable eTable;
    private final EIndex eIndex;

    public IndexEditorBuilder(boolean beAffected, EditorContext context, ETable eTable, EIndex eIndex){
        super(beAffected, context);
        this.eTable = eTable;
        this.eIndex = eIndex;
    }

    @Override
    public EIndex getSource() { return this.eIndex; }

    @Override
    public void rename(String newName) {
        if (StringUtils.isBlank(newName)) {
            throw new NullPointerException("new name is null.");
        }
        String oldName = this.eIndex.getName();
        if (StringUtils.equals(oldName, newName)) {
            return;
        }
        //
        IndexEditor indexEditor = new TableEditorBuilder(true, this.context, this.eTable).getIndexEditor(newName);
        if (indexEditor != null) {
            throw new ConflictException("index '" + newName + " already exists.");
        }
        //
        if (context.getBuilderProvider().supportIndexRename()) {
            EIndex oldIndex = this.eIndex.clone();
            this.eIndex.setName(newName);
            super.triggerIndexRename(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), oldIndex, newName);
            return;
        }
        //
        EIndex oldIndex = this.getSource().clone();
        this.delete();
        //
        String[] columns = oldIndex.getColumnList().toArray(new String[0]);
        new TableEditorBuilder(this.beAffected, this.context, this.eTable).addIndexEditor(newName, oldIndex.getType(), columns);
    }

    @Override
    public void addColumn(String[] columnName) {
        List<String> needAdd = new ArrayList<>();
        for (String column : columnName) {
            if (!this.eIndex.getColumnList().contains(column)) {
                needAdd.add(column);
            }
        }
        //
        if (needAdd.isEmpty()) {
            return;
        }
        //
        List<String> tableAllColumns = this.eTable.getColumnList().stream().map(EColumn::getName).collect(Collectors.toList());
        for (String column : needAdd) {
            if (!tableAllColumns.contains(column)) {
                throw new NoColumnException("column '" + column + "' not found.");
            }
        }
        //
        EIndex oldIndex = this.eIndex.clone();
        this.eIndex.getColumnList().addAll(needAdd);
        //
        if (this.eIndex.getColumnList().size() == needAdd.size()) {
            super.triggerIndexCreate(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), this.eIndex);
        } else {
            super.triggerIndexAddColumn(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), oldIndex, needAdd);
        }
    }

    @Override
    public void removeColumn(String[] columnName) {
        List<String> needRemove = new ArrayList<>();
        for (String column : columnName) {
            if (this.eIndex.getColumnList().contains(column)) {
                needRemove.add(column);
            }
        }
        //
        if (needRemove.isEmpty()) {
            return;
        }
        //
        EIndex oldIndex = this.eIndex.clone();
        this.eIndex.getColumnList().removeAll(needRemove);
        if (this.eIndex.getColumnList().isEmpty()) {
            this.delete();
        } else {
            super.triggerIndexDropColumn(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), oldIndex, needRemove);
        }
    }

    @Override
    public void delete() {
        EIndex oriIndex = this.eTable.getIndices().stream().filter(index -> {
            return index.getName().equals(eIndex.getName());
        }).findFirst().orElse(null);
        //
        if (oriIndex != null) {
            this.eTable.getIndices().remove(oriIndex);
            super.triggerIndexDrop(this.beAffected, this.eTable.getCatalog(), this.eTable.getSchema(), this.eTable.getName(), oriIndex);
        }
    }
}
