package com.clougence.schema.umi;

import java.sql.SQLException;
import java.util.List;

import com.clougence.schema.DsType;

/**
 * Unified Meta Information Service
 * 
 * @author mode 2020/12/8 15:21
 */
public interface UmiService {

    public DsType getDataSourceType();

    /** 获取版本信息 */
    public String getVersion() throws SQLException;

    public List<UmiSchema> getRootSchemas() throws SQLException;

    public UmiSchema getSchemaByPath(String... parentPath) throws SQLException;

    public List<UmiSchema> getChildSchemaByPath(String... parentPath) throws SQLException;
}
