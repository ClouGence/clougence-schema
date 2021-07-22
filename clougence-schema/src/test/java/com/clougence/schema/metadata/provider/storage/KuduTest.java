package com.clougence.schema.metadata.provider.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.kudu.client.KuduClient;
import org.apache.kudu.test.KuduTestHarness;
import org.junit.Rule;
import org.junit.Test;
import com.clougence.schema.metadata.domain.storage.kudu.*;

public class KuduTest {

    @Rule
    public KuduTestHarness harness = new KuduTestHarness();

    protected KuduTable createTable(String name, String comment, String owner) {
        KuduTable table = new KuduTable();
        table.setTableName(name);
        table.setComment(comment);
        table.setOwner(owner);
        return table;
    }

    protected KuduColumn createColumn(String name, KuduTypes kuduType, Integer length, boolean isPrimaryKey) {
        KuduColumn column = new KuduColumn();
        column.setPrimaryKey(isPrimaryKey);
        column.setName(name);
        column.setKuduTypes(kuduType);
        column.setLength(length);
        return column;
    }

    protected KuduPartition createPartition(int numBuckets, Integer seed, List<String> columns) {
        KuduPartition partition = new KuduPartition();
        partition.setNumBuckets(numBuckets);
        partition.setSeed(seed);
        partition.setColumns(columns);
        partition.setPartitionType(KuduPartitionType.HashPartition);
        return partition;
    }

    @Test
    public void test() throws Exception {
        // Get a KuduClient configured to talk to the running mini cluster.
        KuduClient client = harness.getClient();

        // Create a new Kudu table.
        KuduTable tbUser = createTable("tb_user", "test table", "zyc");
        List<KuduColumn> columns = new ArrayList<>();
        columns.add(createColumn("uuid", KuduTypes.VARCHAR, 128, true));
        columns.add(createColumn("name", KuduTypes.VARCHAR, 128, true));
        columns.add(createColumn("password", KuduTypes.VARCHAR, 128, true));
        columns.add(createColumn("age", KuduTypes.DECIMAL, null, false));
        columns.add(createColumn("create_time", KuduTypes.DATE, null, false));
        //
        tbUser.setPartitionList(new ArrayList<>());
        tbUser.getPartitionList().add(createPartition(3, 11, Arrays.asList("uuid")));
        tbUser.getPartitionList().add(createPartition(3, 11, Arrays.asList("name", "password")));

        KuDuMetadataProvider provider = new KuDuMetadataProvider(client);
        provider.createTable(tbUser, columns, 5, TimeUnit.SECONDS);
        //
        KuduTable tableInfo = provider.getTable("tb_user");
        List<KuduColumn> kuduColumns = provider.getColumns("tb_user");
        // client.openTable("aa").newInsert().setRow();
        assert kuduColumns != null;
    }
}
