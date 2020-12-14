package com.austin.test;

import com.austin.dao.IUserDao;
import com.austin.io.Resources;
import com.austin.pojo.User;
import com.austin.sqlSession.SqlSession;
import com.austin.sqlSession.SqlSessionFactory;
import com.austin.sqlSession.SqlSessionFactoryBuilder;
import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;

public class IPersistenceTest {
    private SqlSession sqlSession;

    @Before
    public void before() throws PropertyVetoException, DocumentException {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        sqlSession = sqlSessionFactory.openSession();
    }

    @Test
    public void testSelect(){
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        List<User> all = userDao.findAll();
        for (User user1 : all) {
            System.out.println(user1);
        }
    }

    @Test
    public void testInsert(){
        User user = new User();
        user.setId(4);
        user.setUsername("李四");

        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        userDao.addUser(user);
    }

    @Test
    public void testUpdate() throws Exception {
        User user = new User();
        user.setId(4);
        user.setUsername("老王");

        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        userDao.updateUser(user);
    }

    @Test
    public void testDelete(){
        User user = new User();
        user.setId(4);
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        userDao.deleteUser(user);
    }
}
