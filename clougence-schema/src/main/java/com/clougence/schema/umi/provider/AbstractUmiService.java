package com.clougence.schema.umi.provider;

import com.clougence.utils.function.ESupplier;
import com.clougence.schema.metadata.MetaDataService;
import com.clougence.schema.umi.UmiService;

/**
 * mysql DsSchemaRService
 *
 * @author mode 2021/1/8 19:56
 */
public abstract class AbstractUmiService<T extends MetaDataService, E extends Throwable> implements UmiService {

    protected final ESupplier<T, E> metadataSupplier;

    public AbstractUmiService(ESupplier<T, E> metadataSupplier){
        this.metadataSupplier = metadataSupplier;
    }
}
