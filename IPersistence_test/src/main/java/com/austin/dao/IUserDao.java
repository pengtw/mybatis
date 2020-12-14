package com.austin.dao;

import com.austin.pojo.User;

import java.util.List;

public interface IUserDao {

    //查询所有用户
    public List<User> findAll();

    //根据条件进行用户查询
    public User findByCondition(User user);

    //插入用户
    public int addUser(User user);

    //更新用户
    public int updateUser(User user) throws Exception;

    //根据id删除用户
    public int deleteUser(User user);

}
