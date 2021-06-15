package com.clougence.schema.umi;
import com.clougence.schema.DataSourceType;

import java.sql.SQLException;
import java.util.List;

/**
 * Unified Meta Information Service
 * @author mode 2020/12/8 15:21
 */
public interface UmiService {
    public DataSourceType getDataSourceType();

    /** 获取版本信息 */
    public String getVersion() throws SQLException;

    public List<UmiSchema> getRootSchemas() throws SQLException;

    public UmiSchema getSchemaByPath(String... parentPath) throws SQLException;

    public List<UmiSchema> getChildSchemaByPath(String... parentPath) throws SQLException;
}
