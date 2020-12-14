package com.austin.sqlSession;

import com.austin.pojo.Configuration;
import com.austin.pojo.MappedStatement;
import com.austin.pojo.SqlType;

import java.lang.reflect.*;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    private Executor executor;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
        this.executor = new SimpleExecutor(configuration);
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {
        //根据statementId获取唯一的MappedStatement
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        List<Object> list = executor.query(mappedStatement, params);
        return (List<E>) list;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId, params);
        if (objects == null || objects.size() == 0){
            return null;
        }else if(objects.size()==1){
            return (T) objects.get(0);
        }else {
            throw new RuntimeException("查询结果过多");
        }
    }

    @Override
    public int insert(String statementId, Object... params) throws Exception {
        return update(statementId,params);
    }

    @Override
    public int update(String statementId, Object... params) throws Exception {
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        return executor.update(mappedStatement, params);
    }

    @Override
    public int delete(String statementId, Object... params) throws Exception {
        return update(statementId,params);
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        // 使用JDK动态代理来为Dao接口生成代理对象，并返回
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 获取mapper接口全限定名和当前调用的方法名 用来确定唯一的MappedStatement
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + methodName;

                //获取MappedStatement
                MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
                //查询
                if (mappedStatement.getSqlType() == SqlType.SELECT) {
                    // 获取被调用方法的返回值类型
                    Type genericReturnType = method.getGenericReturnType();
                    // 判断是否进行了 泛型类型参数化 也就是判断返回参数是否是集合
                    if (genericReturnType instanceof ParameterizedType) {
                        List<Object> objects = selectList(statementId, args);
                        return objects;
                    }
                    return selectOne(statementId, args);
                    //新增
                } else if (mappedStatement.getSqlType() == SqlType.INSERT) {
                    return insert(statementId, args);
                    //修改
                } else if (mappedStatement.getSqlType() == SqlType.UPDATE) {
                    return update(statementId, args);
                    //删除
                } else if (mappedStatement.getSqlType() == SqlType.DELETE) {
                    return delete(statementId, args);
                }

                return null;
            }
        });
        return (T) proxyInstance;
    }


}
