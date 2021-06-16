package com.clougence.schema.editor;
import com.clougence.schema.DsType;
import com.clougence.schema.editor.builder.TableEditorBuilder;
import com.clougence.schema.editor.domain.EIndexType;
import com.clougence.schema.editor.domain.ETable;
import com.clougence.schema.umi.constraint.GeneralConstraintType;
import com.clougence.schema.umi.provider.UmiServiceRegister;
import com.clougence.schema.umi.provider.rdb.RdbUmiService;
import com.clougence.schema.umi.special.rdb.RdbColumn;
import com.clougence.schema.umi.special.rdb.RdbForeignKeyRule;
import com.clougence.schema.umi.special.rdb.RdbPrimaryKey;
import com.clougence.schema.umi.special.rdb.RdbTable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.hasor.utils.ExceptionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class EditorHelper {
    private final RdbUmiService rdbUmiService;

    public EditorHelper(DsType dsType, Connection connection) {
        this.rdbUmiService = UmiServiceRegister.createRdbUmiService(dsType, connection);
    }

    public EditorHelper(DsType dsType, DataSource dataSource) {
        this.rdbUmiService = UmiServiceRegister.createRdbUmiService(dsType, dataSource);
    }

    public EditorHelper(RdbUmiService rdbUmiService) {
        this.rdbUmiService = Objects.requireNonNull(rdbUmiService, "rdbUmiService must not null.");
    }

    private EditorContext createEditorContext(EditorOptions options) throws SQLException {
        EditorContext editorContext = new EditorContext(this.rdbUmiService.getDataSourceType());
        editorContext.setIncludeAffected(options.isIncludeAffected());
        if (options.getCaseSensitivity() != null) {
            if (options.isUseDelimited()) {
                editorContext.setPlainCaseSensitivity(this.rdbUmiService.getPlain());
                editorContext.setDelimitedCaseSensitivity(options.getCaseSensitivity());
            } else {
                editorContext.setPlainCaseSensitivity(options.getCaseSensitivity());
                editorContext.setDelimitedCaseSensitivity(this.rdbUmiService.getDelimited());
            }
        } else {
            editorContext.setPlainCaseSensitivity(this.rdbUmiService.getPlain());
            editorContext.setDelimitedCaseSensitivity(this.rdbUmiService.getDelimited());
        }
        editorContext.setUseDelimited(options.isUseDelimited());
        return editorContext;
    }

    public ETable loadTable(String catalog, String schema, String table) throws SQLException {
        RdbTable rdbTable = this.rdbUmiService.loadTable(catalog, schema, table);
        if (rdbTable == null) {
            return null;
        }
        //
        EditorContext editorContext = this.createEditorContext(new EditorOptions());
        TableEditor tableEditor = new TableEditorBuilder(schema, rdbTable.getName(), editorContext);
        //
        // table
        tableEditor.setComment(rdbTable.getComment());
        // columns
        rdbTable.getProperties().forEach((columnName, columnInfo) -> {
            RdbColumn rdbColumn = (RdbColumn) columnInfo;
            String dbType = columnInfo.getDataType().getCodeKey();
            boolean nullable = columnInfo.hasConstraint(GeneralConstraintType.NonNull);
            Long length = rdbColumn.getCharLength();
            Integer numericPrecision = rdbColumn.getNumericPrecision();
            Integer numericScale = rdbColumn.getNumericScale();
            Integer datetimePrecision = rdbColumn.getDatetimePrecision();
            String defaultValue = rdbColumn.getDefaultValue();
            String comment = rdbColumn.getComment();
            tableEditor.addColumn(columnName, dbType, nullable, length, numericPrecision, numericScale, datetimePrecision, defaultValue, false, comment);
        });
        // pk
        if (rdbTable.getPrimaryKey() != null) {
            RdbPrimaryKey primaryKey = rdbTable.getPrimaryKey();
            String primaryName = primaryKey.getName();
            List<String> columns = primaryKey.getColumnList();
            tableEditor.createPrimaryEditor(primaryName, columns);
        }
        // index
        rdbTable.getIndices().stream().filter(idx -> {
            return idx.getType().equalsIgnoreCase("Normal") || idx.getType().equalsIgnoreCase("Unique");
        }).forEach(idx -> {
            boolean isUnique = idx.getType().equalsIgnoreCase("Unique");
            if (isUnique) {
                tableEditor.addIndexEditor(idx.getName(), EIndexType.Unique, idx.getColumnList().toArray(new String[0]));
            } else {
                tableEditor.addIndexEditor(idx.getName(), EIndexType.Normal, idx.getColumnList().toArray(new String[0]));
            }
        });
        // fk
        rdbTable.getForeignKey().forEach(fk -> {
            RdbForeignKeyRule updateRole = fk.getUpdateRule();
            RdbForeignKeyRule deleteRole = fk.getDeleteRule();
            Map<String, String> referenceMapping = new LinkedHashMap<>();
            for (String column : fk.getColumnList()) {
                String refToColumn = fk.getReferenceMapping().get(column);
                referenceMapping.put(column, refToColumn);
            }
            tableEditor.addForeignKeyEditor(fk.getName(), fk.getReferenceSchema(), fk.getReferenceTable(), updateRole, deleteRole, referenceMapping);
        });
        return tableEditor.getSource();
    }

    public TableEditor createTableEditor(String catalog, String schema, String table, EditorOptions options) throws SQLException {
        RdbTable rdbTable = this.rdbUmiService.loadTable(catalog, schema, table);
        if (rdbTable != null) {
            throw new ConflictException("table '" + schema + "." + table + " already exists.");
        }
        //
        EditorContext editorContext = this.createEditorContext(options);
        return new TableEditorBuilder(schema, table, editorContext);
    }

    public TableEditor editTableEditor(String catalog, String schema, String table, EditorOptions options) throws SQLException {
        ETable eTable = loadTable(catalog, schema, table);
        if (eTable == null) {
            throw new ConflictException("table '" + schema + "." + table + " is not exists.");
        }
        //
        EditorContext editorContext = this.createEditorContext(options);
        eTable.initDomain();
        return new TableEditorBuilder(eTable, editorContext);
    }

    public TableEditor restoreTableEditor(String editorData, EditorOptions options) throws SQLException {
        EditorContext editorContext = createEditorContext(options);
        editorContext.setUseDelimited(options.isUseDelimited());
        //
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ETable eTable = objectMapper.readValue(editorData, new TypeReference<ETable>() {
            });
            //
            if (eTable == null) {
                throw new NullPointerException("ETable data deserialization failed. data is null.");
            }
            return new TableEditorBuilder(eTable, editorContext);
        } catch (Exception e) {
            String msg = "editorData to TableEditor error.msg:" + ExceptionUtils.getRootCauseMessage(e);
            throw new RuntimeException(msg, e);
        }
    }
}
