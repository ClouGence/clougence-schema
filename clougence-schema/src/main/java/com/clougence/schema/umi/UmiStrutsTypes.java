package com.clougence.schema.umi;

import java.sql.JDBCType;

import com.clougence.schema.metadata.FieldType;

public enum UmiStrutsTypes implements FieldType {

    Any("ANY"), // 不确定的结构
    Array("ARRAY"), // 结构化的，表示数组或集合
    Struts("STRUTS"), // 结构化的，带有具体 Key/Value 的结构体
    Map("MAP") // 结构化的，Key-Value 映射对
    ;

    //
    private final String codeKey;

    UmiStrutsTypes(String codeKey){
        this.codeKey = codeKey;
    }

    public static UmiStrutsTypes valueOfCode(String code) {
        for (UmiStrutsTypes tableType : UmiStrutsTypes.values()) {
            if (tableType.codeKey.equalsIgnoreCase(code)) {
                return tableType;
            }
        }
        return null;
    }

    @Override
    public String getCodeKey() { return this.codeKey; }

    @Override
    public Integer getJdbcType() { return null; }

    @Override
    public JDBCType toJDBCType() {
        return null;
    }
}
