package com.clougence.schema.editor;
import com.clougence.schema.AbstractMetadataServiceSupplierTest;
import com.clougence.schema.DsType;
import com.clougence.schema.DsUtils;
import com.clougence.schema.editor.builder.actions.Action;
import com.clougence.schema.metadata.CaseSensitivityType;
import com.clougence.schema.metadata.provider.rdb.MySqlMetadataProvider;
import com.clougence.schema.metadata.typemapping.TypeMapping;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.utils.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EditorTest extends AbstractMetadataServiceSupplierTest<MySqlMetadataProvider> {
    @Override
    protected Connection initConnection() throws SQLException {
        return DsUtils.localMySQL();
    }

    @Override
    protected MySqlMetadataProvider initRepository(Connection con) {
        return new MySqlMetadataProvider(con);
    }

    @Override
    protected void beforeTest(JdbcTemplate jdbcTemplate, MySqlMetadataProvider repository) throws SQLException, IOException {
        applySql("drop table tb_user");
        applySql("drop table proc_table_ref");
        applySql("drop table proc_table");
        applySql("drop table t3");
        applySql("drop table t1");
        //
        jdbcTemplate.loadSplitSQL(";", StandardCharsets.UTF_8, "/net_hasor_db/metadata/mysql_script.sql");
    }

    @Test
    public void editorInit() throws SQLException {
        EditorHelper helper = new EditorHelper(DsType.MySQL, this.connection);
        TableEditor tableEditor = helper.editTableEditor(null, repository.getCurrentSchema(), "tb_user", new EditorOptions());
        tableEditor.getColumn("loginName").delete();
        tableEditor.getColumn("index").rename("except");
        tableEditor.getPrimaryEditor().delete();
        //
        tableEditor.configCaseSensitivity(false, CaseSensitivityType.Upper);
        List<Action> actions = tableEditor.buildCreate(DsType.PostgreSQL);
        //
        System.out.println(StringUtils.join(actions.stream().map(action -> {
            return StringUtils.join(action.getSqlString().toArray(), '\n');
        }).toArray(), '\n'));
    }

    public static void main(String[] args) {
        TypeMapping.DEFAULT.get();
    }
}
