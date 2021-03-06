package com.austin.sqlSession;


import com.austin.config.BoundSql;
import com.austin.pojo.Configuration;
import com.austin.pojo.MappedStatement;
import com.austin.utils.GenericTokenParser;
import com.austin.utils.ParameterMapping;
import com.austin.utils.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor {

    private Configuration configuration;

    public SimpleExecutor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> query(MappedStatement mappedStatement, Object... params) throws Exception {
        //获取预处理对象
        PreparedStatement preparedStatement = getPreparedStatement(mappedStatement,params);
        //执行sql
        ResultSet resultSet = preparedStatement.executeQuery();
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);

        List<Object> objects = new ArrayList<>();
        //封装返回结果集
        while (resultSet.next()){
            Object o =resultTypeClass.newInstance();
            //元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                // 字段名
                String columnName = metaData.getColumnName(i);
                // 字段的值
                Object value = resultSet.getObject(columnName);

                //使用反射或者内省 根据数据库表和实体的对应关系 完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o,value);
            }
            objects.add(o);
        }
        return (List<E>) objects;
    }

    @Override
    public int update(MappedStatement mappedStatement, Object[] params) throws Exception {
        //获取预处理对象
        PreparedStatement preparedStatement = getPreparedStatement(mappedStatement,params);
        return preparedStatement.executeUpdate();
    }

    /**
     * 获取连接 封装参数
     * @return
     */
    private PreparedStatement getPreparedStatement(MappedStatement mappedStatement, Object... params) throws Exception {
        // 注册驱动，获取数据库连接
        Connection connection = configuration.getDataSource().getConnection();

        //获取sql语句
        String sql = mappedStatement.getSql();
        //转换sql语句
        BoundSql boundSql = getBoundSql(sql);

        //获取预处理对象
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        //获取参数的全路径
        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClass = getClassType(parameterType);
        if (parameterTypeClass != null){
            //循环从sql中解析出来的参数，从传入的参数对象通过反射来取值并设置到PreparedStatement中
            List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
            for (int i = 0; i < parameterMappingList.size(); i++) {
                ParameterMapping parameterMapping = parameterMappingList.get(i);
                String content = parameterMapping.getContent();
                //反射
                Field declaredField = parameterTypeClass.getDeclaredField(content);
                //暴力访问
                declaredField.setAccessible(true);
                Object o = declaredField.get(params[0]);
                //PreparedStatement设置参数索引从1开始，所以加1
                preparedStatement.setObject(i+1,o);
            }
        }
        return preparedStatement;
    }

    /**
     * 根据全限定名加载class
     **/
    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {
        if(parameterType!=null){
            Class<?> aClass = Class.forName(parameterType);
            return aClass;
        }
         return null;
    }


    /**
     * 完成对#{}的解析工作：
     *  1.将#{}使用？进行代替
     *  2.解析出#{}里面的参数值进行存储
     */
    private BoundSql getBoundSql(String sql) {
        //标记处理类：配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析出来的sql
        String parseSql = genericTokenParser.parse(sql);
        //#{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql,parameterMappings);
         return boundSql;
    }

}
