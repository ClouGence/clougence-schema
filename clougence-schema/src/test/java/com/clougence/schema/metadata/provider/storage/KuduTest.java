package com.clougence.schema.metadata.provider.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.kudu.client.KuduClient;
import org.junit.Test;
import com.clougence.schema.metadata.domain.rdb.kudu.*;
import com.clougence.schema.metadata.provider.rdb.KuDuMetadataProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KuduTest {

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
    public void testRemote() throws Exception {
        // Get a KuduClient configured to talk to the running mini cluster.
        KuduClient client = new KuduClient.KuduClientBuilder("192.168.0.244:7051").build();

        // Create a new Kudu table.
        KuduTable tbUser = createTable("tb_user", "test table", "zyc");
        List<KuduColumn> columns = new ArrayList<>();
        columns.add(createColumn("uuid", KuduTypes.STRING, 128, true));
        columns.add(createColumn("name", KuduTypes.STRING, 128, true));
        columns.add(createColumn("password", KuduTypes.STRING, 128, true));
        columns.add(createColumn("age", KuduTypes.DECIMAL, null, false));
        columns.add(createColumn("create_time", KuduTypes.DATE, null, false));
        //
        tbUser.setPartitionList(new ArrayList<>());
        tbUser.getPartitionList().add(createPartition(3, 11, Arrays.asList("uuid")));
        tbUser.getPartitionList().add(createPartition(3, 11, Arrays.asList("name", "password")));

        KuDuMetadataProvider provider = new KuDuMetadataProvider(client);
        //
        if (provider.getTable("tb_user") != null) {
            provider.dropTable("tb_user", 5, TimeUnit.SECONDS);
            log.info("drop table tb_user.");
        }

        //
        provider.createTable(tbUser, columns, 5, TimeUnit.SECONDS);
        //
        KuduTable tableInfo = provider.getTable("tb_user");
        List<KuduColumn> kuduColumns = provider.getColumns("tb_user");
        // client.openTable("aa").newInsert().setRow();
        assert kuduColumns != null;
    }
}
