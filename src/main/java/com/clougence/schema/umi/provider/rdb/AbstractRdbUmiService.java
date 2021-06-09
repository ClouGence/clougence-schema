package com.clougence.schema.umi.provider.rdb;
import com.clougence.schema.metadata.CaseSensitivityType;
import com.clougence.schema.metadata.provider.rdb.RdbMetaDataService;
import com.clougence.schema.umi.provider.AbstractUmiService;
import net.hasor.utils.function.ESupplier;

import java.sql.SQLException;

/**
 * mysql DsSchemaRService
 * @author mode 2021/1/8 19:56
 */
public abstract class AbstractRdbUmiService<T extends RdbMetaDataService> extends AbstractUmiService<T, SQLException> implements RdbUmiService {
    public AbstractRdbUmiService(ESupplier<T, SQLException> metadataSupplier) {
        super(metadataSupplier);
    }

    @Override
    public String getVersion() throws SQLException {
        return this.metadataSupplier.get().getVersion();
    }

    public CaseSensitivityType getPlain() throws SQLException {
        return this.metadataSupplier.get().getPlain();
    }

    public CaseSensitivityType getDelimited() throws SQLException {
        return this.metadataSupplier.get().getDelimited();
    }

    public String getCurrentSchema() throws SQLException {
        return this.metadataSupplier.get().getCurrentSchema();
    }

    public String getCurrentCatalog() throws SQLException {
        return this.metadataSupplier.get().getCurrentCatalog();
    }
}
