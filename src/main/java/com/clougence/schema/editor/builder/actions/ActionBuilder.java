/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clougence.schema.editor.builder.actions;
import com.clougence.schema.editor.domain.EColumn;
import com.clougence.schema.editor.domain.EForeignKey;
import com.clougence.schema.editor.domain.EIndex;
import com.clougence.schema.editor.domain.EPrimaryKey;

import java.util.List;

/**
 * @author mode 2021/6/8 19:56
 */
public class ActionBuilder {
    public Action buildTableRename(List<String> sqlString, String catalog, String schema, String table, String newName) {
        return new TableRenameAction(sqlString, catalog, schema, table, newName);
    }

    public Action buildTableComment(List<String> sqlString, String catalog, String schema, String table, String comment) {
        return new TableCommentAction(sqlString, catalog, schema, table, comment);
    }

    public Action buildColumnAdd(List<String> sqlString, String catalog, String schema, String table, EColumn columnInfo) {
        return new ColumnAddAction(sqlString, catalog, schema, table, columnInfo);
    }

    public Action buildColumnDrop(List<String> sqlString, String catalog, String schema, String table, EColumn columnInfo) {
        return new ColumnDropAction(sqlString, catalog, schema, table, columnInfo);
    }

    public Action buildColumnRename(List<String> sqlString, String catalog, String schema, String table, EColumn columnInfo, String newColumnName) {
        return new ColumnRenameAction(sqlString, catalog, schema, table, columnInfo, newColumnName);
    }

    public Action buildColumnChangeType(List<String> sqlString, String catalog, String schema, String table, EColumn columnInfo, EColumn newInfo, List<String> diffChange) {
        return new ColumnChangeTypeAction(sqlString, catalog, schema, table, columnInfo, newInfo, diffChange);
    }

    public Action buildColumnComment(List<String> sqlString, String catalog, String schema, String table, EColumn columnInfo, String comment) {
        return new ColumnCommentAction(sqlString, catalog, schema, table, columnInfo, comment);
    }

    public Action buildPrimaryKeyCreate(List<String> sqlString, String catalog, String schema, String table, EPrimaryKey primaryInfo) {
        return new PrimaryKeyCreateAction(sqlString, catalog, schema, table, primaryInfo);
    }

    public Action buildPrimaryKeyDrop(List<String> sqlString, String catalog, String schema, String table, EPrimaryKey primaryInfo) {
        return new PrimaryKeyDropAction(sqlString, catalog, schema, table, primaryInfo);
    }

    public Action buildPrimaryKeyAddColumn(List<String> sqlString, String catalog, String schema, String table, EPrimaryKey primaryInfo, List<String> needAddColumns) {
        return new PrimaryKeyAddColumnAction(sqlString, catalog, schema, table, primaryInfo, needAddColumns);
    }

    public Action buildPrimaryKeyDropColumn(List<String> sqlString, String catalog, String schema, String table, EPrimaryKey primaryInfo, List<String> needRemoveColumns) {
        return new PrimaryKeyDropColumnAction(sqlString, catalog, schema, table, primaryInfo, needRemoveColumns);
    }

    public Action buildIndexCreate(List<String> sqlString, String catalog, String schema, String table, EIndex indexInfo) {
        return new IndexCreateAction(sqlString, catalog, schema, table, indexInfo);
    }

    public Action buildIndexRename(List<String> sqlString, String catalog, String schema, String table, EIndex indexInfo, String newIndexName) {
        return new IndexRenameAction(sqlString, catalog, schema, table, indexInfo, newIndexName);
    }

    public Action buildIndexDrop(List<String> sqlString, String catalog, String schema, String table, EIndex indexInfo) {
        return new IndexDropAction(sqlString, catalog, schema, table, indexInfo);
    }

    public Action buildIndexAddColumn(List<String> sqlString, String catalog, String schema, String table, EIndex indexInfo, List<String> needAddColumns) {
        return new IndexAddColumnAction(sqlString, catalog, schema, table, indexInfo, needAddColumns);
    }

    public Action buildIndexDropColumn(List<String> sqlString, String catalog, String schema, String table, EIndex indexInfo, List<String> needRemoveColumns) {
        return new IndexDropColumnAction(sqlString, catalog, schema, table, indexInfo, needRemoveColumns);
    }

    public Action buildForeignKeyCreate(List<String> sqlString, String catalog, String schema, String table, EForeignKey foreignKeyInfo) {
        return new ForeignKeyCreateAction(sqlString, catalog, schema, table, foreignKeyInfo);
    }

    public Action buildForeignKeyDrop(List<String> sqlString, String catalog, String schema, String table, EForeignKey foreignKeyInfo) {
        return new ForeignKeyDropAction(sqlString, catalog, schema, table, foreignKeyInfo);
    }

    public Action buildForeignRename(List<String> sqlString, String catalog, String schema, String table, EForeignKey foreignKeyInfo, String newForeignKeyName) {
        return new ForeignRenameAction(sqlString, catalog, schema, table, foreignKeyInfo, newForeignKeyName);
    }
}
