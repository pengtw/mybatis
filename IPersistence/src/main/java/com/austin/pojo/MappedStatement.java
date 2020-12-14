package com.austin.pojo;

/**
 * 每个mapper.xml下的select、insert、update、delete都对应一个MappedStatement
 **/
public class MappedStatement {

    /**
     *  id标识 namespace+id
     */
    private String id;

    /** 返回值类型 **/
    private String resultType;

    /** 参数值类型 **/
    private String parameterType;

    /** sql语句 **/
    private String sql;

    /** sql类型 **/
    private SqlType sqlType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    public void setSqlType(SqlType sqlType) {
        this.sqlType = sqlType;
    }
}
