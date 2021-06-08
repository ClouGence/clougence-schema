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
package com.clougence.schema.editor.builder;
import com.clougence.schema.editor.EditorContext;
import com.clougence.schema.editor.builder.actions.Action;
import com.clougence.schema.editor.domain.EColumn;
import com.clougence.schema.editor.domain.EForeignKey;
import com.clougence.schema.editor.domain.EIndex;
import com.clougence.schema.editor.domain.EPrimaryKey;
import com.clougence.schema.editor.provider.BuilderProvider;
import com.clougence.schema.editor.triggers.TriggerContext;
import com.clougence.schema.metadata.CaseSensitivityType;

import java.util.List;

/**
 * @author mode 2021/5/21 19:56
 */
public class AbstractBuilder {
    protected final EditorContext context;
    protected       List<Action>  actions;
    protected       boolean       beAffected;

    AbstractBuilder(boolean beAffected, EditorContext context) {
        this.beAffected = beAffected;
        this.context = context;
        this.actions = context.getActions();
    }

    public CaseSensitivityType plainCaseSensitivity() {
        return this.context.getPlainCaseSensitivity();
    }

    public CaseSensitivityType delimitedCaseSensitivity() {
        return this.context.getDelimitedCaseSensitivity();
    }

    public boolean useDelimited() {
        return this.context.isUseDelimited();
    }

    protected TriggerContext buildContext() {
        TriggerContext buildContext = new TriggerContext();
        buildContext.setPlainCaseSensitivity(this.context.getPlainCaseSensitivity());
        buildContext.setDelimitedCaseSensitivity(this.context.getDelimitedCaseSensitivity());
        buildContext.setUseDelimited(this.context.isUseDelimited());
        buildContext.setCascade(this.context.isCascade());
        return buildContext;
    }
    // -------------------------------------------------------------------------------------------------------------------------- Table

    protected void triggerTableRename(boolean beAffected, String catalog, String schema, String table, String newName) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.tableRename(buildContext(), catalog, schema, table, newName);
            this.actions.add(context.getActionBuilder().buildTableRename(sqlString, catalog, schema, table, newName));
        }
    }

    protected void triggerTableComment(boolean beAffected, String catalog, String schema, String table, String comment) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.tableComment(buildContext(), catalog, schema, table, comment);
            this.actions.add(context.getActionBuilder().buildTableComment(sqlString, catalog, schema, table, comment));
        }
    }
    // -------------------------------------------------------------------------------------------------------------------------- Column

    protected void triggerColumnAdd(boolean beAffected, String catalog, String schema, String table, EColumn columnInfo) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.addColumn(buildContext(), catalog, schema, table, columnInfo);
            this.actions.add(context.getActionBuilder().buildColumnAdd(sqlString, catalog, schema, table, columnInfo));
        }
    }

    protected void triggerColumnDrop(boolean beAffected, String catalog, String schema, String table, EColumn columnInfo) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.dropColumn(buildContext(), catalog, schema, table, columnInfo);
            this.actions.add(context.getActionBuilder().buildColumnDrop(sqlString, catalog, schema, table, columnInfo));
        }
    }

    protected void triggerColumnRename(boolean beAffected, String catalog, String schema, String table, EColumn columnInfo, String newColumnName) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.columnRename(buildContext(), catalog, schema, table, columnInfo, newColumnName);
            this.actions.add(context.getActionBuilder().buildColumnRename(sqlString, catalog, schema, table, columnInfo, newColumnName));
        }
    }

    protected void triggerColumnChangeType(boolean beAffected, String catalog, String schema, String table, EColumn columnInfo, EColumn newInfo, List<String> diffChange) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.columnChange(buildContext(), catalog, schema, table, columnInfo, newInfo, diffChange);
            this.actions.add(context.getActionBuilder().buildColumnChangeType(sqlString, catalog, schema, table, columnInfo, newInfo, diffChange));
        }
    }

    protected void triggerColumnComment(boolean beAffected, String catalog, String schema, String table, EColumn columnInfo, String comment) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.columnComment(buildContext(), catalog, schema, table, columnInfo, comment);
            this.actions.add(context.getActionBuilder().buildColumnComment(sqlString, catalog, schema, table, columnInfo, comment));
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------- PrimaryKey
    protected void triggerPrimaryKeyCreate(boolean beAffected, String catalog, String schema, String table, EPrimaryKey primaryInfo) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.createPrimaryKey(buildContext(), catalog, schema, table, primaryInfo);
            this.actions.add(context.getActionBuilder().buildPrimaryKeyCreate(sqlString, catalog, schema, table, primaryInfo));
        }
    }

    protected void triggerPrimaryKeyDrop(boolean beAffected, String catalog, String schema, String table, EPrimaryKey primaryInfo) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.dropPrimaryKey(buildContext(), catalog, schema, table, primaryInfo);
            this.actions.add(context.getActionBuilder().buildPrimaryKeyDrop(sqlString, catalog, schema, table, primaryInfo));
        }
    }

    protected void triggerPrimaryKeyAddColumn(boolean beAffected, String catalog, String schema, String table, EPrimaryKey primaryInfo, List<String> needAddColumns) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.primaryKeyAddColumn(buildContext(), catalog, schema, table, primaryInfo, needAddColumns);
            this.actions.add(context.getActionBuilder().buildPrimaryKeyAddColumn(sqlString, catalog, schema, table, primaryInfo, needAddColumns));
        }
    }

    protected void triggerPrimaryKeyDropColumn(boolean beAffected, String catalog, String schema, String table, EPrimaryKey primaryInfo, List<String> needRemoveColumns) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.primaryKeyDropColumn(buildContext(), catalog, schema, table, primaryInfo, needRemoveColumns);
            this.actions.add(context.getActionBuilder().buildPrimaryKeyDropColumn(sqlString, catalog, schema, table, primaryInfo, needRemoveColumns));
        }
    }
    // -------------------------------------------------------------------------------------------------------------------------- Index

    protected void triggerIndexCreate(boolean beAffected, String catalog, String schema, String table, EIndex indexInfo) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.createIndex(buildContext(), catalog, schema, table, indexInfo);
            this.actions.add(context.getActionBuilder().buildIndexCreate(sqlString, catalog, schema, table, indexInfo));
        }
    }

    protected void triggerIndexRename(boolean beAffected, String catalog, String schema, String table, EIndex indexInfo, String newIndexName) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.indexRename(buildContext(), catalog, schema, table, indexInfo, newIndexName);
            this.actions.add(context.getActionBuilder().buildIndexRename(sqlString, catalog, schema, table, indexInfo, newIndexName));
        }
    }

    protected void triggerIndexDrop(boolean beAffected, String catalog, String schema, String table, EIndex indexInfo) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.dropIndex(buildContext(), catalog, schema, table, indexInfo);
            this.actions.add(context.getActionBuilder().buildIndexDrop(sqlString, catalog, schema, table, indexInfo));
        }
    }

    protected void triggerIndexAddColumn(boolean beAffected, String catalog, String schema, String table, EIndex indexInfo, List<String> needAddColumns) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.indexAddColumn(buildContext(), catalog, schema, table, indexInfo, needAddColumns);
            this.actions.add(context.getActionBuilder().buildIndexAddColumn(sqlString, catalog, schema, table, indexInfo, needAddColumns));
        }
    }

    protected void triggerIndexDropColumn(boolean beAffected, String catalog, String schema, String table, EIndex indexInfo, List<String> needRemoveColumns) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.indexDropColumn(buildContext(), catalog, schema, table, indexInfo, needRemoveColumns);
            this.actions.add(context.getActionBuilder().buildIndexDropColumn(sqlString, catalog, schema, table, indexInfo, needRemoveColumns));
        }
    }
    // -------------------------------------------------------------------------------------------------------------------------- ForeignKey

    protected void triggerForeignKeyCreate(boolean beAffected, String catalog, String schema, String table, EForeignKey foreignKeyInfo) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.createForeignKey(buildContext(), catalog, schema, table, foreignKeyInfo);
            this.actions.add(context.getActionBuilder().buildForeignKeyCreate(sqlString, catalog, schema, table, foreignKeyInfo));
        }
    }

    protected void triggerForeignKeyDrop(boolean beAffected, String catalog, String schema, String table, EForeignKey foreignKeyInfo) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.dropForeignKey(buildContext(), catalog, schema, table, foreignKeyInfo);
            this.actions.add(context.getActionBuilder().buildForeignKeyDrop(sqlString, catalog, schema, table, foreignKeyInfo));
        }
    }

    protected void triggerForeignRename(boolean beAffected, String catalog, String schema, String table, EForeignKey foreignKeyInfo, String newForeignKeyName) {
        BuilderProvider provider = context.getBuilderProvider();
        if (provider == null) {
            return;
        }
        if (context.isIncludeAffected() || !beAffected) {
            List<String> sqlString = provider.foreignKeyRename(buildContext(), catalog, schema, table, foreignKeyInfo, newForeignKeyName);
            this.actions.add(context.getActionBuilder().buildForeignRename(sqlString, catalog, schema, table, foreignKeyInfo, newForeignKeyName));
        }
    }
}
