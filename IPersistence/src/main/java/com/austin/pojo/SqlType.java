package com.austin.pojo;

/**
 * 区分MappedStatement类型 增删改查
 */
public enum SqlType {

    /**
     * 查询
     */
    SELECT,
    /**
     * 插入
     */
    INSERT,
    /**
     * 更新
     */
    UPDATE,
    /**
     * 删除
     */
    DELETE,
}