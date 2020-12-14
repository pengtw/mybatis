package com.austin.sqlSession;

import com.austin.pojo.Configuration;
import com.austin.pojo.MappedStatement;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

    public <E> List<E> query(MappedStatement mappedStatement,Object... params) throws Exception;

    public int update(MappedStatement mappedStatement, Object[] params) throws Exception;
}
