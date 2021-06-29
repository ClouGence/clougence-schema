package com.clougence.schema.metadata.provider.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.CreateTableOptions;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.ListTablesResponse;
import org.apache.kudu.test.KuduTestHarness;
import org.junit.Rule;
import org.junit.Test;

public class KuduTest {

    // The KuduTestHarness automatically starts and stops a real Kudu cluster
    // when each test is run. Kudu persists its on-disk state in a temporary
    // directory under a location defined by the environment variable TEST_TMPDIR
    // if set, or under /tmp otherwise. That cluster data is deleted on
    // successful exit of the test. The cluster output is logged through slf4j.
    @Rule
    public KuduTestHarness harness = new KuduTestHarness();

    @Test
    public void test() throws Exception {
        // Get a KuduClient configured to talk to the running mini cluster.
        KuduClient client = harness.getClient();

        // Create a new Kudu table.
        List<ColumnSchema> columns = new ArrayList<>();
        columns.add(new ColumnSchema.ColumnSchemaBuilder("key", Type.INT32).key(true).build());
        Schema schema = new Schema(columns);
        CreateTableOptions opts = new CreateTableOptions().setRangePartitionColumns(Collections.singletonList("key"));
        client.createTable("table-1", schema, opts);

        // Now we may insert rows into the newly-created Kudu table using 'client', scan the table, etc.
        client.getTableStatistics("");
        ListTablesResponse tablesList = client.getTablesList();
        List<ListTablesResponse.TableInfo> tableInfosList = tablesList.getTableInfosList();
        ListTablesResponse.TableInfo tableInfo = tableInfosList.get(0);
        tableInfo.getTableName();

        //        client.openTable("aa").newInsert().setRow();

    }

    private static ColumnSchema newColumn(String name, Type type, Boolean isKey) {
        ColumnSchema.ColumnSchemaBuilder column = new ColumnSchema.ColumnSchemaBuilder(name, type);
        column.key(isKey);
        column.
        return column.build();
    }

    private void aa() {
        //设置表的schema
        LinkedList<ColumnSchema> columns = new LinkedList<>();

        /*
         *和RDBMS不同的是，Kudu不提供自动递增列功能，因此应用程序必须始终
         * 在插入期间提供完整的主键
         */
        columns.add(newColumn("id", Type.INT32, true));
        columns.add(newColumn("name", Type.STRING, false));
        Schema schema = new Schema(columns);

        //创建表时提供的所有选项
        CreateTableOptions options = new CreateTableOptions();

        //设置表的replica备份和分区规则
        LinkedList<String> parcols = new LinkedList<>();
        parcols.add("id");

        //设置表的备份数
        options.setNumReplicas(1);

        //设置hash分区和数量
        options.addHashPartitions(parcols, 3);
    }
}
